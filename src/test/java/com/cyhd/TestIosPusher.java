package com.cyhd;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.push.ios.IosPusher;

public class TestIosPusher {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IosPusher iosPusher = context.getBean(IosPusher.class);
		iosPusher.initApnsServiceByBundleId("im.jiansheng.zhou");
	}
}
