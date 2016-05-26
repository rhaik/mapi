package com.cyhd.service.dao.po.doubao;

import java.io.Serializable;
import java.util.Date;

public class OrderProductLottery implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private long order_sn;
	private int user_id;
	private int order_product_id;
	private int product_activity_id;
	private int number;
	private Date create_time;
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public long getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(long order_sn) {
		this.order_sn = order_sn;
	}

	public int getOrder_product_id() {
		return order_product_id;
	}


	public void setOrder_product_id(int order_product_id) {
		this.order_product_id = order_product_id;
	}


	public int getProduct_activity_id() {
		return product_activity_id;
	}


	public void setProduct_activity_id(int product_activity_id) {
		this.product_activity_id = product_activity_id;
	}


	public int getNumber() {
		return number;
	}


	public void setNumber(int number) {
		this.number = number;
	}



	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
}
