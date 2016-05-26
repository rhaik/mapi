package com.cyhd.web.action.web;

import com.cyhd.common.util.Helper;
import com.cyhd.common.util.JsonUtils;
import com.cyhd.common.util.MD5Util;
import com.cyhd.common.util.NumberUtil;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.impl.AppVendorService;
import com.cyhd.service.util.RequestSignUtil;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.web.MyHttpServletRequestWrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 应用厂商回调接口
 * Created by hy on 9/15/15.
 */
@Controller
@RequestMapping("/www/vendor")
public class AppVendorAction {

    private final static Logger logger = LoggerFactory.getLogger("vendor");

    //最长的请求延迟时间，超过这个时间为无效请求
    private final static int MAX_REQUEST_DELAY = 300;

    @Resource
    private AppVendorService appVendorService;

    /**
     * 有米回调
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/youmicallback.3w", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String onVendorYoumiCallback(HttpServletRequest request) throws Exception {
    	 String clickid = request.getParameter("clickid");
    	 MyHttpServletRequestWrapper myrequest = new MyHttpServletRequestWrapper(request);
    
		 clickid = URLDecoder.decode(clickid, "utf-8"); 
		 Map mp = Helper.getEncodedUrlParams(clickid);
    	 myrequest.setMyParams(mp);
    	 
    	 String result = checkParameters(myrequest);
         int code = 0;

         if (result == null){
             String appKey = myrequest.getParameter("appkey");
             AppVendor vendor = appVendorService.getAppVendorByAppKey(appKey);
             if (vendor == null){
                 result = "invalid appkey";
             }else if (!checkSign(mp, vendor)){
                 result = "sign error";
             }else { //app vendor and sign ok
                 //appTask id
                 String adid = myrequest.getParameter("adid");
                 String deviceId = myrequest.getParameter("device_id");

                 result = appVendorService.onVendorCallback(vendor, adid, deviceId);
             }
         }

         if (result == null){
             code = 0;
             result = "ok";
         }else {
             code = 400;
         }

         String response = String.format("{\"code\": %d,\"result\": %s}", code, JsonUtils.jsonQuote(result));
         logger.info("AppVendorCallback:result:{}", response);

         return response;
    	 
    }

    /**
     * 第三方回调接口
     * @return
     */
    @RequestMapping(value = "/callback.3w", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String onVendorCallback(HttpServletRequest request){
    	String query = RequestUtil.getQueryString(request);
    	
        String result = checkParameters(request);
        int code = 0;

        if (result == null){
            String appKey = request.getParameter("appkey");
            AppVendor vendor = appVendorService.getAppVendorByAppKey(appKey);
            if (vendor == null){
                result = "invalid appkey";
            }else if (!checkSign(request, vendor)){
                result = "sign error";
            }else { //app vendor and sign ok
                //appTask id
                String adid = request.getParameter("adid");
                String deviceId = request.getParameter("device_id");

                result = appVendorService.onVendorCallback(vendor, adid, deviceId);
            }
        }

        if (result == null){
            code = 0;
            result = "ok";
        }else {
            code = 400;
        }

        String response = String.format("{\"code\": %d,\"result\": %s}", code, JsonUtils.jsonQuote(result));
        logger.info("AppVendorCallback:result:{},query:{},", response,query);

        return response;
    }

    @RequestMapping(value = "/callback_today.3w", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String onVendorCallbackByToday(HttpServletRequest request){
    	String ip = RequestUtil.getIpAddr(request);
    	String query = RequestUtil.getQueryString(request);
    	StringBuilder sb = new StringBuilder();
    	sb.append("ip:").append(ip);
    	sb.append(",query:").append(query);
    	
        String success =  "false";
        String result = null;
        if (result == null){
            String appKey = request.getParameter("appkey");
            if(StringUtils.isBlank(appKey)){
            	 result = "invalid appkey";
            }
            if(result == null){
            	AppVendor vendor = appVendorService.getAppVendorByAppKey(appKey);
	            if (vendor == null){
	                result = "invalid appkey";
	            }else { //app vendor and sign ok
	                //appTask id
	                //String adid = request.getParameter("app_id");
	                String deviceId = request.getParameter("idfa");
	                int app_id = ServletRequestUtils.getIntParameter(request, "app_id", 0);
	                int adid =  appVendorService.convertToDayAppId(app_id);
	                if(adid > 0){
	                	result = appVendorService.onVendorCallback(vendor, String.valueOf(adid), deviceId);
	                }else{
	                	result = "app_id is not found";
	                }
	             }
            }
        }

        if (result == null){
            success = "true";
            result = "ok";
        }

        String response = String.format("{\"message\":%s,\"success\":%s}", JsonUtils.jsonQuote(result),success);
        logger.info(sb.toString()+"AppVendorCallback:result:{}", response);

        return response;
    }
    
    @RequestMapping(value = "/callback_qunar.3w", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String onVendorCallbackByQunarGongLve(HttpServletRequest request){
    	StringBuilder sb = new StringBuilder(320);
    	sb.append("IP：").append(RequestUtil.getIpAddr(request));
    	sb.append(",query:").append(RequestUtil.getQueryString(request));
    	ExecuteVendorResponseBean responseBean = executeVendorCallBack(request);
        String response = String.format("{\"success\": %b,\"result\": %s}", responseBean.isOK(), JsonUtils.jsonQuote(responseBean.getResult()));
        logger.info("去哪儿攻略 AppVendorCallback:result:{},query:{},", response,sb.toString());

        return response;
    }
    
    @RequestMapping(value = "/callback_xygj.3w", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String onVendorCallbackByXinYongGuanjia(HttpServletRequest request){
    	StringBuilder sb = new StringBuilder(320);
    	sb.append("IP：").append(RequestUtil.getIpAddr(request));
    	sb.append(",query:").append(RequestUtil.getQueryString(request));
    	ExecuteVendorResponseBean responseBean = executeVendorCallBack(request);
        String response = responseBean.isOK()?"ok":"error";
        logger.info("信用管家 AppVendorCallback:result:{},query:{},", response,sb.toString());

        return response;
    }
    
    
    
    /**
     * 检查请求参数是否正确
     * @param request
     * @return
     */
    private String checkParameters(HttpServletRequest request){
        String result = null;
        if (StringUtils.isBlank(request.getParameter("appkey"))){
            result = "appkey is required";
        }else if(StringUtils.isBlank(request.getParameter("adid"))){
            result = "adid is required";
        }else if (StringUtils.isBlank(request.getParameter("timestamp"))){
            result = "activate time is required";
        }else if (StringUtils.isBlank(request.getParameter("device_id"))){
            result = "device_id is required";
        }else if (StringUtils.isBlank(request.getParameter("sign"))){
            result = "sign is required";
        }

        if (result == null){
            int timestamp = NumberUtil.safeParseInt(request.getParameter("timestamp"));
            int now = (int)(System.currentTimeMillis() / 1000);

            String expire  = request.getParameter("expire");
            if("false".equals(expire)) {
                //empty here, do not check timestamp
            } else  if (timestamp > now || (now - timestamp > MAX_REQUEST_DELAY)){
                result = "invalid timestamp";
            }
        }
        return result;
    }

    /**
     * 检查请求参数
     * @param request
     * @param vendor
     * @return
     */
    private boolean checkSign(Map requestMap, AppVendor vendor){
    	String sign = (String) requestMap.get("sign");
    	requestMap.remove("expire");
    	requestMap.remove("sign");
        //根据请求参数生成MD5
        String newSign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(requestMap) + vendor.getApp_secret());
        logger.info("AppVendorCallback request:{}, sign:{}, newSign:{}", requestMap, sign, newSign);

        return newSign.equals(sign);
    }
    /**
     * 检查请求参数
     * @param request
     * @param vendor
     * @return
     */
    private boolean checkSign(HttpServletRequest request, AppVendor vendor){
        Map<String, String> requestMap = new HashMap<String, String>();
        Enumeration enumeration = request.getParameterNames();

        String sign = request.getParameter("sign");
        while (enumeration.hasMoreElements()){
            String name = String.valueOf(enumeration.nextElement());
            if ("sign".equals(name) || "expire".equals(name)){
                continue;
            }
            requestMap.put(name, request.getParameter(name));
        }

        //根据请求参数生成MD5
        String newSign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(requestMap) + vendor.getApp_secret());

        logger.info("AppVendorCallback request:{}, sign:{}, newSign:{}", requestMap, sign, newSign);

        return newSign.equals(sign);
    }
	/***
	 * 将原有的 code和result封装一起
	 * @param request
	 * @return 
	 */
    private ExecuteVendorResponseBean executeVendorCallBack(HttpServletRequest request){
    	
        String result = checkParameters(request);
        int code = 0;

        if (result == null){
            String appKey = request.getParameter("appkey");
            AppVendor vendor = appVendorService.getAppVendorByAppKey(appKey);
            if (vendor == null){
                result = "invalid appkey";
            }else if (!checkSign(request, vendor)){
                result = "sign error";
            }else { //app vendor and sign ok
                //appTask id
                String adid = request.getParameter("adid");
                String deviceId = request.getParameter("device_id");

                result = appVendorService.onVendorCallback(vendor, adid, deviceId);
            }
        }
        if (result == null){
            code = 0;
            result = "ok";
        }else {
            code = 400;
        }
        
        ExecuteVendorResponseBean response = new ExecuteVendorResponseBean(code,result);
        return response;
    }
    static class ExecuteVendorResponseBean{
    	private int code;
    	private String result;
    	
		public ExecuteVendorResponseBean(int code, String result) {
			this.code = code;
			this.result = result;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		/**
		 *  处理成功没有
		 * @return
		 */
		public boolean isOK(){
			return code==0;
		}
    	
    }
}
