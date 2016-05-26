package com.cyhd.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang.math.NumberUtils;

/**
 * 数字工具
 *
 */
public class NumberUtil extends NumberUtils{

	/**
	 * 向上取整
	 * @param num
	 * @param ratio
	 * @return
	 */
	public static int upInt(int num, int ratio) {
		
		if(num % ratio > 0) {
			return num / ratio + 1 ;
		} else {
			return num / ratio ;
		}
	}
	
	private static DecimalFormat format_one = new DecimalFormat("#.#") ;
	private static DecimalFormat format_tow = new DecimalFormat("#.##") ;
	
	public static double getDotOne(int num, int move) {
		BigDecimal decimal = new BigDecimal(num);    	
		double temp = decimal.movePointLeft(move).stripTrailingZeros().floatValue();
		return Double.parseDouble(format_one.format(temp)) ;
	}
	
	public static double getDotTow(int num, int move) {
		BigDecimal decimal = new BigDecimal(num);    	
		double temp = decimal.movePointLeft(move).stripTrailingZeros().floatValue();
		return Double.parseDouble(format_tow.format(temp)) ;
	}
	
	public static long safeParseLong(String str){
		try{
			return Long.parseLong(str);
		}catch(Exception e){
			return 0;
		}
	}
	
	public static int safeParseInt(String str){
		try{
			return Integer.parseInt(str) ;
		}catch(Exception e){
			return 0;
		}
	}

	public static double safeParseDouble(String str){
		try{
			return Double.parseDouble(str) ;
		}catch(Exception e){
			return 0;
		}
	}
	
	public static double round(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(value) ;
		bd = bd.setScale(scale, roundingMode) ;
		return bd.doubleValue() ;
	}
	
	public static String snsNumberString(int scale) {
		if(scale < 10)
			return "" ;
		else if(scale < 50)
			return scale + "人" ;
		else if(scale < 100)
			return "50+人" ;
		else if(scale < 500)
			return "100+人" ;
		else if(scale < 1000)
			return "500+人" ;
		else {
			return scale/1000 * 1000 + "+人" ;
		}
	}
}
