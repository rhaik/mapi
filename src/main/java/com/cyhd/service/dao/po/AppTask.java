package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.util.IdEncoder;

public class AppTask implements Serializable {

	/**
	 */
	private static final long serialVersionUID = 7462416732944485411L;

	/**
	 * 新手任务中的邀请任务
	 */
	public final static int SYS_INVITE_TASK = 1;

	/**
	 * 新手任务中的分享任务
	 */
	public final static int SYS_SHARE_TASK = 2;

	/**
	 * 新手任务中的试用任务
	 */
	public final static int SYS_APP_TASK = 3;

	/**
	 * 新手任务中的钥匙入口任务
	 */
	public final static int SYS_YAOSHI_TASK = 5;

	private int id;
	private int app_id;
	private String name;
	private String description;
	private String keywords;
	private int current_rank;
	private int target_rank;
	private int state;			//1:有效，2：无效
	private int amount;
	private int friends_amount;
	private float share_rate;  //分成比例
	private Date start_time;
	private Date end_time;
	/**展示结束时间*/
	private Date show_end_time;
	private Date createtime;
	private String action;
	private int duration;  //试用时长，秒
	private int sort;
	private int task_type;	//0--所有任务，1--系统任务
	private int distribution_id;

	private int total_task;
	private int current_task;
	private int received_task;
	private int total_received_task;
	private int total_completed_task;
	private int ischannel;
	private int is_vendor_task;
	private int direct_reward; //是否在回调之前就发奖励，如果值为1，则用户完成任务就发放奖励
	
	private int require_type;

	private int settlement_method;
	
	/**任务的类型 深度*/
	public final static int REQUIRE_TYPE_SHNEGDU = 1;

		/**任务的类型 付费*/
	public final static int REQUIRE_TYPE_FUFEI = 2;

	/** 任务类型，直接下载 */
	public final static int REQUIRE_TYPE_DIRECT = 3;
	
	//是第三方放在我们限时任务中的快速任务
	private int is_quick_task;
	private String ad_id;
	
	public boolean isQuicktask(){
		return is_quick_task >= 1;
	}
	
	public boolean isShenDu(){
		return this.require_type == REQUIRE_TYPE_SHNEGDU;
	}

	public boolean isDirectReward(){
		return this.getDirect_reward() == 1;
	}
	
	public boolean isDirectDownload(){
		return this.require_type == REQUIRE_TYPE_DIRECT;
	}

	public boolean isValid(){
		Date now = GenerateDateUtil.getCurrentDate();
		return isHasLeftTasks() && this.state == Constants.ESTATE_Y && (now.after(start_time) && now.before(end_time));
	}
	public int getLeftTasks(){
		int i = current_task - received_task;
		return i >= 0 ? i : 0;
	}
	
	public boolean isHasLeftTasks(){
		return getLeftTasks() > 0;
	}
	
