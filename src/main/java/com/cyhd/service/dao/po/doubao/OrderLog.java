package com.cyhd.service.dao.po.doubao;

import java.io.Serializable;
import java.util.Date;


/**
 * 订单操作日志
 */
public class OrderLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private Long order_sn;
	private int operator;
	private String operation_content;
	private Date operator_time;
	private int status;
	private int type;
	 

	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Long getOrder_sn() {
		return order_sn;
	}


	public void setOrder_sn(Long order_sn) {
		this.order_sn = order_sn;
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


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
