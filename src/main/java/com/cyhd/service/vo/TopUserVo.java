package com.cyhd.service.vo;

import java.util.Comparator;

import com.cyhd.service.dao.po.User;

public class TopUserVo implements Comparator<TopUserVo>{

	private User u;
	private String income;
	
	private int amount;
	
	public User getU() {
		return u;
	}
	public void setU(User u) {
		this.u = u;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	@Override
	public String toString() {
		return "TopUserVo [u=" + u + ", income=" + income + "]";
	}
	@Override
	public int compare(TopUserVo o1, TopUserVo o2) {
		if(o1.getU().getId() == o2.getU().getId()){
			return 0;
		}
		return o2.getAmount() - o1.getAmount();
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	
	
}
