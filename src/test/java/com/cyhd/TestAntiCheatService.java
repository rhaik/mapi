package com.cyhd;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.impl.AntiCheatService;

public class TestAntiCheatService {

	public static void main(String[] args) {
		//AntiCheatService
		test1();
	}

	private static void test1() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		AntiCheatService antiCheatService = context.getBean(AntiCheatService.class);
		String ip = "203.208.60.188";
		String ip2 = "202.108.31.61";
		boolean same = antiCheatService.isSameAddressIp(ip, ip2);
		System.err.println(same);
	}
}
