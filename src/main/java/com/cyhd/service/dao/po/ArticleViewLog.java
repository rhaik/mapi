package com.cyhd.service.dao.po;

import java.util.Date;

public class ArticleViewLog {

	private int id;
	private int article_id;
	private int task_user_id;
	private String task_user_unionid;
	private String view_unionid;
	private Date createtime;
	private String view_openid;
	private String ip; //记录浏览者ip地址
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getArticle_id() {
		return article_id;
	}
	public void setArticle_id(int article_id) {
		this.article_id = article_id;
	}
	public int getTask_user_id() {
		return task_user_id;
	}
	public void setTask_user_id(int task_user_id) {
		this.task_user_id = task_user_id;
	}
	public String getTask_user_unionid() {
		return task_user_unionid;
	}
	public void setTask_user_unionid(String task_user_unionid) {
		this.task_user_unionid = task_user_unionid;
	}
	public String getView_unionid() {
		return view_unionid;
	}
	public void setView_unionid(String view_unionid) {
		this.view_unionid = view_unionid;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getView_openid() {
		return view_openid;
	}
	public void setView_openid(String view_openid) {
		this.view_openid = view_openid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
