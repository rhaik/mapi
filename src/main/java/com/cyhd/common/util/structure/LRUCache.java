package com.cyhd.common.util.structure;


public class LRUCache<K,V> implements ICache<K,V> {

	protected final ConcurrentLRUCache<K,V> cache;
	
	public LRUCache(int concurrencyLevel, int maxCapacity) {
		this(concurrencyLevel, maxCapacity, true);
	}
	
	public LRUCache(int concurrencyLevel, 
			int maxCapacity, 
			boolean fixedCapacity) 
	{
		if (maxCapacity <= 0) {
			cache = null;
			return;
		}
		
		if (concurrencyLevel < 1) 
			concurrencyLevel = 1;
		cache = new ConcurrentLRUCache<K,V>(concurrencyLevel, 
				(maxCapacity + concurrencyLevel - 1) / concurrencyLevel, 
				fixedCapacity);
	}

	@Override
	public boolean containsKey(K key) {
		if (cache != null)
			return cache.containsKey(key);
		return false;
	}

	@Override
	public V get(K key) {
		if (cache != null)
			return cache.get(key);
		return null;
	}

	@Override
	public V put(K key, V value) {
		if (cache != null)
			return cache.put(key, value);
		return null;
	}

	@Override
	public V remove(K key) {
		if (cache != null)
			return cache.remove(key);
		return null;
	}
	
	public ConcurrentLRUCache<K,V> getCache(){
		return cache;
	}
}
