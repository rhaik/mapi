package com.cyhd;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.impl.UserMessageService;

public class TestMessageTotal {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		UserMessageService userMessageService = context.getBean(UserMessageService.class);
		long lastId = 0;
		int userid = 85;
		Map<String, Object> appmMap = userMessageService.getAppMessageNotReadCount(userid , lastId );
//		System.out.println(appmMap);
		Object list= null;
		
//		 list= userMessageService.getSysMessageNotReadCount(userid, lastId);
//		System.out.println(list);
//		list = userMessageService.getAppMessageNotReadCount(userid, 34);
//		System.out.println(list);
		
		list = userMessageService.getFriendMessageNotReadCount(userid, lastId);
		System.out.println(list);
	}
	
}
