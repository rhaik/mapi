package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

import com.cyhd.service.util.IdEncoder;

public class TransArticleTask implements Serializable{

	private static final long serialVersionUID = -7804365511326223565L;

	/**
	 * 默认的文章类型
	 */
	public static final int TYPE_DEFAULT = 0;

	/**
	 * 微信中的转发任务类型
	 */
	public static final int TYPE_WEIXIN = 1;

	private int id;
	private int user_id;
	private int article_id;
	private String title;
	private String name;
	private String description;
	private int current_rank;
	private int state;			//1:有效，2：无效
	private int amount;
	private int friends_amount;
	private float share_rate;  //分成比例
	private Date start_time;
	private Date end_time;
	private Date createtime;
	private int view_num;
	private long duration;  //试用时长，秒
	private int sort;
	
	private int total_task;
	private int current_task;
	private int received_task;
	private int total_received_task;
	private int total_completed_task;
	/***展示的剩余时间*/
	private Date show_end_time;
	
	/**处理过发放奖励-未开始*/
	public static final int EXECUTE_FLAG_NO_START = 1;
	/**处理过发放奖励-处理中*/
	public static final int EXECUTE_FLAG_STARTTING = 2;
	/**处理过发放奖励-已完毕*/
	public static final int EXECUTE_FLAG_ENDED = 3;
	
	//新增是否处理过奖励 
	private int execute_flag;
	/**第三阶梯的奖励 对应20个人*/
	private int raward_type_two;
	/**第四阶梯的奖励*/
	private int raward_type_three;

	//任务类型，0：默认值，表示应用内分享任务，1：微信分享任务
	private int type;

	private int comercial_id; //广告id
	private int wx_account_id; //微信公众号id

	//raward_type_one,raward_type_two,raward_type_three
	public int getRaward_type_zero(){
		return raward_type_two == 0 && raward_type_three == 0? 0: 10;
	}
	public int getRaward_type_two() {
		return raward_type_two;
	}
	public void setRaward_type_two(int raward_type_two) {
		this.raward_type_two = raward_type_two;
	}
	public int getRaward_type_three() {
		return raward_type_three;
	}
	public void setRaward_type_three(int raward_type_three) {
		this.raward_type_three = raward_type_three;
	}


	/**
	 * 获取达到第二级别用户应得的奖励金额
	 * @return
	 */
	public int getRealRewardTypeTwo(){
		if (this.raward_type_two == 0){
			return this.amount;
		}
		return this.raward_type_two;
	}

	/**
	 * 获取达到第三级别用户应得的奖励金额
	 * @return
	 */
	public int getRealRewardTypeThree(){
		if (this.raward_type_three == 0){
			return this.getRealRewardTypeTwo();
		}
		return this.raward_type_three;
	}

	public boolean isValid(){
		Date now = new Date();
		return this.state == 1 && (now.after(start_time) && now.before(end_time));
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
	
	public boolean isExpired(){
		return new Date().after(end_time);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getArticle_id() {
		return article_id;
	}
	public void setArticle_id(int article_id) {
		this.article_id = article_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public int getCurrent_rank() {
		return current_rank;
	}
	public void setCurrent_rank(int current_rank) {
		this.current_rank = current_rank;
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
	public float getShare_rate() {
		return share_rate;
	}
	public void setShare_rate(float share_rate) {
		this.share_rate = share_rate;
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
	public int getView_num() {
		return view_num;
	}
	public void setView_num(int view_num) {
		this.view_num = view_num;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
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
	
	public int getDurationMinute() {
		return (int)this.duration/60;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	
	public String getStatusText(){
		if(isValid() == false){
			return "任务已过期";
		}else if(isHasLeftTasks() == false){
			return "任务无剩余";
		}
		return "";
	}
	public Date getShow_end_time() {
		return show_end_time;
	}
	public void setShow_end_time(Date show_end_time) {
		this.show_end_time = show_end_time;
	}
	public int getExecute_flag() {
		return execute_flag;
	}
	public void setExecute_flag(int execute_flag) {
		this.execute_flag = execute_flag;
	}
	
	public int getRewardAmount(int view_num) {
		if(view_num >= 80){
			return  this.getRealRewardTypeThree();
		}else if(view_num >= 20){
			return this.getRealRewardTypeTwo();
		}else if(view_num >= 5){
			return this.getAmount();
		}else if(view_num >= 1){
			return this.getRaward_type_zero();
		}
		return 0;
	}
	public int getMaxAmount(){
		int amount = this.getAmount();
		if(this.getRaward_type_three() > 0){
			amount = this.getRaward_type_three();
		}else if(this.getRaward_type_two() > 0){
			amount = this.getRaward_type_three();
		}
		return amount;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getComercial_id() {
		return comercial_id;
	}

	public void setComercial_id(int comercial_id) {
		this.comercial_id = comercial_id;
	}

	public int getWx_account_id() {
		return wx_account_id;
	}

	public void setWx_account_id(int wx_account_id) {
		this.wx_account_id = wx_account_id;
	}

	/**
	 * 供页面展示剩余数量的方法
	 * @return
	 */
	public int getLeftNumForShow(){
		if(this.isValid() == false){
			return 0;
		}
		int leftNum = getLeftTasks();
		if (leftNum <= 0){
			leftNum = 0;
		}else if (leftNum < 89){ //小于97，快速衰减
			leftNum = leftNum / 2 + 1;
		}else { //大于97，则根据leftNum取log10 并+67
			leftNum = (int)( leftNum * Math.log10(leftNum) ) + 29;
		}
		return leftNum;
	}
}
