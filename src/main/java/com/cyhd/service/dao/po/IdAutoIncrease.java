package com.cyhd.service.dao.po;

public class IdAutoIncrease {
	
	private long id;
	
	private long mark;
	
	
	public IdAutoIncrease(){
		
	}
	
	public IdAutoIncrease(long mark){
		this.mark = mark;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMark() {
		return mark;
	}

	public void setMark(long mark) {
		this.mark = mark;
	}
	
	

}
