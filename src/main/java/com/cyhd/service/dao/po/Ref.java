package com.cyhd.service.dao.po;

public class Ref<T extends Object> extends Object{
	private T obj;
	public T getValue(){
		return obj;
	}
	public void setValue(T obj){
		this.obj = obj;
	}
	
	@Override
	public java.lang.String toString(){
		return this.obj.toString();
	}	
}