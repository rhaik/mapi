package com.cyhd.service.vo;


import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.TransArticle;
import com.cyhd.service.dao.po.TransArticleTask;
import com.cyhd.service.dao.po.UserArticleTask;

public class UserArticleTaskVo {

	private UserArticleTask userArticleTask;
	
	private TransArticle transArticle;
	
	private TransArticleTask transArticleTask;
	
	private boolean displayDate = false;
	
	private boolean isReceived;

	//是否标亮
	public boolean isValid(){
		if(isProcessing()) { 	//已接任务
			return true;
		} else if(userArticleTask != null){ //已完成变灰
//			if(userArticleTask.isReward()){
//				return false;
//			}else{
//				return (transArticleTask.isValid() && transArticleTask.isHasLeftTasks()) && !isReceived();
//			}
			return userArticleTask.getExpiretime().after(GenerateDateUtil.getCurrentDate());
		}
		return (transArticleTask.isValid() && transArticleTask.isHasLeftTasks()) && !isReceived();
	}
	//是否在进行中
	public boolean isProcessing(){
		if(transArticleTask.isValid() == false){
			return false;
		}
		return userArticleTask!=null && userArticleTask.getArticle_id() == transArticleTask.getArticle_id() && userArticleTask.isProcessing();
	}
	
	//是否已接过任务
	public boolean isHasReceived(){
		return userArticleTask != null;
	}
	//未接收过任务并且应用在起始时间范围内并且状态是正常，可用数量大于0
	public boolean isCanReceive(){
		return !isHasReceived() && transArticleTask.isValid() && transArticleTask.isHasLeftTasks() && !isReceived();
	}
	
	public boolean isExpired(){
		if(userArticleTask != null){
			return GenerateDateUtil.getCurrentDate().after(userArticleTask.getExpiretime());
		}
		return transArticleTask.isExpired();
	}
	
	public boolean isCompleted(){
		return userArticleTask != null && userArticleTask.isCompleted();
	}
	
	public String getTaskPromptText() {
		if(userArticleTask == null){
			if(transArticleTask.isValid()){
				if(isReceived()){
					return "已接受过";
				}
				if(transArticleTask.isHasLeftTasks() == false){
					return "任务已被抢光";
				}else{
					return "";
				}
			}else{
				return "任务已过期";
			}
		}
		if(transArticleTask.isExpired()){
			if(userArticleTask.isReward() == false){
				if(transArticleTask.getRewardAmount(userArticleTask.getView_num()) > 0){
					return  "等待发放奖励";
				}else{
					return "任务已过期";
				}
			}else{
				return "已发放奖励";
			}
		}else{
			if( userArticleTask.getExpiretime().after(GenerateDateUtil.getCurrentDate()) == false && userArticleTask.getView_num() == 0  ){
				return "任务已过期";
			}
			if(!userArticleTask.isCompleted() ) {
				return "已有" + userArticleTask.getView_num() + "人阅读"; 
			} else if(userArticleTask.getReward() == 0 && userArticleTask.getView_num() > 0) {
				return userArticleTask.isCompleted() ? "等待发放奖励" : "等待任务完成";
			} else {
				return "已发放奖励";
			}
		}
	}
	
	public int getRewardAmount(){
		if(userArticleTask != null){
			return transArticleTask.getRewardAmount(userArticleTask.getView_num());
		}
		return 0;
	}
	public UserArticleTask getUserArticleTask() {
		return userArticleTask;
	}

	public void setUserArticleTask(UserArticleTask userArticleTask) {
		this.userArticleTask = userArticleTask;
	}

	public TransArticle getTransArticle() {
		return transArticle;
	}

	public void setTransArticle(TransArticle transArticle) {
		this.transArticle = transArticle;
	}

	public TransArticleTask getTransArticleTask() {
		return transArticleTask;
	}

	public void setTransArticleTask(TransArticleTask transArticleTask) {
		this.transArticleTask = transArticleTask;
	}

	public boolean isReceived() {
		return isReceived;
	}

	public void setReceived(boolean isReceived) {
		this.isReceived = isReceived;
	}

	public boolean isDisplayDate() {
		return displayDate;
	}

	public void setDisplayDate(boolean displayDate) {
		this.displayDate = displayDate;
	}
	public String getCurrentDate() {
		return DateUtil.format(userArticleTask.getStarttime(), "yyyy-MM-dd");
	}
	/**
	 * 获取任务过期时间(分钟)
	 * 
	 * @return
	 */
	public String getExpireTimeText() {
		long current = GenerateDateUtil.getCurrentDate().getTime();	
		int houns = Constants.hour_millis;
		int seconds = (int) (transArticleTask.getEnd_time().getTime() - current) ;
		//大于一个小时显示小时否则是分钟
		if(seconds >= houns ){
			return (seconds/houns)+"小时";//+((seconds%houns)*60/houns)+"分钟"
		}else{
			return seconds*60/houns+"分钟";
		}
	}
	
}
