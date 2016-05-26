package com.cyhd.service.util;


public class CouponUtil {
	
	public static final int coupontype_daijin = 1 ; // 代金券
	public static final int coupontype_tongcheng = 2 ; // 通用券
	
	public static final int coupontype_test = 100 ; // 测试券
	
	
	public static boolean isTongcheng9To5(int coupontype){
		return coupontype == coupontype_tongcheng;
	}
	
	public static boolean isTongcheng(int coupontype){
		return coupontype == coupontype_tongcheng;
	}

	public static boolean isDaijin(int coupontype){
		return coupontype == coupontype_daijin;
	}
	
	public static boolean isTest(int coupontype){
		return coupontype == coupontype_test;
	}

}
