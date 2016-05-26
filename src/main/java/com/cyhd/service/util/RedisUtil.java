package com.cyhd.service.util;

// redis key 生成工具类
public class RedisUtil {

	public static final String NAME_SELF = "self_redis";
	public static final String NAME_ALIYUAN = "aliyun_redis";
	
	public static String buildTaskReportKey(long userTaskId) {
		return "task_report_" + userTaskId;
	}
	
	public static String buildUserMessageKey(int userId, int type) {
		return "user_message_" + userId + "_" + type;
	}
	
	public static String buildArticleMessagekey(int userId,int type,int client_type){
		return "user_message_" + userId + "_" + type+"_"+client_type;
	}
	//新增获取总条数，最新内容及时间的消息
	public static String buildUserMessageKeyByTotal(int userId,int type){
		return "user_message_total"+userId+"_"+type;
	}
	
	public static String buildUserTodyIncome(int userId){
		return "user_tody_income_r_"+userId;
	}
	
	public static String builderUserInviteFriendByTody(int userId){
		return "user_tody_invite_r_"+userId;
	}
	
	public static String buildUserInstallApp(int userId){
		return "user_install_app_r_"+userId;
	}

	private final static String tokenKey = "ac_token_";
	public static String buildAccessTokenKey(String appid) {
		return tokenKey + appid;
	}
	
	public final static String DEVICE_ALL_USERID_KEY = "device_all_user_id";
	/**idfa玩过的app记录的pre*/
	public final static String IDFA_APP_RECOED = "idfa_app_re";

	/**
	 * 预先排重的App IDFA列表
	 */
	public final static String APP_IDFA_SET_PREFIX = "app_idfa_set_";

	/**
	 * 钥匙版首页消息缓存
	 */
	public final static String HOME_MESSAGE_PREFIX = "user_home_msg_";


	public static String buildIDFAAppKey(String idfa,int appId){
		StringBuilder sb = new StringBuilder(30);
		sb.append(IDFA_APP_RECOED).append("_").append(idfa).append("_").append(appId);
		return sb.toString();
	}
	
	/***构建转发任务流水的查看*/
	public static final String buildArticleLogKey(int article_id,String unionid){
		//ArticleViewLog
		return "a_view_log_"+unionid+"_"+article_id;
	}
	/**用户完成转发任务的key*/
	public static final String builUserArticleLogKey(int user_id){
		return "u_task_log_"+user_id;
	}
	
	public static final String buildUserBlacklistAccount(String account){
		StringBuilder sb = new StringBuilder(64);
		sb.append("black_l_a").append(account);
		return sb.toString();
	}

	/**
	 * 获取app预先排重的redis缓冲键
	 * @param appId
	 * @return
	 */
	public static final String buildPreFilteredIDFAKey(int appId){
		return APP_IDFA_SET_PREFIX + appId;
	}

	/**
	 * 钥匙版本首页消息缓存key
	 * @param userId
	 * @return
	 */
	public static final String buildHomeMessageKey(int userId){
		return HOME_MESSAGE_PREFIX + userId;
	}
	/***
	 * 登陆的ip 缓存在redis中
	 * @param ip
	 * @return
	 */
	public static final String buildLoginIpKey(String ip){
		return "user_login_ip_" + ip;
	}
	
	/***
	 * 上一个登陆的ip 缓存在redis中
	 * @param ip
	 * @return
	 */
	public static final String buildLastTimeLoginIpKey(){
		return "user_login_lt_ip_";
	}

	/**
	 * ios接任务ip的key，缓存每个ip接任务的数量
	 * @param ip
	 * @return
	 */
	public static final String buildIOSTaskIp(String ip){
		return "user_task_ios_ip_" + ip;
	}

	/**
	 * 上一次接限时任务的ip地址
	 * @return
	 */
	public static final String getLastIOSTaskIpKey(){
		return "user_task_last_ip";
	}

	/**
	 * 控制ios接任务的时间间隔
	 * @param ip
	 * @return
	 */
	public static final String buildIOSIPIntervalKey(String ip){
		return "user_task_ip_freq_" + ip;
	}

	/**圣诞活动的key 邀请多少好友**/
	public static final String buildActiveUserKey(int userId,int activity_id){
		return "christ_friend_" + userId + "_" + activity_id;
	}
	
	public static final String builderActivityKey(int activity){
		return  "active_user_draw_log"+activity;
	}

	public static final String buildBigHongbaoKey(int activity){
		return "big_hongbao_of_act_" + activity;
	}

	/**
	 * 缓存做任务的ip地址
	 * @param area
	 * @return
	 */
	public static String buildIpAreaKey(String area){
		return "IP_AREA_" + area;
	}
	
	public static String buildBatteryIdAndAppIdTaskKey(String battery_id,int appId){
		return new StringBuilder(120).append("battery_app_").append(battery_id).append('_').append(appId).toString();
	}
	public static String buildBatteryIdAndAppIdTaskNumKey(String battery_id,int appId){
		return new StringBuilder(120).append("battery_app_num_").append(battery_id).append('_').append(appId).toString();
	}

	public static String buildIntegralWarKey(int userId, String appId) {
		return  new StringBuilder(32).append("jifenq_u_a_").append(userId).append('_').append(appId).append('_').toString();
	}
	public static String buildUserFinshFiveTaskNumKey(int userId){
		return new StringBuilder().append("finsh_t_n_").append(userId).toString();
	}
	
	public static String buildUserInviteFinshTaskFriendIdByDay(int userId,String today){
		return new StringBuilder(64).append("user_invite_tody_finsh_task_i_").append(userId).append('_').append(today).toString();
	}
	
	public static String buildInviterTodayRankKey(String today){
		return new StringBuilder(64).append("user_invite_tody_finsh_task_rank_").append(today).toString();
	}
	
	public static String buildUserInviteDateListKey(int userId){
		return new StringBuilder(64).append("user_invite_day_friend_list_").append(userId).toString();
	}
	
	public static String buildIsCompeleteRankListData(String day){
		return new StringBuilder(64).append("finsh_invite_rank_data_").append(day).toString();
	}
	
	public static String buildUserEffectiveInviteReawrdKey(int user){
		return  new StringBuilder(32).append("user_invite_reawrd_flag_").append(user).toString();
	}
}
