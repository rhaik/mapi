package com.cyhd.service.constants;

public class PropertiesConstants {

	public static final String memcached_hosts = "memcached_hosts" ; // memcached hosts ; host1,host2
	public static final String android_app_version = "android_version" ; // android 版本号 
	public static final String ios_app_version = "ios_version" ; // ios 版本号 
	public static final String sms_type = "sms_type" ; // 短信运营商（new）
	
	public static final String interface_update_version = "interface_update_version" ; // 接口版本号控制

	public static final String android_push_channels = "android_push_channels" ; // push参数
	
	public static final String SCOPE_TEST = "test";
    public static final String SCOPE_DEPLOY = "deploy";
    
    public static final String START_PICTURE_KEY = "start_picture";
    /**app限时任务的更新时间提示信息 如：<b>任务更新时间为上午11:00、下午18:00左右</b>*/
    public static final String APP_TASK_HINT="app_task_hint";
    /**转发任务的更新时间提示信息 如：<b>任务更新时间为上午11:00、下午18:00左右</b>*/
    public static final String ARTICLE_TASK_HINT="article_task_hint";

	/**
	 * 微信分享的域名列表
	 */
	public static final String article_share_hosts = "article_share_hosts";
}
