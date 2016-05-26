package com.cyhd.service.dao.po;

import java.util.Date;

public class UserFriend {

	private int id;
	private int user_id;  //邀请人 
	private int friend;   //被邀请人
	private Date invi_time;
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
	public int getFriend() {
		return friend;
	}
	public void setFriend(int friend) {
		this.friend = friend;
	}
	public Date getInvi_time() {
		return invi_time;
	}
	public void setInvi_time(Date invi_time) {
		this.invi_time = invi_time;
	}
}
