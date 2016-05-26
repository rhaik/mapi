package com.cyhd.service.push;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class PushConstants {
	
	// push 超时控制
	public static final int connect_timeout = 10000;
	public static final int so_timeout = 6000;
	
	// 默认最早push时间 毫秒， 上午7点
	public static final int EARLIEST_PUSH_TIME = 7 * 60 * 60 * 1000;
	// 默认最晚push时间 毫秒， 晚上23点
	public static final int LATEST_PUSH_TIME = 23 * 60 * 60 * 1000;
	
	
	public static final long DEFAULT_PUSH_EXPIRE_TIME = 2 * 3600 * 1000;
	public static final long MATCH_PUSH_EXPIRE_TIME = 1 * 3600 * 1000;
	
	public static final int TYPE_SYS_PROMOT = 1; 		// 系统push
	
	public static final int TYPE_APP_TASK_START = 10;  // 任务开始
	public static final int TYPE_APP_TASK_DOWNLOAD = 11;  // APP已下载成功
	public static final int TYPE_APP_TASK_FINISHED = 12;  // APP试用完成
	public static final int TYPE_APP_TASK_SUCCESS = 13;  // APP试用审核通过
	public static final int TYPE_APP_TASK_FAILED = 14;  // APP试用审核不通过
	public static final int TYPE_APP_TASK_EXPIRE = 15;  // 即将过期
	public static final int TYPE_FRIEND_PROMOT = 21; 	// 好友消息push
	public static final int TYPE_SYSTEM_PROMOT = 30; 	// 系统消息push
	/**转发任务的push*/
	public static final int TYPE_TRAN_ARTICLE_PROMOT = 40;
	
	private static HashMap<Integer, Long> offlineExpireTimes = new HashMap<Integer, Long>();
	
	
	public static JSONObject newPushParam(int type){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", type);
		return jsonObject;
	}
	
	public static long getPushExpireTime(int type){
		Long t = offlineExpireTimes.get(type);
		return t == null ? DEFAULT_PUSH_EXPIRE_TIME : t;
	}
	
	/**
	 * 个推返回常量
	 */
	public static final String status_ok = "ok" ;
	public static final String status_successed_online = "successed_online" ;
	public static final String status_successed_offline = "successed_offline" ;
	
	public static final String status_successed_ignore = "successed_ignore" ;
	public static final String status_flow_exceeded = "flow_exceeded" ;
	public static final String status_AppidError = "AppidError" ;
	public static final String status_AppidNoAppSecret = "AppidNoAppSecret" ;
	public static final String status_TokenMD5NoUsers = "TokenMD5NoUsers" ;
	public static final String status_PushTotalNumOverLimit = "PushTotalNumOverLimit" ;
	public static final String status_AppidNoMatchAppKey = "AppidNoMatchAppKey" ;
	
	public static final String status_sign_error = "sign_error" ;
	public static final String status_domain_error = "domain_error" ;
	public static final String status_appkey_error = "appkey_error" ;
	public static final String status_action_error = "action_error" ;
	public static final String status_Error = "Error" ;
	public static final String status_OtherError = "OtherError" ;
	
	public static Map<String, String> statusName = new HashMap<String, String>() ;
	static {
		statusName.put(status_successed_online, "用户在线，消息在线下发") ;
		statusName.put(status_successed_offline, "用户离线，消息存入离线系统") ;
		
		statusName.put(status_successed_ignore, "无效用户，消息丢弃") ;
		statusName.put(status_flow_exceeded, "接口消息推送流量已超限") ;
		statusName.put(status_AppidError, "绑定的appid与推送的appid不符") ;
		statusName.put(status_AppidNoAppSecret, "appid未找到对应的appSecret") ;
		statusName.put(status_TokenMD5NoUsers, "在系统中未查找到用户") ;
		statusName.put(status_PushTotalNumOverLimit, "推送消息个数总数超限") ;
		statusName.put(status_AppidNoMatchAppKey, "appid和鉴权的appkey不匹配") ;
		
		statusName.put(status_sign_error, "鉴权失败") ;
		statusName.put(status_domain_error, "填写的域名错误或者无法解析") ;
		statusName.put(status_appkey_error, "Appkey填写错误") ;
		statusName.put(status_action_error, "未找到对应的action动作") ;
		statusName.put(status_Error, "请求信息填写有误") ;
		statusName.put(status_OtherError, "未知错误") ;
	}
	
	/*
	 * push推送的errcode
	 */
	public static final int errcode_unknown = -1 ;// 未知错误
	public static final int errcode_param_wrong = 1 ;// 参数错误
	public static final int errcode_condition_filter = 2 ; //push条件过滤
	public static final int errcode_device_not_exist = 3 ; //设备不存在
	public static final int errcode_logout = 4 ; //用户退出
	public static final int errcode_ios = 5 ; //ios push异常
	public static final int errcode_android = 6 ; //android push异常
	
	/**
	 * 处理乘客的取消订单申请的类型
	 */
	public static final int push_deal_passenger_cancel_agree = 1 ; // 同意申请
	public static final int push_deal_passenger_cancel_reject = 2 ; // 拒绝申请
	public static final int push_deal_passenger_cancel_timeout_reject = 3 ; // 超时拒绝申请
}