	public String getEncodedId(){
		return IdEncoder.encode(id);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getApp_id() {
		return app_id;
	}
	public void setApp_id(int app_id) {
		this.app_id = app_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public int getDistribution_id() {
		return distribution_id;
	}

	public void setDistribution_id(int distribution_id) {
		this.distribution_id = distribution_id;
	}

	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getFriends_amount() {
		return friends_amount;
	}
	public void setFriends_amount(int friends_amount) {
		this.friends_amount = friends_amount;
	}
	public Date getStart_time() {
		return start_time;
	}
	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}
	public Date getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public int getTask_type() {
		return task_type;
	}
	public void setTask_type(int task_type) {
		this.task_type = task_type;
	}
	public int getTotal_task() {
		return total_task;
	}
	public void setTotal_task(int total_task) {
		this.total_task = total_task;
	}
	public int getCurrent_task() {
		return current_task;
	}
	public void setCurrent_task(int current_task) {
		this.current_task = current_task;
	}
	public int getReceived_task() {
		return received_task;
	}
	public void setReceived_task(int received_task) {
		this.received_task = received_task;
	}
	public int getTotal_received_task() {
		return total_received_task;
	}

	public void setTotal_received_task(int total_received_task) {
		this.total_received_task = total_received_task;
	}

	public int getTotal_completed_task() {
		return total_completed_task;
	}

	public void setTotal_completed_task(int total_completed_task) {
		this.total_completed_task = total_completed_task;
	}

	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getCurrent_rank() {
		return current_rank;
	}
	public void setCurrent_rank(int current_rank) {
		this.current_rank = current_rank;
	}
	public int getTarget_rank() {
		return target_rank;
	}
	public void setTarget_rank(int target_rank) {
		this.target_rank = target_rank;
	}
	public float getShare_rate() {
		return share_rate;
	}
	public void setShare_rate(float share_rate) {
		this.share_rate = share_rate;
	}
	public int getIschannel() {
		return ischannel;
	}
	public void setIschannel(int ischannel) {
		this.ischannel = ischannel;
	}
	public int getDurationMinute() {
		return (int)this.duration/60;
	}

	public int getDirect_reward() {
		return direct_reward;
	}

	public void setDirect_reward(int direct_reward) {
		this.direct_reward = direct_reward;
	}

	/**
	 * 是不是厂商回调任务
	 * @return
	 */
	public boolean isVendorTask(){
		return is_vendor_task == 1;
	}

	public int getIs_vendor_task() {
		return is_vendor_task;
	}

	public void setIs_vendor_task(int is_vendor_task) {
		this.is_vendor_task = is_vendor_task;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(500);
		sb.append( "AppTask [id=").append( id ).append( ", app_id=" ).append( app_id ).append( ", name=" ).append( name).append( ", description=" ).append( description ).append(", keywords=" ).append( keywords ).append( ", state=" ).append( state
		).append( ", amount=" ).append( amount ).append( ", friends_amount=" ).append( friends_amount ).append( ", start_time=" ).append( start_time ).append( ", end_time=").append(end_time ).append( ", createtime="
		).append(createtime ).append( ", action=" ).append( action ).append( ", sort=" ).append( sort ).append( ", task_type=" ).append( task_type ).append( ", total_task=" ).append( total_task ).append( ", current_task="
		).append( current_task ).append( ", received_task=" ).append(received_task + "]");
		
		return sb.toString();
	}
	public int getRequire_type() {
		return require_type;
	}
	public void setRequire_type(int require_type) {
		this.require_type = require_type;
	}


	/**
	 * 供页面展示剩余数量的方法
	 * @return
	 */
	public int getLeftNumForShow(){
		//任务无效 显示0
		if(this.isValid() == false){
			return 0;
		}
		int leftNum = getLeftTasks();
		if (leftNum <= 0){
			leftNum = 0;
		}else if (leftNum < 97){ //小于97，快速衰减
			leftNum = leftNum / 2 + 1;
		}else { //大于97，则根据leftNum取log10 并+67
			leftNum = (int)( leftNum * Math.log10(leftNum) ) + 67;
		}
		return leftNum;
	}

	/**
	 * 供页面展示预告App的数量
	 * @return
	 */
	public int getFutureNumForShow(){
		int leftNum = getLeftTasks();
		if (leftNum < 500){
			leftNum = 500;
		}else {
			leftNum = (int)( leftNum * Math.log10(leftNum));
		}

		int mod = leftNum % 100;
		if (mod > 0){
			leftNum -= mod;
		}
		return leftNum;
	}



	public Date getShow_end_time() {
		return show_end_time;
	}

	public void setShow_end_time(Date show_end_time) {
		this.show_end_time = show_end_time;
	}

	public String getFutureTime(){
		if (start_time != null && start_time.after(new Date())){
			return DateUtil.getDatetimeName(start_time);
		}
		return "";
	}

	public int getIs_quick_task() {
		return is_quick_task;
	}

	public void setIs_quick_task(int is_quick_task) {
		this.is_quick_task = is_quick_task;
	}

	public String getAd_id() {
		return ad_id;
	}

	public void setAd_id(String ad_id) {
		this.ad_id = ad_id;
	}

	public int getSettlement_method() {
		return settlement_method;
	}

	public void setSettlement_method(int settlement_method) {
		this.settlement_method = settlement_method;
	}
}
