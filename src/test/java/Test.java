import com.cyhd.common.util.DateUtil;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.net.URLEncoder;
import java.util.Date;


public class Test {

	public static void main(String[] args) throws Exception {
//		System.out.println(URLDecoder.decode("/api%2Fv1%2Fdevice%2Ftoken"));
//		String numstr = "50.0";
//		long f = (long) Float.parseFloat(numstr);

		
		Calendar cal = GregorianCalendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		//System.out.println(283819223L % 7280);

//		System.out.println(283819223L % 7280);
//		System.out.println();
//		String str = "%E6%95%B0%E7%B1%B3%E5%9F%BA%E9%87%91%E5%AE";
//		System.out.println(URLDecoder.decode(str,"utf-8"));
//		String adname="秒赚大钱";
//		String encode = URLEncoder.encode(adname, "utf-8");
//		System.out.println(encode);
//		long time =1415178128 ;
//		System.out.println(new Date(time *1000).toLocaleString());
//		long now = new Date().getTime();
//		System.out.println(now);
//		System.out.println(now-time);
		
		System.out.println(0%4);
		String fileName = "aps_development_jiansheng.cer";
		fileName = fileName.substring(fileName.lastIndexOf("_")+1, fileName.lastIndexOf("."));
		System.out.println(fileName);
		System.out.println("0e3ff923ff77304d2f08992ad20682252ca4f9259a3887c8dfa6603114a9d8dc".length());


		Date d1 = DateUtil.parseCommonDate("2015-12-08 23:00:00");
		Date d2 = new Date();
		System.out.println(d1);
		System.out.println(d2);
		System.out.println(DateUtil.getTwoDatesDifDay(d1, d2, false));
	}
}
