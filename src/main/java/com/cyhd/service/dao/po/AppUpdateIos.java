package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;


public class AppUpdateIos implements Serializable {

	/**
	 */
	private static final long serialVersionUID = -7382206326857844791L;
	
	private int id;
	private String app_name;
	private String bundle_id;
	private String url_scheme;
	private String download_url;
	private String version;		 
	private int status; //是否有效，1为有效
	private Date createtime;
	private int download_num;
	private int weight;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getApp_name() {
		return app_name;
	}
	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}
	public String getBundle_id() {
		return bundle_id;
	}
	public void setBundle_id(String bundle_id) {
		this.bundle_id = bundle_id;
	}
	public String getUrl_scheme() {
		return url_scheme;
	}
	public void setUrl_scheme(String url_scheme) {
		this.url_scheme = url_scheme;
	}
	public String getDownload_url() {
		return download_url;
	}
	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public int getDownload_num() {
		return download_num;
	}
	public void setDownload_num(int download_num) {
		this.download_num = download_num;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	/**是不是钥匙*/
	public boolean isKeyApp(){
		return this.app_name != null && this.app_name.contains("钥匙");
	}
	
	@Override
	public String toString() {
		return "AppUpdateIos{" +
				"id=" + id +
				", app_name='" + app_name + '\'' +
				", bundle_id='" + bundle_id + '\'' +
				", download_url='" + download_url + '\'' +
				", download_num=" + download_num +
				'}';
	}
}
