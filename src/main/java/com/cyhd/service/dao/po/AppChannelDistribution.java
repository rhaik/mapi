package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

public class AppChannelDistribution implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private String channel;
	private int channel_id; 
	private Date createtime;
	private int status;
	private String app_secret;
	
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public int getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getApp_secret() {
		return app_secret;
	}
	public void setApp_secret(String app_secret) {
		this.app_secret = app_secret;
	}
	
}
