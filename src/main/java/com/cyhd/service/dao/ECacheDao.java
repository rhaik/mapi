package com.cyhd.service.dao;

import java.util.Date;

/**
 * 缓存接口
 * 
 */
public interface ECacheDao<T> {

	public boolean isExist(String key);

	public void set(String key, T value);

	public void set(String key, T value, Date expire);

	// ttl 毫秒
	public void set(String key, T value, int ttl);

	public T get(String key);

	public void remove(String key);
}
