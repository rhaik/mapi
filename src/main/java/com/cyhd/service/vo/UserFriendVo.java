package com.cyhd.service.vo;
 
import java.util.Date;

import com.cyhd.service.dao.po.User;
 

public class UserFriendVo {

	private User user;
	private User friend;
	private Date invi_time;
	private boolean displayDate = false;
	public void setDisplayDate(boolean displayDate) {
		this.displayDate = displayDate;
	}
	public boolean getDisplayDate() {
		return this.displayDate;
	}
	public Date getInvi_time() {
		return invi_time;
	}
	public void setInvi_time(Date invi_time) {
		this.invi_time = invi_time;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public User getFriend() {
		return friend;
	}
	public void setFriend(User friend) {
		this.friend = friend;
	}
}
