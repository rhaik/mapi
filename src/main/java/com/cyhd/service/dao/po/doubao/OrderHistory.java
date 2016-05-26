package com.cyhd.service.dao.po.doubao;

import java.io.Serializable;
import java.util.Date;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.NumberUtil;
import com.cyhd.service.util.EmojUtil;

/**
 * 用户订单记录历史，主要用来计算抽奖的号码
 */
public class OrderHistory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private int product_activity_id; //对应的活动id
	private long order_sn;  //订单编号
	private String order_time; //订单时间
	private long time_value; //根据时间计算出来的数值
	private int user_id;
	private String user_name;
	private Date createtime; 
	
	 
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getProduct_activity_id() {
		return product_activity_id;
	}


	public void setProduct_activity_id(int product_activity_id) {
		this.product_activity_id = product_activity_id;
	}


	public long getOrder_sn() {
		return order_sn;
	}


	public void setOrder_sn(long order_sn) {
		this.order_sn = order_sn;
	}

	public String getOrder_time() {
		return order_time;
	}

	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}

	public int getUser_id() {
		return user_id;
	}


	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}


	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public Date getCreatetime() {
		return createtime;
	}


	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public long getTime_value() {
		return time_value;
	}

	public void setTime_value(long time_value) {
		this.time_value = time_value;
	}


	public String getUniUserName(){
		if(user_name == null || user_name.trim().length() == 0){
			return "小赚";
		}
		return EmojUtil.toCommonString(user_name);
	}

	/**
	 * 根据订单的创建时间和订单号生成订单历史显示的时间和时间值
	 * @param createtime
	 * @param order_sn
	 */
	public void setOrderTimeAndValue(Date createtime, long order_sn){
		Date orderTime = createtime;
		//数据库中获取的时间不包含毫秒，从订单编号中获取毫秒值
		long orderTimeMillis = orderTime.getTime();
		if (orderTimeMillis % 1000 == 0){
			orderTimeMillis += order_sn % 1000;
			orderTime = new Date(orderTimeMillis);
		}
		setOrder_time(DateUtil.format(orderTime, "HH:mm:ss.SSS"));
		setTime_value(NumberUtil.safeParseLong(DateUtil.format(orderTime, "HHmmssSSS")));
	}
}
