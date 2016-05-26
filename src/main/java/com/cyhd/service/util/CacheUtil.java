package com.cyhd.service.util;


public class CacheUtil {

	public static final int MAX_LIVE_TIME = 30 * 1000;
			
	public static final int MAX_LRU_CACHED_SIZE = GlobalConfig.getIntValue("max_lru_cache_size", 512);
	
	public static final boolean USE_MEMCACHED = GlobalConfig.getBoolValue("use_memcache", true);
	
	public static final int MAX_EXPIRE_TIME = GlobalConfig.getIntValue("memcache_expire_time", 86000) * 1000;
	
	// 进程外，memcached 实现
	public static final String MEMCACHED_RESOURCE = "memcached";
	
	public static final String SINGLE_MEMCACHED_RESOURCE = "single_memcached";

	// 进程内，内存LRU实现
	public static final String RAM_LRU_RESOURCE = "ram_lru";
	
	// 进程内，内存实现，一直常驻内存
	public static final String RAM_RESOURCE = "ram";
	
	public static final String RAM_LA_RESOURCE = "ram_liveaccess";
	
	public static final String RAM_URL_LA_RESOURCE = "ram_lrm_liveaccess";
	
	public static final String DUAL_CACHE_RESOURCE = "dual_cache";

	// 各个应用场景的key
	private static final String USER_PREFIX = "user_";  // 用户信息缓存
	private static final String TICKET_PREFIX = "ticket_"; // 用户ticket缓存
	private static final String BANNER_PREFIX = "banner_"; // banner
	private static final String DEVICE_PREFIX = "device_"; // 
	private static final String CAPTCHAS_PREFIX = "captchas_"; //充值验证码


	// 阅读者的IP信息
	private static final String READER_IP_PREFIX = "IP_";
	

	public static String getUserKey(long uid){
		return USER_PREFIX + uid;
	}
	
	public static String getTicketKey(String ticket){
		return TICKET_PREFIX + ticket;
	}

	public static String getUserBannerRoleKey(int cityid, int type) {
		return BANNER_PREFIX + cityid + "_" + type;
	}

	public static String getDevicesKey(long userId) {
		return DEVICE_PREFIX + userId;
	}
	
	public static String getUserImageKey(long id) {
		return "USER_IMG_" + id;
	}

	public static String getUserSettingKey(long userid) {
		return "USER_SETTING_";
	}

	public static String getUserTasksKey(int userId) {
		return "user_doing_tasks_" + userId;
	}
	public static String getCaptchasKey(int userId) {
		return CAPTCHAS_PREFIX + userId;
	}


	/**
	 * 用户最近几条签到记录的缓存键
	 * @param userId
	 * @return
	 */
	public static String getUserCheckInListKey(int userId){
		return USER_PREFIX + "checkin_list_" + userId;
	}

	/**
	 * 用户签到统计信息的缓存键
	 */
	public static String getUserCheckInStatKey(int userId){
		return USER_PREFIX + "checkin_stat_" + userId;
	}

	/**
	 * 获取阅读者ip的缓存键
	 * @param ip
	 * @return
	 */
	public static String getIPCacheKey(String ip){
		return READER_IP_PREFIX + ip;
	}

	/**
	 * 获取夺宝商品的缓存key
	 * @param id
	 * @return
	 */
	public static String getProductKey(int id) {
		return "PRODUCT_"+ id;
	}

	/**
	 * 获取夺宝规则的缓存key
	 * @param id
	 * @return
	 */
	public static String getProductRuleKey(int id) {
		return "PRODUCT_RULE_"+ id;
	}

	/**
	 * 获取夺宝订单的缓存key
	 * @param id
	 * @return
	 */
	public static String getOrderProductKey(int id) {
		return "ORDER_PRODUCT_"+id;
	}

	/**
	 * 获取夺宝号的缓存key
	 * @param productActivityId
	 * @return
	 */
	public static String getProductLotteryNumberKey(int productActivityId) {
		return "PRODUCT_LOTTERY_NUMBER_" + productActivityId;
	}

	/**
	 * 获取夺宝活动的缓存key
	 * @param productActivityId
	 * @return
	 */
	public static String getProductActivityKey(int productActivityId) {
		return "PRODUCT_ACTIVITY_"+productActivityId;
	}

	public static String getHotProductActivityKey() {
		return "PRODUCT_ACTIVITY_HOT";
	}

	public static String getShareOrderKey(int uid) {
		return "SHARE_ORDER_USER_"+uid;
	}

	public static String getUserCartKey(int uid) {
		return "USER_CART_" + uid;
	}

	/**
	 * 获取某一商品最新一期活动的缓存key
	 * @param productId
	 * @return
	 */
	public static String getLatestActivityKey(int productId){
		return "LATEST_PRODUCT_ACTIVITY_" + productId;
	}
}
