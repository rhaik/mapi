package com.cyhd.service.dao.db.mapper;


import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.AppTaskChannel;


@Repository
public interface AppTaskChannelMapper {
	@Select("select * from money_app_task_channel where task_id=#{0}")
	public AppTaskChannel getAppTaskChannelByTaskId(int taskId);
	
	@Select("select * from money_app_task_channel where adid=#{1} limit 1")
	public AppTaskChannel getAppTaskChannelByAdid(int adid);
	
	public int addAppTaskChannel(AppTaskChannel channel);
}