package com.cyhd.service.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

import com.cyhd.common.util.HttpUtil;
import com.cyhd.web.common.ClientInfo;
/**
 * @author rhaik
 * 
 */
public class DianruInterfaceUtil {
	private String appid = GlobalConfig.getValue("third_dianru_app_id", "8605");
	private String uid = "11891"; 
	private String key="miaozhuandaqian.com";
	private String appsecret="miaozhuandaqian.com";
	private String url = "http://api.mobile.dianru.com/miaozhuan/click";
	private String sid = "1";
	private String message;
	
	private static Logger log = LoggerFactory.getLogger("third");
	
	public DianruInterfaceUtil() {}
	public DianruInterfaceUtil(String url, String uid) {
		this.url = url == null ? this.uid : url;
		this.uid = uid == null ? this.uid : uid;
	}
	/**
	 * 点击请求
	 * 
	 * @param cid
	 * @param adid
	 * @param appuserid
	 * @param client
	 * 
	 * @return boolean
	 */
	public boolean onClick(String cid, int adid, int appuserid, ClientInfo client) {
		Map<String, String> pars = new HashMap<String, String>();
		pars.put("sid", sid);
		pars.put("type", "2");
		pars.put("cid", cid);
		pars.put("adid", adid+"");
		pars.put("appid", appid);
		pars.put("uid", uid);
		pars.put("appuserid", appuserid+"");
		pars.put("device", client.getModel());
		pars.put("screen", client.getScreen());
		pars.put("os", client.getOs());
		pars.put("osver", client.getAppVer());
		pars.put("idfa", client.getIdfa());
		pars.put("mac", "02:00:00:00:00:00");
		pars.put("localip", client.getIpAddress());
		Date time = new Date();
		pars.put("time", time.getTime()+"");
		
		pars.put("checksum", getClickSign(pars));
		
		String rs ="";
		try{
			log.info("DianruInterfaceUtil onClick request url:{},params:{}",url, pars.toString());
			rs = HttpUtil.post(url, pars, "");
			log.info("DianruInterfaceUtil onClick retrun: {}", rs);
			JSONObject json = new JSONObject(rs);  
			setMessage(json.get("message").toString());
			return json.get("success").equals("true") ? true : false;
		} catch(Exception e) {
			log.info("DianruInterfaceUtil onClick error:" + e.getMessage());
			return false;
		}
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	/**
	 * 验证签名
	 * 
	 * @param pars
	 * @return
	 */
	public boolean validateSign(LinkedHashMap<String, String>  pars) {
		String sign = getCallBackSign(pars);
		if(pars.containsKey("checksum") && pars.get("checksum").equals(sign)) {
			return true;
		} 
		return false;
	}
	/**
	 * 生成按字母排序的地址
	 * 
	 * @param params
	 * @return
	 */
	 public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
	/**
	 * 生成签名
	 * 
	 * @param pars
	 * @return
	 */
	private String getClickSign(Map<String, String> pars) {
		String prestr = createLinkString(pars) + key;
		log.info("DianruInterfaceUtil onClick sign params:{},key:{}",  prestr, key);
		return MD5.getMD5(prestr.getBytes()); // 把最终的字符串签名，获得签名结果
	}
	/**
	 * 生成回调签名
	 * 
	 * @param pars
	 * @return
	 */
	private String getCallBackSign(LinkedHashMap<String, String> pars) {
		pars.put("appsecret", appsecret);
		StringBuilder prestr = new StringBuilder();
		for (String name : pars.keySet()) {
			if(name.equals("checksum")) {
				continue;
			}
			String value = pars.get(name); 
			prestr.append("&" + name + "=" + value);
		}
		prestr.delete(0, 1);
		String keyString = "?" + prestr.toString();
		log.info("DianruInterfaceUtil onClick sign params:{}",  keyString);
		return MD5.getMD5(keyString.getBytes()); // 把最终的字符串签名，获得签名结果
	}
 	public static void main(String[] args) throws ParseException{
 		 System.out.println(GlobalConfig.getValue("third_dianru_app_id", "8605"));
 	}
 	
} 

