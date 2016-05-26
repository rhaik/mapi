package com.cyhd;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.impl.UserTaskReportService;

public class TestUserTaskReportService {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		UserTaskReportService userTaskReportService  = context.getBean(UserTaskReportService.class);
		userTaskReportService.reportChannelTaskFinsh();
	}
}
