package com.cyhd.common.util.structure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CopyOnWriteMap<K, V> implements Map<K, V> {

	private volatile Map<K, V> impl;
	
	public CopyOnWriteMap() {
		impl = new HashMap<K, V>();
	}
	
	public CopyOnWriteMap(int capacity) {
		impl = new HashMap<K, V>(capacity);
	}
	
	public CopyOnWriteMap(Map<K, V> impl) {
		this.impl = impl != null ? impl : new HashMap<K, V>();
	}
	
	@Override
	public synchronized V put(K key, V value) {
		Map<K, V> tmp = new HashMap<K, V>(impl);
		V old = tmp.put(key, value);
		impl = tmp;
		return old;
	}
	
	public synchronized V putIfAbsent(K key, V value) {
		if(!containsKey(key))
			return put(key, value);
		else
			return get(key);
	}

	@Override
	public synchronized void putAll(Map<? extends K, ? extends V> m) {
		Map<K, V> tmp = new HashMap<K, V>(impl);
		tmp.putAll(m);
		impl = tmp;
	}

	@Override
	public synchronized V remove(Object key) {
		Map<K, V> tmp = new HashMap<K, V>(impl);
		V value = tmp.remove(key);
		impl = tmp;
		return value;
	}
	
	@Override
	public synchronized void clear() {
		impl = new HashMap<K, V>();
	}

	@Override
	public boolean containsKey(Object key) {
		return impl.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return impl.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return impl.entrySet();
	}

	@Override
	public V get(Object key) {
		return impl.get(key);
	}

	@Override
	public boolean isEmpty() {
		return impl.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return impl.keySet();
	}

	@Override
	public int size() {
		return impl.size();
	}

	@Override
	public Collection<V> values() {
		return impl.values();
	}
	
}
