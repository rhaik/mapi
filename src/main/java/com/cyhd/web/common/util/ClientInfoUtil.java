package com.cyhd.web.common.util;

import javax.servlet.http.HttpServletRequest;

import com.cyhd.common.util.NumberUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.service.util.VersionUtil;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.cyhd.service.util.RequestUtil;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientInfoUtil {

	private static final Logger logger = LoggerFactory.getLogger("apirequest");

	//必须加密的最小版本号，这个版本之后必须加密
	private static final int MIN_ENCRYPTED_VERSION = VersionUtil.getVersionCode("1.5.0");

	private static ClientInfo defaultClientInfo = new ClientInfo();
	static{
		defaultClientInfo.setAppVer("1.1.0");
		defaultClientInfo.setDid("121338472384712841231");
		defaultClientInfo.setModel("iPhone6");
		defaultClientInfo.setClientType("ios");
		defaultClientInfo.setNet("wifi");
		defaultClientInfo.setOs("8.4");
	}
	
	public static ClientInfo getDefaultClientInfo(){
		return defaultClientInfo;
	}

	/**
	 * 获取client info，支持加密
	 * @param request
	 * @return
	 * @throws CommonException
	 */
	public static ClientInfo getClientInfo(HttpServletRequest request) throws CommonException{
		ClientInfo clientInfo = (ClientInfo)request.getAttribute("clientInfo");
		if(clientInfo != null) {
			return clientInfo;
		}

		String ci = request.getHeader("clientInfo");
		if (StringUtils.isEmpty(ci)) {
			logger.error(request.getRequestURI() + ",missing required parameter clientInfo!");
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTINFO);
		}

		boolean isEncrypted = false;
		if (ci.charAt(0) != '{')  { //加密过，使用解密的字符串
			isEncrypted = true;
			ci = (String) request.getAttribute("decyptedClientInfo");
		}

		clientInfo = getClientInfo(request, ci);
		if (clientInfo != null && isEncrypted == false && VersionUtil.getVersionCode(clientInfo.getAppVer()) >= MIN_ENCRYPTED_VERSION){
			clientInfo = null;
		}

		request.setAttribute("clientInfo", clientInfo);

		return getClientInfo(request, ci);
	}

	/**
	 * 根据请求和client info json字符串获取client info
	 * @param request
	 * @param ci
	 * @return
	 * @throws CommonException
	 */
	public static ClientInfo getClientInfo(HttpServletRequest request, String ci) throws CommonException {
		JSONObject json = JSONObject.fromObject(ci);
		if (JSONUtils.isNull(json)) {
			logger.error(request.getRequestURI() + ",error parameter clientInfo !" + ci);
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTINFO);
		}

		ClientInfo clientInfo = new ClientInfo();
		if (json.containsKey("appnm"))
			clientInfo.setAppnm(json.getString("appnm"));

		if (json.containsKey("appVer"))
			clientInfo.setAppVer(json.getString("appVer"));

		if (json.containsKey("clientType"))
			clientInfo.setClientType(json.getString("clientType"));

		if (json.containsKey("model"))
			clientInfo.setModel(json.getString("model"));

		if (json.containsKey("os"))
			clientInfo.setOs(json.getString("os"));

		if (json.containsKey("screen"))
			clientInfo.setScreen(json.getString("screen"));

		if (json.containsKey("did"))
			clientInfo.setDid(json.getString("did"));

		if (json.containsKey("token"))
			clientInfo.setToken(json.getString("token"));

		if (json.containsKey("android_id"))
			clientInfo.setAndroidid(json.getString("android_id"));
		else
			clientInfo.setAndroidid("");
		
		if(json.containsKey("aid")){
			clientInfo.setIdfa(json.getString("aid"));
		}

		if (json.containsKey("dt"))
			clientInfo.setDt(json.getString("dt"));

		if (json.containsKey("tz"))
			clientInfo.setTz(json.getInt("tz"));

		if (json.containsKey("channel"))
			clientInfo.setChannel(json.getString("channel"));

		if (json.containsKey("net")) {
			clientInfo.setNet(json.getString("net"));
		}

		if (json.containsKey("bid")){
			String bid = json.getString("bid");
			if(!"(null)".equalsIgnoreCase(bid) &&  ! "null".equalsIgnoreCase(bid)){
				clientInfo.setBid(bid);
			}
		}

		clientInfo.setCityid(json.optInt("cityid", 131));

		String ip = RequestUtil.getIpAddr(request);
		if (ip.length() > 96) {
			ip = ip.substring(0, 96);
		}
		clientInfo.setIpAddress(ip);

		if (json.containsKey("loc")) {
			String location = json.getString("loc");
			if (!StringUtils.isEmpty(location)) {
				String locs[] = location.split(",");
				if (locs.length >= 3) {
					double latitude = NumberUtil.safeParseDouble(locs[0]);
					double longitude = NumberUtil.safeParseDouble(locs[1]);
					double scale = NumberUtil.safeParseDouble(locs[2]);
					clientInfo.setScale(scale);
					clientInfo.setLatitude(latitude);
					clientInfo.setLongitude(longitude);
				}
			}
		}
		
		request.setAttribute("clientInfo", clientInfo);

		// if(REQUESTLOG.isDebugEnabled()){
		// REQUESTLOG.debug(url+",clientInfo=" + clientInfo);
		// }

		return clientInfo;
	}

	public static ClientInfo getSafariClientInfo(HttpServletRequest request){
		UserAgentUtil.UserAgent ua = UserAgentUtil.getUserAgent(request);

		ClientInfo client = new ClientInfo();
		client.setAppVer(GlobalConfig.safari_version);
		client.setDid("safariapp00150102ad0b0301de");

		if (ua.isIPad()){
			client.setModel("iPad");
		}else if (ua.isIPhone()){
			client.setModel("iPhone");
		}else {
			client.setModel("unkonwn");
		}
		client.setClientType("ios");
		client.setNet("wifi");
		client.setIpAddress(RequestUtil.getIpAddr(request));

		String userAgent = request.getHeader("user-agent");
		if (StringUtil.isBlank(userAgent)) {
			client.setOs("8.4.3");
		}else{
			Matcher matcher = Pattern.compile("OS\\s(\\d[_\\d]+)").matcher(userAgent);
			if (matcher.find()){
				String os = matcher.group(1);
				client.setOs(os.replaceAll("_", "."));
			}else {
				client.setOs("8.4.3");
			}
		}
		return client;
	}
	public static void main(String[] args) {
		String bid = "null";
		System.out.println(!("(null)".equalsIgnoreCase(bid) || "null".equalsIgnoreCase(bid)));
		System.out.println(!"(null)".equalsIgnoreCase(bid) && !"null".equalsIgnoreCase(bid));
	
	}
}
