package com.cyhd.service.vendor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserInstalledAppService;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONObject;

@Service("guaZiClickService")
public class GuaZiClickService implements IVendorClickService {

	private List<String> osList = null;
	
	@PostConstruct
	private void initOsVersion(){
		osList = Arrays.asList("8.1",
				"8.4","8.0.2","8.1.2","7.1.1","8.1.1","9.2","9.2.1","9.1","9.0.2");
		
	}
	
	@Resource
	private UserInstalledAppService userInstalledAppService;
	
	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		return clickApp(vendor, user, app, appTask, clientInfo);
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		
		return clickApp(vendor, null, app, appTask, clientInfo);
	}

	public boolean clickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		
		String idfaQUery = "http://wuxian.guazi.com/exguazi/tui/queryidfa?friend=fengbao";
		Map<String, String> parameters = new HashMap<>();
		parameters.put("appid", app.getAppstore_id());
		parameters.put("idfa", clientInfo.getIdfa());
		JSONObject json = null;
		String response = null;
		try {
			logger.info("瓜子排重-开始,request:{}",parameters);
			response = HttpUtil.postByForm(idfaQUery, parameters);
			logger.info("瓜子排重-结束,response:{}",response);
			json = JSONObject.fromObject(response);
			if(json != null){
				int value = json.optInt(clientInfo.getIdfa(), 1);
				if(value == 1){
					if(user != null){
						try {
							userInstalledAppService.insert(user.getId(), app.getId(), clientInfo.getDid(), app.getAgreement());
						} catch (Exception e) {}
					}
					return false;
				}
			}
		
			parameters.clear();
			parameters.put("appid", app.getAppstore_id());
			parameters.put("idfa", clientInfo.getIdfa());
			parameters.put("ip", clientInfo.getIpAddress());
			parameters.put("Mac", null);
			parameters.put("callback", genCallbackUrl(vendor, app, clientInfo.getIdfa()));
			parameters.put("OS", getOsVersion(clientInfo));
			logger.info("瓜子排重-点击开始,idfa:{},request:{}",clientInfo.getIdfa(),parameters);
			response = HttpUtil.postByForm(vendor.getClick_url(), parameters);
			logger.info("瓜子排重-点击结束,idfa:{},response:{}",clientInfo.getIdfa(),response);
			json = JSONObject.fromObject(response);
			if(json != null){
				return json.optInt("code", 1) == 0;
			}
			
		} catch (Exception e) {
			logger.error("瓜子二手车,点击,cause by:{}",e);
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
	
	private String executeDisctinct(AppVendor vendor, App app, String idfas) {
		String idfaQUery = "http://wuxian.guazi.com/exguazi/tui/queryidfa?friend=fengbao";
		Map<String, String> parameters = new HashMap<>();
		parameters.put("appid", app.getAppstore_id());
		parameters.put("idfa", idfas);
		String response = null;
		try {
			logger.info("瓜子-分发排重开始,request:{}",parameters);
			response = HttpUtil.postByForm(idfaQUery, parameters);
			logger.info("瓜子-分发排重结束,response:{}",response);
			return response;
		}catch(Exception e){
			logger.error("瓜子-分发排重出现异常:{}",e);
		}
		return null;
	}
	
	private String getOsVersion(ClientInfo clientInfo){
		try{
			return clientInfo.getOSVersion();
		}catch(Exception e){
			int index = ThreadLocalRandom.current().nextInt(osList.size());
			return osList.get(index);
		}
	}
}
