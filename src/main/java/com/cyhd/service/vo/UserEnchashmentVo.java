package com.cyhd.service.vo;
 
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserEnchashment;
 

public class UserEnchashmentVo {

	private User user;
	private UserEnchashment userEnchashment;
	private boolean displayDate = false;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public UserEnchashment getUserEnchashment() {
		return userEnchashment;
	}
	public void setUserEnchashment(UserEnchashment userEnchashment) {
		this.userEnchashment = userEnchashment;
	}
	public void setDisplayDate(boolean displayDate) {
		this.displayDate = displayDate;
	}
	public boolean getDisplayDate() {
		return this.displayDate;
	}
}
