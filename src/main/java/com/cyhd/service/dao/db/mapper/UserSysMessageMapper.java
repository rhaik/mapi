package com.cyhd.service.dao.db.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserMessagePage;
import com.cyhd.service.dao.po.UserSystemMessage;


@Repository
public interface UserSysMessageMapper {

//	@Select("select * from money_user_system_message where (user_id=#{1} or user_id=0) and groupid=#{0} order by id desc limit #{2}, #{3}")
//	public List<UserSystemMessage> getMessages(int groupid, int user_id, int start, int size);
//	
	@Select("select * from money_user_system_message where (user_id=#{0} or user_id=0) and sort_time <#{2} and (push_client_type=#{4} or push_client_type=3) and create_time >= #{1} order by id desc limit #{3}")
	public List<UserSystemMessage> getMessagesById(int userid, Date starttime, long lastId, int size,int clientType);
	
	@Select("select count(*) from money_user_system_message where (user_id=#{0} or user_id=0) and (push_client_type=#{2} or push_client_type=3) and create_time >= #{1}")
	public int getCount(int userid, Date starttime,int clientType);
	
	@Select("select * from money_user_system_message where id=#{0}")
	public UserSystemMessage getById(int id);

	@Select("select * from money_user_system_message where create_time > #{0} and `send`=0 and estate=1 order by id asc limit #{1}")
	public List<UserSystemMessage> getUnSendSystemMessages(Date starttime, int size);

	@Update("update money_user_system_message set `send`=1, send_time=now() where id=#{0}")
	public int setSended(long id);
	
	@Insert("insert into money_user_system_message(id, user_id, title, description, content, target_url, type, `send`, send_time, create_time, sort_time,push_client_type) values ("
			+ "#{id},#{user_id},#{title}, #{description}, #{content}, #{target_url}, #{type}, #{send}, #{send_time}, #{create_time}, #{sort_time},#{push_client_type})")
	public int add(UserSystemMessage message);

	@Select("select count(*) from money_user_system_message where (user_id=#{0} or user_id=0) and (push_client_type=#{3} or push_client_type=3) and sort_time > #{2} and create_time > #{1}")
	public int getCountByLastId(int userid, Date startime, long lastid,int clientType);
	
	@Select("select * from money_user_system_message where (user_id=#{0} or user_id=0) and (push_client_type=#{2} or push_client_type=3) and create_time > #{1} order by id DESC limit 1")
	public UserSystemMessage getSysMessageLastByUserId(int userid, Date starttime,int clientType);
	
	@Select("select * from money_message_page where id=#{0} limit 1")
	public UserMessagePage getSysMessagePageById(int id);
}