package com.cyhd.service.vendor;

import java.net.URLDecoder;
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

@Service("xiuSeXiuChangClickService")
public class XiuSeXiuChangClickService implements IVendorClickService {

	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		return doClickApp(vendor, user, app, appTask, clientInfo);
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		return doClickApp(vendor, null, app, appTask, clientInfo);
	}

	private boolean doClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		if(this.disctinct(vendor, app, user, appTask, clientInfo) == false){
			return false;
		}
		//发起点击请求
		String url = null;
		Map<String, String> signParameters = new HashMap<String, String>(4);
		long timestamp = System.currentTimeMillis()/1000;
		
		try {
			String callback = this.genCallbackUrl(vendor, app, clientInfo.getIdfa());
			signParameters.put("appid","107");
			signParameters.put("idfa",clientInfo.getIdfa());
			signParameters.put("callback",URLDecoder.decode(callback, "utf-8"));
			signParameters.put("source","7");
			signParameters.put("timestamp", timestamp+"");
			signParameters.put("ip", clientInfo.getIpAddress());
			String signSrc= RequestSignUtil.getSortedRequestString(signParameters, "&")+"abcdefg";
			if(logger.isDebugEnabled()){
				logger.debug("sign src:{}",signSrc);
			}
			String sign = MD5Util.getMD5(signSrc);
			signParameters.put("sign", sign);
			signParameters.remove("callback");
			signParameters.put("callback", callback);
			logger.info("请求秀色秀场的点击开始，user:{},request:{}",user,signParameters);
			String response = HttpUtil.get(vendor.getClick_url(), signParameters);
			logger.info("请求秀色秀场的点击结束，response:{}",response);
			
			JSONObject json = JSONObject.fromObject(response);
			return json != null && json.optInt("code",1) == 0;
		} catch (Exception e) {
			logger.error("请求秀色秀场的点击出错：url:{},cause by:{}",url,e);
			return false;
		}
	}

	
	@Override
	public boolean disctinct(AppVendor vendor, App app, User user, AppTask appTask, ClientInfo clientInfo) {
		String data = disctinct(vendor, clientInfo.getIdfa());
		JSONObject json = JSONObject.fromObject(data);
		return json != null && json.optInt(clientInfo.getIdfa(),1) == 0;
		
	}
	
	@Override
	public Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas) {
		String data = disctinct(vendor, idfas);
		return handleDiactinctNew(data, 1);
	}
	
	@Override
	public String disctinct(AppVendor vendor, App app, String idfas) {
		String data = disctinct(vendor, idfas);
		return handleDiactinct(data, 1);
	}
	
	private String disctinct(AppVendor vendor, String idfas){
		//http://113.31.25.4:8087/v2/fengbaoasodata/dedup?appid=107&idfa=1d2f16dc-a384-45bf-a01a-b5ae510288ec,1d2f16dc-a384-45bf-a01a-b5ae510288eb&timestamp=1456124993
		
		String query = "http://113.31.25.4:8087/v2/fengbaoasodata/dedup"; 
		
		long timestamp = System.currentTimeMillis() /1000;
		StringBuilder sb = new StringBuilder(360);
		sb.append(query);
		sb.append("?appid=").append("107");
		sb.append("&idfa=").append(idfas);
		sb.append("&timestamp=").append(timestamp);
		
		String url = sb.toString();
		try {
			logger.info("请求秀色秀场排重开始:request:{}",url);
			String response = HttpUtil.get(url, null);
			logger.info("请求秀色秀场排重结束,response:{}",response);
			return response;
		} catch (Exception e) {
			logger.info("请求秀色秀场排重异常,request:{},cause by:{}",url,e);
		}
		return "";
	}
}
