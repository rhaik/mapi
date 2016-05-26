package com.cyhd.service.dao.po.doubao;

import java.io.Serializable;
import java.util.Date;

public class ProductShareLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private int share_id;
	private int operator; 
	private Date operator_time;
	private int status;
	private String remarks;
	
	 

	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public int getShare_id() {
		return share_id;
	}



	public void setShare_id(int share_id) {
		this.share_id = share_id;
	}



	public int getOperator() {
		return operator;
	}



	public void setOperator(int operator) {
		this.operator = operator;
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



	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
