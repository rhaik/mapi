package com.cyhd.common.util;

public class CodeUtil {

	// 邮箱验证码生成
	public static String generateEmailCode() {
		int num = (int) (System.currentTimeMillis()%9000 + 1000) ;
		return num + "" ;
	}
	
	// 生成女神邮箱验证码
	public static String generateGoddessCode() {
		int num = (int) (System.currentTimeMillis()%900 + 100) ;
		return "N" + num ;
	}
	
	// 生成媒体人邮箱验证码
	public static String generateMeitiCode() {
		int num = (int) (System.currentTimeMillis()%900 + 100) ;
		return "M" + num ;
	}
	// 生成金融妹子邮箱验证码
	public static String generateJrCode() {
		int num = (int) (System.currentTimeMillis()%900 + 100) ;
		return "J" + num ;
	}
}
