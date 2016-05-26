package com.cyhd.common.util.structure;

import java.util.Comparator;

public class SortedValueCache<K,V> extends SortedValueMap<K,V> {

	private static final long serialVersionUID = -1033794228690382622L;

	private final int maxCapacity;
	private final boolean truncateLast;
	
	public SortedValueCache(int maxCapacity, Comparator<? super V> comparator, boolean naturalPutOrder, boolean outputOrder) {
		this(maxCapacity, comparator, naturalPutOrder, outputOrder, true);
	}
	
	public SortedValueCache(int maxCapacity, Comparator<? super V> comparator, boolean naturalPutOrder, boolean outputOrder, boolean truncateLast) {
		super(maxCapacity, 2f/*ensure no resize*/, comparator, naturalPutOrder, outputOrder);
		this.maxCapacity = maxCapacity;
		this.truncateLast = truncateLast;
	}
	
	public SortedValueCache(int maxCapacity, Comparator<? super V> comparator, boolean naturalPutOrder, boolean outputOrder, boolean truncateLast, boolean fixedCapacity) {
		super(fixedCapacity ? maxCapacity : DEFAULT_INITIAL_CAPACITY, fixedCapacity ? 2f : DEFAULT_LOAD_FACTOR, comparator, naturalPutOrder, outputOrder);
		this.maxCapacity = maxCapacity;
		this.truncateLast = truncateLast;
	}
	
	@Override
	/* return null if max capacity not reached, 
	 * otherwise, return the value that's removed
	 */
	public V put(K key, V value) {
		super.put(key, value);
		if (size() > maxCapacity) {
			return super.remove(truncateLast ? lastKey() : firstKey());
		} 
		return null;
    }
	
	@SuppressWarnings("serial")
	public static class Ascending<K, V> extends SortedValueCache<K,V> {
		public Ascending(int maxCapacity, Comparator<? super V> comparator) {
			super(maxCapacity, comparator, true, true);
		}
	}
	
	@SuppressWarnings("serial")
	public static class Descending<K, V> extends SortedValueCache<K,V> {
		public Descending(int maxCapacity, Comparator<? super V> comparator) {
			super(maxCapacity, comparator, true, false);
		}
		
		public Descending(int maxCapacity, Comparator<? super V> comparator, boolean fixedCapacity) {
			super(maxCapacity, comparator, true, false, true, fixedCapacity);
		}
	}

}
