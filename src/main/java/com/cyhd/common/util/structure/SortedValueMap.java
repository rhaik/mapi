package com.cyhd.common.util.structure;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SortedValueMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Serializable {
	
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    public static final int MAXIMUM_CAPACITY = 1 << 30;
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private transient Entry<K,V>[] table;
    private transient int size;
    
    private int threshold;
    private final float loadFactor;

    private transient Entry<K,V> head;
    private transient Entry<K,V> tail;
    private final Comparator<? super V> comparator;
    
    private final boolean naturalPutOrder;
    private final boolean outputOrder;
    /*
     * naturalPutOrder, true if values are put into this map in the ascending order according to the comparator
     * outputOrder, true if values() should be ascending order according to the comparator  
     */
    public SortedValueMap(int initialCapacity, float loadFactor, Comparator<? super V> comparator, boolean naturalPutOrder, boolean outputOrder) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        }
        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;

        this.loadFactor = loadFactor;
        threshold = (int)(capacity * loadFactor);
        table = Entry.newArray(capacity);
        this.comparator = comparator;
        this.naturalPutOrder = naturalPutOrder;
        this.outputOrder = outputOrder;
    }

    /*
     * e1ΪҪ�����Ԫ�أ����e1�ϴ��򵱷���ֵ����0ʱЧ�����
     * ��Ҫ��comparator��ʵ�����ϸ�ʹ��:   return e1 - e2;
     */
    @SuppressWarnings("unchecked")
    private int compare(Entry<K,V> e1, Entry<K,V> e2) {
    	int k = ((Comparable<K>)e1.key).compareTo(e2.key);
    	if(k == 0 || comparator == null) return k;
    	int v = comparator.compare(e1.value, e2.value);
        return v == 0 ? k : v;
    }
    
	private void insertIntoSortedList(Entry<K,V> e) {
    	if ( outputOrder ) {
    		if (naturalPutOrder) {
    			addToTail4Asc(e);
    		} else {
    			addToHead4Asc(e);
    		}
    	} else {
    		if (naturalPutOrder) {
    			addToHead4Desc(e);
    		} else {
    			addToTail4Desc(e);
    		}
    	}
    }
    
    // ��ͷ������
    private  void addToHead4Asc(Entry<K,V> e) {
    	e.forward = null;
    	e.backward = null;
    	
    	if(head == null) { head = tail = e; return; }
    	
    	Entry<K,V> h = head;
    	while(h != null) {
    		int r = compare(e, h);
    		if(r > 0) {
    			h = h.forward;
    		} else if(r == 0) {
    			return;
    		} else if(r < 0) {
    			// found
    			Entry<K,V> b = h.backward;
    			e.backward = b;
    			h.backward = e;
    			e.forward = h;
    			if(b == null) head = e;
    			else b.forward = e;
    			return;
    		}
    	}
    	
    	e.backward = tail;
    	tail.forward = e;
    	tail = e;
    }
    // ��ͷ������
    private  void addToHead4Desc(Entry<K,V> e) {
    	e.forward = null;
    	e.backward = null;
    	
    	if(head == null) { head = tail = e; return; }
    	
    	Entry<K,V> h = head;
    	while(h != null) {
    		int r = compare(e, h);
    		if(r < 0) {
    			h = h.forward;
    		} else if(r == 0) {
    			return;
    		} else if(r > 0) {
    			// found
    			Entry<K,V> b = h.backward;
    			e.backward = b;
    			h.backward = e;
    			e.forward = h;
    			if(b == null) head = e;
    			else b.forward = e;
    			return;
    		}
    	}
    	
    	e.backward = tail;
    	tail.forward = e;
    	tail = e;
    }

    // ��β������
    private void addToTail4Asc(Entry<K,V> e) {
    	e.forward = null;
    	e.backward = null;
    	
    	if(head == null) { head = tail = e; return; }
    	
    	Entry<K,V> b = tail;
    	while(b != null) {
    		int r = compare(e, b);
    		if(r < 0) {
    			b = b.backward;
    		} else if(r == 0) {
    			return;
    		} else if(r > 0) {
    			// found
    			Entry<K,V> f = b.forward;
    			e.forward = f;
    			b.forward = e;
    			e.backward = b;
    			if(f == null) tail = e;
    			else f.backward = e;
    			return;
    		}
    	}
    	
    	e.forward = head;
    	head.backward = e;
    	head = e;
    }
    private void addToTail4Desc(Entry<K,V> e) {
    	e.forward = null;
    	e.backward = null;
    	
    	if(head == null) { head = tail = e; return; }
    	
    	Entry<K,V> b = tail;
    	while(b != null) {
    		int r = compare(e, b);
    		if(r > 0) {
    			b = b.backward;
    		} else if(r == 0) {
    			return;
    		} else if(r < 0) {
    			// found
    			Entry<K,V> f = b.forward;
    			e.forward = f;
    			b.forward = e;
    			e.backward = b;
    			if(f == null) tail = e;
    			else f.backward = e;
    			return;
    		}
    	}
    	
    	e.forward = head;
    	head.backward = e;
    	head = e;
    }
    
    // ��������ɾ��
    private void removeFromSortedList(Entry<K,V> e) {
    	Entry<K,V> f = e.forward;
    	Entry<K,V> b = e.backward;
    	
    	if(b == null) head = f;
    	else b.forward = f;
    	
    	if(f == null) tail = b;
    	else f.backward = b;
    }
    
    public int size() {
         return size;
    }

    private static int hash(Object x) {
        int h = x.hashCode();

        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);
        return h;
    }

    private Entry<K,V> getEntry(Object key) {
    	if(key == null) return null;
    	int hash = hash(key);
        int i = indexFor(hash, table.length);
        Entry<K,V> e = table[i];
        while (e != null && !(e.hash == hash && eq(key, e.key)))
            e = e.next;
        return e == null ? null : e;
    }

    private static boolean eq(Object x, Object y) {
        return x == y || x.equals(y);
    }

    private static int indexFor(int h, int length) {
        return h & (length-1);
    }
    
    public V get(Object key) {
        Entry<K,V> e = getEntry(key);
	    return e == null ? null : e.value;
    }

    public boolean containsKey(Object key) {
        Entry<K,V> e = getEntry(key);
	    return e == null ? false : true;
    }

    public V put(K key, V value) {
    	return doPut(key, value, false);
    }
    public V putIfAbsent(K key, V value) {
    	return doPut(key, value, true);
    }
    private V doPut(K key, V value, boolean onlyIfAbsent) {
    	if (key == null || value == null) throw new NullPointerException();
    	
    	int hash = hash(key);
        int i = indexFor(hash, table.length);
        Entry<K,V> e = table[i];
        while (e != null && (e.hash != hash || !eq(key, e.key)))
            e = e.next;

        V oldValue = null;
        if (e != null) {
            oldValue = e.value;
            if (!onlyIfAbsent) {
            	// �޸�value֮ǰ���Ƚ�����б����Ƴ�
            	removeFromSortedList(e);
            	e.value = value;
            	// �޸ĺ����²���
            	insertIntoSortedList(e);
            }
        } else {
            e = table[i];
            table[i] = new Entry<K,V>(hash, key, value, e);
            insertIntoSortedList(table[i]);
            if (size++ >= threshold) resize(2 * table.length);
        }
        return oldValue;
    }
    
    private void resize(int newCapacity) {
        Entry<K,V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry<K,V>[] newTable = Entry.newArray(newCapacity);
        transfer(newTable);
        table = newTable;
        threshold = (int)(newCapacity * loadFactor);
    }

    private void transfer(Entry<K,V>[] newTable) {
        Entry<K,V>[] src = table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            Entry<K,V> e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    Entry<K,V> next = e.next;
                    int i = indexFor(e.hash, newCapacity);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }

    public V remove(Object key) {
	    return doRemove(key);
    }
    private V doRemove(Object key) {
    	if(key == null) return null;
    	
    	int hash = hash(key);
        int i = indexFor(hash, table.length);
        Entry<K,V> prev = table[i];
        Entry<K,V> e = prev;

        while (e != null) {
            Entry<K,V> next = e.next;
            if (e.hash == hash && eq(key, e.key)) {
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                removeFromSortedList(e);
                break;
            }
            prev = e;
            e = next;
        }
        return (e == null ? null : e.value);
    }
    
    @SuppressWarnings("unchecked")
	private Entry<K,V> removeMapping(Object o) {
        if (!(o instanceof Map.Entry))
            return null;

        Map.Entry<K,V> entry = (Map.Entry<K,V>)o;
        Object k = entry.getKey();
        if(k == null) return null;
        
        int hash = hash(k);
        int i = indexFor(hash, table.length);
        Entry<K,V> prev = table[i];
        Entry<K,V> e = prev;

        while (e != null) {
            Entry<K,V> next = e.next;
            if (e.hash == hash && e.equals(entry)) {
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                removeFromSortedList(e);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    public void clear() {
        Entry<K,V>[] tab = table;
        for (int i = 0; i < tab.length; i++)
            tab[i] = null;
        size = 0;
        head = tail = null;
    }
    //TODO: use sorted value list to optimize
    public boolean containsValue(Object value) {
		if (value == null) return false;
	
		Entry<K,V>[] tab = table;
	        for (int i = 0; i < tab.length ; i++)
	            for (Entry<K,V> e = tab[i] ; e != null ; e = e.next)
	                if (value.equals(e.value))
	                    return true;
		return false;
    }

    protected Entry<K,V> forward(Entry<K,V> c) {
    	if(c == null) return null;
    	return c.forward;
    }
    
    protected Entry<K,V> firstEntry() {
    	return head;
    }
    
    protected Entry<K,V> lastEntry() {
    	return tail;
    }
    
    public K firstKey() {
    	Entry<K,V> h = firstEntry();
    	return h == null ? null : h.key;
    }
    
    public K lastKey() {
    	Entry<K,V> t = lastEntry();
    	return t == null ? null : t.key;
    }
    
    public Collection<Entry<K,V>> toEntryCollection() {
		Collection<Entry<K,V>> c = new ArrayList<Entry<K,V>>();
        for(int i = 0; i < table.length; i++) {
            Entry<K,V> e = table[i];
            while(e != null) {
            	c.add(e);
            	e = e.next;
            }
        }
        return c;
    }
    
    public Collection<Entry<K,V>> toSortedCollection() {
		Collection<Entry<K,V>> c = new ArrayList<Entry<K,V>>();
    	Entry<K,V> e = head;
    	while (e != null) {
    		c.add(e);
        	e = e.forward;
    	}
        return c;
    }
    
    public Collection<V> toValueCollection() {
		Collection<V> c = new ArrayList<V>();
    	Entry<K,V> e = head;
    	while (e != null) {
    		c.add(e.value);
        	e = e.forward;
    	}
        return c;
    }
    
    public Object[] toValueArray() {
    	return toValueCollection().toArray();
    }
    
    public <T> T[] toValueArray(T[] a) {
    	return toValueCollection().toArray(a);
    }
    
    public Collection<V> toReverseValueCollection() {
    		Collection<V> c = new ArrayList<V>();
	    	Entry<K,V> e = tail;
	    	while (e != null) {
	    		c.add(e.value);
	        	e = e.backward;
	    	}
	        return c;
    }
    
    public Object[] toReverseValueArray() {
    	return toReverseValueCollection().toArray();
    }
    
    public <T> T[] toReverseValueArray(T[] a) {
    	return toReverseValueCollection().toArray(a);
    }
    
    protected static class Entry<K,V> implements Map.Entry<K,V> {
        final K key;
        V value;
        final int hash;
        Entry<K,V> next;
        Entry<K,V> forward;
        Entry<K,V> backward;

        Entry(int h, K k, V v, Entry<K,V> n) {
            value = v;
            next = n;
            key = k;
            hash = h;
        }
        
        public V setValue(V newValue) {
        	V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2)))
                    return true;
            }
            return false;
        }

        public int hashCode() {
            return key.hashCode() ^ value.hashCode();
        }

        public String toString() {
            return getKey() + "=" + getValue();
        }

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}
		
		@SuppressWarnings("unchecked")
		static final <K,V> Entry<K,V>[] newArray(int i) {
		    return new Entry[i];
		}
    }

    private abstract class HashIterator<E> implements Iterator<E> {
    	Entry<K,V> next;
    	Entry<K,V> current;
    	HashIterator() {
    		current = next = SortedValueMap.this.firstEntry();
    	}
        public boolean hasNext() {
            return next != null;
        }

        Entry<K,V> nextEntry() {
        	current = next;
        	next = SortedValueMap.this.forward(next);
        	return current;
        }

        public void remove() {
        	SortedValueMap.this.remove(current.getKey());
        }
    }

    private class ValueIterator extends HashIterator<V> {
        public V next() {
        	return nextEntry().getValue();
        }
    }

    private class KeyIterator extends HashIterator<K> {
        public K next() {
            return nextEntry().getKey();
        }
    }

    private class EntryIterator extends HashIterator<Map.Entry<K,V>> {
        public Map.Entry<K,V> next() {
            return nextEntry();
        }
    }

    // Subclass overrides these to alter behavior of views' iterator() method
    Iterator<K> newKeyIterator()   {
        return new KeyIterator();
    }
    Iterator<V> newValueIterator()   {
        return new ValueIterator();
    }
    Iterator<Map.Entry<K,V>> newEntryIterator()   {
        return new EntryIterator();
    }
    
    private transient Set<Map.Entry<K,V>> entrySet = null;
    private transient Set<K>        		keySet = null;
    private transient Collection<V>		values = null;
    
    public Set<K> keySet() {
        Set<K> ks = keySet;
        return (ks != null ? ks : (keySet = new KeySet()));
    }

    private class KeySet extends AbstractSet<K> {
        public Iterator<K> iterator() {
            return newKeyIterator();
        }
        public int size() {
            return SortedValueMap.this.size();
        }
        public boolean contains(Object o) {
            return containsKey(o);
        }
        public boolean remove(Object o) {
            return SortedValueMap.this.remove(o) != null;
        }
        public void clear() {
            SortedValueMap.this.clear();
        }
    }

    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null ? vs : (values = new Values()));
    }

    private class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return newValueIterator();
        }
        public int size() {
            return SortedValueMap.this.size();
        }
        public boolean contains(Object o) {
            return SortedValueMap.this.containsValue(o);
        }
        public void clear() {
            SortedValueMap.this.clear();
        }
        public Object[] toArray() {
            return SortedValueMap.this.toValueArray();
        }
        public <T> T[] toArray(T[] a) {
            return SortedValueMap.this.toValueArray(a);
        }
    }

    public Set<Map.Entry<K,V>> entrySet() {
        Set<Map.Entry<K,V>> es = entrySet;
        return es != null ? es : (entrySet = new EntrySet());
    }

    private class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public Iterator<Map.Entry<K,V>> iterator() {
            return newEntryIterator();
        }
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K,V> e = (Map.Entry<K,V>)o;
            Entry<K,V> candidate = getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }
        public boolean remove(Object o) {
            return SortedValueMap.this.removeMapping(o) != null;
        }
        public int size() {
            return SortedValueMap.this.size();
        }
        public void clear() {
            SortedValueMap.this.clear();
        }
    }

    private void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
		Iterator<Map.Entry<K,V>> i = entrySet().iterator();
	
		// Write out the threshold, loadfactor, and any hidden stuff
		s.defaultWriteObject();
	
		// Write out number of buckets
		s.writeInt(table.length);
	
		// Write out size (number of Mappings)
		s.writeInt(size);
	
	        // Write out keys and values (alternating)
		while (i.hasNext()) {
	            Map.Entry<K,V> e = i.next();
	            s.writeObject(e.getKey());
	            s.writeObject(e.getValue());
        }
    }

    private static final long serialVersionUID = 362498820763181265L;

    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s)
         throws IOException, ClassNotFoundException
    {
		// Read in the threshold, loadfactor, and any hidden stuff
		s.defaultReadObject();
	
		// Read in number of buckets and allocate the bucket array;
		int numBuckets = s.readInt();
		table = Entry.newArray(numBuckets);
	
		// Read in size (number of Mappings)
		int size = s.readInt();
	
		// Read the keys and values, and put the mappings in the HashMap
		for (int i=0; i<size; i++) {
		    K key = (K) s.readObject();
		    V value = (V) s.readObject();
		    put(key, value);
		}
    }
}
