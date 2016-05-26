package com.cyhd.service.util;

import com.cyhd.common.util.StringUtil;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * http request util
 *
 */
public class RequestUtil {

	 /**
     * 获取客户端真实ip
     * @param request
     * @return
      */
     public static String getIpAddr(HttpServletRequest request) {
         String ip = request.getHeader("X-Real-IP");
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getHeader("x-forwarded-for");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getHeader("Proxy-Client-IP");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getHeader("WL-Proxy-Client-IP");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getRemoteAddr();
         }
         if (ip == null || "unknown".equalsIgnoreCase(ip)) {
             return "";
         } else {
             return ip.split(",")[0];
         }
     }
    /***
     * 获得请求的参数
     * @param request
     * @return
     */
    public static String getQueryString(HttpServletRequest request) {
    	if("POST".equalsIgnoreCase(request.getMethod())){
    		Map<String, String[]> params = request.getParameterMap(); 
//        	String queryString = "";
        	StringBuilder sb = new StringBuilder(1000);
        	for (String key : params.keySet()) {
        		String[] values = params.get(key);
        		for (int i = 0; i < values.length; i++) {
        			sb.append(key).append("=").append(values[i]).append("&");
//        			String value = values[i];
//        			queryString += key + "=" + value + "&";
        		}
        	}
    		// 去掉最后一个空格  
    		if(sb.length() > 0) {
    		   sb.deleteCharAt(sb.lastIndexOf("&"));  
    		}
    		//sb.append("<请求方式POST>");
    		return sb.toString();
    	}
    	return request.getQueryString();
    }


    /**
     * 获取请求参数
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getRequestParams(HttpServletRequest request) {
        String result = "{";

        Map maps = request.getParameterMap();
        Iterator keys = maps.keySet().iterator();
        boolean flag1 = false;
        while (keys.hasNext()) {
            String k = (String)keys.next();
            String v = request.getParameter(k);

            result += k + ":[";
            result += v;
            result += "],";
            flag1 = true;
        }
        if (flag1)
            result = result.substring(0, result.length() - 1);
        result += "}";
        return result;
    }


    /**
     * 根据请求中host，获取最后两段构成的域名，比如请求中host：api.miaozhuandaqian.com，返回miaozhuandaqian.com
     * @param request
     * @return
     */
    public static String getAuthCodeDomain(HttpServletRequest request){
        String host = request.getHeader("Host");

        if(host != null){
            host = host.substring(host.indexOf('.') + 1);
        }
        return host;
    }

    /**
     * 判断是不是Ajax请求
     * @param request
     * @return
     */
    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    /**
     * 根据请求获取完整的请求地址
     * @param request
     * @return
     */
    public static String getFullUrl(HttpServletRequest request){
        String qs = request.getQueryString();
        String fullUrl = (GlobalConfig.isDeploy? "https://" : "http://") + request.getServerName() + request.getContextPath() + request.getRequestURI();
        if(!StringUtil.isEmpty(qs)){
            fullUrl += "?" + qs;
        }
        return fullUrl;
    }
}
