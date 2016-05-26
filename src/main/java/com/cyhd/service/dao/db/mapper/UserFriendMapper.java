package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserFriend;


@Repository
public interface UserFriendMapper {

	
	@Select("select count(*) from money_user_invitation_friends where user_id=#{0}")
	public int getInvitationFriendsCount(int userId);
	
	@Select("select * from money_user_invitation_friends where user_id=#{0} order by id desc limit #{1}, #{2}")
	public List<UserFriend> getInvitationFriends(int userid, int start, int size);
	

	@Insert("INSERT ignore INTO `money_user_invitation_friends` (`user_id`, `friend`, `invi_time`) VALUES "
			+ "(#{user_id}, #{friend}, #{invi_time}) ")
	public int addInvitationFriends(UserFriend friend);
	
	@Select("select user_id from money_user_invitation_friends where friend=#{0}")
	public Integer getInvitor(int userid);

	//使用连接查询提高查询效率
	//@Select("SELECT count(*) FROM money_user_invitation_friends where user_id in (select friend from money_user_invitation_friends where user_id=#{0})")
	@Select(" select count(1) from money_user_invitation_friends f1, money_user_invitation_friends f2 " +
			" where f1.`user_id` = #{0} and f2.`user_id` = f1.friend")
	public int getGrandsonCount(int userId);
	
	//得到今天邀请的好友数
	@Select("SELECT count(id) FROM  `money_user_invitation_friends`  WHERE user_id=#{0} and DATE(now())=DATE(invi_time)")
	public int getTodyInviteFriendCount(int userId);
}