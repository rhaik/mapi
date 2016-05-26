package com.cyhd;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserTask;
import com.cyhd.service.impl.AppTaskService;
import com.cyhd.service.impl.AppVendorService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.impl.UserTaskService;
import com.cyhd.web.common.ClientInfo;



/***
 * 用于测试厂商的Test
 * 目的是测试接口通没有
 * 通啦 真机测试环境测试
 * 
 * @author yurunmin
 *
 */
public class TestVendorTaskService {

	public static void main(String[] args) {
		test1();
	}

	private static void test1() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		AppVendorService appVendorService = context.getBean(AppVendorService.class);
		UserService userService = context.getBean(UserService.class);
		AppTaskService appTaskService = context.getBean(AppTaskService.class);
		UserTaskService userTaskService = context.getBean(UserTaskService.class);
		ClientInfo clientInfo = new ClientInfo();
		//real is 2B0224F3-3A98-48A5-AA35-FC40772F76DE
		clientInfo.setIdfa("QQZIE0D9-3CBB-4BA4-0000-A59EF66D4E4A");
		//实际202.108.31.61
		clientInfo.setIpAddress("202.108.31.60");
		clientInfo.setOs("iPhone OS8.2");
		int userId = 1;
		String idfas = "0000480D-38F5-42A2-8F6E-EF9DB5A3C248,0000690A-57D2-4147-96BE-4B6544A47CDD,00008CBF-A983-98C9-C7D0-CF0BF448F037,000099B9-2F59-DDF1-EA63-FB3FB8C886F1,0000AB3E-E0A2-4964-8EE3-01E330836C46,0000B4C6-9E9F-4566-ADE2-FBA333C09F27,0000BECB-63C3-408E-850E-3FACF85BA490,0000E0D6-3CBB-4BA4-A098-A59EF66D4E4A,0000F393-9535-4360-BF0E-6061EBC6D518,000149AD-48D6-4AC6-AC17-AA261D0683B2,00016448-CC56-4C26-9F12-1D9C633FC490,00016BB7-03E1-4DB5-BE51-0CF5B2F31638,00019BDB-BDAA-4639-A95E-F7863744AB5D,0001B450-02CC-448C-B5BF-B187DC5FCF83,0001E162-01F4-B109-B065-05AF003A8C3F,0001EB55-B572-44F7-8DDC-616E01DAEE9C,0001F4F5-11D8-4BFC-8A2B-1B3B8F59D4DA,0001FDF0-FC72-4016-B0BA-36D74209507C,000200B3-C0A3-4057-BF4C-353DE350247F,00023ABE-BA0D-B9CB-3872-5D13F8EB7D4B";
		//835  832：启信宝
		//这是变的参数
		int apptaskid = 906;
		//可变参数结束
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppTask appTask = appTaskService.getAppTask(apptaskid);
		User user  = userService.getUserById(userId);
		boolean flag = false;
		if(appTask.isVendorTask()){
			System.err.println("有厂商");
			flag = appVendorService.onClick(user, appTask, clientInfo);
		}
		System.err.println("接任务情况："+flag);
		if(flag){
			UserTask newUt =userTaskService.getUserTask(userId, apptaskid);
			if(newUt == null){
				newUt =  userTaskService.addTask(userId, appTask, clientInfo);
			}
			if(newUt != null){
				appTaskService.onUserReceiveTask(apptaskid);
			}
		}
	}
}
