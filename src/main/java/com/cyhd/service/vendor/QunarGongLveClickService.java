package com.cyhd.service.vendor;


import java.net.URLDecoder;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserInstalledAppService;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONObject;

@Service("qunarGongLveClickService")
public class QunarGongLveClickService implements IVendorClickService {

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

		String response = null;
		JSONObject json = null;

		if(disctinct(vendor, app, user, appTask, clientInfo) == false ){
			return false;
		}
		// 排重结束
        String callbackUrl = "http://third.miaozhuandaqian.com/www/vendor/callback_qunar.3w";
        if (!GlobalConfig.isDeploy){
            callbackUrl = "http://www.mapi.lieqicun.cn/www/vendor/callback_qunar.3w";
        }	
		try {
			StringBuilder sb = new StringBuilder();
			// 签名
			String fullCallbackUrl = genCallbackUrlByUrl(vendor, app, clientInfo.getIdfa(),callbackUrl);
			sb.append("idfa=").append(clientInfo.getIdfa());
			sb.append("&callback=").append(URLDecoder.decode(fullCallbackUrl, "utf-8"));
			sb.append("&source=").append("fengbao");

			String qunar_sign = MD5Util.getMD5(sb.toString());
			sb.delete(0, sb.length());
			sb.append(vendor.getClick_url());
			sb.append("?idfa=").append(clientInfo.getIdfa());
			sb.append("&callbackurl=").append(fullCallbackUrl);
			sb.append("&sign=").append(qunar_sign);
			String url = sb.toString();

			logger.info("去哪儿攻略,点击接口开始:idfa:{},user:{},reuquest:{}", clientInfo.getIdfa(), user, url);
			response = HttpUtil.get(sb.toString(), null);
			logger.info("去哪儿攻略,点击接口结束:response:{},idfa:{},user:{}", response, clientInfo.getIdfa(), user);
			json = JSONObject.fromObject(response);
			if (json != null) {
				return json.optBoolean("success", false);
			}
		} catch (Exception e) {
			logger.error("qunarGongLveClickService error,点击接口, appTask:{}, user:{}, error:{}", appTask, user, e);
			return false;
		}
		return false;
	}
	@Override
	public boolean disctinct(AppVendor vendor, App app, User user, AppTask appTask, ClientInfo clientInfo) {
		boolean flag = false;
		String query = "http://api.travel.qunar.com/api/idfa/query";

		StringBuilder sb = new StringBuilder();
		sb.append(query);
		sb.append("?idfa=").append(clientInfo.getIdfa());
		String url = sb.toString();
		try {
			logger.info("去哪儿攻略开始,排重接口:idfa:{},user:{},request:{}", clientInfo.getIdfa(), user, url);
			String response = HttpUtil.get(url, null);
			logger.info("去哪儿攻略结束,排重接口:response:{},idfa:{},user:{}", response, clientInfo.getIdfa(), user);
			JSONObject json = JSONObject.fromObject(response);
			if (json != null) {
				if (json.optInt(clientInfo.getIdfa()) == 1) {
					// 安装过
					try {
						if(user != null){
							userInstalledAppService.insert(user.getId(), app.getId(), clientInfo.getDid(),app.getAgreement());
						}
					} catch (Exception e) {
					}
					logger.info("去哪儿攻略排重,idfa:{},userId:{},已经安装过", clientInfo.getIdfa(), (user == null? "无用户信息" : user.getId()));
				}else {
					flag = true;
				}
			}
		} catch (Exception e) {
			logger.error("qunarGongLveClickService error,排重接口, appTask:{}, user:{}, error:{}", appTask, user, e);
		}
		return flag;
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
		String query = "http://api.travel.qunar.com/api/idfa/query";
		String user = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		sb.append("?idfa=").append(idfas);
		String url = sb.toString();
		try {
			logger.info("去哪儿攻略开始,排重接口:idfa:{},user:{},request:{}",idfas, user, url);
			String response = HttpUtil.get(url, null);
			logger.info("去哪儿攻略结束,排重接口:response:{},idfa:{},user:{}", response, idfas, user);
			//1 存在，0是不存在
			return response;
		} catch (Exception e) {
			logger.error("qunarGongLveClickService error,排重接口,, user:{}, error:{}",  user, e);
		}
		return null;
	}
}

