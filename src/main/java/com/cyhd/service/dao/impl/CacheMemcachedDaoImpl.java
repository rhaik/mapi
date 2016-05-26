package com.cyhd.service.dao.impl;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.impl.PropertiesService;
import com.cyhd.service.util.CacheUtil;
import com.schooner.MemCached.MemcachedItem;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

/**
 * memcached实现缓存
 *
 */
@Service(CacheUtil.MEMCACHED_RESOURCE)
public class CacheMemcachedDaoImpl implements CacheDao {
	
	protected static Logger logger = LoggerFactory.getLogger(Constants.cache_log);
	
	protected String cache_name = CacheUtil.MEMCACHED_RESOURCE;
	
	@Resource
	private PropertiesService propertiesService;
	
	@Override
	public boolean isExist(String key) {
		try {
			if(!useMemcache){
				return false;
			}
			if(logger.isInfoEnabled()){
				logger.info("[" + cache_name + "]:isExist key=" + key);
			}
			return mcc.keyExists(key);
		} catch (Exception e) {
			logger.error("CacheMemcachedDaoImpl.isExist error.", e);
		}
		return false ;
	}
	
	@Override
	public Object get(String key) {
		Object value = null ;
		try {
			if(!useMemcache){
				return null;
			}
			value = mcc.get(key);
			if(logger.isInfoEnabled()){
				logger.info("[" + cache_name + "]:get key=" + key + ", return=" + value);
			}
		} catch (Exception e) {
			logger.error("CacheMemcachedDaoImpl.get error.", e);
		}
		return value ;
	}
	 
	public MemcachedItem gets(String key) {
		MemcachedItem value = null ;
		try {
			if(!useMemcache){
				return null;
			}
			value = mcc.gets(key);
			if(logger.isInfoEnabled()){
				logger.info("[" + cache_name + "]:gets key=" + key + ", return=" + value);
			}
		} catch (Exception e) {
			logger.error("CacheMemcachedDaoImpl.gets error.", e);
		}
		return value ;
	}
	/**
	 * 存入
	 * @param key
	 * @param value
	 * @param casUnique
	 * @return
	 */
	public boolean cas(String key, Object value, long casUnique) {
		boolean success = false;
		try {
			if(!useMemcache){
				return false;
			}
			success = mcc.cas(key, value, casUnique);
			if(logger.isInfoEnabled()){
				logger.info("[" + cache_name + "]:cas key=" + key + ", value=" + value + ", casUnique:" + casUnique);
			}
		} catch (Exception e) {
			logger.error("CacheMemcachedDaoImpl.cas error.", e);
			success = false;
		}
		return success;
	}
	
	@Override
	public void set(String key, Object value) {
		
		set(key, value, new Date(GenerateDateUtil.getCurrentTime() + Constants.day_millis)); 
	}
	
	@Override
	public void set(String key, Object value, long ttl) {
		Date d = new Date(System.currentTimeMillis() + ttl);
		set(key, value, d);
	}
	
	@Override
	public void set(String key, Object value, Date expire) {
		try {
			if(!useMemcache){
				return;
			}
			mcc.set(key, value, expire) ;
			if(logger.isInfoEnabled()){
				logger.info("[" + cache_name + "]:set key=" + key + ", value=" + value + ", expire:" + DateUtil.format(expire, "yyyy-MM-dd HH:mm:ss"));
			}
		} catch (Exception e) {
			logger.error("CacheMemcachedDaoImpl.set error.", e);
		}
	}

	@Override
	public void remove(String key) {
		try {
			if(!useMemcache){
				return;
			}
			mcc.delete(key) ;
			if(logger.isInfoEnabled()){
				logger.info("[" + cache_name + "]:remove key=" + key);
			}
		} catch (Exception e) {
			logger.error("CacheMemcachedDaoImpl.remove error.", e);
		}
	}
	
	
	/**
	 * memcached client init
	 */
	private MemCachedClient mcc = null;
	
	private boolean useMemcache = CacheUtil.USE_MEMCACHED;
	
	@PostConstruct
	public void init(){
		if(useMemcache){
			String poolName = "moeny";
			
			String servers[] = propertiesService.getMemcachedHosts() ;
			if(servers == null || servers.length == 0){
				if(logger.isWarnEnabled())
					logger.warn("init memcached error! servers == null");
				useMemcache = false;
				return;
			}
			SockIOPool pool = SockIOPool.getInstance(poolName);
			
			pool.setServers(servers);
			
			pool.setInitConn(5);
			pool.setMinConn(5);
			pool.setMaxConn(250);
			pool.setMaxIdle(1000 * 60 * 30); 
			
			pool.setMaintSleep(3000);
			
			pool.setNagle(false);
			pool.setSocketConnectTO(500);
			pool.setSocketTO(500);
			
			pool.initialize();
			mcc = new MemCachedClient(poolName);
		}
	}
}
