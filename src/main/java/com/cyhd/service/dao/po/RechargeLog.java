package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

public class RechargeLog implements Serializable {  
	/**
	 * 
	 */
	private static final long serialVersionUID = 2586679669209800309L;
	private int id; 
	private int recharge_id;
	private int status;
	private Date createtime;
	private String remarks;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRecharge_id() {
		return recharge_id;
	}
	public void setRecharge_id(int recharge_id) {
		this.recharge_id = recharge_id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	 
	
}
