package com.cyhd;

import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserTask;
import com.cyhd.service.impl.AppTaskService;
import com.cyhd.service.impl.ChannelService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.impl.UserTaskService;
import com.cyhd.service.vo.AppTaskChannelVo;
import com.cyhd.web.common.ClientInfo;

public class TestChannelService {

	public static void main(String[] args) {
		//test();
		//testRevice();
		//testGetChannel();
		//testReviceAndReport();
	}

	private static void test2() {
		String idfa = "2B0224F3-3A98-48A5-AA35-FC40772F76DE";
		String[] idfas = idfa.split("-");
		for(String tmp:idfas){
			System.out.println(Long.parseLong(tmp, 32));
		}
		System.out.println(UUID.randomUUID().toString());
		//10+22 
	}
	/***
	 * 只接任务
	 */
	private static void testRevice() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		ChannelService channelService = context.getBean(ChannelService.class);
		UserService userService = context.getBean(UserService.class);
		AppTaskService appTaskService = context.getBean(AppTaskService.class);
		UserTaskService userTaskService = context.getBean(UserTaskService.class);
		ClientInfo clientInfo = new ClientInfo();
		//real is 2B0224F3-3A98-48A5-AA35-FC40772F76DE
		//使用uuid的方式产生一个 不好 UUID.randomUUID().toString().toUpperCase()
		clientInfo.setIdfa("ZZ41AD0A-WSHZ-C49E-ZIY1-ED3D19745281");
		//实际202.108.31.61
		clientInfo.setIpAddress("202.108.31.60");
		clientInfo.setOs("iPhone OS8.2");
		int userId = 1801;
		
		//可变参数
		int  appTaskId =874 ;
		//可变参数结束
		
		User u = userService.getUserById(userId);
		AppTask appTask = appTaskService.getAppTask(appTaskId);
		
		boolean flag = channelService.isAllowReceiveTask(u, appTask, clientInfo);
		System.err.println("接任务的情况："+flag);
		if(flag){
			UserTask newUt =userTaskService.getUserTask(u.getId(), appTaskId);
			if(newUt == null){
				newUt =  userTaskService.addTask(u.getId(), appTask, clientInfo);
			}
			if(newUt != null){
				appTaskService.onUserReceiveTask(appTaskId);
			}
		}
	}
	/***
	 * 接任务 并上报任务
	 */
	public static void  testReviceAndReport(){
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		ChannelService channelService = context.getBean(ChannelService.class);
		UserService userService = context.getBean(UserService.class);
		AppTaskService appTaskService = context.getBean(AppTaskService.class);
		UserTaskService userTaskService = context.getBean(UserTaskService.class);
		ClientInfo clientInfo = new ClientInfo();
		//real is 2B0224F3-3A98-48A5-AA35-FC40772F76DE
		//使用uuid的方式产生一个 不好 UUID.randomUUID().toString().toUpperCase()
		clientInfo.setIdfa("ZYZYAD0A-Z14Y-Y49E-75A4-ED3D19745281");
		//实际202.108.31.61
		clientInfo.setIpAddress("202.108.31.211");
		clientInfo.setOs("iPhone OS8.2");
		//1697
		int userId = 1801;
		
		//可变参数
		int  appTaskId =878 ;
		//可变参数结束
		
		User u = userService.getUserById(userId);
		AppTask appTask = appTaskService.getAppTask(appTaskId);
		
		//AppTask appTask, UserTask ut, ClientInfo clientInfo
		
		//接任务 
		boolean flag = channelService.isAllowReceiveTask(u, appTask, clientInfo);
		System.err.println("接任务的情况："+flag);
		
		if(flag){
			UserTask newUt =userTaskService.getUserTask(u.getId(), appTaskId);
			if(newUt == null){
				newUt =  userTaskService.addTask(u.getId(), appTask, clientInfo);
				appTaskService.onUserReceiveTask(appTaskId);
			}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//上报任务
		flag = channelService.reportTaskToChannel(appTask,newUt, clientInfo);
		System.err.println("接任务的情况："+flag);
		}	
	}
	
	public static void testGetChannel(){
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		ChannelService channelService = context.getBean(ChannelService.class);
		int taskId = 448;
		AppTaskChannelVo vo = channelService.getAppTaskChannel(taskId );
		System.out.println(vo);
	}
}
