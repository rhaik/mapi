package com.cyhd.service.dao.db.mapper;


import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserTaskDianru;


@Repository
public interface UserTaskChannelMapper {
	@Insert("INSERT INTO `money_user_task_dianru` (`user_task_id`, `hashid`, `appid`, `adid`, `adname`, `createtime`, `userid`, `deviceid`, `source`, `point`, `time`) VALUES "
			+ "(#{user_task_id}, #{hashid}, #{appid}, #{adid}, #{adname}, now(), #{userid}, #{deviceid}, #{source}, #{point}, #{time})")
	public int addUserTaskDianru(UserTaskDianru channel);
}