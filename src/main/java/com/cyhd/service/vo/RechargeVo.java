package com.cyhd.service.vo;

import java.io.Serializable;

import com.cyhd.service.dao.po.Recharge;

public class RechargeVo implements Serializable {  
	/**
	 * 
	 */
	private static final long serialVersionUID = -6987122580549378389L;
	 
	private Recharge recharge;
	
	private boolean displayDate;

	public Recharge getRecharge() {
		return recharge;
	}
	public void setRecharge(Recharge recharge) {
		this.recharge = recharge;
	}
	
	public void setDisplayDate(boolean displayDate) {
		this.displayDate = displayDate;
	}
	public boolean getDisplayDate() {
		return this.displayDate;
	}
	public String getStatusText() {
		switch(recharge.getStatus()){
		case 0: return "无效订单";
		case 1: return "充值中";
		case 2: return "充值成功";
		case 3: return "充值失败";
		}
		return "";
	}
	
}
