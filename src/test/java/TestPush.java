

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.po.UserSystemMessage;
import com.cyhd.service.push.PushService;

public class TestPush {

	private static Logger logger = LoggerFactory.getLogger(TestPush.class);
	
	public static void main(String[] args)throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		PushService service1 = context.getBean(PushService.class) ;
//		
//		Thread.sleep(3000);
//		//logger.error("appTask=" + service1.loginAuthCode("18612693280", "1244"));
////		JSONObject params = new JSONObject();
////		params.put("type", 111);
////		service1.push(1, "测试push", params, true);
//		
		service1.notifyUserSystemPrompt(1196, "说明:sort={1-最有帮助,2-最高评分,3-最低评分,4-最新发表}获得用户的所有评论,在上面#{获的用户的评论}中取得！",UserSystemMessage.PUSH_CLIENT_TYPE_ANDROID);
		//service1.notifyUserSystemPrompt(835, "欢迎来试用！",UserSystemMessage.PUSH_CLIENT_TYPE_ANDROID);
		
		System.out.print(toDate(-1));
	}
	
	private static Date toDate(int second){
		return new Date((long)second * 1000);
	}

}
