package com.cyhd.service.vendor;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONObject;

@Service("u51XinYongKaClickService")
public class U51XinYongKaClickService implements IVendorClickService {

	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		if(disctinct(vendor, app, user, appTask, clientInfo) == false){
			return false;
		}
		String url  = null;
		try{
			StringBuilder sb = new StringBuilder(640);
			sb.append(vendor.getClick_url());
			sb.append("?app_id=").append("6475");
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&plat=").append("1");
			sb.append("&channel=").append(getChannel(app));
			sb.append("&callback=").append(genCallbackUrl(vendor, app, clientInfo.getIdfa()));
			url = sb.toString();
			logger.info("请求51信用卡管家点击开始,request:{}",url);
			String response = HttpUtil.get(url, null);
			logger.info("请求51信用卡管家点击响应,request:{},response:{}",url,response);
			JSONObject object = JSONObject.fromObject(response);
			return object != null && object.optInt("code",1) == 0;
		}catch(Exception e){
			logger.error("请求51信用卡管家点击异常,request:{},cause by:{}",url,e);
		}
		return false;
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		return onClickApp(vendor, null, app, appTask, clientInfo);
	}
	
	

	@Override
	public String disctinct(AppVendor vendor, App app, String idfas) {
		return handleDiactinct(handleDisctinct(vendor, app, idfas), 1);
	}
	
	@Override
	public Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas) {
		return handleDiactinctNew(handleDisctinct(vendor, app, idfas), 1);
	}
	
	@Override
	public boolean disctinct(AppVendor vendor, App app, User user, AppTask appTask, ClientInfo clientInfo) {
		String response = handleDisctinct(vendor, app, clientInfo.getIdfa());
		JSONObject json = JSONObject.fromObject(response);
		return json != null && json.optInt(clientInfo.getIdfa(), 1) == 0;
	}
	
	private String handleDisctinct(AppVendor vendor, App app, String idfas){
		StringBuilder sb = new StringBuilder();
		sb.append("https://www.51zhangdan.com/service/sys/idfa_query.ashx");
		sb.append("?idfa=").append(idfas);
		String url = sb.toString();
		try {
			logger.info("请求51信用卡管家排重开始,idfa:{}",idfas);
			String response =HttpUtil.get(url, null);
			logger.info("请求51信用卡管家排重结束,idfa:{},response:{}",idfas,response);
			return response;
		} catch (Exception e) {
			logger.info("请求51信用卡管家排重异常,idfa:{},cause by:{}",idfas,e);
		}
		return null;
	}
	
	/**如果是pro那channe为5**/
	private int getChannel(App app){
		if("564765093".equals(app.getAppstore_id().trim())){
			return 5;
		}
		return 4;
	}
}
