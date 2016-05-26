package com.cyhd.service.dao.po;

public class EffectiveInvite {

	private int user;
	
	private int num;
	
	private String day;
	
	public EffectiveInvite() {
	}
	
	public EffectiveInvite(int user, String day) {
		this.user = user;
		this.day = day;
		this.num = 1;
	}

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	@Override
	public String toString() {
		return "EffectiveInvite [user=" + user + ", num=" + num + ", day=" + day + "]";
	}
	
	
}
