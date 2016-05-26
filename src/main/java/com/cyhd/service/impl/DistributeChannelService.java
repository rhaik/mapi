package com.cyhd.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.AppChannelDistributionMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.AppChannelDistribution;


@Service
public class DistributeChannelService extends BaseService {
	
	//缓存第三方渠道任务信息
	private CacheLRULiveAccessDaoImpl<AppChannelDistribution> cacheAppChannel = new CacheLRULiveAccessDaoImpl<AppChannelDistribution>(Constants.hour_millis * 1, 1024);
	
	@Resource
	private AppChannelDistributionMapper appChannelDistributionMapper; 
	
	public AppChannelDistribution getAppChannelDistribution(int channelid) {
		String cacheKey = "AppChannelDistribution_" + String.valueOf(channelid);
		
		AppChannelDistribution acd = cacheAppChannel.get(cacheKey);
		if(acd == null) {
			List<AppChannelDistribution> appChannel = appChannelDistributionMapper.getAppChannelDistribution();
			
			for(AppChannelDistribution channel : appChannel){
				String key = "AppChannelDistribution_" + String.valueOf(channel.getChannel_id());
				cacheAppChannel.set(key, channel);
				if(channelid ==  channel.getChannel_id()) {
					acd = channel;
				}
			}
		}
		return acd;
	}
	
}
