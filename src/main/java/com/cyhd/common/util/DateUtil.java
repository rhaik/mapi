package com.cyhd.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Description:Date操作工具
 * @version 1.0
 */
public class DateUtil{
    
    static class CalendarFactory { 
        private ThreadLocal<Calendar> calendarRef = new ThreadLocal<Calendar>() { 
          protected Calendar initialValue() { 
            return new GregorianCalendar(); 
          } 
        }; 
        private static CalendarFactory instance = new CalendarFactory(); 
       
        public static CalendarFactory getFactory() { return instance; } 
       
        public Calendar getCalendar() { 
          return calendarRef.get(); 
        } 
       
        // Don't let outsiders create new factories directly 
        private CalendarFactory() {} 
      } 
    /**  
     *  格式化日期  
     *    
     *  @param  dateStr  
     *                        字符型日期  
     *  @param  format  
     *                        格式  
     *  @return  返回日期  
     */
    public static java.util.Date parseDate(String dateStr, String format) {
        java.util.Date date = null;
        try {
            DateFormat df = new SimpleDateFormat(format);
            date = df.parse(dateStr);
        } catch (Exception e) {
        }
        return date;
    }

    /**
     * 日期格式为:yyyy-MM-dd HH:mm:ss
     * @param dateStr
     * @return
     */
    public static java.util.Date parseCommonDate(String dateStr) {
        try {
			return common_format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return null;
    }

    /**
     * 格式为:yyyy-MM-dd
     * @param dateStr
     * @return
     */
    public static java.util.Date parseDate(String dateStr) {
        return parseDate(dateStr, "yyyy-MM-dd");
    } 

    public static java.util.Date parseDate(java.sql.Date date) {
        return date;
    }

    public static java.sql.Date parseSqlDate(java.util.Date date) {
        if (date != null)
            return new java.sql.Date(date.getTime());
        else
            return null;
    }

    public static java.sql.Date parseSqlDate(String dateStr, String format) {
        java.util.Date date = parseDate(dateStr, format);
        return parseSqlDate(date);
    }

    public static java.sql.Date parseSqlDate(String dateStr) {
        return parseSqlDate(dateStr, "yyyy/MM/dd");
    }

    public static java.sql.Timestamp parseTimestamp(String dateStr,
            String format) {
        java.util.Date date = parseDate(dateStr, format);
        if (date != null) {
            long t = date.getTime();
            return new java.sql.Timestamp(t);
        } else
            return null;
    }

    public static java.sql.Timestamp parseTimestamp(String dateStr) {
        return parseTimestamp(dateStr, "yyyy/MM/dd  HH:mm:ss");
    }

    /**  
     *  格式化输出日期  
     *    
     *  @param  date  
     *                        日期  
     *  @param  format  
     *                        格式  
     *  @return  返回字符型日期  
     */
    public static String format(java.util.Date date, String format) {
        String result = "";
        try {
            if (date != null) {
                java.text.DateFormat df = new java.text.SimpleDateFormat(format);
                result = df.format(date);
            }
        } catch (Exception e) {
        }
        return result;
    }


    /**
     * 获取GMT格式的日期字符串
     */
    public static String formatGMT(Date date){
        SimpleDateFormat gmtFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz");
        gmtFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return gmtFormat.format(date);
    }

    public static String format(java.util.Date date) {
        return common_format.format(date);
    }

    private static final DateFormat common_format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    
    /**  
     *  返回年份  
     *    
     *  @param  date  
     *                        日期  
     *  @return  返回年份  
     */
    public static int getYear(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.YEAR);
    }

