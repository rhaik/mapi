package com.cyhd.common.util.structure;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @param <K>
 * @param <V>
 */
public class MapSet<K,V> {

	private final Map<K, LinkedHashSet<V>> map = new LinkedHashMap<K, LinkedHashSet<V>>();
	
	public void put(K key, V value){
		if (key == null || value == null) return ;
		LinkedHashSet<V> list = map.get(key);
		if (list == null) {
			synchronized (map) {
				list = map.get(key);
				if (list == null) {
					list = new LinkedHashSet<V>();
					map.put(key, list);
				}
			}
		}
		list.add(value);
	}
	
	public Set<V> getValueSet(K key){
		if (key == null) return null;
		return map.get(key);
	}
	
	public Set<K> getKeySet(){
		return map.keySet();
	}
}
