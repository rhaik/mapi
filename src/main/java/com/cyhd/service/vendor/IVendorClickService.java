package com.cyhd.service.vendor;

import com.cyhd.common.util.MD5Util;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RequestSignUtil;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用厂商点击的接口<br/>
 * 所有实现的类，继承此接口，并且需要为服务进行命名，AppVendor中的service_name即为实现此接口的点击服务
 * Created by hy on 9/15/15.
 */
public interface IVendorClickService {

    //日志工具
    Logger logger = LoggerFactory.getLogger("vendor");

    /**
     * 应用厂商点击数据的处理
     * @param user
     * @param app
     * @param appTask
     * @param clientInfo
     * @return
     */
    boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo);
    
    /**
     * 应用厂商点击数据的处理
     * @param user
     * @param app
     * @param appTask
     * @param clientInfo
     * @return
     */
    boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo);
    /***
     * 生成默认的回调地址 <br/>
     * <code>
     *  String callbackUrl = "http://third.miaozhuandaqian.com/www/vendor/callback.3w";<br/>
        if (!GlobalConfig.isDeploy){<br/>
            callbackUrl = "http://www.mapi.lieqicun.cn/www/vendor/callback.3w";<br/>
        }	<br/>
     * </code>
     * @param vendor
     * @param app
     * @param idfa
     * @return
     * @throws UnsupportedEncodingException 
     */
    default String genCallbackUrl(AppVendor vendor,App app,String idfa) throws UnsupportedEncodingException{
		 //回调地址
        String callbackUrl = "http://third.miaozhuandaqian.com/www/vendor/callback.3w";
        if (!GlobalConfig.isDeploy){
            callbackUrl = "http://www.mapi.lieqicun.cn/www/vendor/callback.3w";
        }		
	    return genCallbackUrlByUrl(vendor, app, idfa, callbackUrl);
    }
    
   default String genCallbackUrlByUrl(AppVendor vendor,App app,String idfa,String callbackUrl) throws UnsupportedEncodingException{
	    return URLEncoder.encode(genCallbackUrlByUrlNotUrlEncoder(vendor, app, idfa, callbackUrl), "utf-8");
    }
   
   default String genCallBackURLNotEncoder(AppVendor vendor,App app,String idfa){
	   //回调地址
       String callbackUrl = "http://third.miaozhuandaqian.com/www/vendor/callback.3w";
       if (!GlobalConfig.isDeploy){
           callbackUrl = "http://www.mapi.lieqicun.cn/www/vendor/callback.3w";
       }		
       return genCallbackUrlByUrlNotUrlEncoder(vendor, app, idfa, callbackUrl);
   }
   
   default String genCallbackUrlByUrlNotUrlEncoder(AppVendor vendor,App app,String idfa,String callbackUrl){
		StringBuilder sb = new StringBuilder();
   
       Map<String, String> callbackParams = new HashMap<>();
       callbackParams.put("appkey", vendor.getApp_key());
       callbackParams.put("adid", String.valueOf(app.getId() * 11 + 997));
       callbackParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
       callbackParams.put("device_id", idfa);

       String sign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(callbackParams) + vendor.getApp_secret());
       callbackParams.put("expire", "false");
       callbackParams.put("sign", sign);

       sb.append(callbackUrl).append("?").append(RequestSignUtil.getSortedRequestString(callbackParams));
       //String fullCallbackUrl = callbackUrl + "?" + RequestSignUtil.getSortedRequestString(callbackParams);
	    return sb.toString();
   }
   /**排重 我们自己的用户*/
   default boolean disctinct(AppVendor vendor, App app,User user, AppTask appTask, ClientInfo clientInfo){
	   return false;
   }
   
   default String disctinct(AppVendor vendor, App app, String idfas){
	   return null;
   }
   
   default Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas){
	   return new HashMap<>(1);
   }
   
   /***
    * 对厂商排重接口中的响应值是int的转化成我们系统的true、false
    * <br/>
    * 我们系统中true是已安装
    * 
    * @param idfaJson 
    * @param isInstallValue 已安装的是哪个值
    * @return
    */
   default String handleDiactinct(String idfaJson,int isInstallValue){
	   JSONObject json = JSONObject.fromObject(idfaJson);
		if (json != null) {
			StringBuilder sb = new StringBuilder(640);
			Iterator<String> it = json.keys();
			String key = null;
			int value ;
			sb.append("{");
			while(it.hasNext()){
				key = it.next();
				value = json.optInt(key, isInstallValue);
				sb.append("\"").append(key).append("\"").append(":");
				sb.append(value == isInstallValue?true:false).append(",");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append("}");
			return sb.toString();
		}
		return null;
   }
   /***
    * 新的排重接口的响应值
    * @param idfaJson
    * @param isInstallValue
    * @return
    */
   default Map<String, Integer> handleDiactinctNew(String idfaJson,int isInstallValue){
	   JSONObject json = JSONObject.fromObject(idfaJson);
	   Map<String, Integer > map = new HashMap<>();
		if (json != null) {
			Iterator<String> it = json.keys();
			String key = null;
			int value ;
			while(it.hasNext()){
				key = it.next();
				value = json.optInt(key, isInstallValue);
				map.put(key, value == isInstallValue?1:0);
			}
		}
		return map;
   }
}
