package com.cyhd.service.dao.po;

import java.util.Date;

import com.cyhd.service.util.IdEncoder;

public class UserAppMessage {
	
	private long id;
	private int user_id;
	private int amount;
	private int task_id;
	private long user_task_id;
	private Date create_time;
	private Date finish_time;
	private String task_description;
	private int trial_time;
	private Date expired_time;
	private String keyword;
	private int status;
	private int is_read;
	private Date read_time;
	private String app_icon;
	private String app_name;
	private String agreement;
	private String bundle_id;
	private String extra_info;  //审核信息等
	
	
	public static final int STATUS_APP_START = 10;
	public static final int STATUS_APP_DOWNLOADS = 11;
	public static final int STATUS_APP_COMPLETE = 12;
	public static final int STATUS_APP_AUDIT_SUCCESS = 13;
	public static final int STATUS_APP_AUDIT_FAIL = 14;
	public static final int STATUS_APP_WILL_EXPIRE = 15;
	
	private long sort_time;
	public long getSort_time() {
		return sort_time == 0 ? id : sort_time;
	}
	public void setSort_time(long sort_time) {
		this.sort_time = sort_time;
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
	public long getUser_task_id() {
		return user_task_id;
	}
	public void setUser_task_id(long user_task_id) {
		this.user_task_id = user_task_id;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public Date getFinish_time() {
		return finish_time;
	}
	public void setFinish_time(Date finish_time) {
		this.finish_time = finish_time;
	}
	public String getTask_description() {
		return task_description;
	}
	public void setTask_description(String task_description) {
		this.task_description = task_description;
	}
	public int getTrial_time() {
		return trial_time;
	}
	public void setTrial_time(int trial_time) {
		this.trial_time = trial_time;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getIs_read() {
		return is_read;
	}
	public void setIs_read(int is_read) {
		this.is_read = is_read;
	}
	public Date getRead_time() {
		return read_time;
	}
	public void setRead_time(Date read_time) {
		this.read_time = read_time;
	}
	public String getApp_icon() {
		return app_icon;
	}
	public void setApp_icon(String app_icon) {
		this.app_icon = app_icon;
	}
	public String getApp_name() {
		return app_name;
	}
	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}
	public String getAgreement() {
		return agreement;
	}
	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}
	public String getExtra_info() {
		return extra_info;
	}
	public void setExtra_info(String extra_info) {
		this.extra_info = extra_info;
	}
	public Date getExpired_time() {
		return expired_time;
	}
	public void setExpired_time(Date expired_time) {
		this.expired_time = expired_time;
	}
	public long getExpiredTimestamp() {
		return expired_time == null ? 0 : expired_time.getTime();
	}
	public long getTimestamp(){
		return getCreate_time()==null ? 0:getCreate_time().getTime();
	}
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	
	public String getBundle_id() {
		return bundle_id;
	}
	public void setBundle_id(String bundle_id) {
		this.bundle_id = bundle_id;
	}
	public String getEncodedTaskId(){
		return IdEncoder.encode(task_id);
	}
}
