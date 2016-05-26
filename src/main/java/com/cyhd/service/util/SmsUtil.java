package com.cyhd.service.util;


public class SmsUtil {

	// 电话号码数组转为字符串
	public static String converMobiles(String[] mobiles) {
		
		StringBuilder stringBuilder = new StringBuilder() ;
		for(int i=0; i< mobiles.length; i++) {
			if(mobiles[i].startsWith("1")) {
				stringBuilder.append(mobiles[i]) ;
			}
			if(mobiles.length -1 != i) {
				stringBuilder.append(",") ;
			}
		}
		return stringBuilder.toString() ;
	}
}
