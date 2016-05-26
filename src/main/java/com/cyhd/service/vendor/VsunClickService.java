package com.cyhd.service.vendor;

import java.net.URLEncoder;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.JsonUtils;
import com.cyhd.common.util.MD5Util;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONObject;


@Service("vsunClickService")
public class VsunClickService implements IVendorClickService {

	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		if(this.disctinct(vendor, app, user, appTask, clientInfo) == false){
			return false;
		}
		String userId = user == null?"channel_task":String.valueOf(user.getId());
		try{
			String callbackNotEncoder = genCallBackURLNotEncoder(vendor, app, clientInfo.getIdfa());
			long timestamp = System.currentTimeMillis()/1000;
			StringBuilder signSb = new StringBuilder(320);
			signSb.append("appid=").append(app.getAppstore_id());
			signSb.append("&callback=").append(callbackNotEncoder);
			signSb.append("&idfa=").append(clientInfo.getIdfa());
			signSb.append("&source=").append("miaozhuandq");
			signSb.append("&timestamp=").append(timestamp);
			signSb.append("WEIXUN");
			
			String signSrc = signSb.toString();
			String sign = MD5Util.getMD5(signSrc);
			
			StringBuilder querySB = new StringBuilder(320);
			querySB.append(vendor.getClick_url());
			querySB.append("?appid=").append(app.getAppstore_id());
			querySB.append("&callback=").append(URLEncoder.encode(callbackNotEncoder, "utf-8"));
			querySB.append("&idfa=").append(clientInfo.getIdfa());
			querySB.append("&source=").append("miaozhuandq");
			querySB.append("&timestamp=").append(timestamp);
			querySB.append("&sign=").append(sign);
			String query = querySB.toString();
			logger.info("请求微寻点击开始,userId:{},idfa:{}",userId,clientInfo.getIdfa());
			String response = HttpUtil.get(query, null);
			logger.info("请求微寻点击结束,userId:{},idfa:{},response:{}",userId,clientInfo.getIdfa(),response);
			JSONObject json = JSONObject.fromObject(JsonUtils.handleBegin(response));
			return json != null && json.optInt("code",1) == 0;
		}catch(Exception e){
			logger.info("请求微寻点击异常,userId:{},idfa:{},cause by:{}",userId,clientInfo.getIdfa(),e);
			
		}
		return false;
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		return onClickApp(vendor,null, app, appTask, clientInfo);
	}
	
	@Override
	public boolean disctinct(AppVendor vendor, App app, User user, AppTask appTask, ClientInfo clientInfo) {
		String data = doDisctinct(vendor,app, clientInfo.getIdfa());
		JSONObject json = JSONObject.fromObject(data);
		return json != null && json.optInt(clientInfo.getIdfa(),1) == 0;
	}
	@Override
	public String disctinct(AppVendor vendor, App app, String idfas) {
		return handleDiactinct(doDisctinct(vendor, app, idfas), 1);
	}
	@Override
	public Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas) {
		return handleDiactinctNew(doDisctinct(vendor, app, idfas), 1);
	}

	private String doDisctinct(AppVendor vendor, App app, String idfas){
		StringBuilder sb = new StringBuilder(320);
		sb.append("http://service.51vsun.com/ad/idfaState");
		sb.append("?idfa=").append(idfas);
		
		String query = sb.toString();
		try{
			logger.info("请求微寻排重开始,idfa:{}",idfas);
			String response = HttpUtil.get(query, null);
			logger.info("请求微寻排重结束,response:{}",response);
			return JsonUtils.handleBegin(response);
		}catch(Exception e){
			logger.info("请求微寻排重异常,idfa:{}，cause by:{}",idfas,e);
		}
		
		return null;
	}
}
