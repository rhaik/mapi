package com.cyhd.service.dao.po;

import java.util.Date;

public class UserEnchashment {

	private int id;
	private int user_id;
	private int amount;
	private String account;
	private String account_name;
	private Date mention_time;
	private int status;
	private int type;
	private String reason;
	private Date distribute_time;
	private int assigner;
	private String ip;
	private int reward; //是否已经给用户返还金币
	private int score;
	
	public static final int ACCOUNT_TYPE_WX = 1;   //wx
	public static final int ACCOUNT_TYPE_ALIPAY = 2;  //支付宝

	public static final int STATUS_INIT = 1; //提现中
	public static final int STATUS_AUDIT_FAIL = 4; //审核失败
	public static final int STATUS_SUCCESS = 5; //提现成功
	/**审核成功**/
	public static final int STATUS_AUDIT_SUCCESS = 3;
	public int getId() {
		return id;
	}
	public void setId(int id) {
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
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAccount_name() {
		return account_name;
	}
	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}
	public Date getMention_time() {
		return mention_time;
	}
	public void setMention_time(Date mention_time) {
		this.mention_time = mention_time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Date getDistribute_time() {
		return distribute_time;
	}
	public void setDistribute_time(Date distribute_time) {
		this.distribute_time = distribute_time;
	}
	public int getAssigner() {
		return assigner;
	}
	public void setAssigner(int assigner) {
		this.assigner = assigner;
	}
	public String getTypeText() {
		return this.type == ACCOUNT_TYPE_WX ? "微信" : "支付宝";
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	public boolean isRewarded(){
		return this.reward == 1;
	}

	public String getStatusText() {
		switch (this.getStatus()) {
		case STATUS_SUCCESS:
			return "已到账"; 
		case STATUS_AUDIT_FAIL:
			return "审核不通过";
		}
		return "提现中";
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "UserEnchashment{" +
				"id=" + id +
				", user_id=" + user_id +
				", amount=" + amount +
				", account='" + account + '\'' +
				", account_name='" + account_name + '\'' +
				", mention_time=" + mention_time +
				", status=" + status +
				", type=" + type +
				", reason='" + reason + '\'' +
				", distribute_time=" + distribute_time +
				", assigner=" + assigner +
				", ip='" + ip + '\'' +
				'}';
	}
}
