package com.cyhd.service.vendor;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RequestSignUtil;
import com.cyhd.web.common.ClientInfo;

@Service("yiMaClickService")
public class YiMaClickService implements IVendorClickService{

	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app,
			AppTask appTask, ClientInfo clientInfo) {
		//http://221.122.127.169:17202/idfa/action.htm?appid=892332027&idfa=849C2983-8C78-476D-A940-A7D00E762CE0&chn=mzdq&callback=http%3A%2F%2Fios.api.i4.cn%2Fappactivatecb.xhtml%3Faisicid%3D200102%26aisi%3D249702%26appid%3D892332027%26rt%3D2%26mac%3D%26idfa%3D849C2983-8C78-476D-A940-A7D00E762CE0

		return clickApp(vendor, user, app, appTask, clientInfo);
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask,
			ClientInfo clientInfo) {
		return clickApp(vendor, null, app, appTask, clientInfo);
	}

	public boolean clickApp(AppVendor vendor, User user, App app,
		AppTask appTask, ClientInfo clientInfo) {
	
		Map<String, String> callbackParams = new HashMap<>();
	    callbackParams.put("appkey", vendor.getApp_key());
	    callbackParams.put("adid", String.valueOf(app.getId() * 11 + 997));
	    callbackParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
	    callbackParams.put("device_id", clientInfo.getIdfa());
	
	     String sign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(callbackParams) + vendor.getApp_secret());
	     callbackParams.put("expire", "false");
	     callbackParams.put("sign", sign);

       //回调地址
         String callbackUrl = "http://third.miaozhuandaqian.com/www/vendor/callback.3w";
         if (!GlobalConfig.isDeploy){
             callbackUrl = "https://www.mapi.lieqicun.cn/www/vendor/callback.3w";
         }
         
         String fullCallbackUrl = callbackUrl + "?" + RequestSignUtil.getSortedRequestString(callbackParams);
         
		StringBuilder sb = new StringBuilder();
		sb.append(vendor.getClick_url());
		sb.append("?appid=").append(app.getAppstore_id());
		sb.append("&idfa=").append(clientInfo.getIdfa());
		sb.append("&chn=").append("mzdq");
		
		try{
			sb.append("&callback=").append(URLEncoder.encode(fullCallbackUrl, "utf-8"));
			String request = sb.toString();
			logger.info("请求亿玛-YiMaClickService,request:{}",request);
			String resp = HttpUtil.get(request, null);
			logger.info("亿玛响应-YiMaClickService:{}",resp);
			//{"message":"接收信息成功!","success":"true"}
			JSONObject json = JSONObject.fromObject(resp);
			return json != null && json.optBoolean("success", false);
		}catch(Exception e){
			 logger.error("亿玛-YiMaClickService error, appTask:{}, user:{}, error:{}", appTask, user, e);
			return false;
		}
}
	
}
