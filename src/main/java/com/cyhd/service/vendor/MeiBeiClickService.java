package com.cyhd.service.vendor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.util.RequestSignUtil;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONObject;

@Service("meiBeiClickService")
public class MeiBeiClickService implements IVendorClickService {

	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		return clickApp(vendor, user, app, appTask, clientInfo);
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		return clickApp(vendor, null, app, appTask, clientInfo);
	}
	
	public boolean clickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		String userId = user == null?"分发渠道来源":user.toString();
		
		try{
			Map<String, String> parameters = new HashMap<>();
			
			parameters.put("appid", String.valueOf(app.getAppstore_id()));
			parameters.put("idfa", clientInfo.getIdfa());
			parameters.put("source", "StormASO");
			parameters.put("ip", clientInfo.getIpAddress());
			parameters.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
			parameters.put("callback", genCallbackUrl(vendor, app, clientInfo.getIdfa()));
			
			String secret = "37049e9101d64dd7b2b39ea61bd78242";
			String sign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(parameters) + secret);
			
			parameters.put("sign", sign);
			logger.info("请求美呗开始:idfa:{},user:{},sign:{}",clientInfo.getIdfa(),userId,sign);
			String response = HttpUtil.post(vendor.getClick_url(), parameters,null);
			logger.info("请求美呗结束:idfa:{},user:{},response:{}",clientInfo.getIdfa(),userId,response);
			JSONObject json = JSONObject.fromObject(response);
			json  = json.getJSONObject("data");
			if(json!=null){
				int code = json.optInt("code", 1);
				return code == 0;
			}
		}catch(Exception e){
			logger.error("请求美呗异常:idfa:{},user:{},cause by:{}",clientInfo.getIdfa(),userId,e.getMessage());
			return false;
		}
		return false;
	}

}
