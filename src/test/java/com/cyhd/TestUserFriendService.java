package com.cyhd;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.impl.UserFriendService;

public class TestUserFriendService {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		UserFriendService service = context.getBean(UserFriendService.class);
		for(int i = 605;i< 623; i++){
			//service.addEffectiveInvite(577, i, i-300, "2016-05-08");
		}
	}
}
