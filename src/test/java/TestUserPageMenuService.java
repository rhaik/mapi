import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.po.UserHomePageMenu;
import com.cyhd.service.impl.UserHomePageMenuService;


public class TestUserPageMenuService {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		UserHomePageMenuService service = context.getBean(UserHomePageMenuService.class);
		int clientType = UserHomePageMenu.IOS;
		String currentVersion = "1.1.0";
		List<UserHomePageMenu> data = service.getHomePageMenus(clientType,currentVersion);
		System.out.println(data.size());
		clientType = UserHomePageMenu.ANDROID;
		currentVersion = "1.1.1";
		data = service.getHomePageMenus(clientType,currentVersion);
		System.out.println(data);
	}
}
