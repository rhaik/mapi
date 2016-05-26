package com.cyhd.service.dao.po;

import java.util.Date;

public class UserInstalledApp implements Cloneable {

	private int id;
	private int user_id;
	private int app_id;
	private String did;
	private Date createtime;
	private String agreement;
	
	public UserInstalledApp() {}

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

	public int getApp_id() {
		return app_id;
	}

	public void setApp_id(int app_id) {
		this.app_id = app_id;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	
	@Override
	public String toString() {
		return "UserInstalledApp [id=" + id + ", user_id=" + user_id
				+ ", app_id=" + app_id + ", did=" + did + ", createtime="
				+ createtime + "]";
	}

	@Override
	public UserInstalledApp clone()  {
		UserInstalledApp app = null;
		try {
			app =  (UserInstalledApp) super.clone();
		} catch (CloneNotSupportedException e) {
			app = new UserInstalledApp();
			app.setUser_id(getUser_id());
		}
		return app;
	}
	public static void main(String[] args) throws Exception {
		UserInstalledApp app = new UserInstalledApp();
		app.setApp_id(1);
		System.out.println(app);
		UserInstalledApp app2 = app.clone();
		System.out.println(app2);
		
	}

	public String getAgreement() {
		return agreement;
	}

	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}
}
