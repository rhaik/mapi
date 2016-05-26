package com.cyhd.web.exception;

import java.util.HashMap;

public class ErrorCode {
	
	/**
	 * 系统级的error code ， 在100 以内
	 */
	public static final int ERROR_CODE_UNKNOWN = 1;  //系统未知异常
	public static final int ERROR_CODE_PARAMETER = 2;  //参数异常
	public static final int ERROR_CODE_TICKET = 3;  //Ticket error
	public static final int ERROR_CODE_LOCATION = 4;  //定位错误 error
	public static final int ERROR_CODE_USER_TYPE = 5;  //用户身份异常
	public static final int ERROR_CODE_USER_NOT_LOGIN = 6;  // 用户未登陆(或 ticket错误)
	public static final int ERROR_CODE_VERCODE = 7;  // 验证码错误
	public static final int ERROR_CODE_UPLOAD_IMAGE = 8;  // 图片上传失败
	
	public static final int ERROR_CODE_CLIENTINFO = 10; //client info 错误
	public static final int ERROR_CODE_CLIENTAUTH = 11; //client auth 错误
	public static final int ERROR_CODE_INVITE_CODE = 12 ; // 邀请码异常
	public static final int ERROR_CODE_USER_DUPLICATE = 13 ; //同一手机，换微信账号登陆
	public static final int ERROR_CODE_HOST = 14 ; //域名访问出错
	
	public static final int ERROR_CODE_USER_MASKED = 20 ; //用户黑名单
	
	/**
	 * 钱包相关  900 开始
	 */
	public static final int ERROR_CODE_WALLET_NOT_ENOUTH = 901 ; // 余额不足
	public static final int ERROR_CODE_WALLET_PARAMETER_WRONG = 902 ; // 参数错误
	
	public static final int ERROR_CODE_LACA_WX_PARAMETER = 903 ; // 缺少微信提现参数
	public static final int ERROR_CODE_LACA_ALIPAY_PARAMETER = 904 ; // 缺少支付宝提现参数
	/**同一天出现相同的支付账号*/
	public static final int ERROR_CODE_SAME_DAY=905;
	private static HashMap<Integer, String> errorsMap = new HashMap<Integer, String>(); 
	
	static {
		errorsMap.put(ERROR_CODE_UNKNOWN, "系统运行时异常");
		errorsMap.put(ERROR_CODE_PARAMETER, "请求参数错误");
		errorsMap.put(ERROR_CODE_TICKET, "ticket 错误");
		errorsMap.put(ERROR_CODE_LOCATION, "定位错误");
		errorsMap.put(ERROR_CODE_USER_TYPE, "用户身份异常");
		errorsMap.put(ERROR_CODE_USER_NOT_LOGIN, "获取用户信息失败");
		errorsMap.put(ERROR_CODE_CLIENTINFO, "参数错误(ci)");
		errorsMap.put(ERROR_CODE_CLIENTAUTH, "网络错误(ca)");
		errorsMap.put(ERROR_CODE_VERCODE, "验证码错误");
		errorsMap.put(ERROR_CODE_UPLOAD_IMAGE, "图片上传失败");
		errorsMap.put(ERROR_CODE_INVITE_CODE, "邀请码错误！");
		errorsMap.put(ERROR_CODE_HOST, "访问受限");
		errorsMap.put(ERROR_CODE_USER_MASKED, "网络错误(ui)");
	}
	
	public static String getErrorMsg(int code){
		String msg = errorsMap.get(code);
		return msg == null ? "异常信息未定义" : msg;
	}
	
	
	public static CommonException getParameterErrorException(String detailReason){
		return new CommonException(ERROR_CODE_PARAMETER, detailReason);
	}
	
}
