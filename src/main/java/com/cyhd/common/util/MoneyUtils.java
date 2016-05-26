/*
 */
package com.cyhd.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 货币相关操作工具类
 *
 * @version 1.0
 */
public class MoneyUtils {

	public static float discount(int discount){
		if(discount >= 10){
			discount = 10;
		}else if(discount <= 0){
			discount = 0;
		}
		BigDecimal decimal = new BigDecimal(discount);    	
		 return decimal.movePointLeft(1).stripTrailingZeros().floatValue();
	}
	
    public static int yuan2fen(double yuan) {
        BigDecimal decimal = new BigDecimal(yuan);
        return decimal.movePointRight(2).intValue();
    }
    
    public static double fen2yuan(long fen){
    	BigDecimal decimal = new BigDecimal(fen);    	
    	return fen2yuan(decimal);
    }
    
    public static double fen2yuan(BigDecimal fen){
        return fen.movePointLeft(2).stripTrailingZeros().floatValue();
    }
    
    public static String fen2yuanS(long fen){
    	return format(fen2yuan(fen));
    }
    
    public static String fen2yuanS2(long fen){
    	return format2(fen2yuan(fen));
    }
    
    private static DecimalFormat decimalFormat=new DecimalFormat("#.##");
    public static String format(double fen){
    	BigDecimal decimal = new BigDecimal(fen);  
    	return decimalFormat.format(decimal);
    }
    
    private static DecimalFormat decimalFormat2=new DecimalFormat("0.00");
    public static String format2(double fen){
    	BigDecimal decimal = new BigDecimal(fen);  
    	return decimalFormat2.format(decimal);
    }
    
    public static void main(String[] args){
    	//System.out.println(MoneyUtils.yuan2fen(10.111));
//    	System.out.println(MoneyUtils.fen2yuanS(1000001));
//    	String s = "车费%s";
//    	System.out.println(String.format(s, MoneyUtils.fen2yuanS(1000000)));
    	
    	//System.out.println(MoneyUtils.discount(6));
    	System.out.println(MoneyUtils.format2(0.1));
    }
}
