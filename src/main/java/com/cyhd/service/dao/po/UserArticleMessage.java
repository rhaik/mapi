package com.cyhd.service.dao.po;

import java.util.Date;

import com.cyhd.common.util.MoneyUtils;
import com.cyhd.service.util.IdEncoder;

public class UserArticleMessage {

	private long id;
	private int user_id;
	private int amount;
	private int task_id;
	private long user_task_id;
	private Date create_time;
	private Date finish_time;
	private String task_name;
	private String task_description;
	private int view_num;
	private Date expired_time;
	private int is_read;
	private Date read_time;
	private String extra_info;  //审核信息等
	private long sort_time;
	private String amount_des;
	private int client_type;
	
	private int type;
	
	
	/**任务开始*/
	public final static int MESS_TYPE_START = 10;//
	/**审核通过--也就是收到奖励*/
	public final static int MESS_TYPE_PASS = 13;
	/**审核不通过*/
	public final static int MESS_TYPE_NO_PASS = 14;
	/**任务即将过期*/
	public final static int MESS_TYPE_WILL_EXPRIED = 15;
	
	public String getEncodetaskId(){
		return IdEncoder.encode(task_id);
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
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
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
	public Date getExpired_time() {
		return expired_time;
	}
	public void setExpired_time(Date expired_time) {
		this.expired_time = expired_time;
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
	public String getExtra_info() {
		return extra_info;
	}
	public void setExtra_info(String extra_info) {
		this.extra_info = extra_info;
	}
	public long getSort_time() {
		return sort_time;
	}
	public void setSort_time(long sort_time) {
		this.sort_time = sort_time;
	}
	public int getClient_type() {
		return client_type;
	}
	public void setClient_type(int client_type) {
		this.client_type = client_type;
	}
	public String getTask_name() {
		return task_name;
	}
	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}
	public int getView_num() {
		return view_num;
	}
	public void setView_num(int view_num) {
		this.view_num = view_num;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getTimestamp(){
		return getCreate_time()==null ? getSort_time() : getCreate_time().getTime();
	}

	public String getAmount_des() {
		if(amount_des == null || "".equals(amount_des.trim())){
			return MoneyUtils.fen2yuanS(amount);
		}
		return amount_des;
	}

	public void setAmount_des(String amount_des) {
		this.amount_des = amount_des;
	}
}
