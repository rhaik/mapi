package com.cyhd.common.util.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantMap<K, V> implements Map<K, V> {

	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();
	
	private final Map<K, V> impl;
	
	public ReentrantMap() {
		impl = new HashMap<K, V>();
	}
	
	public ReentrantMap(int capacity) {
		impl = new HashMap<K, V>(capacity);
	}
	
	public ReentrantMap(Map<K, V> impl) {
		this.impl = impl != null ? impl : new HashMap<K, V>();
	}
	
	@Override
	public V put(K key, V value) {
		w.lock(); try { return impl.put(key, value); } finally { w.unlock(); }
	}
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		w.lock(); try { impl.putAll(m); } finally { w.unlock(); }
	}
	@Override
	public V remove(Object key) {
		w.lock(); try { return impl.remove(key); } finally { w.unlock(); }
	}
	@Override
	public void clear() {
		w.lock(); try { impl.clear(); } finally { w.unlock(); }
	}
	public V putIfAbsent(K key, V value) {
		w.lock();
		try {
			if(!impl.containsKey(key))
				return impl.put(key, value);
			else
				return impl.get(key);
		} finally {
			w.unlock();
		}
	}
	
	@Override
	public boolean containsKey(Object key) {
		r.lock(); try { return impl.containsKey(key); } finally { r.unlock(); }
	}
	@Override
	public boolean containsValue(Object value) {
		r.lock(); try { return impl.containsValue(value); } finally { r.unlock(); }
	}
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		r.lock(); try { return impl.entrySet(); } finally { r.unlock(); }
	}
	@Override
	public V get(Object key) {
		r.lock(); try { return impl.get(key); } finally { r.unlock(); }
	}
	@Override
	public boolean isEmpty() {
		r.lock(); try { return impl.isEmpty(); } finally { r.unlock(); }
	}
	@Override
	public Set<K> keySet() {
		r.lock(); try { return impl.keySet(); } finally { r.unlock(); }
	}
	@Override
	public int size() {
		r.lock(); try { return impl.size(); } finally { r.unlock(); }
	}
	
	/**
	 * return a copy of original map values to avoid concurrent modification.
	 */
	@Override
	public Collection<V> values() {
		r.lock(); try { return new ArrayList<V>(impl.values()); } finally { r.unlock(); }
	}
    
}
