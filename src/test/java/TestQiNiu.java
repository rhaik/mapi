import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.impl.UserShareService;


public class TestQiNiu {

	public static void main(String[] args) {
		//getUserShareURLByQiNiuPic
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		UserShareService service = context.getBean(UserShareService.class);
		UserService userService =context.getBean(UserService.class);
		long start = System.currentTimeMillis();
		User user = userService.getUserById(268);
		service.getUserShareURLByQiNiuPic(user);
		System.out.println(System.currentTimeMillis()- start);
		user = new User();
		user.setInvite_code("test32");
		service.getUserShareURLByQiNiuPic(user);
		user = new User();
		user.setInvite_code("test34");
		service.getUserShareURLByQiNiuPic(user);
		System.out.println("ok");
	}
}
