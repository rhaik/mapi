package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

public class Recharge implements Serializable {  
	/**
	 * 
	 */
	private static final long serialVersionUID = -6987122580549378389L;
	private int id;
	private long order_sn;
	private int recharge_denomination_id;
	private String mobilephone;
	private String mobile_area;
	private int value;
	private int quantity;
	private int channel;
	private int pay_amount;
	private int user_id;
	private int status;
	private Date createtime;
	private String third_oid;
	private int total_price;
	
	private boolean displayDate;

	/**无效订单*/
	public static final int ORDER_STATUS_INVALID = 0;  
	/**充值中*/
	public static final int ORDER_STATUS_RECHARGE = 1;  
	/**成功**/
	public static final int ORDER_STATUS_SUCCESS = 2; 
	/**失败*/
	public static final int ORDER_STATUS_FAIL = 3;
	
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

	public int getRecharge_denomination_id() {
		return recharge_denomination_id;
	}

	public void setRecharge_denomination_id(int recharge_denomination_id) {
		this.recharge_denomination_id = recharge_denomination_id;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}
	
	public String getMobile_area() {
		return mobile_area;
	}

	public void setMobile_area(String mobile_area) {
		this.mobile_area = mobile_area;
	}
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getPay_amount() {
		return pay_amount;
	}

	public void setPay_amount(int pay_amount) {
		this.pay_amount = pay_amount;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
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

	public String getThird_oid() {
		return third_oid;
	}

	public void setThird_oid(String third_oid) {
		this.third_oid = third_oid;
	}

	public int getTotal_price() {
		return total_price;
	}

	public void setTotal_price(int total_price) {
		this.total_price = total_price;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getChannelName() {
		switch(this.channel) {
		case 1: return "移动";
		case 2: return "联通";
		case 3: return "电信";
		}
		return "";
	}
	public void setDisplayDate(boolean displayDate) {
		this.displayDate = displayDate;
	}
	public boolean getDisplayDate() {
		return this.displayDate;
	}
	
}
