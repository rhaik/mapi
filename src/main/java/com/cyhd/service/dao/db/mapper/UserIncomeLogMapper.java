package com.cyhd.service.dao.db.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserIncomeLog;


@Repository
public interface UserIncomeLogMapper {
	
	@Insert("INSERT INTO `money_user_income_log` (`user_id`, `user_task_id`,`from_user`,`friend_level`, `action`, `amount`,  `type`, `operator_time`, `remarks`) "
			+ "VALUES (#{user_id}, #{user_task_id}, #{from_user}, #{friend_level}, #{action}, #{amount}, #{type}, #{operator_time}, #{remarks})")
	public int add(UserIncomeLog userIncome);
	
	@Select("select sum(amount) from money_user_income_log where user_id =#{0} AND operator_time > #{1} AND operator_time < #{2} AND type=1")
	public Integer getUserAmountByUserIdAndDate(int user_id, Date startTime, Date endTime);
	
	@Select("select sum(amount) from money_user_income_log where user_id =#{0} AND operator_time > #{1} AND operator_time < #{2} AND action=1 AND type=1")
	public Integer getUserAppAmountByUserIdAndDate(int user_id, Date startTime, Date endTime);
	
	@Select("select sum(amount) from money_user_income_log where user_id =#{0} AND operator_time > #{1} AND operator_time < #{2} AND action=2 AND type=1")
	public Integer getUserFriendAmountByUserIdAndDate(int user_id, Date startTime, Date endTime);
	
//	@Select("select sum(amount) from money_user_income_log where user_id =#{0} AND action=2 AND type=1")
//	public Integer getUserFriendAmountByUserId(int user_id);
//	
//	@Select("select sum(amount) from money_user_income_log where user_id in(select f2.user_id from money_user_invitation_friends AS f1 left JOIN money_user_invitation_friends AS f2 ON f1.`friend`=f2.`user_id` where f1.user_id=#{0}) AND action=2 AND type=1")
//	public Integer getUserGrandsonAmountByUserId(int user_id);
	
	@Select("select * from money_user_income_log ORDER BY id DESC LIMIT #{0}")
	public List<UserIncomeLog> getUserIncomeLogs(int num);
	
	@Select("select count(distinct from_user)  from money_user_income_log where user_id = #{0} AND action = 2 AND friend_level = #{1}")
	public int countUserFriendInviteIncome(int userId,int friend_level);
	
	@Select("select from_user,sum(amount) as amount,operator_time from money_user_income_log where user_id = #{0} AND action = 2 AND friend_level = #{1}  group by from_user order by operator_time DESC LIMIT #{2},#{3}")
	public List<UserIncomeLog> getUserFriendInviteIncome(int userId,int friend_level, int start, int size);
	

	@Select("select * from money_user_income_log where user_id = #{0} AND from_user = #{1} AND action = 2 order by operator_time DESC LIMIT #{2},#{3}")
	public List<UserIncomeLog> getUserFriendInviteIncomeDetail(int userId,int from_user, int start, int size);
	
	@Select("select count(*) from money_user_income_log where user_id = #{0} AND from_user = #{1} AND action = 2")
	public int countUserFriendInviteIncomeDetail(int userId,int friend_level);
	
	
	@Select("select * from money_user_income_log where user_id = #{0} AND action IN(3,4,6,8) order by operator_time DESC")
	public List<UserIncomeLog> getUserOtherIncome(int userId);
	
}