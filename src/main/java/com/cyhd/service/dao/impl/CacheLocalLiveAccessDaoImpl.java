package com.cyhd.service.dao.impl;

import com.cyhd.common.util.LiveAccess;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.util.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地内存缓存，缓存一小段时间，自动过期
 *
 */
@Service(CacheUtil.RAM_LA_RESOURCE)
public class CacheLocalLiveAccessDaoImpl implements CacheDao {
	
	protected static Logger logger = LoggerFactory.getLogger(Constants.cache_log);
	
	private ConcurrentHashMap<String, LiveAccess<Object>> cache = new ConcurrentHashMap<String, LiveAccess<Object>>();
	
	protected String getCacheName(){
		return CacheUtil.RAM_LA_RESOURCE;
	}
	
	@Override
	public boolean isExist(String key) {
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:isExist key=" + key);
		}
		return this.cache.containsKey(key);
	}
	
	@Override
	public Object get(String key) {
		LiveAccess<Object> la = cache.get(key);
		Object value = null;
		if(la != null){
			value = la.getElement();
			if(value == null){
				cache.remove(key);
			}
		}
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:get key=" + key + ", return=" + value);
		}
		return value ;
	}

	@Override
	public void set(String key, Object value) {
		Date d = new Date(System.currentTimeMillis() + CacheUtil.MAX_LIVE_TIME);
		set(key, value, d);
	}
	
	@Override
	public void set(String key, Object value, Date expires) {
		int ttl = (int)(Math.abs(System.currentTimeMillis() - expires.getTime()));
		this.cache.put(key, new LiveAccess<Object>(ttl, value));
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:set key=" + key + ", value=" + value);
		}
	}
	
	@Override
	public void set(String key, Object value, long ttl) {
		Date d = new Date(System.currentTimeMillis() + ttl);
		set(key, value, d);
	}

	@Override
	public void remove(String key) {
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:remove key=" + key);
		}
		this.cache.remove(key) ;
	}

}
