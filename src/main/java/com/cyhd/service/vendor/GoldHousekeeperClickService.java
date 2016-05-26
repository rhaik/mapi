package com.cyhd.service.vendor;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import net.sf.json.JSONObject;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.Base64;
import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.AppTaskService;
import com.cyhd.service.impl.AppVendorService;
import com.cyhd.service.impl.UserInstalledAppService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RequestSignUtil;
import com.cyhd.web.common.ClientInfo;

@Service("goldHousekeeperClickService")
public class GoldHousekeeperClickService implements IVendorClickService{

	private final  String KEY = "gpaykey123";
	private final  String md5Key = "md5key";
	private final  String deskey ="deskeydeskey";
	
	@Resource
	private UserInstalledAppService userInstalledAppService;
	
	
	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app,
			AppTask appTask, ClientInfo clientInfo) {
	
		String idfa = clientInfo.getIdfa();
		
		Map<String, String> callbackParams = new HashMap<>();
	    callbackParams.put("appkey", vendor.getApp_key());
	    callbackParams.put("adid", String.valueOf(app.getId() * 11 + 997));
	    callbackParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
	    callbackParams.put("device_id", idfa);
	
	     String sign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(callbackParams) + vendor.getApp_secret());
	     callbackParams.put("expire", "false");
	     callbackParams.put("sign", sign);

       //回调地址
         String callbackUrl = "https://api.miaozhuandaqian.com/www/vendor/callback.3w";
         if (!GlobalConfig.isDeploy){
             callbackUrl = "https://www.mapi.lieqicun.cn/www/vendor/callback.3w";
         }
         
         String fullCallbackUrl = callbackUrl + "?" + RequestSignUtil.getSortedRequestString(callbackParams);
         
         try {
        	 callbackParams.clear();
        	 callbackParams.put("data.idfa", doDESEncode(idfa,KEY));
        	 callbackParams.put("data.callBackUrl",doDESEncode(fullCallbackUrl,KEY));
        	 sign = sign(callbackParams);
        	 callbackParams.put("sign", sign);
        	 logger.info("请求黄金管家点击接口参数:{}",callbackParams);
        	String resp =  HttpUtil.postByForm(vendor.getClick_url(), callbackParams);
        	logger.info("请求黄金管家点击接口,Response:{}",resp);
        	JSONObject json = JSONObject.fromObject(resp);
        	if(json != null){
        		if(json.containsKey("msg")){
        			json = json.getJSONObject("msg");
        			int code = json.getInt("code");
        			//0 表示成功 1 表示已接受 2 已接受并注册成功 3 参数为空
        			if(code == 0){
        				return true;
        			}else{
        				//已安装
        				if(code == 2){
        					try{
        						userInstalledAppService.insert(user.getId(), app.getId(), clientInfo.getDid(), app.getAgreement());	
        					}catch(Exception e){}
        				}
        				return false;
        			}
        		}
        	}
         } catch (Exception e) {
			return false;
		}
		
		return false;
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask,
			ClientInfo clientInfo) {
		String idfa = clientInfo.getIdfa();
		
		Map<String, String> callbackParams = new HashMap<>();
	    callbackParams.put("appkey", vendor.getApp_key());
	    callbackParams.put("adid", String.valueOf(app.getId() * 11 + 997));
	    callbackParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
	    callbackParams.put("device_id", idfa);
	
	     String sign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(callbackParams) + vendor.getApp_secret());
	     callbackParams.put("expire", "false");
	     callbackParams.put("sign", sign);

       //回调地址
         String callbackUrl = "http://third.miaozhuandaqian.com/www/vendor/callback.3w";
         if (!GlobalConfig.isDeploy){
             callbackUrl = "http://www.mapi.lieqicun.cn/www/vendor/callback.3w";
         }
         
         String fullCallbackUrl = callbackUrl + "?" + RequestSignUtil.getSortedRequestString(callbackParams);
         
         try {
        	 callbackParams.clear();
        	 callbackParams.put("data.idfa", doDESEncode(idfa,KEY));
        	 callbackParams.put("data.callBackUrl",doDESEncode(fullCallbackUrl,KEY));
        	 sign = sign(callbackParams);
        	 callbackParams.put("sign", sign);
        	 logger.info("请求黄金管家点击接口参数:{}",callbackParams);
        	String resp =  HttpUtil.postByForm(vendor.getClick_url(), callbackParams);
        	logger.info("请求黄金管家点击接口,Response:{}",resp);
        	JSONObject json = JSONObject.fromObject(resp);
        	if(json != null){
        		if(json.containsKey("msg")){
        			json = json.getJSONObject("msg");
        			int code = json.getInt("code");
        			//0 表示成功 1 表示已接受 2 已接受并注册成功 3 参数为空
        			return code == 0;
        		}
        	}
         } catch (Exception e) {
			return false;
		}
		
		return false;
	}

	private final String doDESEncode(String plainText,String password){
		String ciphertext = null;
		
		byte[] passwordByte = password.getBytes();
		byte[] inputData = plainText.getBytes();
		
		try {
			inputData = encrypt(inputData, Base64.encode(passwordByte));
			ciphertext = Base64.encode(inputData);
		} catch (Exception e) {
		}
		return ciphertext;
	}
	
	private final String sign(Map<String, String> map){
		List<String> list = new ArrayList<String>(map.keySet());	
		Collections.sort(list);
		StringBuilder sb = new StringBuilder(500);
		for(String key:list){
			sb.append(key).append(map.get(key));
		}
		String afterDes  = doDESEncode(sb.toString(),deskey);
		sb.delete(0, sb.length());
		sb.append(afterDes).append(md5Key);
		return MD5Util.getMD5(sb.toString());
	}
	
	private final  byte[] encrypt(byte[] data, String key) throws Exception {
		Key k = toKey(Base64.decode(key));
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, k);
		return cipher.doFinal(data);
	}

	private final Key toKey(byte[] key) throws Exception {
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(dks);
		// 当使用其他对称加密算法时，如AES、Blowfish等算法时，用下述代码替换上述三行代码
		// SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
		return secretKey;
	}

	
	public static void main(String[] args) {
	//{"dt":"2015-11-19 11:33:34","tz":28800, "appnm":"im.qianjin.zhou","appVer":"1.2.0","clientType":"ios", "model":"iPhone", "os":"iPhone OS8.4.1", "screen":"320x568", "channel":"inHouse","did":"5294C880-2B1E-48DA-B62B-6A495CA01BA9","aid":"AEAF8DB5-CE83-4C1E-88B4-2FFBEE8E5E16","token":"lucky_you_2015"}
		ClientInfo clientInfo = new ClientInfo();
		clientInfo.setIdfa("AEAF8DB5-CE83-4C1E-88B4-2FFBEE8E5E16");
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		GoldHousekeeperClickService goldHousekeeperClickService = context.getBean(GoldHousekeeperClickService.class);
		AppTaskService appTaskService = context.getBean(AppTaskService.class);
		AppVendorService appVendorService = context.getBean(AppVendorService.class);
		UserService userService = context.getBean(UserService.class);
		
		User user = userService.getUserById(1562);
		AppVendor vendor = appVendorService.getAppVendor(1011);
		App app = appTaskService.getApp(136);
		
		AppTask appTask = appTaskService.getAppTask(327);
		goldHousekeeperClickService.onClickApp(vendor, user, app, appTask, clientInfo);
		//i1cQ0ryiPhgMpmafBdC2HFaxea/IEhtke/SzsEX2U3wYblJHYEtpdQ==
		//i1cQ0ryiPhgMpmafBdC2HFaxea/IEhtke/SzsEX2U3wYblJHYEtpdQ==
	}
}
