package com.cyhd.common.util;

/**
 * Live access object holds only one element, it can
 * record the create time of the element. If current
 * time of touching the element is far away from the
 * create time, then the element expires, and result
 * null.
 */
public class LiveAccess<E> implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int ttlInMillis;
	private final E element;
	private final long createTime;
	
	public LiveAccess(int ttlInMillis, E element) {
		this.ttlInMillis = ttlInMillis;
		this.element = element;
		this.createTime = System.currentTimeMillis();
	}
	
	public E getElement() {
		long currentTime = System.currentTimeMillis();
		if(ttlInMillis > 0 && currentTime - createTime > ttlInMillis) {
			return null;
		} else {
			return element;
		}
	}
	
	public E getElementIfNecessary() {
		return element;
	}

}
