package com.cyhd.common.util.job;

public class PriorityHandler<T> implements Comparable<PriorityHandler<T>>{
	
	JobHandler<T> handler;
	int priority;
	
	
	public PriorityHandler(JobHandler<T> handler, int priority) {
		super();
		this.handler = handler;
		this.priority = priority;
	}


	@Override
	public int compareTo(PriorityHandler<T> o) {
		return priority - o.priority;
	}
	
}
