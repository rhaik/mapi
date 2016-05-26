package com.cyhd.service.util;

import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.cyhd.common.util.DateUtil;

public class GlobalConfig {
	static Properties mapping = new Properties();
	
	static {
		try{
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("globalconfig.properties");
			mapping.load(in);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static final boolean isInnerTest = true; // 是否是内测阶段
	public static final boolean isNeedInviteCode = true;  //是否必须要邀请码
	
//	private static final int[] api_server_range = {201, 220};
//	private static final int[] mis_server_range = {221, 230};
//	private static final int[] act_server_range = {231, 235};
	
	public static final int server_id = Integer.parseInt(System.getProperty("server_id", "1"));
	
	public static final String server_ip = System.getProperty("jetty.host", "127.0.0.1");
	
	public static final String server_type = System.getProperty("server_type", "test");
	
	public static final String server_name = System.getProperty("server_name", "api");
	
	public static String base_url = mapping.getProperty("base_url", "http://mapi.lieqicun.cn/");
	public static String base_url_https = mapping.getProperty("base_url_https", "http://mapi.lieqicun.cn/");
	public static String admin_url = mapping.getProperty("admin_url", "http://money.lieqicun.cn/");;
	static{
		if(!base_url.endsWith("/"))
			base_url += "/";
	}
	
	public static final String mongodb_hosts = mapping.getProperty("mongodb.hosts", "42.62.78.2:12030") ;
	
	public static boolean isApiServer;
	public static boolean isMisServer;
	public static boolean isActServer;
	public static boolean isJobServer;
	public static boolean isDeploy;
	
	public static String jedis_common_server = "114.215.130.131";
	public static int jedis_common_port = 6579;
	public static String jedis_common_pwd="";
	
	public static String jedis_self_server = "114.215.130.131";
	public static int jedis_self_port = 6579;
	
	public static boolean runJob = Boolean.parseBoolean(System.getProperty("run_job", "false"));
	public static final String logo = "http://7xiptf.com5.z0.glb.clouddn.com/logo-small.jpeg";
	
	public static final String mongodb_lbs_master_hosts = mapping.getProperty("mongodb.lbs.master.hosts", "42.62.78.2:12030") ;
	public static final String mongodb_lbs_slave_hosts = mapping.getProperty("mongodb.lbs.slave.hosts", "42.62.78.2:12030") ;
	
	public static final String share_base_dir = getValue("share_base_dir", "/data/share/");
	
	public static final String default_avatar = getValue("default_avatar", null);
	
	/**万普的密钥*/
	public static final String callBackKey = mapping.getProperty("wanpu_key", "test");
	
	public static final String callBackey_youmi=mapping.getProperty("youmi_key", "84117d9aa0f0c9e3");
	/**点入Android回调的key*/
	public static final String CALLBACK_KEY_ANDROID_DIANRU=mapping.getProperty("dianru_android_key", "84117d9aa0f0c9e3");
	/**这个密钥是人家趣米给我们的 和以往的不同*/
	public static final String CALLBACK_KEY_ANDROID_QUMI=mapping.getProperty("qumi_android_key", "3b6791837cea67ea");
	public static final String callBackKey_android = mapping.getProperty("wanpu_android_key", "test");
	public static final String CALLBACK_KEY_ANDROID_BEIDUO= mapping.getProperty("beiduo_android_key", "3b6rthvb837cea67ea");
	
	public static final String CALLBACK_KEY_ANDROID_DIANJOY= mapping.getProperty("bdianjoy_android_key", "uen37uslah6e02nm");
	
	public static final String CALLBACK_KEY_IOS_DIANJOY= mapping.getProperty("bdianjoy_ios_key", "sjKm7GO0jdB5Tvdlzawb");
	
	/**米迪给的 不能改*/
	public static final String CALLBACK_KEY_ANDROID_MIDI = mapping.getProperty("midi_android_key", "6ecomyvnp3is6xce7p2j5gpw2htb6h");
	/**给的 不能改*/
	public static final String CALLBACK_KEY_ANDROID_DUOMENG = mapping.getProperty("midi_android_key", "b50cd03a");
	/**接得微信积分墙转发任务*/
	public static final String CALLBACK_KEY_WECHAT_ZHUANFA= mapping.getProperty("WECHAT_ZHUANFA_JIFENQIAN","HO5t4FGOPIYRWBMEQWSCVGKL8ihxvd");
	public static final String WECHAT_MID = mapping.getProperty("WECHAT_ZHUANFA_MID", "");
	/*** 趣米的是人家给的 **/
	public static final String CALLBACK_KEY_IOS_QUMI = mapping.getProperty("qumi_ios_key", "38e6d50c7f07ef3e");
	
	/**点入ios回调的key*/
	public static final String CALLBACK_KEY_IOS_DIANRU=mapping.getProperty("dianru_ios_key", "8Jldkm3IhdbalpiBwans");
	
	static {
		isDeploy = server_id > 100 && server_type.equalsIgnoreCase("deploy");
		isApiServer = server_name.equalsIgnoreCase("api");
		isMisServer = server_name.equalsIgnoreCase("mis");
		isActServer = server_name.equalsIgnoreCase("act");
		isJobServer = server_name.equalsIgnoreCase("job");
		
		jedis_common_server = getValue("jedis_common_server","114.215.130.131");
		jedis_common_port = getIntValue("jedis_common_port", 6579);
		jedis_common_pwd = getValue("jedis_common_pwd", "");
		
		jedis_self_server = getValue("jedis_self_server","114.215.130.131");
		jedis_self_port = getIntValue("jedis_self_port", 6579);
	}
	
	public static String getValue(String string) {
		return mapping.getProperty(string);
	}
	
	public static String getValue(String string, String defaultValue) {
		String v = mapping.getProperty(string);
		if(StringUtils.isEmpty(v)){
			return defaultValue;
		}
		return v;
	}
	
	public static int getIntValue(String string, int defaultValue) {
		String v = mapping.getProperty(string);
		if(StringUtils.isEmpty(v)){
			return defaultValue;
		}
		v = v.trim();
		int i = safeParseInt(v);
		if(i == ERROR_INT){
			return defaultValue;
		}
		return i;
	}
	
	public static boolean getBoolValue(String string, boolean b) {
		String v = mapping.getProperty(string);
		if(StringUtils.isEmpty(v)){
			return b;
		}
		v = v.trim();
		if(v.equals("0"))
			return false;
		if(v.equalsIgnoreCase("true"))
			return true;
		return false;
	}
	
	public static void main(String[] args){
		System.out.println(GlobalConfig.jedis_common_server);
		System.out.println(GlobalConfig.jedis_common_port);
//		for(int i = 0; i < 10; i++){
//			System.out.println(GlobalConfig.getBaiduApiAk());
//		}
	}
	
	private static final int ERROR_INT = -21993961;
	private static int safeParseInt(String str){
		try{
			return Integer.parseInt(str);
		}catch(Exception e){
			return ERROR_INT;
		}
	}

	public static String[] getMemcachedHosts() {
		return null;
	}
	/**redis中存放对象的序列化*/
	public static String jedis_serializer = mapping.getProperty("jedis_serializer","hessian");
	
	public static String pusher = mapping.getProperty("android.push.pusher", "");
	
	public static boolean pusherIsUmeng(){
		return "umeng".equalsIgnoreCase(pusher);
	}
	/**友盟的title不能为null*/
	public static final String PUSH_TITLE = "秒赚大钱";

	/** safari版本使用的版本号 */
	public static String safari_version = mapping.getProperty("safari_version", "1.2.0");

	/**
	 * 钥匙版的url scheme
	 */
	public static String yaoshi_scheme = mapping.getProperty("yaoshi_scheme", "bmyaoshi://");

	/**
	 * websocket 地址
	 */
	public static String websocket_address = mapping.getProperty("websocket_address", "wss://ws.miaozhuandaqian.com:8976/mi");
	
	//public static final Date FIRST_NEW_USER_BASE_TIME = DateUtil.parseDate("20151114 13:15", "yyyyMMdd HH:mm");
	
	/**每一次只能有一个活动ID*/
	public static final int ACTIVITY_ID = 1002;  //1000：双旦活动；1001：春节摇红包活动 1002 三月
	public static final String ACTIVITY_REASON = "三月轻松赢iPhone";

	/***活动的开始时间*/
	public static final Date ACTIVITY_START = DateUtil.parseDate("2016-03-14 10:00:00","yyyy-MM-dd HH:mm:ss");
	/***活动结束时间*/
	public static final Date ACTIVITY_END = DateUtil.parseDate("2016-03-31 23:59:59","yyyy-MM-dd HH:mm:ss");
	
	/***优化的限时任务奖励**/
	public static final Date OPTIMIZE_INVITE_START = DateUtil.parseDate("2016-05-17 00:00:00","yyyy-MM-dd HH:mm:ss");
	/***首页输入邀请码 判断新用户的开始时间**/
	public static final Date NEW_USER_REGIST_START_ = DateUtil.parseDate("2016-05-03 10:00:00","yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 微信支付使用的appid，目前是秒赚大钱的微信公众号
	 */
	public static String weixin_pay_appid = mapping.getProperty("weixin_pay_appid", "wxd9542c0eb613a9cc");
	/**有米的ios开发者盐*/
	public static String YM_IOS_DEV_SERVER_SECRET = mapping.getProperty("ym_ios_dev_server_secret", "");
	/**有米的ios app*/
	public static String YM_IOS_APP_ID = mapping.getProperty("ym_ios_dev_app_id", "");
	
	public static String DiANJOY_APP_ID_IOS = mapping.getProperty("dianJoy_app_id_ios","6ae41269d291f5970ace81f61d883524");

	/**获得启用的 将积分墙放在我们快速任务中的服务 用逗号分隔*/
	public static String channel_quick_task = mapping.getProperty("channel_quick_task", "");
	
	public static String QUICK_TASK_FILTER_KEY_NAME = "quick_task_filter";
	/**安卓积分墙显现配置**/
	public static String INTEGAL_SHOW_ANDROID_CONF = "integal_wall_android";
}

