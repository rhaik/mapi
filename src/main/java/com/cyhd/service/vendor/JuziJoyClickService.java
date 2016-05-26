package com.cyhd.service.vendor;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserInstalledAppService;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RequestSignUtil;
import com.cyhd.web.common.ClientInfo;

@Service(value="juziJoyClickService")
public class JuziJoyClickService  implements IVendorClickService{

	@Resource
	private UserInstalledAppService userInstalledAppService;
	
	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app,
			AppTask appTask, ClientInfo clientInfo) {
		String response = null;
		JSONObject json = null;
		
		String host = "http://testapi.app.happyjuzi.com/v2.3/domob/check";
		if(GlobalConfig.isDeploy){
			host = "http://api.app.happyjuzi.com/v2.3/domob/check";
		}
		
		try {
			 Map<String, String> params = new HashMap<String, String>(1);
			 params.put("idfa", clientInfo.getIdfa());
			 response = HttpUtil.postByForm(host, params);
			 logger.info("橘子娱乐,排重接口:response:{},idfa:{},user:{}",response,clientInfo.getIdfa(),user.getId());
			json = JSONObject.fromObject(response);
			if(json != null){
				if(json.optInt(clientInfo.getIdfa()) == 1){
					//安装过
					try {
						userInstalledAppService.insert(user.getId(), app.getId(), clientInfo.getDid(), app.getAgreement());
					} catch (Exception e) {}
					logger.info("橘子娱乐排重,idfa:{},userId:{},已经安装过",clientInfo.getIdfa(),user.getId());
					return false;
				}
			}
		} catch (Exception e) {
			logger.error("JuziJoyClickService error,排重接口, appTask:{}, user:{}, error:{}", appTask, user, e);
			return false;
		}
		//排重结束
		StringBuilder sb = new StringBuilder();
		 //回调地址
        String callbackUrl = "http://third.miaozhuandaqian.com/www/vendor/callback.3w";
        if (!GlobalConfig.isDeploy){
            callbackUrl = "http://www.mapi.lieqicun.cn/www/vendor/callback.3w";
        }
        
        try{
	        Map<String, String> callbackParams = new HashMap<>();
	        callbackParams.put("appkey", vendor.getApp_key());
	        callbackParams.put("adid", String.valueOf(app.getId() * 11 + 997));
	        callbackParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
	        callbackParams.put("device_id", clientInfo.getIdfa());
	
	        String sign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(callbackParams) + vendor.getApp_secret());
	        callbackParams.put("expire", "false");
	        callbackParams.put("sign", sign);
	
	        String fullCallbackUrl = callbackUrl + "?" + RequestSignUtil.getSortedRequestString(callbackParams);
	    
			sb.append(vendor.getClick_url());
			
			sb.append("?appId=").append(app.getAppstore_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&source=").append("mzdq");
			sb.append("&callback_url=").append(URLEncoder.encode(fullCallbackUrl, "utf-8"));
			
			response = HttpUtil.get(sb.toString(), null);
			
			logger.info("橘子娱乐,点击接口:response:{},idfa:{},user:{}",response,clientInfo.getIdfa(),user.getId());
			json = JSONObject.fromObject(response);
			if(json != null){
				return "ok".equalsIgnoreCase(json.getString("result"));
			}
		} catch (Exception e) {
			logger.error("JuziJoyClickService error,点击接口, appTask:{}, user:{}, error:{}", appTask, user, e);
			return false;
		}
		return false;
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask,
			ClientInfo clientInfo) {
		String response = null;
		JSONObject json = null;
		String host = "http://testapi.app.happyjuzi.com/v2.3/domob/check";
		if(GlobalConfig.isDeploy){
			host = "http://api.app.happyjuzi.com/v2.3/domob/check";
		}
		
		try {
			 Map<String, String> params = new HashMap<String, String>(1);
			 params.put("idfa", clientInfo.getIdfa());
			 response = HttpUtil.postByForm(host, params);
			 logger.info("橘子娱乐,排重接口:response:{},idfa:{},user:{}",response,clientInfo.getIdfa());
			json = JSONObject.fromObject(response);
			if(json != null){
				if(json.optInt(clientInfo.getIdfa()) == 1){
					return false;
				}
			}
		} catch (Exception e) {
			logger.error("JuziJoyClickService error,排重接口, appTask:{}, user:{}, error:{}", appTask,  e);
			return false;
		}
		//排重结束
		
		StringBuilder sb = new StringBuilder();
		 //回调地址
        String callbackUrl = "https://api.miaozhuandaqian.com/www/vendor/callback.3w";
        if (!GlobalConfig.isDeploy){
            callbackUrl = "https://www.mapi.lieqicun.cn/www/vendor/callback.3w";
        }
        
        try{
	        Map<String, String> callbackParams = new HashMap<>();
	        callbackParams.put("appkey", vendor.getApp_key());
	        callbackParams.put("adid", String.valueOf(app.getId() * 11 + 997));
	        callbackParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
	        callbackParams.put("device_id", clientInfo.getIdfa());
	
	        String sign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(callbackParams) + vendor.getApp_secret());
	        callbackParams.put("expire", "false");
	        callbackParams.put("sign", sign);
	
	        String fullCallbackUrl = callbackUrl + "?" + RequestSignUtil.getSortedRequestString(callbackParams);
	    
			sb.append(vendor.getClick_url());
			
			sb.append("?appId=").append(app.getAppstore_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&source=").append("mzdq");
			sb.append("&callback_url=").append(URLEncoder.encode(fullCallbackUrl, "utf-8"));
			
			response = HttpUtil.get(sb.toString(), null);
			
			logger.info("橘子娱乐,点击接口:response:{},idfa:{},",response,clientInfo.getIdfa());
			json = JSONObject.fromObject(response);
			if(json != null){
				return "ok".equalsIgnoreCase(json.getString("result"));
			}
		} catch (Exception e) {
			logger.error("JuziJoyClickService error,点击接口, appTask:{}, error:{}", appTask, e);
			return false;
		}
		return false;
	}
	@Override
	public String disctinct(AppVendor vendor, App app, String idfas) {
		 return handleDiactinct(executeDisctinct(vendor, app, idfas), 1);
	}
	
	@Override
	public Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas) {
		return handleDiactinctNew(executeDisctinct(vendor, app, idfas), 1);
	}
	public String executeDisctinct(AppVendor vendor, App app, String idfas) {
		String host = "http://testapi.app.happyjuzi.com/v2.3/domob/check";
		if(GlobalConfig.isDeploy){
			host = "http://api.app.happyjuzi.com/v2.3/domob/check";
		}
		
		try {
			 Map<String, String> params = new HashMap<String, String>(1);
			 params.put("idfa", idfas);
			 String response = HttpUtil.postByForm(host, params);
			 logger.info("橘子娱乐,排重接口:response:{},idfa:{},user:{}",response);
			 return response;
		} catch (Exception e) {
			logger.error("JuziJoyClickService error,排重接口, app:{}, user:{}, error:{}", app,  e);
		}
		return null;
	}
}
