package com.cyhd.common.util.structure;


public class ConcurrentLRUCache<K,V> {
	
	protected final SynchronizedLRUMap<K,V>[] table;
	
	public ConcurrentLRUCache(int row, int col) {
		this(row, col, true);
	}

	public ConcurrentLRUCache(int row, int col, boolean fixedCapacity) {
		table = SynchronizedLRUMap.newArray(row);
		for(int i=0; i<table.length; i++) {
			table[i] = new SynchronizedLRUMap<K,V>(col, fixedCapacity);
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
	
	public SynchronizedLRUMap<K,V>[] getTable(){
		return table;
	}
}
