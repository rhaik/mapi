package cn.wshz;

import java.math.BigInteger;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.db.mapper.DeviceMapper;
import com.cyhd.service.impl.DeviceService;
import com.cyhd.service.push.PushService;

public class TestDeviceService {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		DeviceMapper mapper = context.getBean(DeviceMapper.class);
//		List<Pair<BigInteger,Long>> datas = mapper.getAllUserIdAndId(0, 100);
//		System.out.println(datas.get(0));
//		System.err.println(datas.get(0).second.getClass());
//		System.err.println(datas.get(0).first.getClass());
//		System.out.println(datas);
		DeviceService service = context.getBean(DeviceService.class);
		System.out.println("ok");
		PushService push = context.getBean(PushService.class);
		//push.notifyAllUsersSystemPrompt("Test push");
	}
}
