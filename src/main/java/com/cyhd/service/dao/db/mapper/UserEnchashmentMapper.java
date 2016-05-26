package com.cyhd.service.dao.db.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserEnchashment;
import com.cyhd.service.dao.po.UserEnchashmentAccount;
import com.cyhd.service.dao.po.UserEnchashmentAccountLog;
import com.cyhd.service.dao.po.UserEnchashmentLog;


@Repository
public interface UserEnchashmentMapper {

	@Select("select * from money_user_enchashment where user_id=#{0} order by id desc limit #{1}, #{2}")
	public List<UserEnchashment> getUserEnchashment(int userid, int start, int size);
	
	@Select("select count(*) from money_user_enchashment where user_id=#{0}")
	public int getUserEnchashmentCount(int userid);
	
	@Insert("INSERT INTO `money_user_enchashment` (`user_id`, `amount`, `account`,`account_name`,`mention_time`,`status`,`type`, `ip`) VALUES "
			+ "(#{user_id}, #{amount}, #{account}, #{account_name}, #{mention_time}, #{status},#{type}, #{ip})")
	public int add(UserEnchashment enchashment);
	
	@Select("select * from money_user_enchashment where id=#{0}")
	public UserEnchashment getById(int id);
	
	//提现账号
	@Select("select * from money_user_enchashment_account where user_id=#{0}")
	public UserEnchashmentAccount getEnchashmentAccountByUserId(int user_id);
	
	@Insert("INSERT INTO `money_user_enchashment_account` (`user_id`, `wx_bank`, `wx_bank_name`, `alipay_name`, `alipay_account`, `createtime`, `updatetime`) "
			+ "VALUES (#{user_id}, #{wx_bank}, #{wx_bank_name}, #{alipay_name}, #{alipay_account}, #{createtime}, #{updatetime}) "
			+ "on duplicate key update wx_bank=#{wx_bank}, wx_bank_name=#{wx_bank_name}, alipay_name=#{alipay_name}, alipay_account=#{alipay_account}, updatetime=#{updatetime},`auto_pass`=0")
	public int addOrUpdateEnchashmentAccount(UserEnchashmentAccount account);
	
	@Insert("INSERT INTO `money_user_enchashment_account_log` (`user_id`, `wx_bank`, `wx_bank_name`, `alipay_name`, `alipay_account`, `createtime`) "
			+ "VALUES (#{user_id}, #{wx_bank}, #{wx_bank_name}, #{alipay_name}, #{alipay_account}, #{createtime}) ")
	public int addEnchashmentAccountLog(UserEnchashmentAccountLog log);
	
	@Insert("INSERT INTO `money_user_enchashment_log` (`user_enchashment_id`, `operator`, `type`,`operator_time`,`remarks`,`status`) VALUES "
			+ "(#{user_enchashment_id}, #{operator}, #{type}, #{operator_time}, #{remarks}, #{status})")
	public int addUserEnchashmentLog(UserEnchashmentLog log);
	
	@Select("select * from money_user_enchashment_account where alipay_account=#{0} limit 1")
	public UserEnchashmentAccount getUserEnchashmentAccountByAlipay_account(String alipay_account);
	
	@Select("select count(user_id) from money_user_enchashment_account where alipay_account=#{0}")
	public int countAccountByAilipay_account(String alipay_account);
	
	@Select("select * from money_user_enchashment where account=#{0}  order by id desc LIMIT 1")
	public UserEnchashment selectLogByAlipayAccount(String alipay_account);
	
	@Select("select user_id from money_user_enchashment_account where alipay_account=#{0} and alipay_name=#{1} and masked=#{2} limit 1")
	public Integer selectLogByAlipayAccountAndMasked(String alipay_account,String alipayName,int masked);
	
	@Select("select * from money_user_enchashment where status=5 order by id asc LIMIT #{0}, #{1}")
	public List<UserEnchashment> getEnchashSuccessUsers(int start, int size);
	@Select("select distinct user_id ,`amount`,`mention_time`,`type` from money_user_enchashment where status=5 and mention_time > #{0} and mention_time<#{1} order by id asc")
	public List<UserEnchashment> getEnchashSuccessUsersByTime(Date successStart,Date successEnd);
	

	/**
	 * 设置为已返还金币
	 * @param id
	 * @return
	 */
	@Update("update money_user_enchashment set reward=1 where id=#{0} and reward=0 and status=5")
	int setRewarded(int id);

	/**
	 * 获取一段时间内的提现总金额
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Select("select sum(amount) from money_user_enchashment where type=#{0} and mention_time >= #{1} and mention_time < #{2}")
	public Integer getTotalEnchashAmount(int type, Date startDate, Date endDate);
	
	@Update("update money_user_enchashment_account set auto_pass=1 where user_id=#{0} and masked != 1")
	public int setAutoPassed(int uid);

	@Update("update money_user_enchashment_account set score=#{1}, score_detail=#{2} where user_id=#{0}")
	public int setEncashAccountScore(int uid, int score, String detail);

	@Update("update money_user_enchashment set score=#{1}, score_detail=#{2} where user_id=#{0} order by id desc limit 1")
	public int setUserEncashScore(int uid, int score, String detail);
}