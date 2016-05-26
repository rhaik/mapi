package com.cyhd.common.util;
/**
 * Idle access object holds only one element, it can
 * record the time of each access to the element. If
 * the current time of touching the element is faraway
 * from the last access time, then the element expires,
 * and the result is null.
 */
public class IdleAccess<E> {

	private final int ttiInMillis;
	private final E element;
	private long lastAccessTime;
	
	public IdleAccess(int ttiInMillis, E element) {
		this.ttiInMillis = ttiInMillis;
		this.element = element;
		touch();
	}
	
	public synchronized E getElement() {
		long currentTime = System.currentTimeMillis();
		if(currentTime - lastAccessTime > ttiInMillis) {
			return null;
		} else {
			lastAccessTime = currentTime;
			return element;
		}
	}
	
	public synchronized void touch() {
		lastAccessTime = System.currentTimeMillis();
	}
	
	public E getElementIfNecessary() {
		return element;
	}

}