    /**  
     *  返回月份  
     *    
     *  @param  date  
     *                        日期  
     *  @return  返回月份  
     */
    public static int getMonth(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.MONTH) + 1;
    }

    /**  
     *  返回日份  
     *    
     *  @param  date  
     *                        日期  
     *  @return  返回日份  
     */
    public static int getDay(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.DAY_OF_MONTH);
    }

    /**  
     *  返回小时  
     *    
     *  @param  date  
     *                        日期  
     *  @return  返回小时  
     */
    public static int getHour(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.HOUR_OF_DAY);
    }

    /**  
     *  返回分钟  
     *    
     *  @param  date  
     *                        日期  
     *  @return  返回分钟  
     */
    public static int getMinute(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.MINUTE);
    }

    /**  
     *  返回秒钟  
     *    
     *  @param  date  
     *                        日期  
     *  @return  返回秒钟  
     */
    public static int getSecond(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.SECOND);
    }

    /**  
     *  返回毫秒  
     *    
     *  @param  date  
     *                        日期  
     *  @return  返回毫秒  
     */
    public static long getMillis(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }

    /**  
     *  返回字符型日期  
     *    
     *  @param  date  
     *                        日期  
     *  @return  返回字符型日期  
     */
    public static String getDate(java.util.Date date) {
        return format(date, "yyyy/MM/dd");
    }

    /**  
     *  返回字符型时间  
     *    
     *  @param  date  
     *                        日期  
     *  @return  返回字符型时间  
     */
    public static String getTime(java.util.Date date) {
        return format(date, "HH:mm:ss");
    }

    /**  
     *  返回字符型日期时间  
     *    
     *  @param  date  
     *                        日期  
     *  @return  返回字符型日期时间  
     */
    public static String getDateTime(java.util.Date date) {
        return format(date, "yyyy/MM/dd  HH:mm:ss");
    }

    /**  
     *  日期相加  
     *    
     *  @param  date  
     *                        日期  
     *  @param  day  
     *                        天数  
     *  @return  返回相加后的日期  
     */
    public static java.util.Date addDate(java.util.Date date, int day) {
    	if(date == null){
    		return null;
    	}
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTimeInMillis(getMillis(date) + ((long) day) * 24 * 3600 * 1000);
        return c.getTime();
    }

    
    public static int getMonthGap(Date now, Date last){
    	java.util.Calendar cnow = java.util.Calendar.getInstance();
    	cnow.setTime(now);
    	
    	java.util.Calendar cold = java.util.Calendar.getInstance();
    	cold.setTime(last);
    	
    	int gap = 0;
    	while(!isSameMonth(cnow, cold)){
    		cold.add(Calendar.MONTH, 1);
    		gap++;
    	}
    	return gap;
    }
    
    public static boolean isSameMonth(Calendar c1, Calendar c2){
    	return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
    }
    
    /**  
     *  日期相减  
     *    
     *  @param  date  
     *                        日期  
     *  @param  date1  
     *                        日期  
     *  @return  返回相减后的日期  
     */
    public static int diffDate(java.util.Date date, java.util.Date date1) {
        return (int) ((getMillis(date) - getMillis(date1)) / (24 * 3600 * 1000L));
    }

    /**
     * 获取两个日期之间的天数间隔
     * @param begin
     * @param end
     * @param abs
     * @return
     */
    public static int getTwoDatesDifDay(Date begin, Date end, boolean abs) {
        long dayMillis = 24 * 3600 * 1000L;


        //需要考虑Timezon的问题，因为getTime返回的是UTC的偏移值
        TimeZone timeZone = TimeZone.getDefault();
        long beginMillis = begin.getTime() + timeZone.getOffset(begin.getTime());
        long endMillis = end.getTime() + timeZone.getOffset(end.getTime());

        //先换算成偏移的天数
        int day1 = (int)(beginMillis  / dayMillis);
    	int day2 = (int)(endMillis / dayMillis);

        //再比较天数
        int diff =  day1 - day2;
        return abs? Math.abs(diff) : diff;
    }
    /**
     * 获得两个日期的天数差
     * 
     */
    public static int getTwoDatesDifDay(Date begin, Date end) {
    	
    	return getTwoDatesDifDay(begin, end, false);
    }
    
    public static String getDateName(Date date) {
    	int dateDiff = DateUtil.getTwoDatesDifDay(date, new Date(),false) ;
    	String datename = DateUtil.format(date, "MM月dd日") ;
    	if(dateDiff == 0) {
    		datename = "今天" ;
    	} else if(dateDiff == 1) {
    		datename = "明天" ;
    	} else if(dateDiff == 2) {
    		datename = "后天" ;
    	} 
    	return datename ;
    }
    
    public static String getBeforeDateTimeShow(Date date) {
    	Date now = new Date();
    	
    	int dateDiff = DateUtil.getTwoDatesDifDay(date, now, false);
    	String time = DateUtil.format(date, "HH:mm") ;
    	String day = "";
    	if(dateDiff == 0) {
    		day = "今天" ;
    	} else if(dateDiff == -1) {
    		day = "昨天" ;
    	} else if(dateDiff == -2) {
    		day = "前天" ;
    	}else {
    		if(now.getYear() == date.getYear()){
    			day = DateUtil.format(date, "MM-dd") ;
    		}else{
    			day = DateUtil.format(date, "yy-MM-dd") ;
    		}
    	}
    	return day +" "+ time ;
    }
    
    public static String getDatetimeName(Date date) {
    	int dateDiff = DateUtil.getTwoDatesDifDay(date, new Date(), false);
    	String datename = DateUtil.format(date, "MM-dd ") ;
    	if(dateDiff == 0) {
    		datename = "今天 ";
    	} else if (dateDiff == 1){
            datename = "明天 ";
        } else if (dateDiff == -1){
            datename = "昨天 ";
        }
    	datename += DateUtil.format(date, "HH:mm") ;
    	return datename ;
    }
    
    /**
     * 获得两个日期的天数差，有正负数的
     * 
     */
    public static int getTwoDateSpace(Date begin, Date end) {
    	
    	long total = end.getTime() -  begin.getTime();
    	long seconds = total / 1000;
    	long day = seconds / 60 / 60 / 24;
    	return Integer.parseInt(day+"");
    }
    
     /**
     * 获得两个日期的天数差 "yyyy-MM-dd"
     * 
     */
    public static int getTwoDatesDifDay(String beginStr, String endStr) {
        Date beginDay = parseDate(beginStr);
        Date endDay = parseDate(endStr);
        return getTwoDatesDifDay(beginDay, endDay, false) ;
    }
    
    public static int getDayNumFromNowToTargetDate(Date targetDate){
    	return getTwoDatesDifDay(new Date(), targetDate) + 1;
    }
    
    /**
     * 取得某天是星期几
     */
    public static int getWeekOfDate(Date dt) {
        Calendar cal = CalendarFactory.getFactory().getCalendar();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return w;
     }
    
    public static String getWeekName(int week){
    	return weekChiMap.get(week);
    }
    
    /**
     * 星期几中文名
     */
    public static final Map<Integer, String>  weekChiMap= new HashMap<Integer, String>() ;
    static {
        weekChiMap.put(0, "日");
        weekChiMap.put(1, "一");
        weekChiMap.put(2, "二");
        weekChiMap.put(3, "三");
        weekChiMap.put(4, "四");
        weekChiMap.put(5, "五");
        weekChiMap.put(6, "六");
    }
    
    /**
     * 获取当前的日期，格式是yyyy-mm-dd
      * 
      * @return  
     */
    public static Date getYMDDate(){
         Calendar calendar = CalendarFactory.getFactory().getCalendar();
        // Calendar calendar=Calendar.getInstance();
         calendar.setTime(new Date());
         calendar.set(Calendar.HOUR_OF_DAY, 0);
         calendar.set(Calendar.MINUTE, 0);
         calendar.set(Calendar.SECOND, 0);
         calendar.set(Calendar.MILLISECOND,0);
         return calendar.getTime();
    }
    
    /**
     * 获取当前的日期，格式是yyyy-mm-dd
     * 
     * @return  
     */
    public static Date getYMDDate(Date date){
    	Calendar calendar = CalendarFactory.getFactory().getCalendar();
    	calendar.setTime(date);
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND,0);
    	return calendar.getTime();
    }
    
    /**
     * 对日期按照指定的类型进行添加或者减少
      * 
      * @param date
      * @param type
      * @param num
      * @return  
     * @throws Exception 
      * @Author: zhanghuajie 
      * @Create: 2013-5-10 下午6:16:07
     */
    public static Date getAddDate(Date date, int type, int num) {
        Calendar cal = CalendarFactory.getFactory().getCalendar();
        cal.setTime(date);
        cal.add(type, num);
        return cal.getTime();
    }

    public static void main(String[] args) {
		try {
//			Date lastWeekBegin = DateUtil.getWeekBegin(-1);
//			Date thisWeekBegin = DateUtil.getWeekBegin(0);
//			//System.out.println(DateUtil.format(lastWeekBegin, "yyyy-MM-dd HH:mm:ss"));
//			//System.out.println(DateUtil.format(thisWeekBegin, "yyyyyy-MM-dd HH:mm:ssyy-MM-dd HH:mm:ss"));
//			Date d = DateUtil.parseSqlDate("2014-10-13 14:11:41", "yyyy-MM-dd HH:mm:ss");
//			//System.out.println(DateUtil.format(d, "yyyy-MM-dd HH:mm:ss"));
//			
//			Date now = (Date)d.clone();
//			
//			d.setMonth(01);
//			System.out.println(DateUtil.format(d, "yyyy-MM-dd HH:mm:ss"));
//			System.out.println(DateUtil.format(now, "yyyy-MM-dd HH:mm:ss"));
//			String ts = "Sun Jan 18 02:04:10 CST 1970";
//			DateFormat df = new SimpleDateFormat();
//	        Date date = df.parse(ts);
//	        System.out.println(date);
			
			
//			long l1 = DateUtil.parseDate("2015-01-12", "yyyy-MM-dd").getTime() ;
//			System.out.println(DateUtil.getWeek(l1));
//			long l2 = DateUtil.parseDate("2015-01-11", "yyyy-MM-dd").getTime() ;
//			System.out.println(DateUtil.getWeek(l2));
//			long l3 = DateUtil.parseDate("2015-01-17", "yyyy-MM-dd").getTime() ;
//			System.out.println(DateUtil.getWeek(l3));
//			Calendar cal = GregorianCalendar.getInstance();
//			cal.set(Calendar.DATE, 21);
//			Date d = cal.getTime();
//			System.out.println(DateUtil.format(d, "yyyy-MM-dd"));
//			System.out.println(cal.get(Calendar.HOUR_OF_DAY));
			
			//System.out.println(DateUtil.getTodayTime(79200000,true));
			//Date yes = DateUtil.parseDate("2015-04-11", "yyyy-MM-dd");
			//System.out.println(getWeekOfDate(yes));
			
			//System.out.println(DateUtil.parseDate("2015-03", "yyyy-MM"));
			
			System.out.println(DateUtil.getNowMonthBeginDate());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public static String getFormatString(String format, Date date){
        if(date == null) 
            return "";
        else{
            SimpleDateFormat dateformat = new SimpleDateFormat(format);
            return dateformat==null?"":dateformat.format(date);
        }
    }
    
    /**
     * 返回当前时间所在周的开始期
     * @param currD
     * @return
     */
    public static String getWeekStartTime() {
    	Calendar c = Calendar.getInstance();
    	int dayOfWeek = getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
		Date d = addDate(new Date(), -dayOfWeek+1);
		return getFormatString("yyyy-MM-dd", d) + " 00:00:00";
    }
    
    /**
     * 返回当前时间所在周的开始日期
     * @return
     */
    public static String getWeekEndTime() {
    	Calendar c = Calendar.getInstance();
		int dayOfWeek = getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
		Date d = addDate(new Date(), 7-dayOfWeek);
		return getFormatString("yyyy-MM-dd", d) + " 23:59:59";
    }
    
    private static int getDayOfWeek(int dw) {
    	if ( dw == 1 ) {//周日
			return  7;
		} 

    	return --dw;
    }

    
    /**
     * 获取某天以后的第一个周几
     * @param start  某天
     * @param week   周几，1：周一，2：周二，.... 0：周日
     * @return
     */
    public static Date getDateAfter(Date start, int week){
    	Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(start);
    	while(true){
    		Date startDay = cal.getTime();
    		int newWeek = DateUtil.getWeekOfDate(startDay);
    		if(newWeek == week){
    			return startDay;
    		}
    		cal.add(Calendar.DAY_OF_MONTH, 1);
    	}
    }
    
    /**
     * 获取准确时间，
     * @param day  某天
     * @param todayTime  当天从0点开始的毫秒数
     * @return
     */
    public static Date getDate(Date day, long todayTime){
    	long time = getYMDDate(day).getTime() + todayTime;
    	return new Date(time);
    }
    
    public static long getNowZeroMillis(Date d) {
    	return getNowZeroMillis(d.getTime());
    }
    /**
     * 获取当天零点到day的时刻毫秒数
     * 
     * @return
     */
    public static long getNowZeroMillis(long day) {
    	
    	Calendar calendar = GregorianCalendar.getInstance();
    	calendar.setTimeInMillis(day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
    	
    	return day - calendar.getTimeInMillis() ;
    }
    
    public static Date getTodayStartDate(){
    	Calendar cal = GregorianCalendar.getInstance();
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
    }
    
    public static Date getTodayEndDate(){
    	Calendar cal = GregorianCalendar.getInstance();
		
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
    }
    
    public static Date getStartDate(Date d){
    	Calendar cal = GregorianCalendar.getInstance();
    	if(d != null)
    		cal.setTime(d);
    	
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
    }
    
    public static Date getEndDate(Date d){
    	Calendar cal = GregorianCalendar.getInstance();
    	if(d != null)
    		cal.setTime(d);
		
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
    }
    
    
    /**
     * 获取day的星期数
     * @param day
     * @return
     */
    public static int getWeek(long day){
    	Calendar calendar = GregorianCalendar.getInstance();
    	calendar.setTimeInMillis(day);
    	
    	return calendar.get(Calendar.DAY_OF_WEEK) ;
    }
    
    public static boolean isSameDay(Date d1, Date d2){
    	if(d1 == null || d2 == null){
    		return false;
    	}
    	Calendar c1 = GregorianCalendar.getInstance();
        c1.setTime(d1);
        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        
        Calendar c2 = GregorianCalendar.getInstance();
        c2.setTime(d2);
        int year2 = c2.get(Calendar.YEAR);
        int month2 = c2.get(Calendar.MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        
        return (year1 == year2) && (month1 == month2) && (day1 == day2);
	}
    static SimpleDateFormat monthDayformat = new SimpleDateFormat("MMdd");
    public static String getMonthDayString(Date date){
    	return monthDayformat.format(date);
    }
    public static Date getWeekBegin(int gap) {
    	int mondayPlus;
    	Calendar cd = Calendar.getInstance();
    	// 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
    	int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
    	if (dayOfWeek == 1) {
    		mondayPlus = 0;
    	} else {
    		mondayPlus = 1 - dayOfWeek;
    	}
    	GregorianCalendar cal = new GregorianCalendar();
    	cal.add(GregorianCalendar.DAY_OF_MONTH, mondayPlus);
    	cal.add(Calendar.WEEK_OF_MONTH, gap);
    	
    	cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
    
    
    public static String getNowWeekBegin() {
    	int mondayPlus;
    	Calendar cd = Calendar.getInstance();
    	// 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
    	int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
    	if (dayOfWeek == 1) {
    		mondayPlus = 0;
    	} else {
    		mondayPlus = 1 - dayOfWeek;
    	}
    	GregorianCalendar currentDate = new GregorianCalendar();
    	currentDate.add(GregorianCalendar.DATE, mondayPlus);
    	Date monday = currentDate.getTime();
    	DateFormat df = DateFormat.getDateInstance();
    	String preMonday = df.format(monday);
    	return preMonday + " 00:00:00";
	}
    
    public static String getNowMonthBegin() {
    	int mondayPlus;
    	Calendar cd = Calendar.getInstance();
    	int dayOfWeek = cd.get(Calendar.DAY_OF_MONTH);
    	if (dayOfWeek == 1) {
    		mondayPlus = 0;
    	} else {
    		mondayPlus = 1 - dayOfWeek;
    	}
    	GregorianCalendar currentDate = new GregorianCalendar();
    	currentDate.add(GregorianCalendar.DATE, mondayPlus);
    	Date monday = currentDate.getTime();
    	DateFormat df = DateFormat.getDateInstance();
    	String preMonday = df.format(monday);
    	return preMonday + " 00:00:00";
	}
    public static Date getNowMonthBeginDate() {
    	int mondayPlus;
    	Calendar cd = Calendar.getInstance();
    	int dayOfWeek = cd.get(Calendar.DAY_OF_MONTH);
    	if (dayOfWeek == 1) {
    		mondayPlus = 0;
    	} else {
    		mondayPlus = 1 - dayOfWeek;
    	}
    	GregorianCalendar currentDate = new GregorianCalendar();
    	currentDate.add(GregorianCalendar.DATE, mondayPlus);
    	currentDate.set(Calendar.HOUR_OF_DAY, 0);
    	currentDate.set(Calendar.MINUTE, 0);
    	currentDate.set(Calendar.SECOND, 0);
    	currentDate.set(Calendar.MILLISECOND, 0);
    	return currentDate.getTime();
	}
    
    /**
     * 获得当前时间的字符串
     * @param time  现在到当天零点的毫秒数
     * @return
     */
    public static String getTodayTime(long time, boolean withSecond) {
    	time = time/1000 ;
    	int second = (int) (time%60) ;
    	time = (time - second )/60 ;
    	int munite = (int) (time % 60) ;
    	time = (time - munite)/60 ;
    	StringBuffer stringBuffer = new StringBuffer() ;
    	if(time < 10) {
    		stringBuffer.append("0").append(time) ;
    	} else {
    		stringBuffer.append(time) ;
    	}
    	stringBuffer.append(":") ;
    	if(munite<10) {
    		stringBuffer.append("0").append(munite) ;
    	} else {
    		stringBuffer.append(munite) ;
    	}
    	if(withSecond){
	    	stringBuffer.append(":") ;
	    	if(second < 10) {
	    		stringBuffer.append("0").append(second) ;
	    	} else {
	    		stringBuffer.append(second) ;
	    	}
    	}
    	return stringBuffer.toString() ;
    }
    
    public static String getTodayStr(){
    	return format(new Date(), "yyyy-MM-dd");
    }
    
    public static String getYesterdayStr(){
    	Calendar cd = Calendar.getInstance();
    	cd.add(Calendar.DAY_OF_YEAR, -1);
    	
    	return format(cd.getTime(),  "yyyy-MM-dd");
    }

    public static int getYMD(Date date) {
    	int flag = 0 ;
    	try {
			String str = format(date, "yyyyMMdd") ;
			flag = Integer.parseInt(str) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return flag ;
    }
    
    private static final SimpleDateFormat shortFormat = new SimpleDateFormat("M-d");
    
    private static final long hour_millis = 60 * 60 * 1000;
	/**
	1）日期显示：今天、明天、本周XX，下周XX 
	 * @return
	 */
	public static String getHintDay(Date useTime){
		String dayStr = null;
        int dayGap = DateUtil.getTwoDatesDifDay(useTime, new Date(), false);
		if(dayGap == 0)
			dayStr="今天";
		else if(dayGap == 1)
			dayStr="明天";
		else if(dayGap == 2)
			dayStr="后天";
		else{
			dayStr = shortFormat.format(useTime);
		}
		return dayStr;
	}

	public static Date getWeekBegin(Calendar cd) {
		int dayPlus;
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			dayPlus = -6;
		} else {
			dayPlus = 2 - dayOfWeek;
		}
		Calendar cal = (Calendar) cd.clone();
		cal.add(GregorianCalendar.DATE, dayPlus);

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static boolean insideTwoTime(Date now, Date expirybegin, Date expiryend) {
		return !(now.before(expirybegin) || now.after(expiryend));
	}
	
	public static int isInsideTwoTime(Date A , Date B , Date C){
    	int result = 0;
    	long a = A.getTime();
    	long b = B.getTime();
    	long c = C.getTime();
    	if(b >= c){
    		return 0;
    	}else if(a >= b && a <= c){
    		result = 2;
    	}else if(a < b){
    		result = 1;
    	}else if(a > c){
    		result = 3;
    	}
    	return result;
    }
	
	public static float getBetweenHours(Date d1, Date d2){
		long ts = d2.getTime() - d1.getTime();
		
		float hour = (int)(ts/3600000);
		
		if(ts %3600000 != 0){
			hour += 0.5;
		}
		return hour;
	}
	/** 
     * 得到几天前的时间 
     *  
     * @param d 
     * @param day 
     * @return 
     */  
    public static Date getDateBefore(Date d, int day) {  
        Calendar now = Calendar.getInstance();  
        now.setTime(d);  
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);  
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();  
    }  
    
    /** 
     * 获取本月第一天 
     * @return Date
     */  
    public static Date getCurrentMonthFristDay() {  
        Calendar cal=Calendar.getInstance();//获取当前日期 
        cal.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天 
        
        return cal.getTime();
    }  
    /** 
     * 获取上一个月第一天
     * @return Date
     */  
    public static Date getLastMonthFristDay() {  
    	Calendar cal=Calendar.getInstance();
    	cal.add(Calendar.MONTH, -1);
    	cal.set(Calendar.DAY_OF_MONTH, 1);
 		return cal.getTime();
    }  
    /** 
     * 获取上一个月最后一天
     * @return Date
     */  
    public static Date getLastMonthLastDay() {  
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }  
    /**午夜*/
    private static final int MIDNIGHT = 24*60*60;
    /**
     * 得到当前时刻到午夜的秒数
     * @return
     */
    public static int getSecondToMidnight(){
    	Calendar c = Calendar.getInstance();
    	
    	int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		
		return MIDNIGHT-(hour*3600+minute*60+second);
    }
}
