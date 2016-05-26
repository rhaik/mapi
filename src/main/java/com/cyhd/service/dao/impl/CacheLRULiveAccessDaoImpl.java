package com.cyhd.service.dao.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyhd.common.util.LiveAccess;
import com.cyhd.common.util.Pair;
import com.cyhd.common.util.structure.LRUCache;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.ECacheDao;
import com.cyhd.service.util.CacheUtil;

/**
 * 本地内存，实现LRU+live access缓存
 *
 */
public class CacheLRULiveAccessDaoImpl<T> implements ECacheDao<T> {
	
	protected static Logger logger = LoggerFactory.getLogger(Constants.cache_log);
	
	protected LRUCache<String, LiveAccess<T>> cache = null;
	
	private int ttl = 1000;  // 缓存时间：ms
	
	public CacheLRULiveAccessDaoImpl(int ttl){
		this(ttl, CacheUtil.MAX_LRU_CACHED_SIZE);
	}
	public CacheLRULiveAccessDaoImpl(){
		this(1000, CacheUtil.MAX_LRU_CACHED_SIZE);
	}
	/**
	 *  ttl: 过期时间：ms
	 *  maxSize： 最大缓存个数
	 */
	public CacheLRULiveAccessDaoImpl(int ttl, int maxSize){
		this.ttl = ttl;
		cache = new LRUCache<String, LiveAccess<T>>(11, maxSize);
	}
	
	protected String getCacheName(){
		return CacheUtil.RAM_URL_LA_RESOURCE;
	}
	@Override
	public boolean isExist(String key) {
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:isExist key=" + key);
		}
		return this.cache.containsKey(key);
	}
	
	@Override
	public T get(String key) {
		LiveAccess<T> la = cache.get(key);
		if(la == null){
			if(logger.isInfoEnabled()){
				logger.info("[" + getCacheName() + "]:get key=" + key + ", return=" + null);
			}
			return null;
		}
		T value = la.getElement();
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:get key=" + key + ", return=" + value);
		}
		
		return value ;
	}

	@Override
	public void set(String key, T value) {
		LiveAccess<T> la = new LiveAccess<T>(ttl, value);
		this.cache.put(key, la);
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:set key=" + key + ", value=" + value);
		}
	}
	
	@Override
	public void set(String key, T value, Date expire) {
		set(key, value);
	}
	
	@Override
	public void set(String key, T value, int ttl) {
		LiveAccess<T> la = new LiveAccess<T>(ttl, value);
		this.cache.put(key, la);
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:set key=" + key + ", value=" + value);
		}
	}

	@Override
	public void remove(String key) {
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:remove key=" + key);
		}
		this.cache.remove(key) ;
	}
	
	public static void main(String[] args) throws Exception {
		CacheLRULiveAccessDaoImpl cacheDao = new CacheLRULiveAccessDaoImpl(6000);
		String key = "1";
		Pair<String, String> p1 = new Pair<String, String>("1111", "1111111");
		cacheDao.set(key, p1);
		Thread.sleep(1000);
		System.out.println(cacheDao.get(key));
		Thread.sleep(1000);
		System.out.println(cacheDao.get(key));
		Thread.sleep(1000);
		System.out.println(cacheDao.get(key));
		Thread.sleep(1000);
		System.out.println(cacheDao.get(key));
		Thread.sleep(1000);
		System.out.println(cacheDao.get(key));
		
	}
}
