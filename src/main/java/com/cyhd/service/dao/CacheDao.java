package com.cyhd.service.dao;

import java.util.Date;

import com.schooner.MemCached.MemcachedItem;

/**
 * 缓存接口
 *
 */
public interface CacheDao {
	
	public boolean isExist(String key) ;
	
	public void set(String key, Object value) ;
	
	public void set(String key, Object value, Date expire) ;
	//ttl 毫秒
	public void set(String key, Object value, long ttl) ;
	
	public Object get(String key)  ;
	
	public void remove(String key) ;

	default MemcachedItem gets(String key){
		return null;
	}

	default boolean cas(String key, Object value, long parseLong){
		return false;
	}
	
}
