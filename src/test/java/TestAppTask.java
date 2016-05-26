

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.impl.AppTaskService;
import com.cyhd.service.impl.UserTaskService;

public class TestAppTask {

	private static Logger logger = LoggerFactory.getLogger(TestAppTask.class);
	
	public static void main(String[] args)throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		AppTaskService service1 = context.getBean(AppTaskService.class) ;
		UserTaskService service = context.getBean(UserTaskService.class) ;
		
		Thread.sleep(3000);
//		logger.error("appTask=" + service1.getAppTask(16));
//		logger.error("app" + service1.getApp(17));
//		
//		logger.error("list=" + service1.getValidTasks());
		
		List<Integer> taskIds = new ArrayList<Integer>();
		taskIds.add(1);
		taskIds.add(3);
		//logger.info("tasks={}", service.getUserTasksByTaskIds(1, taskIds));
		
	}

}
