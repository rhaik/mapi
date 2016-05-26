package com.cyhd.service.vo;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserDrawLog;

public class UserDrawLogVo {

	private User user;
	
	private UserDrawLog userDrawLog;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserDrawLog getUserDrawLog() {
		return userDrawLog;
	}

	public void setUserDrawLog(UserDrawLog userDrawLog) {
		this.userDrawLog = userDrawLog;
	}
	

}
