

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserService;
import com.google.gson.JsonObject;

public class TestUser {

	private static Logger logger = LoggerFactory.getLogger(TestUser.class);
	
	public static void main(String[] args)throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		UserService service1 = context.getBean(UserService.class) ;
		
		Thread.sleep(3000);
		int userId = 108;
		
		User u = service1.getUserById(userId);
//		System.out.println(u.isTaskAppComplete());
//		System.out.println(u.isTaskInviteComplete());
//		System.out.println(u.isTaskAppComplete());
		
		
		//Gson gson = new Gson();
		//gson.
		JsonObject jo = new JsonObject();
		jo.addProperty("name", u.getName());
		jo.addProperty("mobile", u.getMobile());
		
		JsonObject jo_p = new JsonObject();
		jo_p.add("user", jo);
		jo_p.addProperty("ticket", u.getTicket());
		
		System.out.println(jo_p.toString());
		
//		service1.setTaskAppComplete(userId);
//		service1.setTaskInviteComplete(userId);
//		service1.setTaskShareComplete(userId);
//		
//		System.out.println(u.isTaskAppComplete());
//		System.out.println(u.isTaskInviteComplete());
//		System.out.println(u.isTaskAppComplete());
	}

}
