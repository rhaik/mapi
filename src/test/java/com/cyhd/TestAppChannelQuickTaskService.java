package com.cyhd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.channelQuickTask.IQuickTaskService;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.AppChannelQuickTaskService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.vo.UserTaskVo;
import com.cyhd.web.common.ClientInfo;

public class TestAppChannelQuickTaskService {

	//构造请求所需参数
		ClientInfo clientInfo = null;
		User user = null;
		Map<String, String> extraParams = null;
		ApplicationContext context = null;
		UserService userService = null;
		private void initParameters(){
				context = new ClassPathXmlApplicationContext("applicationContext.xml");
				userService = context.getBean(UserService.class);
				
				clientInfo = new ClientInfo();
				clientInfo.setIpAddress("202.108.31.60");
				clientInfo.setOs("iPhone OS8.4.1");
				clientInfo.setModel("iPhone");
				clientInfo.setIdfa("WSHSYDBB-WSHZ-0323-HHXX-TTXSWXYZ0325");
				int userId = 1;
				
				user = userService.getUserById(userId);
				
				extraParams = new HashMap<String, String>();
				extraParams.put("ua", "Mozilla/5.0 (iPhone; CPU iPhone OS 8_4_1 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Version/8.0 Mobile/12H321 Safari/600.1.4");
		}
		
	
	public static void main(String[] args) {
		TestAppChannelQuickTaskService taskService = new TestAppChannelQuickTaskService();
		
		taskService.initParameters();
		taskService.testGetTaskList();
	}

	private  void testGetTaskList() {
		
		AppChannelQuickTaskService appChannelQuickTaskService = context.getBean(AppChannelQuickTaskService.class);
//		List<UserTaskVo>  userTaskVo = appChannelQuickTaskService.getUserTask(clientInfo, user, extraParams);
//		System.out.println(userTaskVo);
		
		
		
	}
}
