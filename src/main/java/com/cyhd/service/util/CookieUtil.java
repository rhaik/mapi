/*
 * Copyright (c) 2012-2022 mayi.com
 * All rights reserved.
 * 
 */
package com.cyhd.service.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

/**
 * cookie 操作工具类
 *
 */
public class CookieUtil {
    
    final static int MAX_AGE = 30 * 24 * 60 * 60;//默认cookie生存期 30天
    
    /** 
     * 添加新的Cookie 
     * @param name 
     * @param value
     * @param HttpServletResponse 
     */  
    public static void setNewCookie(String name,String value,HttpServletResponse response) {  
        Cookie cookie=new Cookie(name,value);  
        cookie.setPath("/");
        cookie.setMaxAge(MAX_AGE);//设置生存期，当设置为负值时，则为浏览器进程Cookie(内存中保存)，关闭浏览器就失效。      
        response.addCookie(cookie);  
    } 
    
    /** 
     * 添加新的Cookie 
     * @param name 
     * @param valueHttp
     * @param maxAge
     * @param ServletResponse 
     */  
    public static void setNewCookie(String name,String value,int maxAge,String domain, HttpServletResponse response) {  
        Cookie cookie=new Cookie(name,value); 
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);//设置生存期，当设置为负值时，则为浏览器进程Cookie(内存中保存)，关闭浏览器就失效。  
        if(domain != null && !domain.isEmpty()){
        	cookie.setDomain(domain);
        }
        response.addCookie(cookie);  
    } 
    
    /** 
     * 获取Cookie值 
     * @param name 
     * @param request 
     * @return 
     */  
    public static String getCookieValue(String name,HttpServletRequest request) {  
        String value="";  
        Cookie[] cookies=request.getCookies(); 
        if(null!=cookies){
            for (int i = 0; i < cookies.length; i++) {  
                if (cookies[i].getName().equalsIgnoreCase(name)) {  
                    value=cookies[i].getValue();  
                }  
            }
        }
        return value;  
    }

    /**
     * 设置HttpOnly的cookie，只支持session cookie
     * @param name
     * @param value
     * @param response
     */
    public static void setHttpOnlyCookie(String name, String value, boolean isSecure , HttpServletResponse response){
        String cookie = String.format("%s=%s; Path=/;%s HttpOnly", name, value, isSecure ? " Secure;" : "");
        response.addHeader("Set-Cookie", cookie);
    }
    
    /** 
     * 删除cookie中对应数值 
     * @param name 
     * @param request 
     * @param response 
     */  
    public static void  deleteCookie(String name,HttpServletRequest request,HttpServletResponse response) {  
        Cookie[] cookies=request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equalsIgnoreCase(name)) {
                    cookies[i].setValue(null);
                    cookies[i].setPath("/");
                    cookies[i].setMaxAge(0);//设置为0为立即删除该Cookie
                    response.addCookie(cookies[i]);
                }
            }
        }
    }  
      
      
    /** 
     * 增加规定cookie中的数值 
     * @param name 
     * @param value 
     * @param request 
     * @param response 
     */  
    public static void addnewCookieValue(String name,BigDecimal value,HttpServletRequest request,HttpServletResponse response) {  
        Cookie[] cookies=request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equalsIgnoreCase(name)) {
                    cookies[i].setValue(new BigDecimal(cookies[i].getValue().trim()).add(value).toString());
                    response.addCookie(cookies[i]);
                }
            }
        }
    }  
      
    /** 
     * 减去规定cookie中的数值 
     * @param name 
     * @param value 
     * @param request 
     * @param response 
     */  
    public static void subCookieValue(String name,BigDecimal value, HttpServletRequest request,HttpServletResponse response) {  
        Cookie[] cookies=request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equalsIgnoreCase(name)) {
                    cookies[i].setValue(new BigDecimal(cookies[i].getValue()).subtract(value).toString());
                    response.addCookie(cookies[i]);
                }
            }
        }
    }
}
