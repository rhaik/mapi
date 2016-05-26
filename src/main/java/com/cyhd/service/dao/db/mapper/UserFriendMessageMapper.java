package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserFriendMessage;


@Repository
public interface UserFriendMessageMapper {

//	@Select("select * from money_user_friend_message where user_id=#{0} order by id desc limit #{1}, #{2}")
//	public List<UserFriendMessage> getMessages(int userid, int start, int size);
	
	@Select("select * from money_user_friend_message where user_id=#{0} and sort_time < #{1} order by id desc limit #{2}")
	public List<UserFriendMessage> getMessagesById(int userid, long lastId, int size);
	
	@Select("select count(*) from money_user_friend_message where user_id=#{0}")
	public int getCount(int userid);
	
	@Insert("INSERT INTO `money_user_friend_message` (`id`, `user_id`, `create_time`, `amount`, `app_name`, `app_icon`, "
			+ "`is_read`, `read_time`, `friend_id`, `friend_task_id`, `friend_avater`, "
			+ "`friend_name`, `friend_amount`, `friend_level`, `middle_friend_id`, sort_time,source) VALUES "
			+ "(#{id}, #{user_id}, #{create_time}, #{amount}, #{app_name}, #{app_icon}, #{is_read}, #{read_time}, #{friend_id}, "
			+ "#{friend_task_id}, #{friend_avater}, #{friend_name}, #{friend_amount}, #{friend_level}, #{middle_friend_id}, #{sort_time},#{source})")
	public int add(UserFriendMessage message);
	
	@Update("UPDATE `money_user_friend_message` SET  `is_read`=1, `read_time`=NOW() WHERE id=#{0}")
	public int updateReadStatus(int id);
	
	@Select("SELECT * FROM money_user_friend_message WHERE user_id=#{0} ORDER BY id DESC LIMIT 1")
	public UserFriendMessage getUserLastFirendMessage(int userid);
	
	@Select("select count(id)  from money_user_friend_message where user_id=#{0} and sort_time > #{1} ")
	public int getCountByLastId(int userid,long lastId);

	
}