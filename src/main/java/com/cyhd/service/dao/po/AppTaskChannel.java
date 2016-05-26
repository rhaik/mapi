package com.cyhd.service.dao.po;

import java.io.Serializable;

public class AppTaskChannel implements Serializable {
	 
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7080874465861872143L;
	
	private int id;
	private int task_id;
	private int app_channel_id;
	private String third_id;
	private int adid;

	private int needReport;
	
	//第三方应用的key和secret，目前美图渠道会用上
	private String third_app_key;
	private String third_app_secret;
	 
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTask_id() {
		return task_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public int getApp_channel_id() {
		return app_channel_id;
	}

	public void setApp_channel_id(int app_channel_id) {
		this.app_channel_id = app_channel_id;
	}

	public String getThird_id() {
		return third_id;
	}

	public void setThird_id(String third_id) {
		this.third_id = third_id;
	}
	public int getAdid() {
		return adid;
	}

	public void setAdid(int adid) {
		this.adid = adid;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getThird_app_key() {
		return third_app_key;
	}

	public void setThird_app_key(String third_app_key) {
		this.third_app_key = third_app_key;
	}

	public String getThird_app_secret() {
		return third_app_secret;
	}

	public void setThird_app_secret(String third_app_secret) {
		this.third_app_secret = third_app_secret;
	}

	public int getNeedReport() {
		return needReport;
	}

	public void setNeedReport(int needReport) {
		this.needReport = needReport;
	}
	
	public boolean isNeedReport(){
		return  needReport == 1;
	}
}
