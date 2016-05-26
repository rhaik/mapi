package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserArticleMessage;
import com.cyhd.service.dao.po.UserFriendMessage;

@Repository
public interface UserArticleMessageMapper {

	@Insert("INSERT INTO `money_user_article_message` ( `user_id`, `task_id`, `user_task_id`, `create_time`, `amount`, "
			+ "`finish_time`, `task_description`, `is_read`, `read_time`, "
			+  "`extra_info`,expired_time, sort_time,`type`,`task_name`,`client_type`,`amount_des`) VALUES "
			+ "( #{user_id}, #{task_id}, #{user_task_id}, now(), #{amount}, #{finish_time}, #{task_description},"
			+ "  0, NULL, #{extra_info},#{expired_time},#{sort_time},#{type},#{task_name},#{client_type},#{amount_des})")
	public int addMessage(UserArticleMessage articleMessage);
	
	@Select("select count(*) from money_user_article_message where user_id=#{0} and client_type=#{1}")
	public int getCount(int userid,int client_type);
	
	@Select("select * from money_user_article_message where user_id=#{0} and sort_time < #{1} and client_type=#{3} order by id desc limit #{2}")
	public List<UserArticleMessage> getMessagesByuUserId(int userid, long lastId, int size,int client_type);
	
	@Select("select count(id)  from money_user_article_message where user_id=#{0} and client_type=#{2} and sort_time > #{1} ")
	public int getCountByLastId(int userid,long lastId,int client_type);
	
	@Select("SELECT * FROM money_user_article_message WHERE user_id=#{0} and client_type=#{1} ORDER BY id DESC LIMIT 1")
	public UserArticleMessage getLastArticleMessageByUserId(int userid,int client_type);
	
}
