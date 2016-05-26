package cn.wshz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestDate {

	public static void main(String[] args) throws Exception {
		//test();
		String dateStr = "2015-04-23";
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 100);
		System.out.println(c.getTime().toLocaleString());
	}

	private static void test() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, 10);
		int midden  = 24*3600;
		int MINUTE = c.get(Calendar.MINUTE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int SECOND = c.get(Calendar.SECOND);
		
		int times = hour*3600+MINUTE*60+SECOND;
		
		System.out.println(midden - times);
	}
}
