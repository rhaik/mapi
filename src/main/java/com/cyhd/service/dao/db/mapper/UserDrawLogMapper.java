package com.cyhd.service.dao.db.mapper;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserDrawLog;

@Repository
public interface UserDrawLogMapper {

	@Insert("insert into money_user_draw_log(user_id,`type`,`reason`,`draw_num`,`createtime`,`activity_id`,friend_id,`draw_amount`) values(#{user_id},#{type},#{reason},1,#{createtime},#{activity_id},#{friend_id},#{draw_amount})")
	public int addUserDrawLog(UserDrawLog log);
	
	@Select("select * from money_user_draw_log where user_id=#{0} and activity_id=#{1} and `type`=#{2}")
	public List<UserDrawLog> getUserDrawLogs(int user_id,int activity,int type);
	
	@Select("select * from money_user_draw_log where activity_id=#{0} and `type`=#{1} limit #{2},#{3}")
	public List<UserDrawLog> getUserDrawLogsToRoll(int activity,int type,int start,int size);


	@Select("select count(1) from money_user_draw_log where user_id=#{0} and activity_id=#{1} and `type`=#{2} and createtime>=#{3}")
	public int countUserDrawLog(int user_id, int activity, int type, Date startTime);
	
	@Select("select * from money_user_draw_log where activity_id=#{0} and `type`=#{1} and draw_amount=-1 and TO_DAYS(createtime)=TO_DAYS(#{2}) limit 1")
	public UserDrawLog getTodyMaxDrawLog(int activity, int type,Date today);
	
}
