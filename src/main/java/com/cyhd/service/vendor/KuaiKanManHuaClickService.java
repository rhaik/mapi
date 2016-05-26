package com.cyhd.service.vendor;

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

@Service("kuaiKanManHuaClickService")
public class KuaiKanManHuaClickService implements IVendorClickService{

	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		
		return clickApp(vendor, user, app, appTask, clientInfo);
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		
		return clickApp(vendor, null, app, appTask, clientInfo);
	}
	public boolean clickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		if(this.disctinct(vendor, app, user, appTask, clientInfo) == false){
			return false;
		}
		
		Map<String, String> map = new HashMap<>();
		map.put("appid", app.getAppstore_id());
		map.put("idfa", clientInfo.getIdfa());
		map.put("source", "1");
		
		String sign = MD5Util.getMD5( RequestSignUtil.getSortedRequestString(map, "&")+"EjnSYBNg1dutlbef79");
		map.put("sign", sign);
		
		try {
			logger.info("请求快看漫画点击开始,request:{}",map);
			String response = HttpUtil.postByForm(vendor.getClick_url(), map);
			logger.info("请求快看漫画点击响应,request:{},response:{}",map,response);
			JSONObject object = JSONObject.fromObject(response);
			return object != null && object.optInt("code",0) == 200;
		} catch (Exception e) {
			logger.error("请求快看漫画点击响应异常,request:{},cause by:{}",map,e);
		}
		
		return false;
	}
	
	
	@Override
	public String disctinct(AppVendor vendor, App app, String idfas) {
		return handleDiactinct(doDisctinct(vendor, app, idfas), 1);
	}
	@Override
	public Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas) {
		return handleDiactinctNew(doDisctinct(vendor, app, idfas), 1);
	}
	
	@Override
	public boolean disctinct(AppVendor vendor, App app, User user, AppTask appTask, ClientInfo clientInfo) {
		String response = doDisctinct(vendor, app, clientInfo.getIdfa());
		JSONObject json = JSONObject.fromObject(response);
		return json != null && json.optInt(clientInfo.getIdfa(), 1) == 0;
	}
	
	public String doDisctinct(AppVendor vendor, App app, String idfas) {
		String query = "http://api.kuaikanmanhua.com/v1/fengbao/idfa";
		Map<String, String> map = new HashMap<String, String>();
		map.put("idfa", idfas);
		map.put("appid", app.getAppstore_id());
		try {
			logger.info("请求快看漫画排重开始,idfa:{}",idfas);
			String response =HttpUtil.postByForm(query, map);
			logger.info("请求快看漫画排重结束,idfa:{},response:{}",idfas,response);
			return response;
		} catch (Exception e) {
			logger.error("请求快看漫画排重异常,idfa:{},cause by:{}",idfas,e);
		}
		return null;
	}
	
}
