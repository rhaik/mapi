package com.cyhd.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.service.constants.Constants;

// 退款工具类
public class IdcardUtil {
	
	protected static Logger logger = LoggerFactory.getLogger(IdcardUtil.class);
	
	private final static int tail_w[] = {7 ,9, 10, 5 ,8, 4, 2 ,1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1} ;
	private final static char tail_v[] = {'1', '0', 'x', '9', '8', '7', '6', '5', '4', '3', '2'} ;

	
	// 获得校验位
	private static String getTail(String idcard) {

		int total = 0 ;
		for(int i=0; i<17; i++) {
			total += Integer.parseInt(idcard.charAt(i) + "") * tail_w[i] ;
		}
		int t = total % 11 ;
		return tail_v[t] + "" ;
	}
	
	// 生日是否合法
	private static boolean isLegalBirthday(String birthday) {
		
		// 年
		int maxyear = Integer.parseInt(DateUtil.format(GenerateDateUtil.getCurrentDate(), "yyyy")) - 16 ;
		int minyear = 1950 ;
		int year = Integer.parseInt(birthday.substring(0, 4)) ;
		if(year < minyear)
			return false ;
		if(year > maxyear)
			return false ;
		// 月
		int v = Integer.parseInt(birthday.substring(4,5)) ;
		if(v > 1)
			return false ;
		else if(v == 1) {
			int m = Integer.parseInt(birthday.substring(5,6)) ;
			if(m > 2)
				return false ;
		}
		// 日
		v = Integer.parseInt(birthday.substring(6, 7)) ;
		if(v > 3)
			return false ;
		else if(v == 3) {
			int m = Integer.parseInt(birthday.substring(7, 8)) ;
			if(m > 1)
				return false ;
		}
		
		return true ;
	}
	
	// 省区号判断
	private static boolean isLegalProvince(String idcard) {
		
		/**
		 * 11 北京市 12 天津市  13 河北省 14 山西省  15 内蒙古自治区
		 * 21 辽宁省 22 吉林省 23 黑龙江省
		 * 31 上海市 32 江苏省 33 浙江省 34 安徽省 35 福建省 36 江西省 37 山东省
		 * 41 河南省 42 湖北省 43 湖南省 44 广东省 45 广西壮族自治区 46 海南省
		 * 50 重庆市  51 四川省  52 贵州省 53 云南省 54 西藏自治区
		 * 61 陕西省 62 甘肃省 63 青海省 64 宁夏回族自治区 65 新疆维吾尔自治区
		 * 71 台湾省 81 香港特别行政区 82 澳门特别行政区
		 */
		int province[] = {11,12,13,14,15,
				21,22,23,
				31,32,33,34,35,36,37,
				41,42,43,44,45,46,
				50,51,52,53,54,
				61,62,63,64,65, 
				71, 81, 82} ;
		int v = Integer.parseInt(idcard.substring(0, 1)) ;
		if(v == 0)
			return false ;
		v = Integer.parseInt(idcard.substring(0, 2)) ;
		boolean flag = false ;
		for(int i : province) {
			if(i == v) 
				flag = true ;
		}
		
		return flag ;
	}
	
	public static boolean auth(String idcard) {
		
		String logmessage = "IdcardUtil.auth idcard:" + idcard ;
		
		// 18 位 
		if(idcard.length() != 18) {
			logger.info(logmessage + " idcard.length is not 18");
			return false ;
		}
		// 前面 17位都为数字, 后一位是数字或者x
		for(int i=0; i<17; i++) {
			char c = idcard.charAt(i) ;
			if(c < '0' || c > '9') {
				logger.info(logmessage + " idcard.0.17 is not number");
				return false ;
			}
		}
		char c = idcard.charAt(17) ;
		if(c != 'x' && (c < '0' || c > '9')) {
			logger.info(logmessage + " idcard.18 is not number");
			return false ;
		}
		// 省区号判断
		if(!isLegalProvince(idcard)) {
			logger.info(logmessage + " idcard.province is error");
			return false ;
		}
		// 生日
		if(!isLegalBirthday(idcard.substring(6, 14))) {
			logger.info(logmessage + " idcard.birthday is error");
			return false ;
		}
		// 尾号
		String tail = getTail(idcard) ;
		if(!tail.equals(idcard.substring(17, 18))) {
			logger.info(logmessage + " idcard.tail is error");
			return false ;
		}
		return true ;
	}
	
	
	
	// 奇数表示男性，偶数表示女性
	// 身份证号为18位
	public static int getSex(String idcard) {
		
		if(idcard.length() != 18) 
			return Constants.sex_unknown ;
		
		int v = Integer.parseInt(idcard.substring(16, 17)) ;
		if(v%2 == 0) {
			return Constants.sex_weman ;
		} else {
			return Constants.sex_man ;
		}
	}
	
	public static void main(String[] args) {
		String idcard = "110108198106294217" ;
		
		System.out.println(getTail(idcard));
		System.out.println(auth(idcard));
	}

}
