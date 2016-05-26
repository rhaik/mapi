

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.db.mapper.UserTaskFinishAuditMapper;
import com.cyhd.service.dao.po.UserTaskReport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestReport {

	private static Logger logger = LoggerFactory.getLogger(TestReport.class);
	
	public static void main(String[] args)throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		UserTaskFinishAuditMapper service1 = context.getBean(UserTaskFinishAuditMapper.class) ;
		
		Thread.sleep(3000);
		service1.addAudit(11111, 2131212, "", 0);
//		
//		UserTaskReport report = new UserTaskReport();
//		report.setUser_id(1);
//		report.setUser_task_id(2);
//		report.setDevicetype(2);
//		report.setDid("111");
//		report.setReport_index(1);
//		report.setDuration(10);
//		report.setReporttime(new Date());
//		report.setId(11212312);
//		
//		String json = reportToString(report);
//		
//		System.out.println(json);
//		
//		System.out.println(stringToReport(json));
		
	}
	
	private static String reportToString(UserTaskReport report){
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		return gson.toJson(report);
	}
	
	private static UserTaskReport stringToReport(String str){
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		return gson.fromJson(str, UserTaskReport.class);
	}

}
