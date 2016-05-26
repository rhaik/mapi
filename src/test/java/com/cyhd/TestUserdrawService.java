package com.cyhd;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.impl.UserDrawService;

public class TestUserdrawService {

	public static void main(String[] args) {
		//test();
	}

	private static void test() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		UserDrawService userDrawService = context.getBean(UserDrawService.class);
		int userId = 1076;
		boolean flag = userDrawService.addUserDrawByFriend(userId, "Test");
		System.err.println(flag);
	}
}
