package com.cyhd.service.dao.po;

import java.util.Date;

import com.cyhd.common.util.MoneyUtils;

public class UserIncome {

	private int user_id;
	private int balance;
	private int income;
	private int consume;
	private int task_total;
	private int share_total; //分成总收入
	private int share_level1_total; //徒弟分成总收入
	private int share_level2_total; //徒孙分成总收入
	private int invite_total;
	private int encash_total;
	private int encashing;
	private int other_total;
	private int gold_total;
	private int gold_convertible;
	private int gold_coin;
	private Date updatetime;
	private int recharge;
	private int recharge_total;
	/**转发任务收入*/
	private int article_total;

	//夺宝充值总金额
	private int duobao_total;
	
	
	/**试用收入*/
	public static final int INCOME_TYPE_TASK = 1;   //试用收入
	/**分成收入*/
	public static final int INCOME_TYPE_SHARE = 2;  //分成收入
	/**新手教程收入**/
	public static final int INCOME_TYPE_BEGINNER = 3; //新手教程收入
	/**其他收入*/
	public static final int INCOME_TYPE_OTHER = 4; //其他收入
	/**提现*/
	public static final int INCOME_TYPE_ENCASH = 5; //提现  
	/**兑换金币*/
	public static final int INCOME_TYPE_EXCHANGE= 6; //兑换金币  
	/**充值*/
	public static final int INCOME_TYPE_RECHARGE= 7; //充值 
	
	/**积分*/
	public static final int INCOME_TYPE_EXCHANGE_YOUMI= 8; //兑换积分
	/**转发任务*/
	public static final int INCOME_TYPE_TRAN_ARTICLE=9;
	/**转发任务中的朋友分成*/
	public static final int INCOME_TYPE_TRAN_ARTICLE_SHARE = 10;  //分成收入
//	/**前两次额外奖励的收入*/
//	public static final int INCOME_TYPE_PRE_TWO_APP_EXTRA = 11;
//	/**收5个徒弟：新用户收前5个徒弟，每个奖励0.5元*/
//	public static final int INCOME_TYPE_PRE_FIVE_FRIEND_EXTRA = 12;
	
	public int getArticle_total() {
		return article_total;
	}
	public void setArticle_total(int article_total) {
		this.article_total = article_total;
	}
	
	/**夺宝*/
	public static final int INCOME_TYPE_DOUBAOCOIN = 11; //夺宝
	
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public int getIncome() {
		return income;
	}
	public void setIncome(int income) {
		this.income = income;
	}
	public int getConsume() {
		return consume;
	}
	public void setConsume(int consume) {
		this.consume = consume;
	}
	public int getTask_total() {
		return task_total;
	}
	public void setTask_total(int task_total) {
		this.task_total = task_total;
	}
	public int getShare_total() {
		return share_total;
	}
	public void setShare_total(int share_total) {
		this.share_total = share_total;
	}
	public int getInvite_total() {
		return invite_total;
	}
	public void setInvite_total(int invite_total) {
		this.invite_total = invite_total;
	}
	public int getEncash_total() {
		return encash_total;
	}
	public void setEncash_total(int encash_total) {
		this.encash_total = encash_total;
	}
	public int getOther_total() {
		return other_total;
	}
	public void setOther_total(int other_total) {
		this.other_total = other_total;
	}
	public int getGold_total() {
		return gold_total;
	}
	public void setGold_total(int gold_total) {
		this.gold_total = gold_total;
	}
	public int getGold_convertible() {
		return gold_convertible;
	}
	public void setGold_convertible(int gold_convertible) {
		this.gold_convertible = gold_convertible;
	}
	public int getGold_coin() {
		return gold_coin;
	}
	public void setGold_coin(int gold_coin) {
		this.gold_coin = gold_coin;
	}
	public int getEncashing() {
		return encashing;
	}
	public void setEncashing(int encashing) {
		this.encashing = encashing;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
	public int getShare_level1_total() {
		return share_level1_total;
	}
	public void setShare_level1_total(int share_level1_total) {
		this.share_level1_total = share_level1_total;
	}
	public int getShare_level2_total() {
		return share_level2_total;
	}
	public void setShare_level2_total(int share_level2_total) {
		this.share_level2_total = share_level2_total;
	}
	public String getShareLevel1TotalYuan() {
		return MoneyUtils.fen2yuanS2(share_level1_total);
	}
	public String getShareLevel2TotalYuan() {
		return MoneyUtils.fen2yuanS2(share_level2_total);
	}
	public int getRecharge() {
		return recharge;
	}
	public void setRecharge(int recharge) {
		this.recharge = recharge;
	}
	public int getRecharge_total() {
		return recharge_total;
	}
	public void setRecharge_total(int recharge_total) {
		this.recharge_total = recharge_total;
	}

	public int getDuobao_total() {
		return duobao_total;
	}

	public void setDuobao_total(int duobao_total) {
		this.duobao_total = duobao_total;
	}

	@Override
	public String toString() {
		return "UserIncome [user_id=" + user_id + ", balance=" + balance + ", income=" + income + ", consume=" + consume + ", task_total=" + task_total
				+ ", share_total=" + share_total + ", invite_total=" + invite_total + ", encash_total=" + encash_total + ", encashing=" + encashing
				+ ", updatetime=" + updatetime + "]";
	}
}
