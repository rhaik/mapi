package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

public class UserTaskDianru implements Serializable {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 8034687388627162029L;
	
	private int id;
	private long user_task_id;
	private String hashid;
	private int appid;
	private int adid;
	private String adname;
	private Date createtime;
	private String userid;
	private String deviceid;
	private String source;
	private int point;
	private Date time;
	
	 
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public long getUser_task_id() {
		return user_task_id;
	}


	public void setUser_task_id(long user_task_id) {
		this.user_task_id = user_task_id;
	}


	public String getHashid() {
		return hashid;
	}


	public void setHashid(String hashid) {
		this.hashid = hashid;
	}


	public int getAppid() {
		return appid;
	}


	public void setAppid(int appid) {
		this.appid = appid;
	}


	public int getAdid() {
		return adid;
	}


	public void setAdid(int adid) {
		this.adid = adid;
	}


	public String getAdname() {
		return adname;
	}


	public void setAdname(String adname) {
		this.adname = adname;
	}


	public Date getCreatetime() {
		return createtime;
	}


	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}


	public String getUserid() {
		return userid;
	}


	public void setUserid(String userid) {
		this.userid = userid;
	}


	public String getDeviceid() {
		return deviceid;
	}


	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public int getPoint() {
		return point;
	}


	public void setPoint(int point) {
		this.point = point;
	}


	public Date getTime() {
		return time;
	}


	public void setTime(Date time) {
		this.time = time;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
