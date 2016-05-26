package com.cyhd.service.dao.po;

import java.util.Date;

import com.cyhd.service.constants.Constants;

public class UserSystemMessage {
	
	private long id;
	private int groupid;
	private String title;
	private String content;
	private Date create_time;
	private int is_read;
	private Date read_time;
	private int send;
	private Date send_time;
	private int send_count;
	private int send_sucess;
	private int user_id;
	private int type;
	private String thumb;
	private String description;
	private String target_url;
	private int third_id;
	
	private int push_client_type;
	
	/**推送的目标的设备类型-仅安卓*/
	public static final int PUSH_CLIENT_TYPE_ANDROID=Constants.platform_android;
	/**推送的目标的设备类型-仅IOS*/
	public static final int PUSH_CLIENT_TYPE_IOS=Constants.platform_ios;
	/**推送的目标的设备类型-APP支持的设备都可以*/
	public static final int PUSH_CLIENT_TYPE_ALL=3;
	
	public static final int TYPE_SYS = 1;			//系统
	public static final int TYPE_ENCHASHMENT = 2;	//提现
	public static final int TYPE_BEGINNER_TASK = 3; //新手任务
	public static final int TYPE_RECHARGE_TASK = 4; //手机充值
	/**转发任务*/
	public static final int TYPE_TRAN_ARTICLE_TASK = 5;
	/**积分墙的信息*/
	public static final int TYPE_INTEGAL = 6;
	/**朋友金币分享(虽然统一为金币)*/
	public static final int TYPE_INTEGAL_SHARE_JINBI = 7;
	/**朋友金币分享-签到(虽然统一为金币)*/
	public static final int TYPE_INTEGAL_SHARE_QIANDAO = 8;
	/**补发金币(虽然统一为金币)*/
	public static final int TYPE_INTEGAL_SHARE_BUFA = 9;
	/**圣诞活动抽奖*/
	public static final int TYPE_ACTIVEITY_DRAW = 10;

	/** 一元夺宝中奖*/
	public static final int TYPE_DUOBAO_SUCCESS = 11;

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
	public int getGroupid() {
		return groupid;
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
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
	public Date getSend_time() {
		return send_time;
	}
	public void setSend_time(Date send_time) {
		this.send_time = send_time;
	}
	public int getSend_count() {
		return send_count;
	}
	public void setSend_count(int send_count) {
		this.send_count = send_count;
	}
	public int getSend_sucess() {
		return send_sucess;
	}
	public void setSend_sucess(int send_sucess) {
		this.send_sucess = send_sucess;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getThird_id() {
		return third_id;
	}
	public void setThird_id(int third_id) {
		this.third_id = third_id;
	}
	public String getTarget_url() {
		return target_url;
	}
	public void setTarget_url(String target_url) {
		this.target_url = target_url;
	}
	public int getSend() {
		return send;
	}
	public void setSend(int send) {
		this.send = send;
	}
	public long getTimestamp(){
		return getCreate_time()==null ? 0:getCreate_time().getTime();
	}
	public int getPush_client_type() {
		return push_client_type;
	}
	public void setPush_client_type(int push_client_type) {
		this.push_client_type = push_client_type;
	}
}
