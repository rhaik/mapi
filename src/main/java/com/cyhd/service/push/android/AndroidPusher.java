package com.cyhd.service.push.android;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



import cn.jpush.api.JPushClient;
import cn.jpush.api.common.APIConnectionException;
import cn.jpush.api.common.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

@Service
public class AndroidPusher {
	
	private JPushClient jpushClient = null;
	public static final Logger PUSHLOG = LoggerFactory.getLogger("push");
	@PostConstruct
	public void init(){
		jpushClient = new JPushClient(Configuration.masterSecret, Configuration.appkey, 3);
	}
	
	public PushResult  push(AndroidPushBean bean){
			Map<String, String> exMap = new HashMap<String, String>(2);
			exMap.put("extra_data", bean.getParams().toString());
		  PushPayload payload = PushPayload.newBuilder()
				  .setPlatform(Platform.android())
	                .setAudience(Audience.registrationId(bean.getDeviceToken()))
	                .setNotification(Notification.android(bean.getAlertBody(), bean.getTitle(), exMap))
	                .build();
		  try {
			  	PushResult result = jpushClient.sendPush(payload);
			  	return result;
			} catch (APIConnectionException e) {
				e.printStackTrace();
			} catch (APIRequestException e) {
				PUSHLOG.error("极光--cause by:{}",e);
		}
		 return null;
	}
}
