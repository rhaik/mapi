package cn.wshz;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.db.mapper.AppTaskMapper;
import com.cyhd.service.dao.po.AppTask;

public class TestGenerator {
//appTaskMapperGen
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		AppTask appTask = new AppTask();
		AppTaskMapper appTaskMapperGen =  context.getBean("appTaskMapperGen",AppTaskMapper.class);
		System.out.println(appTaskMapperGen.addAppTask(appTask));
	}
}
