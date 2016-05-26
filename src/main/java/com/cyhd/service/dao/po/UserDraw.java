package com.cyhd.service.dao.po;

public class UserDraw {

	private int user_id;
	private int total_times;
	private int balance_times;
	
	private int activity_id;

	public int getIncre_times(){
		return this.total_times - this.balance_times;
	}
	
	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getTotal_times() {
		return total_times;
	}

	public void setTotal_times(int total_times) {
		this.total_times = total_times;
	}

	public int getBalance_times() {
		return balance_times;
	}

	public void setBalance_times(int balance_times) {
		this.balance_times = balance_times;
	}

	public int getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(int activity_id) {
		this.activity_id = activity_id;
	}
	
	
}
