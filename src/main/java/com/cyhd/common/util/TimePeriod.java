package com.cyhd.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePeriod {
	
	private long timePeriod = 0;
	
	public final static int DAY_IN_MS = 1000*60*60*24;
	public final static int HOUR_IN_MS = 1000*60*60;
	public final static int MINUTE_IN_MS = 1000*60;
	public final static int SECOND_IN_MS = 1000;
	
	public TimePeriod(Date later, Date earlier)
	{
		setTime(later, earlier);
	}
	public TimePeriod() {}
	public void setTime(Date later, Date earlier)
	{
		long earlierTime = (earlier==null) ? 0 : earlier.getTime();
		long laterTime = (later==null) ? 0 : later.getTime();
		timePeriod = laterTime - earlierTime;
	}
	
	public int getDay() { return getDay(timePeriod); }	
	public int getHour() { return getHour(timePeriod); }	
	public int getMinute() { return getMinute(timePeriod); }
	public int getSecond() { return getSecond(timePeriod); }
	public int getMillisecond() { return getMillisecond(timePeriod); }
	
	private static int getDay(long tp) { return (int)(tp / DAY_IN_MS); }
	private static int getHour(long tp) { return (int)((tp % DAY_IN_MS) / HOUR_IN_MS); }
	private static int getMinute(long tp) { return (int)(((tp % DAY_IN_MS) % HOUR_IN_MS) / MINUTE_IN_MS); }
	private static int getSecond(long tp) { return (int)((((tp % DAY_IN_MS) % HOUR_IN_MS) % MINUTE_IN_MS) / SECOND_IN_MS); }
	private static int getMillisecond(long tp) { return (int)((((tp % DAY_IN_MS) % HOUR_IN_MS) % MINUTE_IN_MS) % SECOND_IN_MS); }
	
	
	public static String formatSecond(int seconds){
		if(seconds <= 0){
			return "00:00";
		}
		int hour = 0;
		int minute = 0;
		int sec = 0;
		hour = seconds/3600;
		minute = (seconds % 3600)/60;
		sec = (seconds % 3600) %60;
		
		String str = "";
		if(hour > 0){
			str += hour +":";
		}
		if(minute < 10)
			str += "0"+ minute + ":";
		else 
			str += minute + ":";
		
		if(sec < 10)
			str += "0"+ sec;
		else 
			str += sec;
		
		return str;
	}
	
	public static String remain(long tp) {
		StringBuffer sb = new StringBuffer();
		int h = (int) ((tp % DAY_IN_MS) / HOUR_IN_MS);
		if (h > 0)
			sb.append(h).append("小时");

		int m = (int) (((tp % DAY_IN_MS) % HOUR_IN_MS) / MINUTE_IN_MS);
		if (m > 0)
			sb.append(m).append("分钟");

		int s = (int) ((((tp % DAY_IN_MS) % HOUR_IN_MS) % MINUTE_IN_MS) / SECOND_IN_MS);
		if (s > 0)
			sb.append(s).append("秒");

		if(sb.length() == 0){
			sb.append("1秒");
		}
		return sb.toString();
	}
	
	public static String remain(Date time)
	{
		long n = System.currentTimeMillis();
		long t = (time==null) ? n : time.getTime();
		long tp = t - n;
		
		return new StringBuffer().append(getDay(tp)).append("天")
								  .append(getHour(tp)).append("小时")
								  .append(getMinute(tp)).append("分").toString();
	}
	
	public static int getBeforeDay(Date time) {
		long n = System.currentTimeMillis();
		long t = (time==null) ? n : time.getTime();
		long tp = n - t;
		
		return (int)(tp / DAY_IN_MS);
	}
	
	public static int getBeforeDay(long time) {
		long n = System.currentTimeMillis();
		long t = time<=0? n : time;
		long tp = n - t;
		
		return (int)(tp / DAY_IN_MS);
	}
	
	public static String before(Date time)
	{
		long n = System.currentTimeMillis();
		long t = (time==null) ? n : time.getTime();
		long tp = n - t;
		
		int d = (int)(tp / DAY_IN_MS);
		if(d >= 3) return new SimpleDateFormat("yyyy-MM-dd").format(time);
		return formatLessDays(tp, d);
	}
	
	public static String before(long time)
	{
		long n = System.currentTimeMillis();
		long t = time<=0? n : time;
		long tp = n - t;
		
		int d = (int)(tp / DAY_IN_MS);
		if(d >= 3) return new SimpleDateFormat("yyyy-MM-dd").format(time);
		return formatLessDays(tp, d);
	}
	
	public static String beforeForQq(long time)
	{
		long n = System.currentTimeMillis();
		long t = time<=0? n : time;
		long tp = n - t;
		
		int d = (int)(tp / DAY_IN_MS);
		if(d >= 3) return new SimpleDateFormat("yyyy-MM-dd").format(time);
		return formatLessDays(tp, d);
	}
	
//	public static String beforeForCateAndTag(Date time)
//	{
//		long n = System.currentTimeMillis();
//		long t = (time==null) ? n : time.getTime();
//		long tp = n - t;
//		
//		int d = (int)(tp / DAY_IN_MS);
//		if(d >= 365) return new SimpleDateFormat("yy-MM-dd").format(time);
//		if(d >= 1) return new SimpleDateFormat("MM-dd").format(time);
//		
//		int h = (int)((tp % DAY_IN_MS) / HOUR_IN_MS);
//		
//		if(h >= 1) return h + "小时前";
//		int m = (int)(((tp % DAY_IN_MS) % HOUR_IN_MS) / MINUTE_IN_MS);
//		if(m > 0) return m + "分钟前";
//		
//		int s = (int)((((tp % DAY_IN_MS) % HOUR_IN_MS) % MINUTE_IN_MS) / SECOND_IN_MS);
//		if(s > 0) return s + "秒钟前";
//		
//		return "1秒钟前";
//	}
	
	public static String beforeForSubscription(long time)
	{
		long n = System.currentTimeMillis();
		long t = time<=0? n : time;
		long tp = n - t;
		
		int d = (int)(tp / DAY_IN_MS);
		if(d >= 365) return new SimpleDateFormat("yy-MM-dd").format(time);
		if(d >= 5) return new SimpleDateFormat("MM-dd").format(time);
		return formatLessDays(tp, d);
	}
	public static String beforeForSubscriptionYear(long time)
	{
		long n = System.currentTimeMillis();
		long t = time<=0? n : time;
		long tp = n - t;
		
		int d = (int)(tp / DAY_IN_MS);
		
		if(d >= 3) {
			Calendar l = Calendar.getInstance();
			int year = l.get(Calendar.YEAR);
			l.setTimeInMillis(t);
			int tYear = l.get(Calendar.YEAR);
			
			if (year == tYear) {
				return new SimpleDateFormat("M月d日").format(time);
			} else {
				return new SimpleDateFormat("M/d/yyyy").format(time);
			}
		}
		return formatLessDays(tp, d);
	}
	private static String formatLessDays(long tp, int d) {
		if(d > 0) return d + "天前";
		
		int h = (int)((tp % DAY_IN_MS) / HOUR_IN_MS);
		if(h > 0) return h + "小时前";
		
		int m = (int)(((tp % DAY_IN_MS) % HOUR_IN_MS) / MINUTE_IN_MS);
		if(m > 0) return m + "分钟前";
		
		int s = (int)((((tp % DAY_IN_MS) % HOUR_IN_MS) % MINUTE_IN_MS) / SECOND_IN_MS);
		if(s > 0) return s + "秒钟前";
		
		return "1秒钟前";
	}
	public static String beforeForSubscription(Date date)
	{
		if(date == null)
			date = new Date();
		return beforeForSubscription(date.getTime());
	}
	
	public static String after(Date time)
	{
		long n = System.currentTimeMillis();
		long t = (time==null) ? n : time.getTime();
		long tp = t - n;
		
		int d = (int)(tp / DAY_IN_MS);
		if(d > 0) return d + "天";
		
		int h = (int)((tp % DAY_IN_MS) / HOUR_IN_MS);
		if(h > 0) return h + "小时";
		
		int m = (int)(((tp % DAY_IN_MS) % HOUR_IN_MS) / MINUTE_IN_MS);
		if(m > 0) return m + "分钟";
		
		int s = (int)((((tp % DAY_IN_MS) % HOUR_IN_MS) % MINUTE_IN_MS) / SECOND_IN_MS);
		if(s > 0) return s + "秒钟";
		
		return "1秒钟";
	}
	//精确定位时间
	public static String pinPoint(Date time){
//		long n = System.currentTimeMillis();
//		long t = (time==null) ? n : time.getTime();
//		long tp = n - t;
//		
//		int d = (int)(tp / DAY_IN_MS);
		//if(d >= 0) 
			return new SimpleDateFormat("MM-dd").format(time);
		//else
		//	return new SimpleDateFormat("hh:mm").format(time);		
	}
	
	public static String getDetailTimeFormat(Date time){
		if(time == null) time = new Date();
		return new SimpleDateFormat("MM月dd日 HH:mm").format(time);
	}
	/**
	 * date和当前时间是否同一天
	 * @param date
	 * @return
	 */
	public static boolean isToday(Date date){//date和当前时间是否同一天
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		return sf.format(date).equals(sf.format(new Date()));
	}
	/**
	 * date和当前时间是否同一周
	 * @param date
	 * @return
	 */
	public static boolean isThisWeek(Date date){//date和当前时间是否同一周
		Calendar  c1  =   new GregorianCalendar();
		Calendar  c2  =   new GregorianCalendar();
		c1.setTime(date);
		c2.setTime(new Date());
		c1.setFirstDayOfWeek(Calendar.MONDAY);
		c2.setFirstDayOfWeek(Calendar.MONDAY);
		if(c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR))
			return true;
		else
			return false;
	}
	/**
	 * date和当前时间是否同一月
	 * @param date
	 * @return
	 */
	public static boolean isThisMonth(Date date){//date和当前时间是否同一月
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
		return sf.format(date).equals(sf.format(new Date()));
	}
	public static void main(String[] args) {
//		System.out.println(TimePeriod.before((2105421918L /1000) *1000));
		System.out.println(TimePeriod.before(new Date()));
		Calendar   c1   =   new   GregorianCalendar(2011,   4,   15,   14,   22,   33);
		System.out.println(isToday(c1.getTime()));
		System.out.println(isThisWeek(c1.getTime()));
		System.out.println(isThisMonth(c1.getTime()));
		System.out.println(beforeForSubscriptionYear(1000));
	}
	/**
	 * 将毫秒转化为秒，保留num位小数
	 * @param mill
	 * @param num
	 * @return
	 */
	public static String millisecond2Second(long mill,int num){
		if(num <= 0) return String.valueOf(mill/SECOND_IN_MS);
		 StringBuilder s = new StringBuilder("0.");
		for(int i = 0; i < num;i++)
			s.append("0");
		java.text.DecimalFormat df = new java.text.DecimalFormat(s.toString());
		return df.format((float)mill/SECOND_IN_MS);
	}
	/**
	 * 将毫秒转化为小时，保留num位小数
	 * @param mill
	 * @param num
	 * @return
	 */
	public static String millisecond2Hour(long mill,int num){
		if(num <= 0) return String.valueOf(mill/HOUR_IN_MS);
		 StringBuilder s = new StringBuilder("0.");
		for(int i = 0; i < num;i++)
			s.append("0");
		java.text.DecimalFormat df = new java.text.DecimalFormat(s.toString());
		return df.format((float)mill/HOUR_IN_MS);
	}
}
