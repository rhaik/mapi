package com.cyhd.service.channelQuickTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.AppChannelQuickTaskService;
import com.cyhd.service.impl.AppTaskService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.impl.UserTaskService;
import com.cyhd.service.vo.UserTaskVo;
import com.cyhd.web.common.ClientInfo;

public class TestAppChanelQuickTaskService {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		AppChannelQuickTaskService service = context.getBean(AppChannelQuickTaskService.class);
		
		UserService userService = context.getBean(UserService.class);
		AppTaskService appTaskService = context.getBean(AppTaskService.class);
		UserTaskService userTaskService = context.getBean(UserTaskService.class);
		ClientInfo clientInfo = new ClientInfo();
		//real is 2B0224F3-3A98-48A5-AA35-FC40772F76DE
		//使用uuid的方式产生一个 不好 UUID.randomUUID().toString().toUpperCase()
		clientInfo.setIdfa("YZ41AD0A-B14B-C49E-ZIY1-ED3D19745281");
		//实际202.108.31.61
		clientInfo.setIpAddress("202.108.31.60");
		clientInfo.setOs("iPhone OS8.2");
		int userId = 1562;
		
		User user = userService.getUserById(userId);
		Map<String, String> extraParams = new HashMap<String, String>();
		List<UserTaskVo> list = service.getUserTask(clientInfo, user, extraParams);
		System.out.println(list);
	}
	
}
