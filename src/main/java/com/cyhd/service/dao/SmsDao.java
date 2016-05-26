package com.cyhd.service.dao;

public interface SmsDao {

	/**
	 * 单个号码发送
	 * @param mobile
	 * @param content
	 * @param type  业务类型编号
	 * @return
	 */
	public boolean sendSms(String mobile, String content, int type, int channel);
	
	/**
	 * 多个号码发送
	 * @param mobiles
	 * @param content
	 * @param type  业务类型编号
	 * @return
	 */
	public boolean sendSms(String[] mobiles, String content, int type, int channel);
	
	// 获取剩余条数
	public int queryBalance();
}
