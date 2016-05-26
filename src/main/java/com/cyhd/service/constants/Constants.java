package com.cyhd.service.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cyhd.service.util.GlobalConfig;


/**
 * 常量类
 * 
 */
public class Constants {
	
	public final static char[] CODESE_QUENCE = { '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'g', 'k', 'm', 'n', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	
	public static final int USER_TEACHEER = 1; //
	public static final int USER_PARENT = 2;
	
	
	public static final int UNSETED = 0; // 未设置
	
	public static final int ESTATE_Y = 1; // 有效
	public static final int ESTATE_N = 2; // 无效
	
	public static final int AUDIT_STATE_INIT = 1;  //审核中
	public static final int AUDIT_STATE_PASSED = 2; // 审核通过
	public static final int AUDIT_STATE_FAILED = 3; // 审核不通过
	
	
	public static final int week_sun = 1 << 0 ; // 周日
	public static final int week_mon = 1 << 1 ; // 周一
	public static final int week_tus = 1 << 2 ; // 周二
	public static final int week_web = 1 << 3 ; // 周三
	public static final int week_thu = 1 << 4 ; // 周四
	public static final int week_fri = 1 << 5 ; // 周五
	public static final int week_sat = 1 << 6 ; // 周六
	
	public static final int platform_unkonwn = 0 ;						// unknown
	public static final int platform_android = 1 ;						// android
	public static final int platform_ios = 2 ;							// ios
	
	public static final String CREATE_QR_CODE_URl=GlobalConfig.admin_url + "Qr/Create?uid=";
	
	/**
	 * 时间常量
	 */
	public static final int minutes_millis = 1 * 60 * 1000 ; // 一分钟毫秒
	public static final int hour_millis = 1 * 60 * 60 * 1000 ; // 一小时毫秒
	public static final int half_hour_millis = 30 * 60 * 1000 ; // 30分钟毫秒
	public static final long day_millis = 24 * hour_millis ; // 一天毫秒
	
	public static final int VERCODE_TTL = 30 * minutes_millis;
	
	public static final long TASK_EXPIRE_TIME = 60*minutes_millis;
	
	public static final int QUICK_TASK_EXPIRE_TIME = 20 * minutes_millis;
	
	public static final long ARTICLE_TASK_EXPIRE_TIME = 12*60*minutes_millis;

	public static boolean auth_switch = false;  //是否真的去国政通认证

	public static long mis_cookie_time = 5 * day_millis;
	/**5分钟的秒数*/
	public static final int FIVE_SECONDS = 5*60;
	/**一天的秒数*/
	public static final int DAY_SECONDS = 1*24*3600;
	/**一个月的秒数*/
	public static final int  MONTH_SECOND_TIME = 30 * DAY_SECONDS;
	
	public static String pay_log = "pay";

	public static final String SCOPE_TEST = "test";

	public static final int SOLVE_N = 2;
	public static final int SOLVE_Y = 1;

	public static final String cache_log = "cache";

	public static final String job_log = "job";
	
	public static final String wallet_log = "wallet";

	public static final String test_mobile_prefix = "6";
	
	public static final  String mobile_apple_test_teacher = "66600000001";
	public static final  String mobile_apple_test_student = "67700000001";

	public static final int user_test_no = 0;
	public static final int user_test_yes = 1;

	public static final int sex_unknown = 0;

	public static final int sex_weman = 2;

	public static final int sex_man = 1;
	
	/**
	 * 优惠券状态
	 */
	public static final int coupon_user_not_active = -1 ;// 未激活
	public static final int coupon_user_not_use = 1 ;// 未使用
	public static final int coupon_user_occupy = 2 ;// 占用
	public static final int coupon_user_useed = 3 ;// 已使用
	public static final int coupon_user_expiry = 4 ;// 失效
	
	public static final String COUPON_GENERATE_BY_CODE_EXCHANGE = "优惠码兑换优惠券";	
	public static final String COUPON_GENERATE_BY_OPER = "运营手动发放优惠券";
	
	/**
	 * 优惠券类型	发放类型。1：运营发放。2：活动发放。3优惠码兑换
	 */
	public static final int COUPON_TYPE_OPER_SEND = 1 ;		// 运营发放
	public static final int COUPON_TYPE_ACTIVE_SEND = 2 ;	// 活动发放
	public static final int COUPON_TYPE_CODE_SEND = 3 ;		// 优惠码兑换
	public static final int COUPON_TYPE_BD_SHOP = 4 ;		// BD商户推广

	/**
	 * 优惠券记录的状态
	 */
	public static final int coupon_record_active = -1 ;// 激活
	public static final int coupon_record_occupy = 1 ;// 占用
	public static final int coupon_record_useed = 2 ;// 已使用
	public static final int coupon_record_cancel_occupy = 3 ;// 取消占用
	public static final int coupon_record_expiry = 4 ;// 失效
	public static final int coupon_record_back = 5 ;// 回退优惠券
	
	/**
	 * 优惠码
	 */
	public static final int COUPON_CODE_SUCCESS = 3000; 			//成功
	public static final int COUPON_CODE_NOT_FOUND = 3001; 			//优惠码对象不存在
	public static final int COUPON_CODE_EXPIRED  = 3002; 			//优惠码过期
	public static final int COUPON_CODE_NOT_INTIME  = 3003; 		//优惠码未开始兑现	（保留，未用）
	public static final int COUPON_CODE_HAS_EXCHANGED  = 3004;		//该优惠码，该用户已兑换过
	public static final int COUPON_CODE_NATIVE_ERROR  = 3005; 		//优惠码本身错误
	public static final int COUPON_CODE_COUNT_OVER_FLOW  = 3006; 	//已达到最大可兑换次数
	public static final int COUPON_CODE_HAS_BEEN_STOPED  = 3007; 	//优惠码被停止
	public static final int COUPON_CODE_PROMOTION_SUCCESS  = 3050; 	// 邀请码验证成功
	public static final int COUPON_CODE_PROMOTION_FAIL  = 3051 ; 	// 邀请码验证失败
	
	public static final int COUPON_CODE_ERROR_POPUP  = 3010 ; 	// 邀请码验证失败，弹窗提示
	
	public static final int COUPON_STATE_INIT  = 0; 				//新建优惠码
	public static final int COUPON_STATE_ON  = 2; 					//优惠码开始可以被兑换
	public static final int COUPON_STATE_OFF  = 3; 					//优惠码不再可以被兑换
	
	public static final int MIS_USER_ROLE_NONE = 0;	//不是MIS用户
	public static final int MIS_USER_ROLE_NORMAL = 1;	//只可读
	public static final int MIS_USER_ROLE_SUPER = 2;	//读写
	
	/**
	 * 支付类型   
	 */
	public static final int pay_type_no = 0 ; // 未支付
	public static final int pay_type_zero = 1 ; // 零元支付（余额或者优惠券）
	public static final int pay_type_weipay = 2 ; // 微信支付
	public static final int pay_type_alipay_web = 3 ; // 支付宝web
	public static final int pay_type_alipay_client = 4 ; // 支付宝客户端
	public static final int pay_type_xinyongka = 5 ; // 信用卡
	public static final int pay_type_um = 6 ; // U付支付
	
	/**
	 * 支付记录  记录类型
	 */
	public static final int pay_record_recharge = 1;  // 充值
	public static final int pay_record_pay = 2 ; // 消费
	public static final int pay_record_withdraw_deposit = 3 ; // 提现

	public static final String SESSION_MIS_USER = "misuserinfo";
	public static final String COOKIE_MIS_TICKET = "mms_ggid_1";
	
	public static final int sms_record_method_up = 1 ;// 上行
	public static final int sms_record_method_down = 2 ;// 下行
	
	/**
	 * smsrecord
	 * 运营商类型 1 梦网  2 百悟
	 */
	public static final int server_type_mw = 1 ;// 梦网
	public static final int server_type_bw = 2 ;// 百悟
	
	
	/**
	 * smsrecord
	 * 业务类型
	 * 
	 */
	public static final int sms_record_type_up_bd = 1 ;// 上行bd拓展
	public static final int sms_record_type_up_td = 2 ;// 上行td退订
	
	public static final int sms_record_type_down_code = 11 ;// 下行验证码
	public static final int sms_record_type_down_mis = 12 ;// 下行mis营销
	public static final int sms_record_type_down_other = 13 ;// 下行其他
	public static final int sms_record_type_down_push = 14 ;// 下行push失败发送
	public static final int sms_record_type_down_internal = 15 ;// 下行内部通知短信
	
	// 普通短信渠道
	public static final int sms_channel_common = 1 ;
	// 营销短信渠道
	public static final int sms_channel_market = 2 ;
	
	/**
	 * smsrecord
	 * 发送结果
	 */
	public static final int sms_record_success = 1 ;// 成功
	public static final int sms_record_fail = 2 ;// 失败
	
	/**
	 * 用户提现到 哪的常量
	 */
	public static final int deposit_to_alipay = 1 ;// 到支付宝
	public static final int deposit_to_bank = 2 ;// 到银行卡
	
    /**
     * 退款状态 
     */
	public static final int deposit_state_create = 1 ;// 创建
	public static final int deposit_state_ing = 2 ;// 提现中
	public static final int deposit_state_success = 3 ;// 提现成功
	public static final int deposit_state_fail = 4 ;// 提现失败
	public static final int deposit_state_back = 5 ;// 提现打回
	public static final int deposit_state_fail_and_back = 6 ;// 提现失败并回到余额
	public static final int deposit_state_fail_and_transfer_again = 7 ;//  提现失败（重新打款）
	public static final int deposit_state_create_not_transfer = 8 ;//  待提现（暂不打）

	//用户试用时间
	public static final int user_trial_time = 5;
	
	
	//分享的相关参数
	
	public static final String share_logo_url = GlobalConfig.logo;
	public static final String share_wx_content = "带你赚钱带你飞";
	public static final String share_wx_title = "赚大钱";
	public static final String share_wx_pre_link=GlobalConfig.base_url_https + "www/downloads/share/";

	/**
	 * 用户分享时设置的cookie名称
	 */
	public static final String INVITE_COOKIE_KEY = "safi_uuid";
	
	public static final byte share_wx_friend = 1;
	public static final byte share_wx_zone=2;
	
	//分享相关参数结束
	
	//邀请码前缀
	public static final String invite_code_prefix = "bigmoney_";
	
	//积分常量开始
	/**积分来源---万普*/
	public static final int INTEGAL_SOURCE_WANPU=1;
	public static final int INTEGAL_SOURCE_YOUMI=2;
	/**积分来源---Android-点入*/
	public static final int INTEGAL_SOURCE_DIANRU=3;
	/**积分来源---Android-趣米*/
	public static final int INTEGAL_SOURCE_QUMI=4;
	/**积分来源---Android-贝多*/
	public static final int INTEGAL_SOURCE_BEIDUO=5;
	/**积分来源---好友分成*/
	public static final int INTEGAL_SOURCE_SHARE = 6;

	/**积分来源---系统补发或者系统奖励*/
	public static final int INTEGAL_SOURCE_SYSTEM = 8;

	/**积分来源---Android-点乐*/
	public static final int INTEGAL_SOURCE_DIANJOY=7;
	/**积分来源---Android-米迪*/
	public static final int INTEGAL_SOURCE_MIDI = 9;
	/**积分来源---Android-多盟*/
	public static final int INTEGAL_SOURCE_DUOMENG = 10;
	/**微信转发任务积分墙*/
	public  static final int INTEGAL_SOURCE_WECHAT = 11;
	/**有米IOS*/
	public static final int INTEGAL_SOURCE_YOUMI_IOS=12;
	/**积分来源---Android-点乐*/
	public static final int INTEGAL_SOURCE_DIANJOY_IOS=13;
	/**积分来源---IOS-趣米*/
	public static final int INTEGAL_SOURCE_QUMI_IOS=14;
	/**积分来源---ios-点入*/
	public static final int INTEGAL_SOURCE_DIANRU_IOS=15;
	/***财神榜奖励***/
	public static final int INTEGAL_SOURCE_CAISHENBANG = 16;
	
	/***兑换比率 多少金币(积分)兑换一元人民币**/
	public static final int INTEGAL_RADIO = 1000;
	/**最少起兑的积分(金币)数*/
	public static final int INTEGAL_MIN_EXCHANGE_NUM= 100;

	
	/** ios最少兑换200金币 */
	public static final int INTEGAL_MIN_EXCHANGE_NUM_IOS = 200;

	/**最多能兑换的积分(金币)*/
	public static final int INTEGAL_MAX_EXCHANGE_NUM= 10000;
	/**余额不足*/
	public static final int INTEGAL_ERROR_CODE_BALANCE=100;
	/**关键参数错误*/
	public static final int INTEGAL_ERROR_CODE_TOKEN=101;
	/**兑换数量小于默认起兑数量*/
	public static final int INTEGAL_ERROR_CODE_MIN=101;
	/**兑换数量大于默认最大兑换数量*/
	public static final int INTEGAL_ERROR_CODE_MAX=101;
	//积分常量结束
	/**微信公众号在这个周期之上需要在重新生成 */
	public static final long QR_CREATE_MIN_TTL=4*day_millis;
	
	public static final long QR_CREATE_MAX_TTL = 7*day_millis;
	/**默认的设备表每次通知的数量*/
	public static final int DEFAULT_DEVICE_PAGE_SIZE = 500;
	
	public static final List<Integer> defaultUserIds = new ArrayList<Integer>();
	/**时间间隔大于此的不 */
	public static final int MAX_RANK_TIME = 1*3600;
	
	//揭晓时间
	public static final int PRODUCT_ANNOUNCED_TIME = 10 * minutes_millis;
	//夺宝活动过期时间，3个月后
	public static final long PRODUCT_ACTIVITY_EXPIRE_TIME = 90 * day_millis;
	
	static{
		if(GlobalConfig.isDeploy){
			defaultUserIds.add(3031);
			defaultUserIds.add(3032);
			defaultUserIds.add(3034);
			defaultUserIds.add(3035);
			defaultUserIds.add(3038);
			defaultUserIds.add(3039);
			defaultUserIds.add(3040);
			defaultUserIds.add(3042);
			defaultUserIds.add(3043);
			defaultUserIds.add(3047);
		}else{
			for(int i = 0; i <= 10; i++){
				if(i == 2){//没有690
					continue;
				}
				defaultUserIds.add(688+i);
			}
		}
	}
	
	public  static final List<Integer> monthUserIds = new ArrayList<Integer>();
	
	static{
		if(GlobalConfig.isDeploy){
			monthUserIds.add(3034);
			monthUserIds.add(3038);
			monthUserIds.add(3039);
			monthUserIds.add(3032);
			monthUserIds.add(3031);
			monthUserIds.add(3042);
			monthUserIds.add(3040);
			monthUserIds.add(3035);
			monthUserIds.add(3047);
			monthUserIds.add(3043);
		}else{
			monthUserIds.add(689);
			monthUserIds.add(691);
			monthUserIds.add(692);
			monthUserIds.add(688);
			monthUserIds.add(698);
			monthUserIds.add(694);
			monthUserIds.add(693);
			monthUserIds.add(696);
			monthUserIds.add(697);
			monthUserIds.add(695);
		}
	}

	public final static String ARTICLE_DEFAULT_IMG = "https://www.mapi.lieqicun.cn/static/images/img/app_icon.png";
	/**如果使用的是客户端一样的加密需要长度为16*/
	public final static String ARTICLE_AES_PASSWORD="8daoi789jkjckfdg";

	/**
	 * 微信分享出去的文章id密钥
	 */
	public final static String ARTICLE_VIEW_PASSWORD="UjjxksiuWA81ysiO";
	
	public static int ARTICLE_DEFAULT_EXPRIE=12*3600;
	static{
		if(GlobalConfig.isDeploy == false){
			ARTICLE_DEFAULT_EXPRIE = 10*60;
		}
	}
	//1、登陆成功
	//2、手机号已知，需要验证码 ，对应现在5的返回码
	//3、手机号未知，需要用户输入手机号并获取验证码。 对应的1、2、3、4的返回码
	//4、快速登陆失败，只能用微信登陆
	/**idfa查找到得did相等*/
	public final static int QUICK_LOGIN_IS_EXIST_USER = 1;
	/**失败*/
	public final static int QUICK_LOGIN_FAIL = 4;
	/**手机号未知，需要用户输入手机号并获取验证码*/
	public final static int QUICK_LOGIN_BAD_DID = 3;
	/**查找到的did不相等 有绑定电话*/
	public final static int QUICK_LOGIN_BAD_DID_PHONE = 2;
	/**支付宝绑定达五次以上的人**/
	public static List<String> blacklist = Arrays.asList("jjww9009@126.com","13866990922","17820346611","18802432896","15242268081","15635762930");

	/**一个电池id能做多少次同一个任务**/
	public static int BATTERY_APP_ID_TIMES = 5;

	/**
	 * json输出格式
	 */
	public static final String APPLICATION_JSON_VALUE = "application/json; charset=UTF-8";
}
