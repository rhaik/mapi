package com.cyhd.service.dao.po.doubao;

import java.io.Serializable;
import java.util.Date;

public class ProductLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private int product_id;
	private int operator;
	private String operation_content;
	private String remarks;
	private Date operator_time; 
	
	   
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getProduct_id() {
		return product_id;
	}


	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}


	public int getOperator() {
		return operator;
	}


	public void setOperator(int operator) {
		this.operator = operator;
	}


	public String getOperation_content() {
		return operation_content;
	}


	public void setOperation_content(String operation_content) {
		this.operation_content = operation_content;
	}


	public String getRemarks() {
		return remarks;
	}


	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	public Date getOperator_time() {
		return operator_time;
	}


	public void setOperator_time(Date operator_time) {
		this.operator_time = operator_time;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
