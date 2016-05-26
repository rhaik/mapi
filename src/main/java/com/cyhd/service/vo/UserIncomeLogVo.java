package com.cyhd.service.vo;


import com.cyhd.common.util.MoneyUtils;
import com.cyhd.common.util.TimePeriod;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.dao.po.UserIncomeLog;

public class UserIncomeLogVo {

	private User user;
	private UserIncomeLog userIncomeLog;
	private User fromUser;
	private boolean displayDate = false;
	private int totalAmount;
	public int getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}
	public boolean isDisplayDate() {
		return displayDate;
	}
	public void setDisplayDate(boolean displayDate) {
		this.displayDate = displayDate;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public User getFromUser() {
		return fromUser;
	}
	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}
	public UserIncomeLog getUserIncomeLog() {
		return userIncomeLog;
	}
	public void setUserIncomeLog(UserIncomeLog userIncomeLog) {
		this.userIncomeLog = userIncomeLog;
	}
	/**
	 * 获取开始到现在有几分钟
	 * @return
	 */
	public String getBeforMinute() {
		return TimePeriod.beforeForSubscription(userIncomeLog.getOperator_time().getTime());
//		long total =  current.getTime() - this.userIncomeLog.getOperator_time().getTime();
//		int Minute = (int) (total / (60 *1000));
//		return Minute > 0 ? Minute : 1;
	}
	public String getRemarks() {
		String yuan = MoneyUtils.fen2yuanS(this.userIncomeLog.getAmount());
		String remark = this.userIncomeLog.getRemarks();
		//remark = remark == null ? "" : remark;
		String text = "";
		
		switch(this.userIncomeLog.getAction()) {
			//应用试用
			case UserIncome.INCOME_TYPE_TASK:text="下载了"+remark+"，赚了"+yuan+"元";break;
			//好友分成
			case UserIncome.INCOME_TYPE_SHARE:text="好友完成"+remark+",分成"+yuan+"元";break;
			//新手教程
			case UserIncome.INCOME_TYPE_BEGINNER:text= remark+",赚了"+yuan+"元";break;
			case UserIncome.INCOME_TYPE_OTHER:
				if (remark != null && !remark.isEmpty()) {text = remark + yuan + "元";} break;
			//提现
			case UserIncome.INCOME_TYPE_ENCASH:text="提现了 "+yuan+ " 元到" + remark;break;
			//金币兑换
			case UserIncome.INCOME_TYPE_EXCHANGE:text="金币兑换了 "+yuan+ " 元";break;
			//充值
			case UserIncome.INCOME_TYPE_RECHARGE:text= "手机充值了 "+ yuan+ " 元";break;
			//积分兑换
			case UserIncome.INCOME_TYPE_EXCHANGE_YOUMI:text="积分兑换了 "+yuan+ " 元";break;
			case UserIncome.INCOME_TYPE_TRAN_ARTICLE:text="完成转发任务"+remark+",赚了"+yuan+"元";break;
			case UserIncome.INCOME_TYPE_TRAN_ARTICLE_SHARE:text="好友完成转发"+remark+",分成"+yuan+"元";break;
			
			
			//case UserIncome.INCOME_TYPE_DOUBAOCOIN:text= "充值夺宝币，花费 "+ yuan+ " 元";break;
		}
		return text;
	}
}
