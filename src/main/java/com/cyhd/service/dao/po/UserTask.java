package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

import com.cyhd.service.constants.Constants;

public class UserTask implements Serializable {

	private static final long serialVersionUID = -9213025870158665237L;

	private long id;
	private int user_id;
	private int task_id;
	private int app_id;
	private int status;		//任务完成状态(1:未完成,2:已完成,3:已过期,4:已放弃)
	private Date starttime;
	private Date expiretime;
	private Date finishtime;
	private int type;
	private String did;
	private int download;
	private int will_expire;
	
	private String idfa;
	private String battery_id; //最多保存40个字符
	
	private int reward; //是否给了奖励，0：没，1：给了奖励 
	private Date rewardtime;
	
	private int active;
	private Date active_time;
	
	private int confirm_finish;
	private Date confirmtime;
	
	private int opened; //应用是否已经打开
	private Date open_time; //应用首次打开的时间
	
	private int  earned_amount ;
	
	public static final int STATUS_INIT = 1;
	public static final int STATUS_COMPLETED = 2;
	public static final int STATUS_EXPIRED= 3; //已过期
	public static final int STATUS_ABORTED = 4; //已放弃
	
	public static final int TYPE_SYSTEM = 1;
	public static final int TYPE_COMMON = 0;
	private String user_ip; //用户接任务时的ip地址
	
	//第三方需要上报任务的状态 1就是上报成功
	private int report;
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isValid(){
		return this.status == STATUS_INIT && !isExpired();
	}
	
	public boolean isExpired(){
		return this.status == STATUS_INIT && expiretime.before(new Date());
	}

	public boolean isTimeout(){
		return expiretime.before(new Date());
	}
	
	public boolean isCompleted(){
		return this.status == STATUS_COMPLETED;
	}
	public boolean isConfirmFinish(){
		return this.confirm_finish == 1;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	public int getApp_id() {
		return app_id;
	}
	public void setApp_id(int app_id) {
		this.app_id = app_id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	public Date getExpiretime() {
		return expiretime;
	}
	public void setExpiretime(Date expiretime) {
		this.expiretime = expiretime;
	}
	public Date getFinishtime() {
		return finishtime;
	}
	public void setFinishtime(Date finishtime) {
		this.finishtime = finishtime;
	}
	public int getWill_expire() {
		return will_expire;
	}

	public void setWill_expire(int will_expire) {
		this.will_expire = will_expire;
	}
	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	public Date getRewardtime() {
		return rewardtime;
	}

	public void setRewardtime(Date rewardtime) {
		this.rewardtime = rewardtime;
	}

	public int getDownload() {
		return download;
	}

	public void setDownload(int download) {
		this.download = download;
	}
	public boolean isDownload() {
		return this.download == 1 ? true : false;
	}
	
	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	/**
	 * 获取任务过期时间(分钟)
	 * 
	 * @return
	 */
	public int getExpireMinuteTime() {
		int minute = 0;
		if(this.getExpiretime()!=null) {
			Date currentTime =  new Date();
			long total = this.getExpiretime().getTime() -currentTime.getTime();
			minute = (int)(total / (60 * 1000));
		} else {
			minute = (int)Constants.TASK_EXPIRE_TIME / (60*1000);
		}
		return minute > 0 ? minute :1;
	}
	
	public int getDefaultExpireMinuteTime() {
		int minute = (int)Constants.TASK_EXPIRE_TIME / (60*1000);
		return minute > 0 ? minute :1;
	}
	
	@Override
	public String toString() {
		return "UserTask [id=" + id + ", user_id=" + user_id + ", task_id=" + task_id + ", app_id=" + app_id + ", status=" + status + ", starttime="
				+ starttime + ", expiretime=" + expiretime + ", finishtime=" + finishtime + ", type=" + type + ", download=" + download + ", reward=" + reward
				+ ", rewardtime=" + rewardtime + "]";
	}

	public String getIdfa() {
		return idfa;
	}

	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}
	
	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public Date getActive_time() {
		return active_time;
	}

	public void setActive_time(Date active_time) {
		this.active_time = active_time;
	}
	public int getConfirm_finish() {
		return confirm_finish;
	}

	public void setConfirm_finish(int confirm_finish) {
		this.confirm_finish = confirm_finish;
	}

	public Date getConfirmtime() {
		return confirmtime;
	}

	public void setConfirmtime(Date confirmtime) {
		this.confirmtime = confirmtime;
	}
	
	public int getOpened(){
		return opened;
	}
	
	public void setOpened(int op){
		opened = op;
	}
	
	public boolean isOpened(){
		return opened == 1;
	}
	
	public Date getOpen_time(){
		return open_time;
	}
	
	public void setOpen_time(Date op_time){
		open_time = op_time;
	}

	public void setUser_ip(String user_ip) {
		this.user_ip = user_ip;
	}

	public String getUser_ip() {
		return user_ip;
	}

	public int getEarned_amount() {
		return earned_amount;
	}

	public void setEarned_amount(int earned_amount) {
		this.earned_amount = earned_amount;
	}

	public boolean isAborted(){
		return status == STATUS_ABORTED;
	}

	public String getBattery_id() {
		return battery_id;
	}

	public void setBattery_id(String battery_id) {
		this.battery_id = battery_id;
	}

	public int getReport_status() {
		return report;
	}

	public void setReport_status(int report_status) {
		this.report = report_status;
	}
}
