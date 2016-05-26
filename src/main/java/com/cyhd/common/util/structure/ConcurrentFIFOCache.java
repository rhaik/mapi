package com.cyhd.common.util.structure;

public class ConcurrentFIFOCache<K,V> {
	
	private final SynchronizedFIFOMap<K,V>[] table;
	
	public ConcurrentFIFOCache(int row, int col) {
		this(row, col, true);
	}
	
	public ConcurrentFIFOCache(int row, int col, boolean fixedCapacity) {
		table = SynchronizedFIFOMap.newArray(row);
		for(int i=0; i<table.length; i++) {
			table[i] = new SynchronizedFIFOMap<K,V>(col, fixedCapacity);
		}
	}

	public V get(K key) {
		return table[hash(key)].get(key);
	}
	
	public V put(K key, V value) {
		return table[hash(key)].put(key, value);
	}
	
	public V remove(K key) {
		return table[hash(key)].remove(key);
	}
	
	public boolean containsKey(K key) {
		return table[hash(key)].containsKey(key);
	}
	
	protected int hash(K key) {
		return Math.abs(key.hashCode()) % table.length;
	}
}
