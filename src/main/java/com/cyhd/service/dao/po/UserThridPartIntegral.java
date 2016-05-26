package com.cyhd.service.dao.po;

import java.util.Date;


public class UserThridPartIntegral {

	private long id;
	
	private int user_id;
	
	private String adv_id;
	
	private String app_id;
	
	private String key;
	
	private String udid;
	
	private String open_udid;
	
	private float bill;
	
	private int points;
	
	private String ad_name;
	
	private int status;
	
	private String activate_time;
	
	private String order_id;
	
	private String random_code;
	
	private String ip;
	
	private int source;
	
	private Date createtime;
	
	private boolean displayDate = false;

	private int client_type;
	
	private int trade_type;
	
	private String itunes_id="";
	
	public boolean isDisplayDate() {
		return displayDate;
	}

	public void setDisplayDate(boolean displayDate) {
		this.displayDate = displayDate;
	}

	public UserThridPartIntegral() {}
	
	public UserThridPartIntegral(String adv_id, String app_id,
			String key, String udid, String open_udid, float bill, int points,
			String ad_name, int status, String activate_time, String order_id,
			String random_code, String ip, int source,int clientType) {
		this.adv_id = adv_id;
		this.app_id = app_id;
		this.key = key;
		this.udid = udid;
		this.open_udid = open_udid;
		this.bill = bill;
		this.points = points;
		this.ad_name = ad_name;
		this.status = status;
		this.activate_time = activate_time;
		this.order_id = order_id;
		this.random_code = random_code;
		this.ip = ip;
		this.source = source;
		this.client_type = clientType;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getAdv_id() {
		return adv_id;
	}

	public void setAdv_id(String adv_id) {
		this.adv_id = adv_id;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getOpen_udid() {
		return open_udid;
	}

	public void setOpen_udid(String open_udid) {
		this.open_udid = open_udid;
	}

	public float getBill() {
		return bill;
	}

	public void setBill(float bill) {
		this.bill = bill;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getAd_name() {
		return ad_name;
	}

	public void setAd_name(String ad_name) {
		this.ad_name = ad_name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getActivate_time() {
		return activate_time;
	}

	public void setActivate_time(String activate_time) {
		this.activate_time = activate_time;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getRandom_code() {
		return random_code;
	}

	public void setRandom_code(String random_code) {
		this.random_code = random_code;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(150);
		sb.append("UserThridPartIntegral [id=" ).append( id ).append( ", user_id=" ).append( user_id
				).append( ",adv_id=" ).append( adv_id ).append( ", app_id=" ).append( app_id ).append( ", key=" ).append( key
				).append( ",udid=" ).append( udid ).append( ", open_udid=" ).append( open_udid ).append( ", bill="
				).append( bill ).append( ", points=" ).append( points ).append( ", ad_name=" ).append( ad_name
				).append( ", status=" ).append( status ).append( ", activate_time=" ).append( activate_time
				).append( ", order_id=" ).append( order_id ).append( ", random_code=" ).append( random_code
				).append( ", ip=" ).append( ip ).append( ", source=" ).append( source ).append( ", createtime="
				).append( createtime ).append( ", displayDate=" ).append( displayDate ).append( "]");
		return sb.toString();
	}

	public int getClient_type() {
		return client_type;
	}

	public void setClient_type(int client_type) {
		this.client_type = client_type;
	}

	public int getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(int trade_type) {
		this.trade_type = trade_type;
	}

	public String getItunes_id() {
		return itunes_id;
	}

	public void setItunes_id(String itunes_id) {
		this.itunes_id = itunes_id;
	}

	
}
