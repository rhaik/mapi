package com.cyhd.service.vo;

import com.cyhd.service.dao.po.AppChannel;
import com.cyhd.service.dao.po.AppTaskChannel;

public class AppTaskChannelVo {
	
	private AppTaskChannel appTaskChannel;
	private AppChannel appChannel;
	public AppTaskChannel getAppTaskChannel() {
		return appTaskChannel;
	}
	public void setAppTaskChannel(AppTaskChannel appTaskChannel) {
		this.appTaskChannel = appTaskChannel;
	}
	public AppChannel getAppChannel() {
		return appChannel;
	}
	public void setAppChannel(AppChannel appChannel) {
		this.appChannel = appChannel;
	} 
}
