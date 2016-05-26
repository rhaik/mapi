package com.cyhd.web.common.util;

import javax.servlet.http.HttpServletRequest;

import com.cyhd.common.util.StringUtil;
import com.cyhd.service.util.RedirectCodeEncoder;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.cyhd.common.util.MD5Util;
import com.cyhd.common.util.MagicKey;
import com.cyhd.common.util.structure.LRUCache;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.web.common.ClientAuth;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;

public class ClientAuthUtil {
	
	private static final Logger logger = LoggerFactory.getLogger("apirequest");
	
	private static final String sign_ser = "zhuandaqian_2015";
	private static final String android_sign_ser = "mzdq_android_2015";
	private static final int magic_key = 717820260;
	private static final int magic_key_android=1953271456;
	private static int MAX_CACHE_SIZE = 60;
	static{
		if(GlobalConfig.isDeploy)
			MAX_CACHE_SIZE = 20000;
	}
	
	private static LRUCache<String, Boolean> tsCache = new LRUCache<String, Boolean>(15, MAX_CACHE_SIZE, true);
	
	
	private static ClientAuth defaultClientAuth = new ClientAuth();
	static {
		defaultClientAuth.setTicket("111");
		defaultClientAuth.setTs(111);
		defaultClientAuth.setCode("213eab239812932131");
	}
	public static ClientAuth getDefaultClientAuth(){
		return defaultClientAuth;
	}
	
	public static ClientAuth getClientAuth(HttpServletRequest request) throws CommonException {
		ClientAuth clientAuth = (ClientAuth)request.getAttribute("clientAuth");
		if(clientAuth != null){
			return clientAuth;
		}
		String url = request.getRequestURI();
		String ci = request.getHeader("clientAuth");
		if (StringUtils.isEmpty(ci)) {
			logger.error(url + ",missing required parameter clientAuth!");
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTAUTH);
		}
		JSONObject json = JSONObject.fromObject(ci);
		if (JSONUtils.isNull(json)) {
			logger.error(url + ",error parameter clientAuth !");
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTAUTH);
		}
		clientAuth = new ClientAuth();
		if (json.containsKey("ticket"))
			clientAuth.setTicket(json.getString("ticket"));
		else
			clientAuth.setTicket("");
		if (json.containsKey("sign"))
			clientAuth.setSign(json.getString("sign"));
		if (json.containsKey("ts"))
			clientAuth.setTs(json.getInt("ts"));
		if (json.containsKey("code"))
			clientAuth.setCode(json.getString("code"));
		if (json.containsKey("rd"))
			clientAuth.setRd(json.getString("rd"));

		if (logger.isDebugEnabled()) {
			logger.debug(url + ",clientAuth=" + clientAuth);
		}
		request.setAttribute("clientAuth", clientAuth);

		return clientAuth;
	}
	public static void checkAuth(HttpServletRequest request, ClientAuth auth, ClientInfo clientInfo) throws CommonException {
		String url = request.getRequestURI();
		String rd = auth.getRd() ;
		if(StringUtils.isEmpty(rd)){
			logger.error(url+", client auth rd == null." + auth + ", clientinfo=" + clientInfo);
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTAUTH);
		}
		String code = auth.getCode();
		if(StringUtils.isEmpty(code)){
			logger.error(url+", client auth code == null."+ auth + ", clientinfo=" + clientInfo);
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTAUTH);
		}
		String sign = auth.getSign();
		if(StringUtils.isEmpty(sign)){
			logger.error(url+", client auth sign == null."+ auth + ", clientinfo=" + clientInfo);
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTAUTH);
		}
		String ticket = auth.getTicket();
		
		String did = clientInfo.getDid();
		if(StringUtils.isEmpty(did)){
			logger.error(url+", client info did is empty."+ clientInfo);
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTAUTH);
		}

		//安卓重定向之后，会使用相同的header再次请求url，此时检查url中重定向参数
		String rnd = request.getParameter("_rnd_");
		if (StringUtil.isNotBlank(rnd)){
			Integer rndValue = RedirectCodeEncoder.decode(rnd);
			if (rndValue != null && rndValue  > 0 && rndValue == auth.getSignValue()){
				//正确的安卓重定向请求，直接允许其通过
				return;
			}
		}

		checkDuplicated(url,ticket, did, rd);
		int clientType = clientInfo.isIos()?Constants.platform_ios:Constants.platform_android;
		int ts = checkCode(url, code,clientType) ;
		checkSign(url,ts,rd, sign,clientType);
	}
	
	/**
	 * 检查是否有重复的伪造请求 <br/>
	 * 在一段时间内每个设备的每个请求可以用 did+ts 唯一标识
	 * @param url
	 * @param did
	 * @param ts
	 * @throws CommonException
	 */
	private static void checkDuplicated(String url,String ticket, String did, String rd) throws CommonException {
		if(ticket == null)
			ticket = "";
		if(did == null)
			did = "";
		String key = ticket+"_"+did+"_"+rd;
		if(tsCache.containsKey(key)){
			tsCache.put(key, true);
			logger.error(url+", duplicate request! did=" + did + ", rd=" +rd);
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTAUTH);
		}else{
			tsCache.put(key, true);
		}
	}
	
	/**
	 * 校验签名字段
	 * @param request
	 * @param ts
	 * @param sign
	 * @throws CommonException
	 */
	private static void checkSign(String url, int ts, String rd,  String sign,int clientType) throws CommonException{
		String s = createLinkString(url);
		if(clientType == Constants.platform_ios){
			s =s + ts+ rd + sign_ser;
		}else if(clientType == Constants.platform_android){
			s = s + ts+ rd + android_sign_ser;
		}
		String smd5 = MD5Util.getMD5(s);
		if(!smd5.equalsIgnoreCase(sign)){
			logger.error(url +" client auth sign validate error!");
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTAUTH);
		}
	}
	
	private static int checkCode(String url, String code,int clientType) throws CommonException {
		int magickey = 0;
		switch (clientType) {
			case Constants.platform_android:
				magickey = magic_key_android;
				break;
			case Constants.platform_ios:
				magickey = magic_key;
				break;
			default:
				break;
		}
		
		MagicKey magic = MagicKey.decode(code, magickey);
		if(magic == null){
			logger.error(url + " auth code decode error!");
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTAUTH);
		} else 
			return magic.getHideValue() ;
	}
	
	private static String createLinkString(String url){
		String[] ss = url.split("/");
		int length = ss.length;
		if(length > 1)
			return ss[length-2] + "/" + ss[length - 1];
		else
			return ss[0];
	}
	
	public static void main(String[] args){
		System.out.println(MagicKey.decode("e7d2a8d548d0af4728c8e671448653d802c0c202838102002777fd2c6dea1de5", magic_key).getHideValue());
		String s = createLinkString("/web/discovery/discover.html");
		s =s + "717820260"+ "9EAF4C16037642889BC6D92D3A10F1E4-717820260" + sign_ser;
		String smd5 = MD5Util.getMD5(s);
		System.out.println(smd5);
	}

}
