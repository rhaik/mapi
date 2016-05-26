package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

import com.cyhd.common.util.MoneyUtils;

public class RechargeDenomination implements Serializable { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5798040662220401403L;
	private int id;
	private int value;
	private int channel;
	private int pay_amount;
	private int state;
	private Date createtime;
	private int sort;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getPayAmountYuan() {
		return MoneyUtils.fen2yuanS2(pay_amount);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
