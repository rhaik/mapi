package com.cyhd.common.util.structure;

public class DefaultLRUCache<K,V> implements ICache<K,V> {

	public DefaultLRUCache() {}
	
	@Override
	public boolean containsKey(K key) {
		return false;
	}

	@Override
	public V get(K key) {
		return null;
	}

	@Override
	public V put(K key, V value) {
		return null;
	}

	@Override
	public V remove(K key) {
		return null;
	}
}
