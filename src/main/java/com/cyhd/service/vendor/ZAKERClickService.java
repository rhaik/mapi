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

@Service("zAKERClickService")
public class ZAKERClickService implements IVendorClickService {

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

	private final boolean clickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo){
		//排重
		//排重结束
		if(this.disctinct(vendor, app, user, appTask, clientInfo) == false){
			return false;
		}
		//点击开始
		try {
			StringBuilder sb = new StringBuilder(320);
			sb.append(vendor.getClick_url());
			sb.append("?idfa=").append(clientInfo.getIdfa());
			sb.append("&callback=").append(genCallbackUrl(vendor, app, clientInfo.getIdfa()));
			sb.append("&ip=").append(clientInfo.getIpAddress());
			
			String clickUrl = sb.toString();
			logger.info("zAKERClickService,请求ZAKER点击接口,idfa:{},request：{}",clientInfo.getIdfa(),clickUrl);
			String response = HttpUtil.get(clickUrl, null);
			logger.info("zAKERClickService,请求ZAKER点击接口,idfa:{},response：{}",clientInfo.getIdfa(),response);
			JSONObject json = JSONObject.fromObject(response);
			if(json != null){
				if(json.optInt("code", 1) == 0){
					return true;
				}
			}
		} catch (Exception e) {
			logger.info("zAKERClickService,请求ZAKER点击接口异常,idfa:{},cause by：{}",clientInfo.getIdfa(),e);
			return false;
		}
		//点击结束
		return false;
	}

	@Override
	public boolean disctinct(AppVendor vendor, App app,User user,AppTask appTask, ClientInfo clientInfo) {
		boolean flag = false;

		StringBuilder sb = new StringBuilder(320);
		sb.append("http://iphone.myzaker.com/zaker/ad/check_idfa.php").append("?idfa=").append(clientInfo.getIdfa());
		String paichong = sb.toString();
		//使用完清空
		sb.delete(0, sb.length());
		//排重开始
		try {
			logger.info("zAKERClickService,请求ZAKER排重接口开始,request：{}",paichong);
			String response = HttpUtil.get(paichong, null);
			logger.info("zAKERClickService,请求ZAKER排重接口,response：{}",response);
			JSONObject json = JSONObject.fromObject(response);
			if(json != null && json.containsKey(clientInfo.getIdfa()) ){
				if(json.optInt(clientInfo.getIdfa(), 0) == 1){
					logger.info("zAKERClickService,请求ZAKER排重接口:idfa:{},已安装",clientInfo.getIdfa());
					if(user != null){
						try{
							userInstalledAppService.insert(user.getId(), app.getId(), clientInfo.getDid(), app.getAgreement());
						}catch(Exception e){}
					}
				}else {
					flag = true;
				}
			}
		} catch (Exception e) {
			logger.info("zAKERClickService,请求ZAKER排重接口-异常,idfa:{},cause by：{}",clientInfo.getIdfa(),e);
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
	private String executeDisctinct(AppVendor vendor, App app, String idfas){
		StringBuilder sb = new StringBuilder(320);
		sb.append("http://iphone.myzaker.com/zaker/ad/check_idfa.php").append("?idfa=").append(idfas);
		String paichong = sb.toString();
		//使用完清空
		sb.delete(0, sb.length());
		//排重开始
		try {
			logger.info("zAKERClickService,请求ZAKER排重接口开始,request：{}",paichong);
			String response = HttpUtil.get(paichong, null);
			logger.info("zAKERClickService,请求ZAKER排重接口,response：{}",response);
			return response;
		} catch (Exception e) {
			logger.info("zAKERClickService,请求ZAKER排重接口-异常,idfa:{},cause by：{}",idfas,e);
		}
		return null;
	}
}
