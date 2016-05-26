package com.cyhd.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.SourceMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.Source;


@Service
public class SourceService extends BaseService {
	
	private CacheLRULiveAccessDaoImpl<Source> cachedSource = new CacheLRULiveAccessDaoImpl<Source>(Constants.hour_millis * 1, 1024);

	@Resource
	private SourceMapper sourceMapper;
	
	
	public Source getSourceByIdentity(String identity) {
		Source s = cachedSource.get(identity);
		if(s == null) {
			s = sourceMapper.getSourceByIdentity(identity);
			cachedSource.set(identity, s);
		}
		return s;
	}
}
