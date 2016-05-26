package com.cyhd;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.UserSystemMessage;
import com.cyhd.service.impl.UserIntegalIncomeService;

public class TestintegalIncomeService {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		UserIntegalIncomeService service = context.getBean(UserIntegalIncomeService.class);
		service.addShare(1196, 100, Constants.INTEGAL_SOURCE_WANPU, "QQ", UserSystemMessage.TYPE_INTEGAL_SHARE_JINBI, Constants.platform_ios);
		//service.addShare(1196, 80, Constants.INTEGAL_SOURCE_WANPU, "签到", UserSystemMessage.TYPE_INTEGAL_SHARE_QIANDAO,  Constants.platform_android);
//		Jedis jedis = new Jedis("114.215.130.131",6379);
//		Set<String> keys = jedis.keys("*_media2_*");
//		for(String key:keys){
//			System.out.println(key+" -> "+jedis.get(key));
//		}
//		System.out.println("ok");
	}
}
