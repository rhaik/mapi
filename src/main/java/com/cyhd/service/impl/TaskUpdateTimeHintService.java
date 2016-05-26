package com.cyhd.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.LiveAccess;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.constants.PropertiesConstants;

@Service
public class TaskUpdateTimeHintService {

	@Resource
	private PropertiesService propertiesService;
	
	/**一个小时*/
	private final int ttlInMillis = Constants.minutes_millis * 3;
	
	private LiveAccess<String> appLive = new LiveAccess<String>(ttlInMillis , null);
	private LiveAccess<String> articleLive = new LiveAccess<String>(ttlInMillis , null);
	
	public String getAppTaskUpdateTimehint(){
		String value = appLive.getElement();
		if(value == null){
			value = propertiesService.getTaskUpdateHint(PropertiesConstants.APP_TASK_HINT);
			appLive = new LiveAccess<String>(ttlInMillis, value);
		}
		return value;
	}
	
	public String getArticleTaskUpdateTimehint(){
		String value = articleLive.getElement();
		if(value == null){
			value = propertiesService.getTaskUpdateHint(PropertiesConstants.ARTICLE_TASK_HINT);
			articleLive = new LiveAccess<String>(ttlInMillis, value);
		}
		return value;
	}
	
}
