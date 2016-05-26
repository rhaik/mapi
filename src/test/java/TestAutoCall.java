

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.impl.AutoCallService;

public class TestAutoCall {

	private static Logger logger = LoggerFactory.getLogger(TestAutoCall.class);
	
	public static void main(String[] args)throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		AutoCallService service1 = context.getBean(AutoCallService.class) ;
		
		Thread.sleep(3000);
		logger.error("appTask=" + service1.loginAuthCode("18612693280", "1244"));
	}

}
