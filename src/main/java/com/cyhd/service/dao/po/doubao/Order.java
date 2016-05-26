package com.cyhd.service.dao.po.doubao;

import java.io.Serializable;
import java.util.Date;

/**
 * 表示用户一次支付购买行为<br/>
 * 可以是参与夺宝，也可能是其他商品信息，通过订单类型order_type字段区分订单类型
 */
public class Order implements Serializable {

	private static final long serialVersionUID = 401861482548099220L;

	private int id;
	private long order_sn;		 //订单编号
	private int user_id;
	private int order_type; 	//订单类型，不同的订单类型对应不同的处理方式，现在夺宝订单为1
	private int pay_type;		//支付方式，现在余额支付类型为1
	private int total_amount; 	//总金额
	private int pay_amount; 	//支付金额
	private int status; 	 	//订单状态(1:待审核2:已确认3:不通过4:已完成)
	private int pay_status; 	//'支付状态(1:已支付0:未支付)'
	private Date createtime;
	private Date paytime;		//支付时间
	private Date check_time;
	private int source; //订单来源
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(int total_amount) {
		this.total_amount = total_amount;
	}

	public int getPay_amount() {
		return pay_amount;
	}

	public void setPay_amount(int pay_amount) {
		this.pay_amount = pay_amount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPay_status() {
		return pay_status;
	}

	public void setPay_status(int pay_status) {
		this.pay_status = pay_status;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getCheck_time() {
		return check_time;
	}

	public void setCheck_time(Date check_time) {
		this.check_time = check_time;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public long getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(long order_sn) {
		this.order_sn = order_sn;
	}

	public int getOrder_type() {
		return order_type;
	}

	public void setOrder_type(int order_type) {
		this.order_type = order_type;
	}

	public int getPay_type() {
		return pay_type;
	}

	public void setPay_type(int pay_type) {
		this.pay_type = pay_type;
	}

	public Date getPaytime() {
		return paytime;
	}

	public void setPaytime(Date paytime) {
		this.paytime = paytime;
	}
}
