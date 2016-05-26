package com.cyhd.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.service.util.GlobalConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.UUIDUtil;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.util.CacheUtil;
import com.cyhd.service.util.CookieUtil;

/**
 * 分布式session服务，以memcache作为存储
 * @author hy
 *
 */
@Service
public class SessionService extends BaseService {
	//放入http request暂存的key
	private static final String REQUEST_ATTR_KEY = "@@REQUEST_ATTR_KEY";
	
	//用作http cookie的键
	private static final String MEM_COOKIE_KEY = "MEM_SESSION_KEY";
	
	//session 有效期
	private static final long SESSION_TTL = 30 * 60 * 1000; //30s

	@Resource(name = CacheUtil.MEMCACHED_RESOURCE)
	private CacheDao memcachedCacheDao;
	
	
	/**
	 * 根据request获取session存储的Map<br/>
	 * 尽量少往session存储数据，避免memcache放不下
	 * @param request
	 * @return
	 */
	public Map<String,Object> getSession(HttpServletRequest request){
		Map<String, Object> sessionMap = null;
		
		//已经在request中放置过sessionMap
		if(request.getAttribute(REQUEST_ATTR_KEY) != null){
			return (Map<String, Object>)request.getAttribute(REQUEST_ATTR_KEY);
		}
		
		//cookie中有值，则从memcache中取数据
		String cookieValue = CookieUtil.getCookieValue(MEM_COOKIE_KEY, request);
		if(StringUtils.isNotBlank(cookieValue)){
			sessionMap = (Map<String,Object>) memcachedCacheDao.get(cookieValue);
			logger.info("get session data from memcache, data={}, cookie={}", sessionMap, cookieValue);
		}else {
			cookieValue = UUIDUtil.getCommonUUID();
		}
		
		//如果未取到数据，或者未包含cookie key
		if(sessionMap == null || !sessionMap.containsKey(MEM_COOKIE_KEY)){
			sessionMap = new HashMap<String, Object>();
			sessionMap.put(MEM_COOKIE_KEY, cookieValue);
		}
		
		//先暂存在request中
		request.setAttribute(REQUEST_ATTR_KEY, sessionMap);
		
		return sessionMap;
	}
	
	/**
	 * 提交session，每次请求处理完成之前调用一次，无论有无设置过session数据
	 * @param request
	 * @param response
	 */
	public void commitSession(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> sessionData = getSession(request);
		
		if(sessionData != null && sessionData.containsKey(MEM_COOKIE_KEY)){
			String cookieValue = (String)sessionData.get(MEM_COOKIE_KEY);
			
			//设置memcache和cookie
			memcachedCacheDao.set(cookieValue, sessionData, SESSION_TTL);
			
			//使用httpOnly cookie
			CookieUtil.setHttpOnlyCookie(MEM_COOKIE_KEY, cookieValue, GlobalConfig.isDeploy, response);
			
			logger.info("save session data and set cookie, data={}, cookie={}", sessionData, cookieValue);
		}else {
			logger.warn("no session data or no cookie value, skip saving session data");
		}
	}
}
