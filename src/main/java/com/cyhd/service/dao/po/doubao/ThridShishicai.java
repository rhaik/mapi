package com.cyhd.service.dao.po.doubao;

import java.io.Serializable;
import java.util.Date;

public class ThridShishicai implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private int periods;
	private String lottery_number; 
	private Date lottery_time;
	  
 

	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public int getPeriods() {
		return periods;
	}



	public void setPeriods(int periods) {
		this.periods = periods;
	}



	public String getLottery_number() {
		return lottery_number;
	}



	public void setLottery_number(String lottery_number) {
		this.lottery_number = lottery_number;
	}



	public Date getLottery_time() {
		return lottery_time;
	}



	public void setLottery_time(Date createtime) {
		this.lottery_time = createtime;
	}

	@Override
	public String toString() {
		return "ThridShishicai{" +
				"id=" + id +
				", periods=" + periods +
				", lottery_number='" + lottery_number + '\'' +
				", lottery_time=" + lottery_time +
				'}';
	}
}
