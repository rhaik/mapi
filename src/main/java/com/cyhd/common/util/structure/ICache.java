package com.cyhd.common.util.structure;

public interface ICache<K, V> {
	V get(K key);
	V put(K key, V value);
	V remove(K key);
	boolean containsKey(K key);
}
