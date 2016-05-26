package com.cyhd.service.vendor;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

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

@Service("qiXinBaoClickService")
public class QiXInBaoClickService implements IVendorClickService {

	@Resource
	private UserInstalledAppService userInstalledAppService;
	
	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		if(!disctinct(vendor, app, user, appTask, clientInfo)){
			return false;
		}
		try{
			String callbackURL = genCallBackURLNotEncoder(vendor, app, clientInfo.getIdfa());
			String salt = "26376CC3F40545C9853CD8C80F3EC9CD";
			
			Map<String, String> signBeforeParames = new HashMap<String, String>();
			signBeforeParames.put("appid", app.getAppstore_id());
			signBeforeParames.put("idfa", clientInfo.getIdfa());
			signBeforeParames.put("version", app.getVersion());
			signBeforeParames.put("source", "北京创力聚点");
			signBeforeParames.put("callbackaddress", callbackURL);
			String beforeSign = RequestSignUtil.getSortedRequestString(signBeforeParames)+salt;
			String sign = MD5Util.getMD5(beforeSign);
			
			signBeforeParames.put("callbackaddress", URLEncoder.encode(callbackURL, "utf-8"));
			signBeforeParames.put("source", "%E5%8C%97%E4%BA%AC%E5%88%9B%E5%8A%9B%E8%81%9A%E7%82%B9");
			signBeforeParames.put("sign", sign);
			
			logger.info("请求启信宝点击接口开始,idfa:{},request:{}",clientInfo.getIdfa(),signBeforeParames);
			String response = HttpUtil.get(vendor.getClick_url(),signBeforeParames,10000);
			logger.info("请求启信宝点击接口结束,idfa:{},user:{},response:{}",clientInfo.getIdfa(),user.getId(),response);
			JSONObject json = new JSONObject(response);
			return json != null && json.optInt("status", 100) == 200;
		}catch(Exception e){
			logger.info("请求启信宝点击接口异常,idfa:{},user:{},cause：",clientInfo.getIdfa(),user.getId(),e);
		}
		return false;
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		return onClickApp(vendor, null, app, appTask, clientInfo);
	}
	

	@Override
	public boolean disctinct(AppVendor vendor, App app, User user, AppTask appTask, ClientInfo clientInfo) {
		JSONObject json = new JSONObject(doDisctinct(vendor, app, clientInfo.getIdfa()));
		int result = 2;
		if(json != null){
			result = json.optInt(clientInfo.getIdfa(), 2);
			if(result == 1){
				try {
					userInstalledAppService.addPreFilteredIDFA(app.getId(), clientInfo.getIdfa());
				} catch (Exception e) {}
			}
			
		}
		return result == 0;
	}
	
	
	@Override
	public String disctinct(AppVendor vendor, App app, String idfas) {
		return handleDiactinct(doDisctinct(vendor, app, idfas), 1);
	}
	
	@Override
	public Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas) {
		return handleDiactinctNew(doDisctinct(vendor, app, idfas), 1);
	}
	
	private String doDisctinct(AppVendor vendor,App app,String idfa){
		String url = "http://api.qixin007.com/APIService/idfa/isIdfaExsit";
		StringBuilder sb = new StringBuilder(120);
		sb.append(url);
		sb.append("?idfa=").append(idfa);
		sb.append("&version=").append(app.getVersion());
		sb.append("&appid=").append(app.getAppstore_id());
		try{
			String request = sb.toString();
			logger.info("请求启信宝排重接口开始:idfa:{},app:{}",idfa,app.getAppstore_id());
			String  response = HttpUtil.get(request, null);
			logger.info("请求启信宝排重接口结束:idfa:{},app:{},reposne:{}",idfa,app.getAppstore_id(),response);
			JSONObject json = new JSONObject(response);
			return json.getJSONObject("data").toString();
		}catch(Exception e){
			logger.info("请求启信宝排重接口结束:idfa:{},app:{}",idfa,app.getAppstore_id());
		}
		return null;
	}
	
}
