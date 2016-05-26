package com.cyhd.service.vendor;


import java.util.Map;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONObject;

@Service("leYouYunYinTongClickService")
public class LeYouYunYinTongClickService implements IVendorClickService {

	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		
		return clickApp(vendor, user, app, appTask, clientInfo);
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		return clickApp(vendor, null, app, appTask, clientInfo);
	}

	private boolean clickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		String  data = executeDisctinct(clientInfo.getIdfa(), app);
		JSONObject jsonObject = JSONObject.fromObject(data);
		return jsonObject != null && jsonObject.optInt(clientInfo.getIdfa(), 1) == 0;
	}
	
	@Override
	public String disctinct(AppVendor vendor, App app, String idfas) {
		return handleDiactinct(executeDisctinct(idfas, app), 1);
	}
	
	@Override
	public Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas) {
		return handleDiactinctNew(executeDisctinct(idfas, app), 1);
	}
	
	public String executeDisctinct(String idfa,App app){
		String url = "http://leyou.com.cn/mob/appIdfaClickRepeat";
		StringBuilder sBuilder = new StringBuilder(640);
		sBuilder.append(url);
		sBuilder.append("?idfa=").append(idfa);
		sBuilder.append("&timestamp=").append(GenerateDateUtil.getCurrentTime());
		sBuilder.append("&sign=").append(MD5Util.getMD5("!Leyou@SC#MD5Key"+idfa));
		sBuilder.append("&appid=").append(app.getAppstore_id());
		String requestURL = sBuilder.toString();
		try{
			logger.info("乐友孕婴童,排重请求开始:request:{}",requestURL);
			String response = HttpUtil.get(requestURL, null);
			logger.info("乐友孕婴童,排重请求结束:request:{},response:{}",requestURL,response);
			return  response;
		}catch(Exception e){
			logger.info("乐友孕婴童,排重请求异常:request:{},cause by:{}",requestURL,e);
		}
		return null;
	}
	
	
}
