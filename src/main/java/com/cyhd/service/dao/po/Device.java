package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

import com.cyhd.service.constants.Constants;

/**
 * Description:硬件设备实体类
 */
public class Device implements Serializable {
	
	private static final long serialVersionUID = -5495103758191095439L;

	private long id;
	
	private int estate; 
	
	private long userid;
	
	private String token; //token
	
	private int tokentype; //推送类型
	
	private int devicetype;
	
	private String devicemodel;
	
	private String appver;
	
	private int cityid;
	
	private int setting; // ios push方式  
	
	private Date createtime;
	
	private Date updatetime;
	
	private String bundle_id;
	
	public static final int TOKEN_TYPE_IOS = 0; 	//iOS
	public static final int TOKEN_TYPE_GETUI = 1; 	//个推
	public static final int TOKEN_TYPE_IXIN = 2; 	//爱心推送
	public static final int TOKEN_TYPE_TX = 3;   	//腾讯推送
	/**友盟*/
	public static final int TOKEN_TYPE_UM=4;
	public void setTokenTypeGetui(){
		this.tokentype = TOKEN_TYPE_GETUI;
	}
	public boolean isTokenGetui(){
		return this.tokentype == TOKEN_TYPE_GETUI;
	}
	public void setTokenTypeIxin(){
		this.tokentype = TOKEN_TYPE_IXIN;
	}
	public boolean isTokenIxin(){
		return this.tokentype == TOKEN_TYPE_IXIN;
	}
	public void setTokenUM(){
		this.tokentype = TOKEN_TYPE_UM;
	}
	public boolean isTokenUM(){
		return this.tokentype == TOKEN_TYPE_UM;
	}
	public void setTokenTypeTx(){
		this.tokentype = TOKEN_TYPE_TX;
	}
	public boolean isTokenTx(){
		return this.tokentype == TOKEN_TYPE_TX;
	}
	public boolean isIosDevice(){
		return this.getDevicetype() == Constants.platform_ios;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public boolean isBind(){
		return this.estate == Constants.ESTATE_Y;
	}

	public int getEstate() {
		return estate;
	}

	public void setEstate(int estate) {
		this.estate = estate;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(int devicetype) {
		this.devicetype = devicetype;
	}

	public int getCityid() {
		return cityid;
	}

	public void setCityid(int cityid) {
		this.cityid = cityid;
	}

	public int getSetting() {
		return setting;
	}

	public void setSetting(int setting) {
		this.setting = setting;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
	public String getDevicemodel() {
		return devicemodel;
	}

	public void setDevicemodel(String devicemodel) {
		this.devicemodel = devicemodel;
	}

	public String getAppver() {
		return appver;
	}

	public void setAppver(String appver) {
		this.appver = appver;
	}

	public int getTokentype() {
		return tokentype;
	}

	public void setTokentype(int tokentype) {
		this.tokentype = tokentype;
	}

	@Override
	public String toString() {
		return "Device [id=" + id + ", estate=" + estate + ", userid=" + userid + ", token=" + token + ", tokentype=" + tokentype + ", devicetype="
				+ devicetype + ", devicemodel=" + devicemodel + ", appver=" + appver + ", cityid=" + cityid + ", setting=" + setting + ", createtime="
				+ createtime + ", updatetime=" + updatetime + "]";
	}
	public String getBundle_id() {
		return bundle_id;
	}
	public void setBundle_id(String bundle_id) {
		this.bundle_id = bundle_id;
	}

}
