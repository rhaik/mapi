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

@Service(value="zhaiDouClickService")
public class ZhaiDouClickService implements IVendorClickService {

	@Resource
	private UserInstalledAppService userInstalledAppService;
	
	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		if(! disctinct(vendor, app, user, appTask, clientInfo)){
			return false;
		}
		//http://stg.zhaidou.com/itools?appid=2212322&source=aso&idfa=123×tamp=123&callback=123
		try{
			StringBuilder sb = new StringBuilder();
			sb.append(vendor.getClick_url());
			sb.append("?appid=").append(app.getAppstore_id());
			sb.append("&source=aso");
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&timestamp=").append(System.currentTimeMillis());
			sb.append("&callback=").append(genCallbackUrl(vendor, app, clientInfo.getIdfa()));
			String query = sb.toString();
			logger.info("请求宅豆点击开始:request:{}",query);
			String response = HttpUtil.get(query, null);
			logger.info("请求宅豆点击结束:request:{},response:{}",query,response);
			JSONObject json = JSONObject.fromObject(response);
			return json.optInt("code", 2) == 0;
		}catch(Exception e){
			logger.error("请求宅豆点击接口异常",e);
		}
		return false;
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		return onClickApp(vendor, null, app, appTask, clientInfo);
	}
	
	@Override
	public boolean disctinct(AppVendor vendor, App app, User user, AppTask appTask, ClientInfo clientInfo) {
		String data = doDistinct(vendor, app, clientInfo.getIdfa());
		JSONObject json = JSONObject.fromObject(data);
		if(json != null ){
			int success = json.optInt(clientInfo.getIdfa(), 2);
			if(success == 1){
				try {
					userInstalledAppService.addPreFilteredIDFA(app.getId(), clientInfo.getIdfa());
				} catch (Exception e) {}
			}
			return success == 0;
		}
		return false;
	}

	@Override
	public String disctinct(AppVendor vendor, App app, String idfas) {
		return handleDiactinct(doDistinct(vendor, app, idfas), 1);
	}
	
	@Override
	public Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas) {
		return handleDiactinctNew(doDistinct(vendor, app, idfas), 1);
	}
	private String doDistinct(AppVendor vendor, App app, String idfas){
		//http://stg.zhaidou.com/itools/distinct?
		StringBuilder sb = new StringBuilder(160);
		sb.append("http://www.zhaidou.com/itools/distinct");
		sb.append("?idfa=").append(idfas);
		
		String query = sb.toString();
		try {
			logger.info("请求宅豆排重接口开始,idfa:{}",idfas);
			String response = HttpUtil.get(query, null);
			logger.info("请求宅豆排重接口结束,idfa:{},response:{}",idfas,response);
			return response;
		} catch (Exception e) {
			logger.info("请求宅豆排重接口异常,idfa:{},casue:",idfas,e);
		}
		return "{}";
	}
}
