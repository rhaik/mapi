package com.cyhd.service.dao.db.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.AppChannel;


@Repository
public interface AppChannelMapper {
	
	@Select("select * from money_app_channel")
	public List<AppChannel> getAppChannel();
	
	@Select("select * from money_app_channel where id=#{0}")
	public AppChannel getAppChannelById(int id);
}