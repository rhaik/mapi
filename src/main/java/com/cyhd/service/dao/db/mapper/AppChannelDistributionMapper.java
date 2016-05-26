package com.cyhd.service.dao.db.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.AppChannelDistribution;


@Repository
public interface AppChannelDistributionMapper {
	
	@Select("select * from money_app_channel_distribution")
	public List<AppChannelDistribution> getAppChannelDistribution();
}