package com.cyhd.service.dao.po;

import java.util.Date;

public class UserEnchashmentLog {

	private int id;
	private int user_enchashment_id;
	private int operator;
	private int type;
	private Date operator_time;
	private int status;
	private String remarks;
	private String reason;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_enchashment_id() {
		return user_enchashment_id;
	}
	public void setUser_enchashment_id(int user_enchashment_id) {
		this.user_enchashment_id = user_enchashment_id;
	}
	public int getOperator() {
		return operator;
	}
	public void setOperator(int operator) {
		this.operator = operator;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
