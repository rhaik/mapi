//package com.cyhd;
//
//import org.objectweb.asm.tree.IntInsnNode;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import com.cyhd.service.constants.Constants;
//import com.cyhd.service.dao.po.App;
//import com.cyhd.service.dao.po.AppTask;
//import com.cyhd.service.dao.po.User;
//import com.cyhd.service.dao.po.UserTask;
//import com.cyhd.service.impl.AppTaskService;
//import com.cyhd.service.impl.UserService;
//import com.cyhd.service.impl.UserTaskCalculateService;
//import com.cyhd.service.impl.UserTaskService;
//import com.cyhd.web.common.ClientInfo;
//
//public class TestUserTaskService {
//
//	static ApplicationContext context = null;
//	
//	public static void main(String[] args) {
//		
////	
//	
//	static User user = null;
//	static App app = null;
//	static AppTask appTask = null;
//	static UserTaskService userTaskService = null;
//	static UserService userService = null;
//	static AppTaskService appTaskService = null;
//	static UserTask userTask =  null;
//	static ClientInfo clientInfo =  null;
//	
//	private static void init(){
//		context = new ClassPathXmlApplicationContext("applicationContext.xml");
//		userTaskService = context.getBean(UserTaskService.class);
//		appTaskService = context.getBean(AppTaskService.class);
//		
//		user = userService.getUserById(300);
//		
//		int taskId =  866;
//		appTask = appTaskService.getAppTask(taskId );
//		app = appTaskService.getApp(appTask.getApp_id());
//		userTask = userTaskService.getUserTask(user.getId(), appTask.getId());
//		
//		clientInfo = new ClientInfo();
//		//real is 2B0224F3-3A98-48A5-AA35-FC40772F76DE
//		//使用uuid的方式产生一个 不好 UUID.randomUUID().toString().toUpperCase()
//		clientInfo.setIdfa("ZZ41AD0A-WSHZ-C49E-ZIY1-ED3D19745281");
//		//实际202.108.31.61
//		clientInfo.setIpAddress("202.108.31.60");
//		clientInfo.setOs("iPhone OS8.2");
//	}
//	
//	public void testReviceTask(){
//		userTaskService.addTask(user.getId(), appTask, clientInfo);
//	}
//	
//	public void testActiveUserTask(){
//		userTaskService.onActive(user.getId(), appTask.getId());
//	}
//	
//	public void testFinshUserTask(){
//		UserTaskCalculateService userTaskCalculateService = context.getBean(UserTaskCalculateService.class);
//		userTaskCalculateService.onFinishTask(user, app, appTask, userTask, false, null, Constants.platform_ios);
//	}
//	
//	public static void test1(){
//		init();
//		TestUserTaskService service = new TestUserTaskService();
//		service.testReviceTask();
//		service.testActiveUserTask();
//		service.testFinshUserTask();
//	}
//	
//}
