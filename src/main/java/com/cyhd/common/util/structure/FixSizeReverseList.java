package com.cyhd.common.util.structure;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FixSizeReverseList<T> {

	private volatile int size = 0;
	
	private int max_size = 0;
	
	private ConcurrentLinkedDeque<T> queue = new ConcurrentLinkedDeque<T>();
	
	public FixSizeReverseList(int maxSize){
		if(maxSize <= 0){
			throw new RuntimeException("maxsize must be a positive number!");
		}
		this.max_size = maxSize;
	}
	
	public synchronized void add(T t){
		this.queue.offer(t);
		size++;
		if(size > max_size){
			this.queue.pop();
		}
	}

	public int size(){
		return size;
	}
	
	public Iterator<T> iterator(){
		return queue.descendingIterator();
	}
	
}
