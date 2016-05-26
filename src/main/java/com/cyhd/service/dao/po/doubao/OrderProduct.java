package com.cyhd.service.dao.po.doubao;

import com.cyhd.common.util.StringUtil;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户参与夺宝的订单记录，存储用户，夺宝活动id，夺宝商品id以及夺宝次数等信息<br/>
 * 夺宝活动中奖后，收货地址/发货状态在这里操作，不使用Order里相关的信息
 */
public class OrderProduct implements Serializable {

	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private long order_sn; //对应支付订单的信息
	private int user_id;
	private int product_id;
	private int product_activity_id;
	private int number;  //参与的次数
	private int price;
	private Date createtime; 
	private String ip;
	private String ip_area;
	private int lottery; //是否中奖,0或者1
	private int share;  //是否已分享
	private int status;  //状态(1:进行中2:已揭晓3:已过期)
	private int refund; //是否已退款
	private Date refund_time; //退款时间

	//收货地址
	private String consignee;
	private String consignee_mobile;
	private String address;
	private Date consignee_time;

	//发货状态
	private Date shipping_time;
	private String shipping;  //快递方式
	private String shipping_sn; //快递单号
	private Date sign_time;
	private int shipping_status;  //物流状态(1:待完善信息2:已确认3:已发货4:已签收

	/**
	 * 订单是不是等待签收
	 * @return
	 */
	public boolean isWaitingConfirm(){
		return shipping_status == 3;
	}

	public String getConsignee() {
		return consignee;
	}


	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}


	public String getConsignee_mobile() {
		return consignee_mobile;
	}


	public void setConsignee_mobile(String consignee_mobile) {
		this.consignee_mobile = consignee_mobile;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public Date getConsignee_time() {
		return consignee_time;
	}


	public void setConsignee_time(Date consignee_time) {
		this.consignee_time = consignee_time;
	}


	public Date getShipping_time() {
		return shipping_time;
	}


	public void setShipping_time(Date shipping_time) {
		this.shipping_time = shipping_time;
	}


	public void setOrder_sn(long order_sn) {
		this.order_sn = order_sn;
	}

	public String getShipping() {
		return shipping;
	}

	public void setShipping(String shipping) {
		this.shipping = shipping;
	}

	public String getShipping_sn() {
		return shipping_sn;
	}


	public void setShipping_sn(String shipping_sn) {
		this.shipping_sn = shipping_sn;
	}


	public Date getSign_time() {
		return sign_time;
	}


	public void setSign_time(Date sign_time) {
		this.sign_time = sign_time;
	}


	public int getShipping_status() {
		return shipping_status;
	}


	public void setShipping_status(int shipping_status) {
		this.shipping_status = shipping_status;
	}


	public int getShare() {
		return share;
	}


	public void setShare(int share) {
		this.share = share;
	}


	public int getLottery() {
		return lottery;
	}


	public void setLottery(int lottery) {
		this.lottery = lottery;
	}
	public boolean isLottery() {
		return this.lottery == 1;
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


	public int getUser_id() {
		return user_id;
	}


	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}


	public int getProduct_id() {
		return product_id;
	}


	public void setProduct_id(int product_id) {
		this.product_id = product_id;
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


	public int getPrice() {
		return price;
	}


	public void setPrice(int price) {
		this.price = price;
	}


	public Date getCreatetime() {
		return createtime;
	}


	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public String getIp_area() {
		return ip_area;
	}


	public void setIp_area(String ip_area) {
		this.ip_area = ip_area;
	}
	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public int getRefund() {
		return refund;
	}


	public void setRefund(int refund) {
		this.refund = refund;
	}


	public Date getRefund_time() {
		return refund_time;
	}


	public void setRefund_time(Date refund_time) {
		this.refund_time = refund_time;
	}

	
	public String getHideIp() {
		if(StringUtil.isNotBlank(ip) && ip.lastIndexOf('.') > 0) {
			return ip.substring(0, ip.lastIndexOf('.'))+".***";
		}
		return "";
	}

	public String getShippingDesc() {
		String desc = "";
		switch (shipping_status){
			case 1:
				break;
			case 2:
				desc = "待发货";
				break;
			case 3:
				desc = "待收货";
				break;
			case 4:
				desc = (share == 0) ? "待晒单" : "已完成";
				break;
		}
		return desc;
	}
}
