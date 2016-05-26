package com.cyhd.common.util;

public class Pair<E,T> {
	
	public E first;
	public T second;
	
	public Pair(){
		
	}
	
	public Pair(E f, T t){
		first = f;
		second = t;
	}
	
	public E getFirst() {
		return first;
	}
	public void setFirst(E first) {
		this.first = first;
	}
	public T getSecond() {
		return second;
	}
	public void setSecond(T second) {
		this.second = second;
	}
	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}
	
	
}
