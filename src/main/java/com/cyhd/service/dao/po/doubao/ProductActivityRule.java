package com.cyhd.service.dao.po.doubao;

import java.io.Serializable;
import java.util.Date;

public class ProductActivityRule implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private int product_id;
	private String name;
	private String lable;
	private Date start_time; 
	private Date end_time; 
	private Date createtime; 
	private int stock;
	private int lock_stock;
	private int price;
	private int number;
	private int min_buy_number;
	private int vendor_price; //采购价格
	private int presell;
	private int status;   //状态(0:待审核，1:已经上线， 2:未上线， 3:审核失败)
	 

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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	public int getNumber() {
		return number;
	}


	public void setNumber(int number) {
		this.number = number;
	}


	public int getMin_buy_number() {
		return min_buy_number;
	}


	public void setMin_buy_number(int min_buy_number) {
		this.min_buy_number = min_buy_number;
	}

	public String getLable() {
		return lable;
	}


	public void setLable(String lable) {
		this.lable = lable;
	}


	public Date getStart_time() {
		return start_time;
	}


	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}


	public Date getEnd_time() {
		return end_time;
	}


	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}


	public Date getCreatetime() {
		return createtime;
	}


	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public int getLock_stock() {
		return lock_stock;
	}


	public void setLock_stock(int lock_stock) {
		this.lock_stock = lock_stock;
	}


	public int getStock() {
		return stock;
	}


	public void setStock(int stock) {
		this.stock = stock;
	}


	public int getPrice() {
		return price;
	}


	public void setPrice(int price) {
		this.price = price;
	}

	public int getPresell() {
		return presell;
	}


	public void setPresell(int presell) {
		this.presell = presell;
	}
	
	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}

	public int getVendor_price() {
		return vendor_price;
	}

	public void setVendor_price(int vendor_price) {
		this.vendor_price = vendor_price;
	}

	public boolean isHashStock() {
		return stock > lock_stock;
	}


	public boolean isAllowBuildNextPeriod() {
		Date now = new Date();
		return isHashStock() && status == 1 &&  (now.after(start_time) && now.before(end_time));
	}

	@Override
	public String toString() {
		return "ProductActivityRule{" +
				"lock_stock=" + lock_stock +
				", id=" + id +
				", name='" + name + '\'' +
				", product_id=" + product_id +
				", stock=" + stock +
				", price=" + price +
				", number=" + number +
				", vendor_price=" + vendor_price +
				", status=" + status +
				'}';
	}
}
