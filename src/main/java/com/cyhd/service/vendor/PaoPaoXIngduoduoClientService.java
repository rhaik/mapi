package com.cyhd.service.vendor;


import java.util.Map;

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

@Service("paoPaoXIngduoduoClientService")
public class PaoPaoXIngduoduoClientService implements IVendorClickService {

	@Resource
	private UserInstalledAppService userInstalledAppService;
	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		return ClickApp(vendor, user, app, appTask, clientInfo);
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		return ClickApp(vendor, null, app, appTask, clientInfo);
	}

	private boolean ClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		/***
		 * {difa}:0|1
0：已存在
1：不存在
例如：
{"5B2C5E06-62E9-4C9E-87F8-E5E130F4DEF4":"0"}
		 */
		StringBuilder sb = new StringBuilder(320);
		//排重开始
		String url = "http://bbs.xingduoduo.com:8080/bbs3/app/new/appUserExistBatch1.jhtml";
		String apk = "xdd_chuangli";
		sb.append(url);
		sb.append("?idfa=").append(clientInfo.getIdfa());
		sb.append("&apk=").append(apk);
		url = sb.toString();
		String response = null;
		JSONObject json = null;
		try {
			logger.info("paoPaoXIngduoduoClientService,请求性多多排重开始:user:{},idfa:{},request:{}",user,clientInfo.getIdfa(),url);
			response = HttpUtil.get(url, null);
			logger.info("paoPaoXIngduoduoClientService,请求性多多排重结束:user:{},idfa:{},response:{}",user,clientInfo.getIdfa(),response);
		    json = JSONObject.fromObject(response);
			if(json != null){
				if(json.containsKey(clientInfo.getIdfa()) == false){
					return false;
				}
				//他们家是0 是已存在
				if(json.getInt(clientInfo.getIdfa()) == 0){
					try {
						if(user != null){
							userInstalledAppService.insert(user.getId(), app.getId(), user.getDid(), app.getAgreement());
						}
					} catch (Exception e) {}
					return false;
				}
			}
		} catch (Exception e) {
			logger.info("paoPaoXIngduoduoClientService,请求性多多排重结束:user:{},idfa:{},cause by:{}",user,clientInfo.getIdfa(),e.getMessage());
			return false;
		}
		//排重结束
		//点击开始
		try {
			sb.delete(0, sb.length());
			sb.append(vendor.getClick_url());
			sb.append("?apk=").append(apk);
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&callback=").append(genCallbackUrl(vendor, app, clientInfo.getIdfa()));
			url = sb.toString();
			logger.info("paoPaoXIngduoduoClientService,请求性多多点击开始:user:{},idfa:{},request:{}",user.getId(),clientInfo.getIdfa(),url);
			response = HttpUtil.get(url, null);
			logger.info("paoPaoXIngduoduoClientService,请求性多多点击结束:user:{},idfa:{},request:{}",user.getId(),clientInfo.getIdfa(),response);
			json = JSONObject.fromObject(response);
			if(json != null){
				return json.optBoolean("success", false);
			}
		} catch (Exception e) {
			logger.info("paoPaoXIngduoduoClientService,请求性多多点击异常:user:{},idfa:{},cause by:{}",user.getId(),clientInfo.getIdfa(),e.getMessage());
		}
		//点击结束
		return false;
	}
	@Override
	public String disctinct(AppVendor vendor, App app, String idfas) {
		return handleDiactinct(executeDisctinct(vendor, app, idfas), 0);
	}
	
	@Override
	public Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas) {
		return handleDiactinctNew(executeDisctinct(vendor, app, idfas), 0);
	}
	
	private String executeDisctinct(AppVendor vendor, App app, String idfas) {
		StringBuilder sb = new StringBuilder(320);
		//排重开始
		String url = "http://bbs.xingduoduo.com:8080/bbs3/app/new/appUserExistBatch1.jhtml";
		String apk = "xdd_chuangli";
		sb.append(url);
		sb.append("?idfa=").append(idfas);
		sb.append("&apk=").append(apk);
		url = sb.toString();
		String response = null;
		try {
			logger.info("paoPaoXIngduoduoClientService,请求性多多排重开始:user:{},idfa:{},request:{}",url);
			response = HttpUtil.get(url, null);
			logger.info("paoPaoXIngduoduoClientService,请求性多多排重结束:user:{},idfa:{},response:{}",response);
			return response;
		} catch (Exception e) {
			logger.info("paoPaoXIngduoduoClientService,请求性多多排重结束:user:{},idfa:{},cause by:{}",idfas,e.getMessage());
		}
		//排重结束
		return null;
	}
}
