package com.cyhd.service.vo;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;

import java.util.HashMap;
import java.util.Map;

public class AppTaskVo {

	private AppTask appTask;
	private App app;
	private int status;
	
	private String proStatusText;
	
	public String getStatusText() {
		switch(this.getStatus()) {
		case 1: return "未完成"; 
		case 2: return "已完成"; 
		case 3:return "进行中";
		default:
			return "已过期";
		}
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public AppTask getAppTask() {
		return appTask;
	}
	public void setAppTask(AppTask appTask) {
		this.appTask = appTask;
	}
	public App getApp() {
		return app;
	}
	public void setApp(App app) {
		this.app = app;
	}
	public String getProStatusText() {
		return proStatusText;
	}
	public void setProStatusText(String proStatusText) {
		this.proStatusText = proStatusText;
	}

	/**
	 * 给分发渠道的app信息
	 * @return
	 */
	public Map<String, Object> getChannelTaskInfo(){
		Map<String, Object> params = new HashMap<>();
		params.put("adid", app.getAdid());
		params.put("title", app.getName());
		params.put("icon", app.getIcon());
		params.put("app_desc", app.getDescription());
		params.put("process_name", app.getProcess_name());
		params.put("bundle_id", app.getBundle_id());
		params.put("scheme", app.getAgreement());
		params.put("download_size", app.getDownload_size());
		params.put("appstore_id", app.getAppstore_id());
		params.put("url", app.getUrl());

		//任务相关
		params.put("search_word", appTask.getKeywords());
		params.put("task_desc", appTask.getDescription());
		params.put("price", appTask.getAmount());
		params.put("remain_num", appTask.getLeftTasks());
		params.put("duration", appTask.getDuration());
		params.put("search_rank", appTask.getCurrent_rank());
		params.put("task_type", (appTask.isVendorTask() && !appTask.isDirectReward())? 2 : 1);  //1：激活上报任务，2：回调任务

		//付费应用
		if (app.isPayWay()){
			params.put("is_paid", true);
			params.put("pay_amount", app.getPay_money());
		}

		return params;
	}
	
}
