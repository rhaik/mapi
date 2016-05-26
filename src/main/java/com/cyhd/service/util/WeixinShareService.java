package com.cyhd.service.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Parameter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.cyhd.common.util.Pair;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.Account;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.LiveAccess;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.impl.AccountService;
import com.cyhd.service.impl.WeixinService;

// 微信分享utils
@Service
public class WeixinShareService {
	
	@Resource
	private WeixinService weixinService;
	
	@Resource
	private AccountService accountService;
	
	
	public final static String nonceStr = "ea652c9431c3165d4c24a1fe556ffd8853751be9" ;

    CacheLRULiveAccessDaoImpl<Pair<String, String>> jsapiCache = new CacheLRULiveAccessDaoImpl<>(Constants.hour_millis, 10);
	
	public static String createTimestamp() {
		String timestamp = System.currentTimeMillis()/1000 + "" ;
		return timestamp ;
	}
	
//	private static String getAccessToken() throws Exception {
//		
//		String rt = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + appsecret, null) ;
//		JSONObject obj = JSONObject.fromObject(rt) ;
//		
//		return obj.optString("access_token", "") ;
//	}
	
	private  Pair<String, String> createJsapi(Account wxAccount) throws Exception {
		String accessToken = weixinService.getAccessToken(wxAccount) ;
		String rt = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi", null) ;
		
		JSONObject obj = JSONObject.fromObject(rt) ;

        String ticket = obj.optString("ticket", "");
        if (StringUtil.isNotBlank(ticket)){
            return new Pair<>(ticket, createTimestamp());
        }
		return  null;
	}
	
	private Pair<String, String> getJsapi(Account wxAccount) throws Exception {
        Pair<String,String> jsapi = jsapiCache.get(wxAccount.getWxappid());
		if(jsapi == null) {
            jsapi = createJsapi(wxAccount);
			if(jsapi != null) {
				jsapiCache.set(wxAccount.getWxappid(), jsapi);
			}
		}

        if (jsapi == null){
            jsapi = new Pair<>("", createTimestamp());
        }
        return jsapi;
	}


    /**
     * 获取默认公众号的jsapi信息
     * @param
     * @return
     * @throws Exception
     */
    public Map<String, String> sign(HttpServletRequest request) throws Exception{
        return sign(RequestUtil.getFullUrl(request), request.getHeader("Host"));
    }


    /**
     * 获取默认公众号的jsapi信息
     * @param url
     * @return
     * @throws Exception
     */
    public Map<String, String> sign(String url) throws Exception{
        return sign(url, null);
    }

    /**
     * 根据host获取公众号的jsapi信息
     * @param url
     * @param host
     * @return
     * @throws Exception
     */
	public Map<String, String> sign(String url, String host) throws Exception {
        Account wxAccount = accountService.getAccountByHost(host);

        Pair<String, String> jsapi = getJsapi(wxAccount);

        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = nonceStr;
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi.first +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + jsapi.second +
                  "&url=" + url;

        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi.first);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", jsapi.second);
        ret.put("signature", signature);
        ret.put("appid", wxAccount.getWxappid());
        ret.put("logo", GlobalConfig.logo);

        return ret;
    }
	
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
	
	public static void main(String[] args) throws Exception {
		//System.out.println(sign("sdf"));
	}
}
