package com.cyhd.service.dao.po;

import java.util.Date;

import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.service.constants.Constants;

public class UserDrawLog {

	private int user_id;
	
	private int friend_id;
	/**得到抽奖机会、还是消费抽奖机会**/
	private int type;
	
	private String reason;
	/**获得的次数 */
	private int draw_times;
	/***抽奖抽中的金额*/
	private int draw_amount;
	
	private int activity_id;
	
	private Date createtime;
	
	public UserDrawLog() {}
	
	/**得到一个log type不能为null*/
	public UserDrawLog(int user_id, int friend_id, String reason, int activity_id,int draw_amount,UserDrawLogType type) {
		assert(type != null && user_id > 0);
		this.user_id = user_id;
		this.friend_id = friend_id;
		this.reason = reason;
		this.activity_id = activity_id;
		this.setDraw_times(1);
		this.setDraw_amount(draw_amount);
		this.setType(type.getType());
		this.setCreatetime(GenerateDateUtil.getCurrentDate());
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
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public int getActivity_id() {
		return activity_id;
	}
	public void setActivity_id(int activity_id) {
		this.activity_id = activity_id;
	}
	public int getFriend_id() {
		return friend_id;
	}
	public void setFriend_id(int friend_id) {
		this.friend_id = friend_id;
	}
	
	public int getDraw_times() {
		return draw_times;
	}

	public void setDraw_times(int draw_times) {
		this.draw_times = draw_times;
	}

	public int getDraw_amount() {
		return draw_amount;
	}

	public void setDraw_amount(int draw_amount) {
		this.draw_amount = draw_amount;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getTimeText(){
		Date now = GenerateDateUtil.getCurrentDate();
		long mill = (- this.createtime.getTime() + now.getTime());
		if(mill >= Constants.hour_millis){
			return mill / Constants.hour_millis+"小时前";
		}else if (mill > Constants.minutes_millis){
			return mill / Constants.minutes_millis + "分钟前";
		}else {
			return "刚刚";
		}
	}
	

	
	@Override
	public String toString() {
		return "UserDrawLog [user_id=" + user_id + ", friend_id=" + friend_id + ", type=" + type + ", reason=" + reason
				+ ", draw_times=" + draw_times + ", draw_amount=" + draw_amount + ", activity_id=" + activity_id
				+ ", createtime=" + createtime + "]";
	}



	public static enum UserDrawLogType{
		/**得到抽奖机会*/
		INCREMENT(1),
		/***消费抽奖机会*/
		DECREMENT(2);
		
		private int type;
		
		private UserDrawLogType(int type){
			this.type = type;
		}
		public int getType(){
			return type;
		}
	}
}
