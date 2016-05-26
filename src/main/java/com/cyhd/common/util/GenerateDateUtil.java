package com.cyhd.common.util;

import java.util.Calendar;
import java.util.Date;

/**
 * 重复利用已经创建的Date对象 util
 * 
 */
public class GenerateDateUtil {
	
	private static LiveAccess<Date> datePool ;
	private static int ttlInMillis = 1 * 1000 ;// 1 秒

	/**
	 * 获得当前时间 
	 * 
	 * @return
	 */
	public static Date getCurrentDate() {
		
		if(datePool == null) 
			datePool = new LiveAccess<Date>(ttlInMillis, new Date()) ;
		Date date = datePool.getElement() ;
		if(date == null) {
			date = new Date() ;
			datePool = new LiveAccess<Date>(ttlInMillis, date) ;
		}
		
		return date ;
	}
	
	/**
	 * 获得当前时间 
	 * 
	 * @return
	 */
	public static long getCurrentTime() {
		
		return getCurrentDate().getTime() ;
	}
	
	/**
	 * 获得当前年
	 * 
	 * @return
	 */
	public static int getCurrentYear() {
		Date date = getCurrentDate() ;
		Calendar c = Calendar.getInstance() ;
		c.setTime(date) ;
		return c.get(Calendar.YEAR) ;
	}
	
	/**
	 * 获得当前月
	 * 
	 * @return
	 */
	public static int getCurrentMonth() {
		Date date = getCurrentDate() ;
		Calendar c = Calendar.getInstance() ;
		c.setTime(date) ;
		return c.get(Calendar.MONTH) ;
	}

}
