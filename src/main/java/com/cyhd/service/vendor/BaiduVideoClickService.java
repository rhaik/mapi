package com.cyhd.service.vendor;

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

import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度视频点击服务
 * Created by hy on 9/25/15.
 */
@Service("BaiduVideoClickService")
public class BaiduVideoClickService implements IVendorClickService {

    @Resource
    private UserInstalledAppService userInstalledAppService;


    @Override
    public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
        boolean result = false;
        //百度视频需要调用两次，首先调用接口检查idfa是否可以接任务
        //可以接任务之后再调用接口上报点击数据

        //回调地址
        String callbackUrl = "https://api.miaozhuandaqian.com/www/vendor/callback.3w";
        if (!GlobalConfig.isDeploy){
            callbackUrl = "https://mapi.lieqicun.cn/www/vendor/callback.3w";
        }

        //必要的参数
        String appid = app.getAppstore_id();
        String idfa = clientInfo.getIdfa();
        String source = "cyhd";
        String key = "wise_baidu_video_partner";

        Map<String, String> idfaParams = new HashMap<>();
        idfaParams.put("appid", appid);
        idfaParams.put("idfa", idfa);

        try {
            String resp = HttpUtil.get("http://app.video.baidu.com/integralwallidfa/", idfaParams);

            logger.info("BaiduVideoClickService, check idfa result:{}, appTask:{}, user:{}, idfa:{}", resp, appTask, user, idfa);

            JSONObject idfaResult = JSONObject.fromObject(resp);
            if (idfaResult != null){
                if (idfaResult.optInt(idfa, 1) == 0) {
                    Map<String, String> callbackParams = new HashMap<>();
                    callbackParams.put("appkey", vendor.getApp_key());
                    callbackParams.put("adid", String.valueOf(app.getId() * 11 + 997));
                    callbackParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
                    callbackParams.put("device_id", idfa);

                    String sign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(callbackParams) + vendor.getApp_secret());
                    callbackParams.put("expire", "false");
                    callbackParams.put("sign", sign);

                    String fullCallbackUrl = callbackUrl + "?" + RequestSignUtil.getSortedRequestString(callbackParams);

                    Map<String, String> params = new HashMap<>();
                    params.put("appid", appid);
                    params.put("mac", URLEncoder.encode("02:00:00:00:00:00", "utf-8"));
                    params.put("idfa", idfa);
                    params.put("osversion", URLEncoder.encode(clientInfo.getOs(), "utf-8"));
                    params.put("source", source);
                    params.put("callback", URLEncoder.encode(fullCallbackUrl, "utf-8"));


                    params.put("sign", MD5Util.getMD5(appid + "," + idfa + "," + source + "," + key));

                    String clickResult = HttpUtil.get(vendor.getClick_url(), params);

                    logger.info("BaiduVideoClickService, click result:{}, appTask:{}, user:{}", clickResult, appTask, user);

                    JSONObject clickJson = JSONObject.fromObject(clickResult);
                    if (clickJson != null && clickJson.has("status") && clickJson.getBoolean("status")) {
                        result = true;
                    }
                }else{
                    userInstalledAppService.insert(user.getId(), app.getId(), clientInfo.getDid(), app.getAgreement());
                }
            }

        } catch (Exception e) {
            logger.error("BaiduVideoClickService error, appTask:{}, user:{}, error:{}", appTask, user, e);
        }

        return  result;
    }


	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask,
			ClientInfo clientInfo) {
		 boolean result = false;
	        //百度视频需要调用两次，首先调用接口检查idfa是否可以接任务
	        //可以接任务之后再调用接口上报点击数据
		 
	        //回调地址
	        String callbackUrl = "https://api.miaozhuandaqian.com/www/vendor/callback.3w";
	        if (!GlobalConfig.isDeploy){
	            callbackUrl = "https://mapi.lieqicun.cn/www/vendor/callback.3w";
	        }

	        //必要的参数
	        String appid = app.getAppstore_id();
	        String idfa = clientInfo.getIdfa();
	        String source = "cyhd";
	        String key = "wise_baidu_video_partner";

	        Map<String, String> idfaParams = new HashMap<>();
	        idfaParams.put("appid", appid);
	        idfaParams.put("idfa", idfa);

	        try {
	            String resp = HttpUtil.get("http://app.video.baidu.com/integralwallidfa/", idfaParams);

	            logger.info("BaiduVideoClickService, check idfa result:{}, appTask:{}, idfa:{}", resp, appTask, idfa);

	            JSONObject idfaResult = JSONObject.fromObject(resp);
	            if (idfaResult != null){
	                if (idfaResult.optInt(idfa, 1) == 0) {
	                    Map<String, String> callbackParams = new HashMap<>();
	                    callbackParams.put("appkey", vendor.getApp_key());
	                    callbackParams.put("adid", String.valueOf(app.getId() * 11 + 997));
	                    callbackParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
	                    callbackParams.put("device_id", idfa);

	                    String sign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(callbackParams) + vendor.getApp_secret());
	                    callbackParams.put("expire", "false");
	                    callbackParams.put("sign", sign);

	                    String fullCallbackUrl = callbackUrl + "?" + RequestSignUtil.getSortedRequestString(callbackParams);

	                    Map<String, String> params = new HashMap<>();
	                    params.put("appid", appid);
	                    params.put("mac", URLEncoder.encode("02:00:00:00:00:00", "utf-8"));
	                    params.put("idfa", idfa);
	                    params.put("osversion", URLEncoder.encode(clientInfo.getOs(), "utf-8"));
	                    params.put("source", source);
	                    params.put("callback", URLEncoder.encode(fullCallbackUrl, "utf-8"));


	                    params.put("sign", MD5Util.getMD5(appid + "," + idfa + "," + source + "," + key));

	                    String clickResult = HttpUtil.get(vendor.getClick_url(), params);

	                    logger.info("BaiduVideoClickService, click result:{}, appTask:{}, idfa:{}", clickResult, appTask, clientInfo.getIdfa());

	                    JSONObject clickJson = JSONObject.fromObject(clickResult);
	                    if (clickJson != null && clickJson.has("status") && clickJson.getBoolean("status")) {
	                        result = true;
	                    }
	                }
	            }

	        } catch (Exception e) {
	            logger.error("BaiduVideoClickService error, appTask:{}, idfa:{}, error:{}", appTask, clientInfo.getIdfa(), e);
	        }

	        return  result;
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
	   String appid = app.getAppstore_id();
        String idfa = idfas;

        Map<String, String> idfaParams = new HashMap<>();
        idfaParams.put("appid", appid);
        idfaParams.put("idfa", idfa);

        try {
        	logger.info("BaiduVideoClickService request parameters:{}",idfaParams);
            String resp = HttpUtil.get("http://app.video.baidu.com/integralwallidfa/", idfaParams);
            logger.info("BaiduVideoClickService response：{}",resp);
            return resp;
        }catch(Exception e){
        	
        }
        return null;
}
}
