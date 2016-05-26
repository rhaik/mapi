package com.cyhd.service.channelQuickTask;

public final class QuickChannelConfig {
	
	
	/**给用户的奖励最少是**/
	public final static int REWARD_MIN_AMOUNT = 100;
	/**给用户奖励的倍率 -- 如果任务的奖励类型是金币的话**/
	public final static float REWARD_RADIOS = 0.12f;
	
	/**点入的盐**/
	public static final String DIANRU_SALT = "18JMkd9JhsytAlo";
	/**点入的最低价格**/
	public final static int MIN_DINARU_PRICE = 800;
	/**点入快速任务的最高价格**/
	public final static int MAX_DIANRU_PRICE =1000;
}
