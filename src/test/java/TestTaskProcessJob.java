

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.db.mapper.UserTaskFinishJobMapper;

public class TestTaskProcessJob {

	private static Logger logger = LoggerFactory.getLogger(TestTaskProcessJob.class);
	
	public static void main(String[] args)throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		UserTaskFinishJobMapper service1 = context.getBean(UserTaskFinishJobMapper.class) ;
		
		Thread.sleep(3000);
		//logger.error("appTask=" + service1.loginAuthCode("18612693280", "1244"));
		System.out.println(service1.getWaitings("money_user_task_finish_job", 2, 10));
	}

}
