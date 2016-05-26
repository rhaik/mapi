package com.cyhd;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.AppChannelQuickTaskService;
import com.cyhd.service.impl.AppTaskService;
import com.cyhd.service.impl.UserService;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.common.util.ClientInfoUtil;

public class TestAppQuickTaskService {

	public static void main(String[] args) {
		//testClick();
		testClickAndReport();
	}

	private static void testClickAndReport() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		AppChannelQuickTaskService appChannelQuickTaskService = context.getBean(AppChannelQuickTaskService.class);
		UserService userService = context.getBean(UserService.class);
		AppTaskService appTaskService = context.getBean(AppTaskService.class);
		
		ClientInfo clientInfo = ClientInfoUtil.getDefaultClientInfo();
		clientInfo.setIdfa("WSHSYDBB-WSHZ-0323-HHXX-TTXSWXYZ0325");
		clientInfo.setIpAddress("202.108.31.60");
		//变化的值
		int task = 563;
		
		User user = userService.getUserById(1);
		AppTask appTask = appTaskService.getAppTask(task);
		Map<String, String> extraParams = null;
		//boolean flag = appChannelQuickTaskService.reportTaskFinsh(clientInfo, appTask, extraParams);
		//System.out.println(flag);
	}

	private static void testClick() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		AppChannelQuickTaskService appChannelQuickTaskService = context.getBean(AppChannelQuickTaskService.class);
		UserService userService = context.getBean(UserService.class);
		AppTaskService appTaskService = context.getBean(AppTaskService.class);
		
		ClientInfo clientInfo = ClientInfoUtil.getDefaultClientInfo();
		clientInfo.setIdfa("WSHSYDBB-WSHZ-0323-HHXX-TTXSWXYZ0325");
		clientInfo.setIpAddress("202.108.31.60");
		//变化的值
		int task = 563;
		
		User user = userService.getUserById(1);
		AppTask appTask = appTaskService.getAppTask(task);
		Map<String, String> extraParams = null;
		
		boolean clickStatus = appChannelQuickTaskService.click(clientInfo, user, appTask, extraParams);
		System.out.println(clickStatus);
	}
}
