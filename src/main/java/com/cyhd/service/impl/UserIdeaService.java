package com.cyhd.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.service.dao.db.mapper.UserIdeaMapper;
import com.cyhd.service.dao.po.UserIdea;


@Service
public class UserIdeaService extends BaseService {

	@Resource
	private UserIdeaMapper userIdeaMapper;
	
	/**
	 * 添加意见反溃
	 * 
	 * @param UserIdea userIdea
	 * 
	 * @return boolean
	 */
	public boolean save(UserIdea userIdea){
		 return userIdeaMapper.add(userIdea) >=1;
	}
}
