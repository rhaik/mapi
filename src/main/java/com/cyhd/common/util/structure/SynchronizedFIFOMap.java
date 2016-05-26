package com.cyhd.common.util.structure;

public class SynchronizedFIFOMap<K,V> extends EliminatedMap<K,V> {

	private static final long serialVersionUID = 6003013524933148274L;

	public SynchronizedFIFOMap(int maxCapacity) {
		this(maxCapacity, true);
	}
	
	public SynchronizedFIFOMap(int maxCapacity, boolean fixedCapacity) {
		super(maxCapacity, false, fixedCapacity);
	}
	
	@SuppressWarnings("unchecked")
	static final <K,V> SynchronizedFIFOMap<K,V>[] newArray(int sz) {
	    return new SynchronizedFIFOMap[sz];
	}
	
	public synchronized V get(Object key) {
		return super.get(key);
	}
	
	public synchronized V put(K key, V value) {
		return super.put(key, value);
	}
	
	public synchronized V remove(Object key) {
		return super.remove(key);
	}
	
	@Override   
	public synchronized boolean containsKey(Object key) {
		return super.containsKey(key);
	}
}
