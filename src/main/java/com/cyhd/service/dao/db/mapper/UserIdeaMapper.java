package com.cyhd.service.dao.db.mapper;

import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserIdea;


@Repository
public interface UserIdeaMapper {


	@Insert("INSERT INTO `money_user_idea` (`user_id`, `content`, `version`, `equipment`, `create_time`) VALUES "
			+ "(#{user_id}, #{content}, #{version}, #{equipment}, #{create_time})")
	public int add(UserIdea idea);
	
}