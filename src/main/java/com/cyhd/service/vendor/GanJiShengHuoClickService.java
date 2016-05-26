package com.cyhd.service.vendor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONObject;

@Service("ganJiShengHuoClickService")
public class GanJiShengHuoClickService implements IVendorClickService{

	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		return clickApp(vendor, user, app, appTask, clientInfo);
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		return clickApp(vendor, null, app, appTask, clientInfo);
	}
	
	public boolean clickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		Map<String, Integer> map = this.disctinctNew(vendor, app, clientInfo.getIdfa());
		Integer install =  map.get(clientInfo.getIdfa());
		return install != null && install == 0;
	}
	
	@Override
	public String disctinct(AppVendor vendor, App app, String idfas) {
		String jsonResponse = executeDisctinct(vendor, app, idfas);
		JSONObject json = JSONObject.fromObject(jsonResponse);
		StringBuilder sb = new StringBuilder(320);
		if (json != null) {
			Iterator<String> it = json.keys();
			String key = null;
			int value ;
			sb.append('{');
			while(it.hasNext()){
				key = it.next();
				value = json.optInt(key, 1);
				sb.append("\"").append(key).append("\"").append(":");
				sb.append(value == 0?false:true).append(",");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append("}");
			return sb.toString();
		}
		return null;
	}

	@Override
	public Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas) {
		
		JSONObject json = JSONObject.fromObject(executeDisctinct(vendor, app, idfas));
		   Map<String, Integer > map = new HashMap<>();
			if (json != null) {
				Iterator<String> it = json.keys();
				String key = null;
				int value ;
				while(it.hasNext()){
					key = it.next();
					value = json.optInt(key, 1);
					map.put(key, value == 0?0:1);
				}
			}
			return map;
	}
	
	private String executeDisctinct(AppVendor vendor, App app, String idfas) {
		Map<String, String> parameters = new HashMap<String, String>();
		
		parameters.put("idfa", idfas);
		parameters.put("appid", app.getAppstore_id());
		parameters.put("source", "chuanglijudian");
		String url="http://mobds.ganji.cn/openapi/idfaquery";
		
		try {
			logger.info("请求赶集生活排重开始：request:{},parameters:{}",url,parameters);
			String response = HttpUtil.postByForm(url, parameters);
			logger.info("请求赶集生活排重开始：parameters:{},response:{}",parameters,response);
			return response;
		} catch (Exception e) {
			logger.error("请求赶集生活排重异常：parameters:{},cause:{}",parameters,e);
		}
		return null;
	}
}
