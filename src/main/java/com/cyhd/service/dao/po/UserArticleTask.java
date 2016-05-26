package com.cyhd.service.dao.po;

import java.util.Date;

import com.cyhd.service.constants.Constants;

public class UserArticleTask implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private int user_id;
	private int article_id;
	private int article_task_id;
	private int status;		//任务完成状态(1:未完成2:已完成3:已过期4:微信响应没有分享成功)
	private Date starttime;
	private Date expiretime;
	private Date finishtime;
	private String did;
	private int will_expire;
	private int confirm_finish;
	private Date confirmtime;
	private String idfa;
	private String wx_account_id;//使用哪些微信公众号分享
	//user_id,view_num,article_id
	private int reward; //是否给了奖励，0：没，1：给了奖励 
	private Date rewardtime;
	
	private Date sharetime;
	//在任务时间内 有多少个用户查看
	private int view_num;

	//是否已经打开过，首次打开不记录阅读数
	private int opened;

	//用户的ip地址
	private String user_ip;

	//参加任务时的客户端类型
	private int client_type;
	//1: 进行中，2:已完成
	public static final int STATUS_INIT = 1;
	public static final int STATUS_COMPLETED = 2;
	public static final int STATUS_EXPIRED = 3;
	
	public static final int REWARD_OK=1;
	public boolean isReward(){
		return getReward() == REWARD_OK;
	}
	
/*	public boolean isValid(){
		return this.status == STATUS_INIT && !isExpired();
	}*/
	//过期否 没有过期 都记录查看人
//	public boolean isExpired(){
//		return  expiretime.before(new Date());
//	}

	/**
	 * 是否在进行中
	 * @return
	 */
	public boolean isProcessing(){
		return this.status == STATUS_INIT;
	}

	public boolean isCompleted(){
		return this.status == STATUS_COMPLETED;
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

	public int getArticle_id() {
		return article_id;
	}

	public void setArticle_id(int article_id) {
		this.article_id = article_id;
	}

	public int getArticle_task_id() {
		return article_task_id;
	}

	public void setArticle_task_id(int article_task_id) {
		this.article_task_id = article_task_id;
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

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public int getWill_expire() {
		return will_expire;
	}

	public void setWill_expire(int will_expire) {
		this.will_expire = will_expire;
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

	public String getIdfa() {
		return idfa;
	}

	public void setIdfa(String idfa) {
		this.idfa = idfa;
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

	public Date getSharetime() {
		return sharetime;
	}

	public void setSharetime(Date sharetime) {
		this.sharetime = sharetime;
	}

	public int getView_num() {
		return view_num;
	}

	public void setView_num(int view_num) {
		this.view_num = view_num;
	}

	public int getOpened() {
		return opened;
	}

	public void setOpened(int opened) {
		this.opened = opened;
	}

	public String getUser_ip() {
		return user_ip;
	}

	public void setUser_ip(String user_ip) {
		this.user_ip = user_ip;
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
			minute = (int)Constants.ARTICLE_TASK_EXPIRE_TIME / (60*1000);
		}
		return minute > 0 ? minute :1;
	}
	
	public int getDefaultExpireMinuteTime() {
		int minute = (int)Constants.ARTICLE_TASK_EXPIRE_TIME / (60*1000);
		return minute > 0 ? minute :1;
	}

	public String getWx_account_id() {
		return wx_account_id;
	}

	public void setWx_account_id(String wx_account_id) {
		this.wx_account_id = wx_account_id;
	}


	public int getClient_type() {
		return client_type;
	}

	public void setClient_type(int client_type) {
		this.client_type = client_type;
	}
	
	@Override
	public int hashCode() {
		return this.getUser_id()<<1 +this.getArticle_id();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		UserArticleTask tmp = (UserArticleTask)obj;
		if(this.getArticle_id() == tmp.getArticle_id() && this.getUser_id() == tmp.getUser_id()){
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "UserArticleTask{" +
				"user_id=" + user_id +
				", article_id=" + article_id +
				", article_task_id=" + article_task_id +
				", reward=" + reward +
				", status=" + status +
				", view_num=" + view_num +
				'}';
	}
}
