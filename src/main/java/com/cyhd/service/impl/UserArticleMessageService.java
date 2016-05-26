package com.cyhd.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import com.cyhd.service.dao.db.mapper.UserArticleMessageMapper;
import com.cyhd.service.dao.po.UserAppMessage;
import com.cyhd.service.dao.po.UserArticleMessage;

@Service
public class UserArticleMessageService {

	@Resource
	private UserArticleMessageMapper userArticleMessageMapper;
	
	public boolean addUserArticleMessage(UserArticleMessage userArticleMessage){
		return userArticleMessageMapper.addMessage(userArticleMessage) > 0;
	}
	
	public int getCount(int user_id,int client_type){
		return  userArticleMessageMapper.getCount(user_id,client_type);
	}

	public List<UserArticleMessage> getMessagesByUserId(int userid, long lastId, int size,int client_type){
		return userArticleMessageMapper.getMessagesByuUserId(userid, lastId, size,client_type);
	}
	
	public int getCountByLastId(int userid,long lastId,int client_type){
		return userArticleMessageMapper.getCountByLastId(userid, lastId,client_type);
	}
	public UserArticleMessage getLastArticleMessageByUserId(int userid,int client_type){
		return userArticleMessageMapper.getLastArticleMessageByUserId(userid,client_type);
	}
	
}
