package com.cyhd.service.dao.po;

import java.util.Date;

import com.cyhd.common.util.MoneyUtils;


public class UserIncomeLog {

	private long id;
	private int user_id;
	private long user_task_id;
	private int from_user;
	private int friend_level;  //好友级别（0：自己，1：徒弟，2：徒孙）
	private int action;
	private int amount;
	private int type;
	private Date operator_time;
	private String remarks;
	
	public int getFrom_user() {
		return from_user;
	}
	public void setFrom_user(int from_user) {
		this.from_user = from_user;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUser_task_id() {
		return user_task_id;
	}
	public void setUser_task_id(long user_task_id) {
		this.user_task_id = user_task_id;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public Date getOperator_time() {
		return operator_time;
	}
	public void setOperator_time(Date operator_time) {
		this.operator_time = operator_time;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getFriend_level() {
		return friend_level;
	}
	public void setFriend_level(int friend_level) {
		this.friend_level = friend_level;
	}
	public String getAmountYuan() {
		return MoneyUtils.fen2yuanS2(this.amount);
	}
	
	@Override
	public String toString() {
		return "UserIncomeLog [id=" + id + ", user_id=" + user_id + ", user_task_id=" + user_task_id + ", action=" + action + ", amount=" + amount + ", type="
				+ type + ", operator_time=" + operator_time + ", remarks=" + remarks + "]";
	}
	
	
}
