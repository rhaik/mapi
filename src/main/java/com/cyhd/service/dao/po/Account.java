package com.cyhd.service.dao.po;


public class Account {
	private int id;
	private int accountId;
	private String name;
	private String logo;
	private String wxappid;
	private String wxappsecret;
	private String wxaccesstoken;
	private int wxtokenfetchtime;
	private int wxtokenexpiretime;
	
	private String company;
	private String contact_person;
	private String telephone;
	
	private String host;
	
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWxappid() {
		return wxappid;
	}
	public void setWxappid(String wxappid) {
		this.wxappid = wxappid;
	}
	public String getWxappsecret() {
		return wxappsecret;
	}
	public void setWxappsecret(String wxappsecret) {
		this.wxappsecret = wxappsecret;
	}
	public String getWxaccesstoken() {
		return wxaccesstoken;
	}
	public void setWxaccesstoken(String wxaccesstoken) {
		this.wxaccesstoken = wxaccesstoken;
	}
	public int getWxtokenfetchtime() {
		return wxtokenfetchtime;
	}
	public void setWxtokenfetchtime(int wxtokenfetchtime) {
		this.wxtokenfetchtime = wxtokenfetchtime;
	}
	public int getWxtokenexpiretime() {
		return wxtokenexpiretime;
	}
	public void setWxtokenexpiretime(int wxtokenexpiretime) {
		this.wxtokenexpiretime = wxtokenexpiretime;
	}
	
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getContact_person() {
		return contact_person;
	}
	public void setContact_person(String contact_person) {
		this.contact_person = contact_person;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	@Override
	public String toString() {
		return "Account [id=" + id + ", name=" + name + ", wxappid=" + wxappid
				+ ", wxappsecret=" + wxappsecret + ", wxaccesstoken="
				+ wxaccesstoken + ", wxtokenfetchtime=" + wxtokenfetchtime
				+ ", wxtokenexpiretime=" + wxtokenexpiretime +"]";
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
}
