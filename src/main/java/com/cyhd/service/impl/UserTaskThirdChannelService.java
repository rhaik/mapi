package com.cyhd.service.impl;


import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.service.dao.db.mapper.AppChannelMapper;
import com.cyhd.service.dao.db.mapper.AppTaskChannelMapper;
import com.cyhd.service.dao.db.mapper.UserTaskChannelMapper;
import com.cyhd.service.dao.po.UserTaskDianru;


@Service
public class UserTaskThirdChannelService extends BaseService {
	
	@Resource
	private AppChannelMapper appChannelMapper;
	@Resource
	private AppTaskChannelMapper appTaskChannelMapper;
	@Resource
	private UserTaskChannelMapper userTaskChannelMapper;
	
	 
	public boolean addUserTaskDianru(UserTaskDianru channel) {
		return userTaskChannelMapper.addUserTaskDianru(channel) > 0;
	}
}
