package com.cyhd.service.dao.po;

import java.util.Date;

import org.springframework.web.util.HtmlUtils;

public class UserMessagePage {
	
	private int id;
	private String title;
	private String abstracts;
	private String abstracts_picture;
	private String content;
	private Date createtime;
	private int estate;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAbstracts() {
		return abstracts;
	}
	public void setAbstracts(String abstracts) {
		this.abstracts = abstracts;
	}
	public String getAbstracts_picture() {
		return abstracts_picture;
	}
	public void setAbstracts_picture(String abstracts_picture) {
		this.abstracts_picture = abstracts_picture;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getHtmlContent(){
		return HtmlUtils.htmlUnescape(content);
	}
	
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public int getEstate() {
		return estate;
	}
	public void setEstate(int estate) {
		this.estate = estate;
	}
}
