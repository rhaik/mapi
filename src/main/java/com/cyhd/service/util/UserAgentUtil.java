package com.cyhd.service.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.cyhd.common.util.KeywordMatcher;
import com.cyhd.common.util.KeywordMatcher.Match;


public class UserAgentUtil {

	// 设备代码
    public static final int DEVICE_CODE_UNKNOWN = 0;
    public static final int DEVICE_CODE_WINDOWS = 1;
    public static final int DEVICE_CODE_IMAC = 2;
    public static final int DEVICE_CODE_IPAD = 3;
    public static final int DEVICE_CODE_H5_IOS = 4;
    public static final int DEVICE_CODE_H5_ANDROID = 5;
    public static final int DEVICE_CODE_WEIXIN_IOS = 6;
    public static final int DEVICE_CODE_WEIXIN_ANDROID = 7;
    public static final int DEVICE_CODE_APP_IOS = 8;
    public static final int DEVICE_CODE_APP_ANDROID = 9;
    public static final int DEVICE_CODE_OTHER = 10;
    public static final int DEVICE_CODE_OPERATOR = 11;//运营添加的
    
    public static final Map<Integer, String> DEVICE_NAME_MAP = new HashMap<Integer, String>();
    static {
        DEVICE_NAME_MAP.put(DEVICE_CODE_UNKNOWN, "");
        DEVICE_NAME_MAP.put(DEVICE_CODE_WINDOWS, "web-win");
        DEVICE_NAME_MAP.put(DEVICE_CODE_IMAC, "web-mac");
        DEVICE_NAME_MAP.put(DEVICE_CODE_IPAD, "web-ipad");
        DEVICE_NAME_MAP.put(DEVICE_CODE_H5_IOS, "h5-ios");
        DEVICE_NAME_MAP.put(DEVICE_CODE_H5_ANDROID, "h5-android");
        DEVICE_NAME_MAP.put(DEVICE_CODE_WEIXIN_IOS, "weixin-ios");
        DEVICE_NAME_MAP.put(DEVICE_CODE_WEIXIN_ANDROID, "weixin-android");
        DEVICE_NAME_MAP.put(DEVICE_CODE_APP_IOS, "app-ios");
        DEVICE_NAME_MAP.put(DEVICE_CODE_APP_ANDROID, "app-android");
        DEVICE_NAME_MAP.put(DEVICE_CODE_OTHER, "other");
        DEVICE_NAME_MAP.put(DEVICE_CODE_OPERATOR, "mis-operator");

    }
    
    /**
     * app端得到设备代号,jack,for app
     */
    public static int getAppDeviceCode(String str){
        int result=0;
        if(str.equals("mayiyou4ios")){
            result = DEVICE_CODE_APP_IOS;
        }else {
            result = DEVICE_CODE_APP_ANDROID;
        }
        return result;
    }
    /**
     * 获得来源设备代码
     * 
     * @param userAgent http头中的user-agent
     * @return
     */
    public static int getDeviceCode(String userAgentStr) {
        UserAgent userAgent = new UserAgent(userAgentStr);
        if (userAgent.isWindows()) {
            return DEVICE_CODE_WINDOWS;
        }
        if (userAgent.isIMac()) {
            return DEVICE_CODE_IMAC;
        }
        if (userAgent.isIPad()) {
            return DEVICE_CODE_IPAD;
        }
        if (userAgent.isIPhone() && !userAgent.isWeixin()) {
            return DEVICE_CODE_H5_IOS;
        }
        if (userAgent.isAndroid() && !userAgent.isWeixin()) {
            return DEVICE_CODE_H5_ANDROID;
        }
        if (userAgent.isIPhone() && userAgent.isWeixin()) {
            return DEVICE_CODE_WEIXIN_IOS;
        }
        if (userAgent.isAndroid() && userAgent.isWeixin()) {
            return DEVICE_CODE_WEIXIN_ANDROID;
        }
        return DEVICE_CODE_OTHER;
    }

	/**
	 * 获取过一次后，放入request中
	 * @param request
	 * @return
	 */
    public static UserAgent getUserAgent(HttpServletRequest request){
		UserAgent ua = (UserAgent)request.getAttribute("@bm-user-agent");
		if (ua == null){
			ua = new UserAgent(request);
			request.setAttribute("@bm-user-agent", ua);
		}
    	return ua;
    }
    
