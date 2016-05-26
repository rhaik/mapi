package com.cyhd.service.vendor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

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

@Service("todayClickService")
public class TodayClickService implements IVendorClickService{

	@Resource
	private UserInstalledAppService userInstalledAppService;
	
	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app,
			AppTask appTask, ClientInfo clientInfo) {
		Map<String, String> parameters = new HashMap<String, String>();
		/**
		 * 头条ad_id＝23 app_id＝13 app=104/123 appStoreId 他们有好几个app我去
		段子，ad_id=24  app-id＝7  app=102/125 appStoreId 他们有好几个app我去
		 */
		String ad_id="23" ;
		String app_id="13";
		int id = 125;
		if(GlobalConfig.isDeploy){
			id = 102;
		}
		if(app.getId()== id){
			ad_id="24" ;
			app_id="7";
		}
		String appStore_id = app.getAppstore_id();
		String idfa=clientInfo.getIdfa();
		
		String callerid="c5d8f024-8369-11e5-ad79-0cc47a0f8a88";
		String shared_key= "d32f1516-8369-11e5-ad79-0cc47a0f8a88";
		long timestamp  = System.currentTimeMillis()/1000;
		StringBuilder sb = new StringBuilder();
		sb.append(appStore_id).append(idfa).append(timestamp).append(shared_key);
		
		//idfa排重
		parameters.put("appid", appStore_id);
		parameters.put("idfa",idfa);
		parameters.put("callerid", callerid);
		parameters.put("timestamp", String.valueOf(timestamp));
		//add sign
		parameters.put("sign", MD5Util.getMD5(sb.toString()));
		String resp = null;
		try {
			logger.info("调用今日头条的排重接口开始,userId:{}idfa:{},appname",user.getId(),clientInfo.getIdfa(),app.getName());
			resp = HttpUtil.postByForm("https://open.snssdk.com/idfa/check/", parameters);
			logger.info("调用今日头条的排重接口结束,response：{},userId:{},idfa:{},appname",resp,user.getId(),clientInfo.getIdfa(),app.getName());
			JSONObject json = JSONObject.fromObject(resp);
			if(json.containsKey(clientInfo.getIdfa())){
					/**响应格式
					 * {   "D4A750EA-A39F-442F-ABB0-3285F6C4A6EB": 1,  
					 *   "10F8F760-70B9-4BFF-A832-F19D158FC008": 0 } 
					 */
					//安装过啦
					if(json.optInt(clientInfo.getIdfa()) == 1){
						try{
							logger.info("调用今日头条接口,idfa:{}:已安装",clientInfo.getIdfa());
							//有操作的地方就有异常
							userInstalledAppService.insert(user.getId(), app.getId(), clientInfo.getDid(), app.getAgreement());
						}catch(Exception ee){}
						return false;
					}
			}else{
				return false;
			}
		} catch (Exception e1) {
			logger.info("调用今日头条的排重接口,userId:{}idfa:{},appname;cause by:{}",user.getId(),clientInfo.getIdfa(),app.getName(),e1);
			return false;
		}
		//排重结束
		
		//清空
		parameters.clear();
		
		parameters.put("idfa", clientInfo.getIdfa());
		parameters.put("app_id",app_id);
		parameters.put("ad_id", ad_id);
		parameters.put("ip", clientInfo.getIpAddress());
		
//		//回调地址 
//        String callbackUrl = "http://third.miaozhuandaqian.com/www/vendor/callback_today.3w";
//        if (!GlobalConfig.isDeploy){
//            callbackUrl = "https://www.mapi.lieqicun.cn/www/vendor/callback_today.3w";
//        }
		String callbackUrl=vendor.getClick_url();
        try {
			 resp = HttpUtil.get(callbackUrl, parameters);
			logger.info("todayClickService:response:{};idfa:{},appName:{},today_appid:{}",resp,clientInfo.getIdfa(),app.getName(),app_id);
			if(resp != null ){
				JSONObject json = JSONObject.fromObject(resp);
				return json != null && json.containsKey("success") && json.getBoolean("success");
			}
		} catch (Exception e) {
			logger.error("todayClickService失败,idfa:{},appName:{},today_appid:{},cause by:{}",clientInfo.getIdfa(),app.getName(),app_id,e);
			return false;
		}
		return false;
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask,
			ClientInfo clientInfo) {

		Map<String, String> parameters = new HashMap<String, String>();
		/**
		 * 头条ad_id＝23 app_id＝13 app=104/123 appStoreId 他们有好几个app我去
		 段子，ad_id=24  app-id＝7  app=102/125 appStoreId 他们有好几个app我去
		 */
		String ad_id="23" ;
		String app_id="13";
		int id = 125;
		if(GlobalConfig.isDeploy){
			id = 102;
		}
		if(app.getId()== id){
			ad_id="24" ;
			app_id="7";
		}
		String appStore_id = app.getAppstore_id();
		String idfa=clientInfo.getIdfa();

		String callerid="c5d8f024-8369-11e5-ad79-0cc47a0f8a88";
		String shared_key= "d32f1516-8369-11e5-ad79-0cc47a0f8a88";
		long timestamp  = System.currentTimeMillis()/1000;
		StringBuilder sb = new StringBuilder();
		sb.append(appStore_id).append(idfa).append(timestamp).append(shared_key);

		//idfa排重
		parameters.put("appid", appStore_id);
		parameters.put("idfa",idfa);
		parameters.put("callerid", callerid);
		parameters.put("timestamp", String.valueOf(timestamp));
		//add sign
		parameters.put("sign", MD5Util.getMD5(sb.toString()));
		String resp = null;
		try {
			logger.info("调用今日头条的排重接口开始,idfa:{},appname",clientInfo.getIdfa(),app.getName());
			resp = HttpUtil.postByForm("https://open.snssdk.com/idfa/check/", parameters);
			logger.info("调用今日头条的排重接口结束,response：{},idfa:{},appname",resp,clientInfo.getIdfa(),app.getName());
			JSONObject json = JSONObject.fromObject(resp);
			if(json.containsKey(clientInfo.getIdfa())){
				/**响应格式
				 * {   "D4A750EA-A39F-442F-ABB0-3285F6C4A6EB": 1,
				 *   "10F8F760-70B9-4BFF-A832-F19D158FC008": 0 }
				 */
				//安装过啦
				if(json.optInt(clientInfo.getIdfa()) == 1){
					return false;
				}
			}else{
				return false;
			}
		} catch (Exception e1) {
			logger.info("调用今日头条的排重接口,idfa:{},appname;cause by:{}",clientInfo.getIdfa(),app.getName(),e1);
			return false;
		}
		//排重结束

		//清空
		parameters.clear();

		parameters.put("idfa", clientInfo.getIdfa());
		parameters.put("app_id",app_id);
		parameters.put("ad_id", ad_id);
		parameters.put("ip", clientInfo.getIpAddress());

//        }
		String callbackUrl= vendor.getClick_url();
        try {
			resp = HttpUtil.get(callbackUrl, parameters);
			logger.info("今日头条请求响应:{}",resp);
			if(resp != null ){
				JSONObject json = JSONObject.fromObject(resp);
				return json != null && json.containsKey("success") && json.getBoolean("success");
			}
		} catch (Exception e) {
			logger.error("今日头条请求失败 ，cause by:{}",e);
			return false;
		}
		return false;
	}
	@Override
	public String disctinct(AppVendor vendor, App app, String idfas) {
		return handleDiactinct(executeDisctinct(vendor, app, idfas), 1);
	}
	
	@Override
	public Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas) {
		return  handleDiactinctNew(executeDisctinct(vendor, app, idfas), 1);
	}
	
	private String executeDisctinct(AppVendor vendor, App app, String idfas) {
		String appStore_id = app.getAppstore_id();

		String callerid="c5d8f024-8369-11e5-ad79-0cc47a0f8a88";
		String shared_key= "d32f1516-8369-11e5-ad79-0cc47a0f8a88";
		long timestamp  = System.currentTimeMillis()/1000;
		StringBuilder sb = new StringBuilder();
		sb.append(appStore_id).append(idfas).append(timestamp).append(shared_key);

		//idfa排重
		HashMap<String, String >parameters = new HashMap<>();
		parameters.put("appid", appStore_id);
		parameters.put("idfa",idfas);
		parameters.put("callerid", callerid);
		parameters.put("timestamp", String.valueOf(timestamp));
		//add sign
		parameters.put("sign", MD5Util.getMD5(sb.toString()));
		String resp = null;
		try {
			logger.info("调用今日头条的排重接口开始,idfa:{},appname",idfas,app.getName());
			resp = HttpUtil.postByForm("https://open.snssdk.com/idfa/check/", parameters);
			logger.info("调用今日头条的排重接口结束,response：{},idfa:{},appname",resp,app.getName());
			return resp;
		} catch (Exception e1) {
			logger.info("调用今日头条的排重接口,idfa:{},appname;cause by:{}",app.getName(),e1);
		}
		return null;
	}
}
