package com.cyhd.service.vo;


import com.cyhd.common.util.DateUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.UserTask;

public class UserTaskVo {

	private AppTask appTask;
	private App app;
	private UserTask userTask;
	private boolean displayDate = false;
	//新增 是否安装过该app
	private boolean installedApp;
	//是否标亮
	
	public int getRequireType(){
		if(app.isPayWay()){
			return 1;
		}
		if(appTask.isShenDu()){
			return 2;
		}
		return 0;
	}

	//控制列表中背景颜色，valid的时候背景为亮色
	public boolean isValid(){
		if(isHasReceived()) { 	//已接任务
			return userTask.isValid();
		} else { 
			return appTask.isValid() && !isInstalledApp();
		}
	}
	//是否在进行中
	public boolean isApping(){
		return userTask!=null && userTask.isValid();
	}

	/**
	 * 是否在等待回调，即任务已完成，但是未获得奖励
	 * @return
	 */
	public boolean isWaitingCallback(){
		return userTask != null && !userTask.isTimeout() && userTask.isCompleted() && userTask.getReward() == 0;
	}
	
	//是否已接过任务
	public boolean isHasReceived(){
		return userTask != null && !userTask.isAborted();
	}
	//未接收过任务并且应用在起始时间范围内并且状态是正常，可用数量大于0
	public boolean isCanReceive(){
		return !isHasReceived() && appTask.isValid() && appTask.isHasLeftTasks() && !isInstalledApp();
	}
	
	public String getStatusText() {
		if(userTask == null || userTask.isAborted()){
			if(appTask.isValid()){
				if(isInstalledApp()){
					return "您已安装过该App";
				}
				if(appTask.isHasLeftTasks()){
					return "未开始";
				}else{
					return "无剩余";
				}
			}else{
				return "无剩余";
			}
		}
		if(userTask.isExpired()){
			return "已超时";
		}else if( userTask.getReward() > 0) { //已发放奖励
			return "已完成";
		}else if (userTask.isCompleted()){ //已完成，但是未发放奖励
			if (userTask.isTimeout()){
				return "审核未通过";
			}else {
				return "进行中";
			}
		}else{
			return "进行中";
		}
	}
	public String getTaskPromptText() {
		if(userTask == null || userTask.isAborted()){
			if(appTask.isValid()){
				if(isInstalledApp()){
					return "已安装过";
				}
				if(appTask.isHasLeftTasks()){
					return "";
				} else {
					return "任务已被抢光";
				}
			}else{
				return "任务已被抢光";
			}
		}
		if(userTask.isExpired()){
			return "任务已超时";
		}else{
			if(userTask.getReward() > 0) {
				return "已发放奖励";
			} else if(!userTask.isDownload()) {
				return appTask.isDirectDownload()? "等待安装应用" : "等待搜索下载";
			} else  if (userTask.isCompleted()){
				if (userTask.isTimeout()){
					return "审核未通过";
				}else {
					return "等待试用完成";
				}
			} else {
				return "等待试用完成";
			}
		}
		
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
	public UserTask getUserTask() {
		return userTask;
	}
	public void setUserTask(UserTask userTask) {
		this.userTask = userTask;
	}
	public void setDisplayDate(boolean displayDate) {
		this.displayDate = displayDate;
	}
	public boolean getDisplayDate() {
		return this.displayDate;
	}
	public String getCurrentDate() {
		return DateUtil.format(userTask.getStarttime(), "yyyy-MM-dd");
	}
	/**
	 * 获取任务过期时间(分钟)
	 * 
	 * @return
	 */
	public int getExpireTime() {
		if(isApping()) {
			return userTask.getExpireMinuteTime();
		} else {
			long expireTime = Constants.TASK_EXPIRE_TIME;
			if(appTask.isQuicktask()){
				expireTime = Constants.QUICK_TASK_EXPIRE_TIME;
			}
			return (int)expireTime / (60*1000);
		}
	}
	public void setInstalledApp(boolean installedApp) {
		this.installedApp = installedApp;
	}
	//已经安装过的app
	public boolean isInstalledApp() {
		return installedApp;
	}
	
	public int getEarned_amount(){
		if(userTask != null){
			int earned_amount = userTask.getEarned_amount();
			return earned_amount > 0 ? earned_amount:appTask.getAmount();
		}
		return appTask.getAmount();
	}
}
