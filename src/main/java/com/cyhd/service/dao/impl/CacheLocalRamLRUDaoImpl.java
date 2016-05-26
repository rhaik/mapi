package com.cyhd.service.dao.impl;

import com.cyhd.common.util.structure.LRUCache;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.util.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 本地内存，实现LRU缓存
 *
 */
@Service(CacheUtil.RAM_LRU_RESOURCE)
public class CacheLocalRamLRUDaoImpl implements CacheDao {
	
	protected static Logger logger = LoggerFactory.getLogger(Constants.cache_log);
	
	protected LRUCache<String, Object> cache = new LRUCache<String, Object>(16, CacheUtil.MAX_LRU_CACHED_SIZE);
	
	protected String getCacheName(){
		return CacheUtil.RAM_LRU_RESOURCE;
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
		Object value = cache.get(key);
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:get key=" + key + ", return=" + value);
		}
		return value ;
	}

	@Override
	public void set(String key, Object value) {
		this.cache.put(key, value);
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:set key=" + key + ", value=" + value);
		}
	}
	@Override
	public void set(String key, Object value, long ttl) {
		set(key, value);
	}
	@Override
	public void set(String key, Object value, Date expire) {
		set(key, value);
	}

	@Override
	public void remove(String key) {
		if(logger.isInfoEnabled()){
			logger.info("[" + getCacheName() + "]:remove key=" + key);
		}
		this.cache.remove(key) ;
	}
}
