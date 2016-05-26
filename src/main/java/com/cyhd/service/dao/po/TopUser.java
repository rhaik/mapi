package com.cyhd.service.dao.po;

import java.util.Comparator;

public class TopUser implements Comparator<TopUser>{
	
	private int user_id;
	private int income;
	
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getIncome() {
		return income;
	}
	public void setIncome(int income) {
		this.income = income;
	}
	
	@Override
	public int compare(TopUser o1, TopUser o2) {
		return o2.getIncome() - o1.getIncome();
	}
	

}
