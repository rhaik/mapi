package com.cyhd.service.dao.db.mapper.doubao;

import com.cyhd.service.dao.po.doubao.UserDuobaoCoin;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;


@Repository
public interface UserDuobaoCoinMapper {
	
	@Insert("INSERT INTO `money_user_duobao_coin` (`user_id`, `updatetime`) VALUES (#{user_id}, now()) on duplicate key update updatetime=now()")
	public int add(UserDuobaoCoin userIncome);
	

	@Select("select * from money_user_duobao_coin where user_id=#{0}")
	public UserDuobaoCoin getUserDuobaoCoin(int userId);

	@Update("update money_user_duobao_coin set balance=balance+#{1}, income=income+#{1}, updatetime=now() where user_id=#{0}")
	public int addDuobaoCoin(int userId, int sum);


	@Select("select IFNULL(sum(income),0) from money_user_duobao_coin")
	public long getTotalIncomes();

	
	//增加夺宝中金额
	@Update("update money_user_duobao_coin set duobaoing=duobaoing+#{1},duobao_total=duobao_total+#{1},balance=balance-#{1}, updatetime=now() where user_id=#{0} AND `balance`>=#{1}")
	public int userCoinByDuobao(int userId, int amount);

	//完成夺宝，修改夺宝中金额
	@Update("update money_user_duobao_coin set duobaoing=duobaoing-#{1}, updatetime=now() where user_id=#{0} AND `duobaoing`>=#{1}")
	public int finishUserDuobao(int userId, int amount);

	//返回夺宝金额
	@Update("update money_user_duobao_coin set balance=balance+#{1},duobaoing=duobaoing-#{1},duobao_total=duobao_total-#{1},updatetime=now() where user_id=#{0} AND `duobaoing`>=#{1}")
	public int returnUserDuobaoBalance(int userId, int amount);
}