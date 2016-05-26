package com.cyhd.service.dao.po;

import java.util.Date;

import com.cyhd.service.util.EmojUtil;
import com.cyhd.service.util.GlobalConfig;

public class UserFriendMessage {
	
	private long id;
	private int user_id;
	private Date create_time;
	private int amount;
	private String app_name;
	private String app_icon;
	
	private int is_read;
	private String read_time;
	
	private int friend_id;
	private long friend_task_id;
	private String friend_avater;
	private String friend_name;
	private int friend_amount;
	private int friend_level;
	private int middle_friend_id;
	//新增字段 原来的朋友消息只是app限时任务的 现在增加转发任务
	private int source;
	/**原有的app限时任务*/
	public static final int SOURCE_APP_TASK = 1;
	/**新增的转发任务*/
	public static final int SOURCE_TRAN_ARTICLE=2;
//	/**新增的积分*/
//	public static final int SOURCE_INTEGAL_JIFEN = 3;
//	/**新增的金币*/
//	public static final int SOURCE_INTEGAL_JINBI = 4;
//	/**新增的金币-签到*/
//	public static final int SOURCE_INTEGAL_QIANDAO = 5;
	
	private long sort_time;
	public long getSort_time() {
		return sort_time == 0 ? id : sort_time;
	}
	public void setSort_time(long sort_time) {
		this.sort_time = sort_time;
	}
	
	
	public String getFriendUniName(){
		return EmojUtil.string2Unicode(friend_name);
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
	public String getApp_name() {
		return app_name;
	}
	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}
	public int getFriend_amount() {
		return friend_amount;
	}
	public void setFriend_amount(int friend_amount) {
		this.friend_amount = friend_amount;
	}
	public int getIs_read() {
		return is_read;
	}
	public void setIs_read(int is_read) {
		this.is_read = is_read;
	}
	public String getRead_time() {
		return read_time;
	}
	public void setRead_time(String read_time) {
		this.read_time = read_time;
	}
	public int getFriend_id() {
		return friend_id;
	}
	public void setFriend_id(int friend_id) {
		this.friend_id = friend_id;
	}
	public String getApp_icon() {
		return app_icon;
	}
	public void setApp_icon(String app_icon) {
		this.app_icon = app_icon;
	}
	public long getFriend_task_id() {
		return friend_task_id;
	}
	
	public void setFriend_task_id(long friend_task_id) {
		this.friend_task_id = friend_task_id;
	}
	
	public String getFriendHeadImg(){
		if(friend_avater == null || friend_avater.isEmpty()){
			return GlobalConfig.default_avatar;
		}
		return friend_avater;
	}
	public String getFriend_avater() {
		return friend_avater;
	}
	public void setFriend_avater(String friend_avater) {
		this.friend_avater = friend_avater;
	}
	public String getFriend_name() {
		return friend_name;
	}
	public void setFriend_name(String friend_name) {
		this.friend_name = friend_name;
	}
	public int getFriend_level() {
		return friend_level;
	}
	public void setFriend_level(int friend_level) {
		this.friend_level = friend_level;
	}
	public int getMiddle_friend_id() {
		return middle_friend_id;
	}
	public void setMiddle_friend_id(int middle_friend_id) {
		this.middle_friend_id = middle_friend_id;
	}
	public long getTimestamp(){
		return getCreate_time()==null ? 0:getCreate_time().getTime();
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
}
