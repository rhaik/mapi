package com.cyhd.common.util.structure;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantSortedValueMap<K,V> extends SortedValueMap<K,V> {

	private static final long serialVersionUID = 3819005279499520910L;
	
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public ReentrantSortedValueMap(int initialCapacity, float loadFactor, Comparator<? super V> comparator, boolean naturalPutOrder, boolean outputOrder) {
        super(initialCapacity, loadFactor, comparator, naturalPutOrder, outputOrder);
    }

    public int size() {
    	 r.lock();
         try {
        	 return super.size();
         } finally {
         	r.unlock();
         }
    }
    
    public V get(Object key) {
        r.lock();
        try {
        	return super.get(key);
        } finally {
        	r.unlock();
        }
    }

    public boolean containsKey(Object key) {
        r.lock();
        try {
        	return super.containsKey(key);
        } finally {
        	r.unlock();
        }
    }

    public V put(K key, V value) {
    	w.lock();
    	try {
    		return super.put(key, value);
    	} finally {
    		w.unlock();
    	}
    }
    public V putIfAbsent(K key, V value) {
    	w.lock();
    	try {
    		return super.putIfAbsent(key, value);
    	} finally {
    		w.unlock();
    	}
    }

    public V remove(Object key) {
    	w.lock();
    	try {
	        return super.remove(key);
    	} finally {
        	w.unlock();
        }
    }

    public void clear() {
    	w.lock();
     	try {
	        super.clear();
     	} finally {
        	w.unlock();
        }
    }

    public boolean containsValue(Object value) {
		r.lock();
     	try {
			return super.containsValue(value);
     	} finally {
        	r.unlock();
        }
    }
    
    public Collection<Entry<K,V>> toEntryCollection() {
    	r.lock();
    	try {
    		return super.toEntryCollection();
    	} finally {
    		r.unlock();
    	}
    }
    
    public Collection<Entry<K,V>> toSortedCollection() {
    	r.lock();
    	try {
    		return super.toSortedCollection();
    	} finally {
    		r.unlock();
    	}
    }
    
    public Collection<V> toValueCollection() {
    	r.lock();
    	try {
    		return super.toValueCollection();
    	} finally {
    		r.unlock();
    	}
    }
    
    public Object[] toValueArray() {
    	return toValueCollection().toArray();
    }
    
    public <T> T[] toValueArray(T[] a) {
    	return toValueCollection().toArray(a);
    }
    
    public Collection<V> toReverseValueCollection() {
    	r.lock();
    	try {
    		return super.toReverseValueCollection();
    	} finally {
    		r.unlock();
    	}
    }
    
    public Object[] toReverseValueArray() {
    	return toReverseValueCollection().toArray();
    }
    
    public <T> T[] toReverseValueArray(T[] a) {
    	return toReverseValueCollection().toArray(a);
    }
    

    // TO BE thread-safe for following methods
    protected Entry<K,V> firstEntry() {
    	r.lock();
    	try {
    		return super.firstEntry();
    	} finally {
    		r.unlock();
    	}
    }
    
    protected Entry<K,V> lastEntry() {
    	r.lock();
    	try {
    		return super.lastEntry();
    	} finally {
    		r.unlock();
    	}
    }
    
    protected Entry<K,V> forward(Entry<K,V> c) {
    	r.lock();
    	try {
    		return super.forward(c);
    	} finally {
    		r.unlock();
    	}
    }

}
