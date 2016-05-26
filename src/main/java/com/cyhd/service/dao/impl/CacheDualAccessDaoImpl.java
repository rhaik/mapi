package com.cyhd.service.dao.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.ECacheDao;
import com.cyhd.service.util.CacheUtil;

/**
 * 二级缓存，先读取本地缓存，如果没有，再读取memcached
 *
 */
public class CacheDualAccessDaoImpl<T> implements ECacheDao<T> {
	
	protected static Logger logger = LoggerFactory.getLogger(Constants.cache_log);
	
	private CacheLRULiveAccessDaoImpl<T> localCache = null;
	
	private CacheDao remoteCache = null;
	
	/**
	 *  ttl: 过期时间：ms
	 *  maxSize： 最大缓存个数
	 */
	public CacheDualAccessDaoImpl(int ttl, int maxSize, CacheDao remoteCache){
		this.localCache = new CacheLRULiveAccessDaoImpl<T>(ttl, maxSize);
		this.remoteCache = remoteCache;
	}
	
	protected String getCacheName(){
		return CacheUtil.DUAL_CACHE_RESOURCE;
	}
	@Override
	public boolean isExist(String key) {
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:isExist key=" + key);
		}
		return this.localCache.isExist(key) || remoteCache.isExist(key);
	}
	
	@Override
	public T get(String key) {
		T value = localCache.get(key);
		if(value == null){
			value = (T)remoteCache.get(key);
		}
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:get key=" + key + ", return=" + value);
		}
		if(value != null){
			localCache.set(key, value);
		}
		return value ;
	}

	@Override
	public void set(String key, T value) {
		remoteCache.set(key, value);
		localCache.set(key, value);
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:set key=" + key + ", value=" + value);
		}
	}
	
	@Override
	public void set(String key, T value, int ttl) {
		remoteCache.set(key, value, ttl);
		localCache.set(key, value, ttl);
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:set key=" + key + ", value=" + value);
		}
	}
	
	@Override
	public void set(String key, T value, Date expire) {
		remoteCache.set(key, value);
		localCache.set(key, value, expire);
	}

	@Override
	public void remove(String key) {
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:remove key=" + key);
		}
		remoteCache.remove(key);
		localCache.remove(key);
	}
	
	public static void main(String[] args) throws Exception {
		
	}
}
