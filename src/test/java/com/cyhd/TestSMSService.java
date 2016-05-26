package com.cyhd;

import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.common.util.DateUtil;
import com.cyhd.service.dao.po.UserEnchashment;
import com.cyhd.service.impl.SmsService;
import com.cyhd.service.impl.UserEnchashmentService;

import junit.framework.Assert;

public class TestSMSService {

	public static void main(String[] args) {
		//testSendByTime();
		//testSendGoods();
	}
	
	public static void testSendByTime(){
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		SmsService smsService = context.getBean(SmsService.class);
		UserEnchashmentService enchashmentService = context.getBean(UserEnchashmentService.class);
		Date successStart = DateUtil.parseCommonDate("2015-07-19 10:00:00");
		Date successEnd = new Date();
		List<UserEnchashment> users = enchashmentService.getEnchashSuccessUsersByTime(successStart, successEnd);
		System.out.println(users);
	}
	
	public static void testSendGoods(){
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		SmsService smsService = context.getBean(SmsService.class);
		boolean status = smsService.sendSendGoodsPrompt("15201683235", "恭喜你，中奖啦，现在给你发货啦");
		Assert.assertTrue(status);
	}
}
