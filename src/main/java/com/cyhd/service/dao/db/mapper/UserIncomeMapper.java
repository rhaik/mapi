package com.cyhd.service.dao.db.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.TopUser;
import com.cyhd.service.dao.po.UserIncome;


@Repository
public interface UserIncomeMapper {
	
	@Insert("INSERT INTO `money_user_income` (`user_id`, `updatetime`) VALUES (#{user_id}, now()) on duplicate key update updatetime=now()")
	public int add(UserIncome userIncome);
	
	@Select("select user_id, income from money_user_income  where income >0 order by income desc limit #{0}")
	public List<TopUser> getTopUsers(int size);
	
	@Select("select user_id, sum(amount) as income from (select user_id, amount from money_user_income_log where operator_time >= #{0} AND type=1 ) as user_income group by user_id order by income desc limit #{1}")
	public List<TopUser> getTimeTopUsers(Date time, int size);
	
	@Select("select * from money_user_income where user_id=#{0}")
	public UserIncome getUserIncome(int userId);

	@Update("update money_user_income set balance=balance+#{1}, income=income+#{1}, task_total=task_total+#{1}, updatetime=now() where user_id=#{0}")
	public int addAppTaskIncome(int userId, int sum);

	@Update("update money_user_income set balance=balance+#{1}, income=income+#{1}, share_total=share_total+#{1}, share_level1_total=share_level1_total+#{1}, updatetime=now() where user_id=#{0}")
	public int addFriendShareIncomeLevel1(int userId, int sum);
	
	@Update("update money_user_income set balance=balance+#{1}, income=income+#{1}, share_total=share_total+#{1}, share_level2_total=share_level2_total+#{1}, updatetime=now() where user_id=#{0}")
	public int addFriendShareIncomeLevel2(int userId, int sum);
	
	@Update("update money_user_income set balance=balance+#{1}, income=income+#{1}, other_total=other_total+#{1}, updatetime=now() where user_id=#{0}")
	public int addOtherIncome(int userId, int sum);
	
	@Update("update money_user_income set encashing=encashing+balance,balance=0, updatetime=now() where user_id=#{0} AND `balance`>=1000")
	public int addUserEncashing(int userId);

	@Update("update money_user_income set encashing=encashing+#{1},balance=balance-#{1}, updatetime=now() where user_id=#{0} AND `balance`>=1000 AND balance>=#{1}")
	public int addUserEncashingAmount(int userId, int amount);
	
	@Update("update money_user_income set recharge=recharge+#{1},balance=balance-#{1}, updatetime=now() where user_id=#{0} AND `balance`>=#{1}")
	public int addUserRecharge(int userId, int amount);
	
	@Update("update money_user_income set recharge_total=recharge_total+#{1},encash_total=encash_total+#{1},recharge=recharge-#{1}, updatetime=now() where user_id=#{0} AND `recharge`>=#{1}")
	public int addUserRechargeTotal(int userId, int amount);
	
	@Update("update money_user_income set balance=balance+#{1},recharge=recharge-#{1}, updatetime=now() where user_id=#{0} AND `recharge`>=#{1}")
	public int returnRechargeUserBalance(int userId, int amount);

	@Select("select sum(income) from money_user_income")
	public Long getTotalIncomes();
	
	@Update("update money_user_income set balance=balance+#{1}, income=income+#{1}, other_total=other_total+#{1},gold_total=gold_total+#{1}, updatetime=now() where user_id=#{0}")
	public int addExchangeIntegalIncome(int userId,double exchangeIncome);
	
	@Update("update money_user_income set balance=balance+#{1}, income=income+#{1}, other_total=other_total+#{1},gold_coin=gold_coin+#{1}, updatetime=now() where user_id=#{0}")
	public int addExchangeIntegalIncomeYouMi(int userId,double exchangeIncome);
	
	@Update("update money_user_income set balance=balance+#{1}, income=income+#{1}, article_total=article_total+#{1}, updatetime=now() where user_id=#{0}")
	public int addArticleIncome(int userId, int sum);
	
	//兑换夺宝币
	@Update("update money_user_income set duobao_total=duobao_total+#{1},balance=balance-#{1}, updatetime=now() where user_id=#{0} AND `balance` >= #{1}")
	public int exchangeDuobaoCoin(int userId, int amount);

}