	public static class UserAgent {

	    private static final long WINDOWS = 0;
	    private static final long MACINTOSH = 1;
	    private static final long IPAD = 2;
	    private static final long WEIXIN = 3;
	    private static final long IPHONE = 4;
	    private static final long ANDROID = 5;
	    private static final long BIGMONEY = 6;
	    private static final long SAFARI = 7;
	    private static final long QZONE = 8;
	    private static final long WEIBO = 9;
	    private static final long QQ = 10;
	    private static final long MQQBROWSER = 11;
		private static final long ITUNESSTORED = 12;
	    
	    private static KeywordMatcher matcher = new KeywordMatcher();
	    static {
	        matcher.addKeyword("Windows", WINDOWS);
	        matcher.addKeyword("Macintosh", MACINTOSH);
	        matcher.addKeyword("iPad", IPAD);
	        matcher.addKeyword("MicroMessenger", WEIXIN);
	        matcher.addKeyword("iPhone", IPHONE);
	        matcher.addKeyword("Android", ANDROID);
	        matcher.addKeyword("bigmoney", BIGMONEY);
	        matcher.addKeyword("Safari", SAFARI);
	        matcher.addKeyword("Qzone", QZONE);
	        matcher.addKeyword("Weibo", WEIBO);
	        matcher.addKeyword("QQ", QQ);
	        matcher.addKeyword("MQQBrowser", MQQBROWSER);
			matcher.addKeyword("itunesstored", ITUNESSTORED);
	        
	    }

		//是否在安卓的应用内，需要特别判断
		boolean inAndroidApp = false;
	    
	    private Set<Long> set = new HashSet<Long>();
	    
	    public UserAgent(HttpServletRequest request) {
	        if (request == null) {
	            return;
	        }
	        String userAgent = request.getHeader("user-agent");
	        if (StringUtils.isBlank(userAgent)) {
	            return;
	        }

			//判断是否在安卓的应用内，安卓应用内请求的UA设置有点问题
			if (request.getAttribute("clientAuth") != null && userAgent.contains("okhttp")){
				inAndroidApp = true;
			}

	        buildSet(userAgent);
	    }
	    
	    public UserAgent(String userAgent) {
	        if (StringUtils.isBlank(userAgent)) {
	            return;
	        }
	        buildSet(userAgent);
	    }
	    
	    private void buildSet(String userAgent) {
	        Match match;
	        int beginIndex = 0;
	        while(true) {
	            match = matcher.nextMatch(userAgent, beginIndex);
	            if (match == null) {
	                break;
	            }
	            beginIndex = match.beginIndex + 1;
	            set.add(match.info);
	        } 
	    }
	    
	    public boolean isWindows() {
	        return set.contains(WINDOWS);
	    }
	    
	    public boolean isIMac() {
	        return set.contains(MACINTOSH);
	    }
	    
	    public boolean isIPad() {
	        return set.contains(IPAD);
	    }
	    
	    public boolean isWeixin() {
	        return set.contains(WEIXIN);
	    }
	    
	    public boolean isIPhone() {
	        return set.contains(IPHONE);
	    }
	    
	    public boolean isInAppView() {
	        return inAndroidApp || set.contains(BIGMONEY);
	    }
	    
	    public boolean isSafari() {
	        return set.contains(SAFARI);
	    }
	    
	    public boolean isAndroid() {
	        return inAndroidApp || set.contains(ANDROID);
	    }
	    public boolean isQzone() {
	        return set.contains(QZONE);
	    }
	    public boolean isWeibo() {
	        return set.contains(WEIBO);
	    }
	    public boolean isQq() {
	        return set.contains(QQ);
	    }
	    public boolean isMQQBrowser() {
	        return set.contains(MQQBROWSER);
	    }

		//是不是IOS安装应用的请求
		public boolean isItunesstored() {
			return set.contains(ITUNESSTORED);
		}
	    
	    /*public static void main(String[] args) {
	        UserAgent agent = new UserAgent("abcd iPhon abcd MicroMessenger abcd");
	        System.out.println("wexin:" + agent.isWeixin());
	        System.out.println("iphone:" + agent.isIPhone());
	        System.out.println("win:" + agent.isWindows());
	    }*/
	    
	}
}
