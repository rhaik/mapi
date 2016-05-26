package com.cyhd.service.vo;

//用户收入统计（首页统计）
public class UserIncomeStatsVo {

	private int currentMonthAmount; //当月收入
	private int lastMonthAmount; // 上月收入
	private int balance;	//账户余额
	
	private int yestodyAppAmount;  //昨日试用收入
	private int yestodyFriendAmount;  //昨日好友分成收入
	
	private int sevenDayAppAmount;  //最近7天试用收入
	private int sevenDayFriendAmount; //最近7填好友分成收入
	
	private int thirtyDayAppAmount; //最近30天试用收入
	private int thirtyDayFriendAmount;  //最近30天好友分成收入
	
	
	public int getCurrentMonthAmount() {
		return currentMonthAmount;
	}
	public void setCurrentMonthAmount(int currentMonthAmount) {
		this.currentMonthAmount = currentMonthAmount;
	}
	public int getLastMonthAmount() {
		return lastMonthAmount;
	}
	public void setLastMonthAmount(int lastMonthAmount) {
		this.lastMonthAmount = lastMonthAmount;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public int getYestodyAppAmount() {
		return yestodyAppAmount;
	}
	public void setYestodyAppAmount(int yestodyAppAmount) {
		this.yestodyAppAmount = yestodyAppAmount;
	}
	public int getYestodyFriendAmount() {
		return yestodyFriendAmount;
	}
	public void setYestodyFriendAmount(int yestodyFriendAmount) {
		this.yestodyFriendAmount = yestodyFriendAmount;
	}
	public int getSevenDayAppAmount() {
		return sevenDayAppAmount;
	}
	public void setSevenDayAppAmount(int sevenDayAppAmount) {
		this.sevenDayAppAmount = sevenDayAppAmount;
	}
	public int getSevenDayFriendAmount() {
		return sevenDayFriendAmount;
	}
	public void setSevenDayFriendAmount(int sevenDayFriendAmount) {
		this.sevenDayFriendAmount = sevenDayFriendAmount;
	}
	public int getThirtyDayAppAmount() {
		return thirtyDayAppAmount;
	}
	public void setThirtyDayAppAmount(int thirtyDayAppAmount) {
		this.thirtyDayAppAmount = thirtyDayAppAmount;
	}
	public int getThirtyDayFriendAmount() {
		return thirtyDayFriendAmount;
	}
	public void setThirtyDayFriendAmount(int thirtyDayFriendAmount) {
		this.thirtyDayFriendAmount = thirtyDayFriendAmount;
	}
	
	
	
}
