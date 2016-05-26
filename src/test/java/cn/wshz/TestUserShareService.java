package cn.wshz;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserShareService;

public class TestUserShareService {

	public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		UserShareService service = context.getBean(UserShareService.class);
		User u  = new User();
		u.setId(224);
		u.setInvite_code("b86df870f1ed64ddb6f1522f6f26680c");
		u.setUser_identity(45678732);
		service.getUserShareURLByQiNiuPic(u  );
	}
}
