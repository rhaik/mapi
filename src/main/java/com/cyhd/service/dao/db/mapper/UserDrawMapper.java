package com.cyhd.service.dao.db.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserDraw;

@Repository
public interface UserDrawMapper {
	/***
	 * 默认就是一次
	 * @param user_id
	 * @param activity
	 * @return
	 */
	public int addOrUpdateUserDraw(int user_id,int activity);

	/**
	 * 增加或更新用户的抽奖次数
	 * @param user_id
	 * @param activity
	 * @param times
	 * @return
	 */
	public int addOrUpdateUserDrawTimes(int user_id,int activity, int times);
	
	@Select("Select * from money_user_draw where user_id=#{0} and activity_id=#{1}")
	public UserDraw getUserDraw(int userId,int activity);
	
	@Update("update money_user_draw set balance_times=balance_times-1 where user_id=#{0} and activity_id=#{1} and balance_times > 0")
	public int decreBalance(int user_id,int activity);

	@Update("update money_user_draw set total_times=total_times-balance_times+#{2},balance_times=#{2} where user_id=#{0} and activity_id=#{1}")
	int updateDrawBalance(int userId, int activityId, int balance);
}
