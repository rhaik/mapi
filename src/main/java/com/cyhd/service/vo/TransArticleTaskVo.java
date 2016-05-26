package com.cyhd.service.vo;

import com.cyhd.service.dao.po.TransArticle;
import com.cyhd.service.dao.po.TransArticleTask;
import com.cyhd.service.dao.po.UserArticleTask;

public class TransArticleTaskVo {

	private TransArticleTask task;
	private TransArticle article;
	private int status;
	private UserArticleTask userTask;
	
	public TransArticleTask getTask() {
		return task;
	}
	public void setTask(TransArticleTask task) {
		this.task = task;
	}
	public TransArticle getArticle() {
		return article;
	}
	public void setArticle(TransArticle article) {
		this.article = article;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public UserArticleTask getUserTask() {
		return userTask;
	}
	public void setUserTask(UserArticleTask userTask) {
		this.userTask = userTask;
	}
	
	
}
