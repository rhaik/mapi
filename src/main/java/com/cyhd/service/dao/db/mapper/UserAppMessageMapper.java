package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserAppMessage;


@Repository
public interface UserAppMessageMapper {

//	@Select("select * from money_user_app_message where user_id=#{0} order by id desc limit #{1}, #{2}")
//	public List<UserAppMessage> getMessages(int userid, int start, int size);
	
	@Select("select * from money_user_app_message where user_id=#{0} and sort_time < #{1} order by id desc limit #{2}")
	public List<UserAppMessage> getMessagesById(int userid, long lastId, int size);
	
	@Select("select count(*) from money_user_app_message where user_id=#{0}")
	public int getCount(int userid);

	@Insert("INSERT INTO `money_user_app_message` (`id`, `user_id`, `task_id`, `user_task_id`, `create_time`, `amount`, "
			+ "`finish_time`, `task_description`, `trial_time`, `expired_time`, `keyword`, `status`, `is_read`, `read_time`, "
			+ "`app_icon`, `app_name`, `agreement`, `extra_info`, sort_time) VALUES "
			+ "(#{id}, #{user_id}, #{task_id}, #{user_task_id}, now(), #{amount}, #{finish_time}, #{task_description},"
			+ " #{trial_time}, #{expired_time}, #{keyword}, #{status}, 0, NULL, #{app_icon}, #{app_name}, #{agreement}, #{extra_info}, #{sort_time})")
	public int add(UserAppMessage message);
	
	@Update("UPDATE `money_user_app_message` SET  `is_read`=1, `read_time`=NOW() WHERE id=#{0}")
	public int updateReadStatus(int id);
	
	@Select("SELECT * FROM money_user_app_message WHERE user_id=#{0} ORDER BY id DESC LIMIT 1")
	public UserAppMessage getLastAppMessageByUserId(int userid);
	
	@Select("select count(id) from money_user_app_message where user_id=#{0} and sort_time > #{1}")
	public int getCountByLastId(int userid,long lastId);
}