package com.cyhd.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Resource;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.ECacheDao;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.common.util.structure.ConcurrentLRUCache;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.EffectiveInviteMapper;
import com.cyhd.service.dao.db.mapper.UserFriendMapper;
import com.cyhd.service.dao.db.mapper.UserIncomeLogMapper;
import com.cyhd.service.dao.po.EffectiveInvite;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserFriend;
import com.cyhd.service.dao.po.UserThridPartIntegral;
import com.cyhd.service.util.RedisUtil;
import com.cyhd.service.vo.InviteUserVo;
import com.cyhd.service.vo.UserFriendVo;

import redis.clients.jedis.Tuple;


@Service
public class UserFriendService extends BaseService {

	@Resource
	private UserFriendMapper userFriendMapper;
	
	@Resource
	private UserService userService;
	
	@Resource
	private BeginnerService beginnerService;
	
	@Resource
	private UserIncomeService userIncomeService;
	
	@Resource(name=RedisUtil.NAME_ALIYUAN)
	private IJedisDao userFriendRedisCache;
	
	@Resource
	private UserDrawService userDrawService;
	
	@Resource
	private EffectiveInviteMapper effectiveInviteMapper;
	
	//保存邀请关系，key的邀请人是value
	private ConcurrentLRUCache<Integer, Integer> friendsCache = new ConcurrentLRUCache<Integer, Integer>(11, 300);

	//用户好友数量的缓存
	private ECacheDao<Integer> friendCountCache = new CacheLRULiveAccessDaoImpl(Constants.minutes_millis * 2, 2048);

	private  CacheLRULiveAccessDaoImpl< List<InviteUserVo>> inviteRankCache = new CacheLRULiveAccessDaoImpl<>(Constants.FIVE_SECONDS*3);
	
	@Resource
	private UserIntegalIncomeService userIntegalIncomeService;
	
	@Resource
	private UserMessageService userMessageService;
	
	@Resource
	private UserThridPartIntegralService userThridPartIntegralService;
	/**
	 * 注册的时候，如果是有邀请人
	 * @param invitor_id：邀请人的 identify_id
	 * @param u : 注册人
	 */	
	public boolean onAddUserFriend(User invitor, User u,String idfa) {
//		//老用户不存在师徒关系
//		if(!userService.isNewUser(u)){
//			return ;
//		}
		if(invitor.getId() > u.getId()){
			logger.warn("邀请人的id有问题,邀请人：{},user:{}",invitor.getId(),u.getId());
			return false;
		}
		int invitorId = invitor.getId();
		UserFriend uf  = new UserFriend();
		uf.setUser_id(invitorId);
		uf.setFriend(u.getId());
		uf.setInvi_time(new Date());
		boolean result = this.save(uf);
		if(result){
			if(!invitor.isTaskInviteComplete()){  //该用户还没有完成邀请好友任务
				//beginnerService.onInviteTaskComplete(invitor,idfa,UserSystemMessage.PUSH_CLIENT_TYPE_ALL);
				beginnerService.addPreFiveFriendExtraReward(invitor, idfa);
			}
			this.onHasInvitorNewUserOpenApp(u, invitor);
		}
		return result;
	}
	
	
	/**
	 * 用户好友列表
	 * 
	 * @param int userid
	 * @param int start
	 * @param int size
	 * 
	 * @return List<UserFriendVo>
	 */
	public List<UserFriendVo> getUserFriends(int userid, int start, int size){
		List<UserFriend> friends = userFriendMapper.getInvitationFriends(userid, start, size);
		
		List<UserFriendVo> vos = new ArrayList<UserFriendVo>();
		String dateKey = new String();
		for(UserFriend friend : friends){
			UserFriendVo vo = new UserFriendVo();
			
			String temp = DateUtil.format(friend.getInvi_time(), "yyyyMMdd");
			vo.setDisplayDate(false);
			if(dateKey.isEmpty()) {
				dateKey = temp;
				vo.setDisplayDate(true);
			} else if(!dateKey.equals(temp)){
				if(vos.size() > 0) {
					dateKey = temp;
					vo.setDisplayDate(true);
				}
			}
			vo.setFriend(userService.getUserById(friend.getFriend()));
			vo.setInvi_time(friend.getInvi_time());
			vos.add(vo);
		}
		return vos;
	}

	/**
	 * 计算用户的好友数量，带缓存
	 * @param userId
	 * @return
	 */
	public int countUserFriends(int userId){
		Integer count = friendCountCache.get(String.valueOf(userId));
		if (count ==  null) {
			count = userFriendMapper.getInvitationFriendsCount(userId);
			if (count != null){
				friendCountCache.set(String.valueOf(userId), count);
			}
		}
		return count == null ? 0 : count;
	}
	public void removeFriendCountCache(int userId){
		friendCountCache.remove(String.valueOf(userId));
	}

	@Resource
	private UserIncomeLogMapper userIncomeLogMapper;
//	public int countUserFriendsAmount(int userId) {
//		Integer i = userIncomeLogMapper.getUserFriendAmountByUserId(userId);
//		return i == null ? 0 : i;
//	}
	public int countUserGrandson(int userId){
		Integer i = userFriendMapper.getGrandsonCount(userId);
		return i == null ? 0 : i;
	}
	/**
	 * 统计徒孙
	 * @param userId
	 * @return
	 */
//	public int countUserGrandsonAmount(int userId) {
//		Integer i = userIncomeLogMapper.getUserGrandsonAmountByUserId(userId);
//		return i == null ? 0 : i;
//	}
	/**
	 * 保存好友
	 * 
	 * @param UserFriend friend
	 * 
	 * @return boolean
	 */
	public boolean save(UserFriend friend){
		boolean flag = false;
		try{
			 flag =  userFriendMapper.addInvitationFriends(friend) >=1;
		}catch(Exception e){
			logger.error("save UserFriend",e);
		}
		 if(flag){
			 //清除各种缓存
			 try {
				 friendsCache.remove(friend.getFriend());
				 friendCountCache.remove(String.valueOf(friend.getUser_id()));
				 userFriendRedisCache.remove(RedisUtil.builderUserInviteFriendByTody(friend.getUser_id()));
			} catch (Exception e) {
			}
		 }
		 return flag;
	}
	//获取用户的邀请人
	public int getInvitor(int userId){
		Integer uid = friendsCache.get(userId);
		if(uid == null){
			uid = userFriendMapper.getInvitor(userId);
			if(uid == null){
				uid = 0;
			}
			friendsCache.put(userId, uid);
		}
		return uid == null ? 0 : uid;
	}
	/**
	 * 获取今日收徒 
	 * TODO 先不用缓存 之后测试通过 加上缓存
	 * @param userId
	 * @return
	 */
	public int countTodyInviteFriendByuserId(int userId){
		String key = RedisUtil.builderUserInviteFriendByTody(userId);
		int invite = 0;
		
		try {
			String value = userFriendRedisCache.get(key);
			if(value != null){
				return Integer.parseInt(value);
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("get user tody invite count:cause by:{}",e);
			}
		}
		
		invite = userFriendMapper.getTodyInviteFriendCount(userId);
		
		try {
			userFriendRedisCache.set(key, Integer.toString(invite), 600);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("set user tody invite count:cause by:{}",e);
			}
		}
		
		return invite;
				
	}

	/**
	 * 根据用户的好友数量获取其分成比例<br/>
	 * 根据用户收入对其师傅进行分成时，需传入师傅的用户id
	 * @param userId
	 * @return
	 */
	public float getShareRate(int userId){
		float shareRate = 0.1f;

		//双十二活动，已结束
//		int friends = countUserFriends(userId);
//		if (friends > 9) { //10名及以上
//			shareRate = 0.2f;
//		}else if (friends > 4){//5名及以上
//			shareRate = 0.15f;
//		}


		return shareRate;
	}

	/**
	 * 根据邀请人和当前的金额计算其分成收入
	 * @param userId
	 * @param amount
	 * @return
	 */
	public int getShareAmount(int userId, int amount){
		return Math.round(amount * getShareRate(userId));
	}
	
	/***
	 * app限时任务的师傅分成计算<br/>
	 * 已经加上额外的分成啦
	 * @param userId
	 * @param amount
	 * @return
	 */
	public int getShareAmountByAppTask(int userId, int amount){
		return Math.round((getExtraShareRateByAppTask(userId)+0.1f) * amount);
	}
	/***
	 * app 3月活动 额外的分成,
	 * 师傅和做任务和分成都有额外的分成
	 * @return
	 */
	public float getExtraShareRateByAppTask(int userId){
		return getExtraShareRateByAppTask(userId, countUserFriends(userId));
	}
	/***
	 * app 3月活动 额外的分成：<br/>
	 * 按照邀请人数得到额外的分成
	 * @param userId
	 * @param countUserFriends
	 * @return
	 */
	public float getExtraShareRateByAppTask(int userId,int countUserFriends){
		float rate = 0f;
//		//单价分成涨不停
//		if(countUserFriends >= 50){
//			rate = 0.2f;
//		}else if(countUserFriends >= 40){
//			rate = 0.15f;
//		}else if(countUserFriends >= 30){
//			rate = 0.1f;
//		}else if(countUserFriends >= 20){
//			rate = 0.05f;
//		}else if(countUserFriends >= 10){
//			rate = 0.05f;
//		}
		return rate;
	}
	/***
	 * 有师傅的新用户首次登录的逻辑<br/>
	 * 没有处理邀请人
	 *  
	 * @param user
	 * @param invitor 有可能没有邀请人或者user、invitor相等
	 */
	public void onHasInvitorNewUserOpenApp(User user,User invitor){
		//如果邀请人没有 或者是同一个 
		if(invitor == null || invitor.getId() == user.getId()){
			return ;
		}
		//userDrawService.addUserDrawByFriend(user.getId(), GlobalConfig.ACTIVITY_REASON);
	}
	/****
	 * 用户完成一个限时任务后调用
	 * @param user
	 */
	
	public void onUserFriendFinshFirstAppTask(User user){
//		int invitorid = getInvitor(user.getId());
//		if(invitorid <= 0){
//			return ;
//		}
//		Date now = GenerateDateUtil.getCurrentDate();
//		//新用户是当天的新用户
//		if(! DateUtil.isSameDay(now, user.getCreatetime())){
//			return ;
//		}
//		addEffectiveInvite(invitorid, user.getId(),1);
	}
	/**
	 * 添加一个有效邀请的入口
	 * @param invitorid 邀请人的id
	 * @param userId 被邀请人的id
	 */
	private void addEffectiveInvite(int invitorid,int userId,int inviteNums){
		addEffectiveInvite(invitorid, userId, inviteNums, DateUtil.getTodayStr());
	}
	private volatile boolean isExistRankKey = false;
	/**
	 * 添加一个有效邀请的入口
	 * @param invitorid 邀请人的id
	 * @param userId 被邀请人的id
	 */
	public void addEffectiveInvite(int invitorid,int userId,int inviteNums,String days){
		try {
			EffectiveInvite effectiveInvite = new EffectiveInvite(invitorid,days);
			effectiveInvite.setNum(inviteNums);
			effectiveInviteMapper.insertOrUpdate(effectiveInvite);
			//添加排名
			String todayRankKey = RedisUtil.buildInviterTodayRankKey(days);
			//user每天的邀请列表的徒弟列表的key
			String userInviteByDayListKey = RedisUtil.buildUserInviteFinshTaskFriendIdByDay(invitorid, days);
			//user所有的邀请的天数的key
			String userInviteAllDaysKey = RedisUtil.buildUserInviteDateListKey(invitorid);
			
			//给师傅的邀请人数增加inviteNums
			userFriendRedisCache.zincrby(todayRankKey, inviteNums, String.valueOf(invitorid));
			//用户邀请的徒弟列表
			userFriendRedisCache.sadd(userInviteByDayListKey, String.valueOf(userId));
			//将用户的邀请时间加入到set
			userFriendRedisCache.sadd(userInviteAllDaysKey, days);
			
			//不存在的key 就加上expire这样过期时间长一点
			//防止有错 将过期时间增加 11天
			int seconds = Constants.DAY_SECONDS*11;
			
			if(!isExistRankKey && !(isExistRankKey = userFriendRedisCache.exists(todayRankKey))){
				isExistRankKey = true;
				userFriendRedisCache.expire(todayRankKey, seconds);
			}
			if( !(userFriendRedisCache.exists(userInviteAllDaysKey))){
				userFriendRedisCache.expire(userInviteByDayListKey, seconds);
				userFriendRedisCache.expire(userInviteAllDaysKey, seconds);
			}
			
		} catch (Exception e) {
			logger.error("操作今日收徒出现异常:invitor:{},user:{},cause:",invitorid,userId,e);
		}
	} 
	
	public Map<String, List<InviteUserVo>> getUserAllInvite(int userId){
		String inviteAllDaysKey = RedisUtil.buildUserInviteDateListKey(userId);
		try {
			Map<String, List<InviteUserVo>> trv = new LinkedHashMap<>();
			Set<String> inviteDays = userFriendRedisCache.smembers(inviteAllDaysKey);
			if(inviteDays != null){
				Iterator<String> it = inviteDays.iterator();
				List<InviteUserVo> tmp = null; 
				while(it.hasNext()){
					String inviteDay = it.next();
					String inviteDayKey = RedisUtil.buildUserInviteFinshTaskFriendIdByDay(userId, inviteDay);
					if (StringUtil.isNotBlank(inviteDayKey )) {
						tmp = getUserInviteFriendList(inviteDayKey, inviteDay);
						if (tmp != null && !tmp.isEmpty()) {
							trv.put(inviteDay, tmp);
						}
					}
				}
			}
			return trv;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<InviteUserVo> getUserInviteFriendList(String inviteDaysKey,String day){
		try {
			Set<String> inviteFriends = userFriendRedisCache.smembers(inviteDaysKey);
			if(inviteFriends != null && !inviteFriends.isEmpty()){
				 List<InviteUserVo> trv = new ArrayList<InviteUserVo>();
				 boolean isFirst = true;
				 for(String userStr:inviteFriends){
					 InviteUserVo inviteUserVo = new InviteUserVo();
					 inviteUserVo.setUser(userService.getUserById(Integer.parseInt(userStr)));
					 inviteUserVo.setDay(day);
					 inviteUserVo.setDisplayDate(isFirst); 
					 isFirst = false;
					 if(inviteUserVo.getUser() != null){
						 trv.add(inviteUserVo);
					 }
				 }
				 return trv;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<InviteUserVo> getRankByInviteYesterday(int count){
		return getDayRankByInvite(count, DateUtil.getYesterdayStr());
	}
	
	public int getUserTodayEffectiveInvite(int user){
		return getUserDayEffectiveInvite(user, DateUtil.getTodayStr());
	}
	public int getUserDayEffectiveInvite(int user,String day){
		String todayRankKey = RedisUtil.buildInviterTodayRankKey(day);
		int inviteNum = 0;
		try {
			inviteNum= (int)userFriendRedisCache.zscore(todayRankKey, String.valueOf(user));
		} catch (Exception e) {
			logger.error("从redis中获取用户今日收徒出错,cause :",e);
			inviteNum = effectiveInviteMapper.getUserEffectiveInviteNum(user, day);
		}
		return inviteNum;
	}
	public int getUserTodayInviteRank(int user, int inviteNum){
		String today = DateUtil.getTodayStr();
		return getUserInviteRank(user, inviteNum, today);
	}
	
	public int getUserInviteRank(int user,int inviteNum, String day){
		String todayRankKey = RedisUtil.buildInviterTodayRankKey(day);
		int rank = 0;
		try {
			rank = (int) userFriendRedisCache.zrevrank(todayRankKey, String.valueOf(user));
		} catch (Exception e) {
			logger.error("从redis中获取用户今日收徒排名,cause :",e);
			rank = effectiveInviteMapper.getUserEffectiveInviteRank(inviteNum, day);
		}
		return rank;
	}
	public List<InviteUserVo> getDayRankByInvite(int count,String day){
		String yesterdayRankKey = RedisUtil.buildInviterTodayRankKey(day);
		List<InviteUserVo> rtv = inviteRankCache.get(day);
		Set<Tuple> yesterdayInviteTupleSet = null;
		try {
			if(rtv==null){
				synchronized (this) {
					if(rtv==null){
						yesterdayInviteTupleSet = userFriendRedisCache.zrevrangeByScoreWithScores(yesterdayRankKey, 100000, 10, 0, count);
						//邀请人数最小是10人 
						if(yesterdayInviteTupleSet != null && !yesterdayInviteTupleSet.isEmpty()){
							rtv = new ArrayList<>();
							Iterator<Tuple> it = yesterdayInviteTupleSet.iterator();
							Tuple tuple = null;
							while(it.hasNext()){
								tuple = it.next();
								InviteUserVo vo = new InviteUserVo();
								vo.setInviteNum((int)tuple.getScore());
								vo.setUser(userService.getUserById(Integer.valueOf(tuple.getElement())));
								vo.setRank(getUserInviteRank(vo.getUser().getId(), vo.getInviteNum(), day));
								rtv.add(vo);
							}
							//反正数据不变动 就多放一会15min
							inviteRankCache.set(day, rtv);
						}
					}
				}
			}
		} catch (Exception e) {
			//怎么办 这个先不管 
			logger.error("从redis中获取邀请排行榜出错，cause ",e);
		}
		return rtv;
	}
	public static List<Integer> rankByInviterNum = Arrays.asList(
			150,100,80,60,40,
			30,30,30,30,30,
			15,15,15,15,15,15,15,15,15,15,
			10,10,10,10,10,10,10,10,10,10);
	
	public boolean isShowInviteRankListData(String day ){
		String key = RedisUtil.buildIsCompeleteRankListData(day);
		try {
			return userFriendRedisCache.exists(key);
		} catch (Exception e) {
		}
		return false;
	}
	private volatile boolean isInit = false;
	public  void addCheatInviteData(){
		if(isInit){
			logger.info("加载邀请排名已经开始过了");
		}
		logger.info("加载邀请排名开始");
		try{
			isInit = true;
			init();
			Collections.shuffle(defaultInviteUserList);
			int rankSize = 30;
			InviteUserVo[] rankTmp = new InviteUserVo[rankSize];
			String day = DateUtil.getYesterdayStr();
			fileRankArrayData(rankTmp,rankSize,day);
			fillRedisRankData(rankTmp, day);
			//添加执行完成
			String key = RedisUtil.buildIsCompeleteRankListData(day);
			userFriendRedisCache.set(key , "1");
		}catch(Exception e){
			logger.info("加载邀请排名异常,cause :",e);
		}finally{
			isInit = false;
			logger.info("加载邀请排名结束");
		}
	}
	
	private void fileRankArrayData(InviteUserVo[] rankTmp,int rankSize,String day){
		List<InviteUserVo> lists = getDayRankByInvite(rankSize, day);
		int currentRank = 0;
		
		if(lists != null && !lists.isEmpty()){
			//填充显示的排名数组
			for(int i = 0 ; i < lists.size(); i++){
				if(currentRank > rankTmp.length){
					break;
				}
				InviteUserVo vo = lists.get(i);
				int inviteNum = vo.getInviteNum();
				for(; currentRank < rankTmp.length; currentRank++){
					int rankInviteNum = rankByInviterNum.get(currentRank);
					if(inviteNum >= rankInviteNum){
						rankTmp[currentRank] = vo;
						break;
					}
				}
			}
		}
	}
	private void fillRedisRankData(InviteUserVo[] rankTmp,String day){
		//将索引位置上没有的数据插入redis中
		InviteUserVo currentVo = null;
		List<Integer> rankUserList = new ArrayList<>(30);
		for(InviteUserVo vo:rankTmp){
			if(vo != null){
				rankUserList.add(vo.getUser().getId());
			}
		}
		ThreadLocalRandom random = ThreadLocalRandom.current();
		for(int index = 0 ; index< rankTmp.length; index++){
			if(rankTmp[index]==null){
				int randomUserId = getRandomUserId(rankUserList, index);
				rankUserList.add(randomUserId);
				logger.info("加载到用户邀请排名,:{}",currentVo);
				addEffectiveInvite(randomUserId, defaultInviteUserList.get(random.nextInt(defaultInviteUserList.size())), rankByInviterNum.get(index)+random.nextInt(5), day);
				
			}
		}
	}
	
	private int getRandomUserId(List<Integer> rankUserList,int index){
		if(index > defaultInviteUserList.size()){
			index = 0;
		}
		int randomUserId = defaultInviteUserList.get(index);
		if(rankUserList.contains(randomUserId)){
			return getRandomUserId(rankUserList,++index);
		}
		return randomUserId;
	}
	
	private static List<Integer> defaultInviteUserList = new ArrayList<>(Arrays.asList(1,3,13,29,30,31,92,110,131,142,143,
			161,1756703,245924,1908877,1549378,374187,1849063,1344067,1193186));
	
	volatile boolean isAddUser = false;

	private void init(){
		if(isAddUser == true){
			return ;
		}
		isAddUser = true;
		try {
			for(int i = 38; i < 93;i++){
				if(i == 46||i== 56){
					continue;
				}
				try{
					if(userService.getUserById(i) != null){
						defaultInviteUserList.add(i);
					}
					}catch(Exception e){
						logger.warn("加载rank invite user ,cause by:",e);
					}
			}
		} catch (Exception e) {
			
		}finally{
			isAddUser = false;
		}
	}
	/***
	 * 财神活动打款入口
	 * @param userid
	 * @param rank
	 * @param amount 
	 * @param rewardType 金币 OR 元
	 * 
	 */
	public boolean addUserEffectiveInviteFriendIncome(int userid,int rank,int amount,String rewardType){
		int realAmount = amount;
		if("元".equals(rewardType)){
			realAmount = amount /100;
		}
		String remark = "恭喜您,在参加财神榜会活动中获得第"+rank+"名,系统奖励您"+realAmount+rewardType;
		if("金币".equals(rewardType)){
			userIntegalIncomeService.updateIntegalIncome(userid, Constants.INTEGAL_SOURCE_WANPU, amount,3 );
			User user = userService.getUserById(userid);
			UserThridPartIntegral integral = new UserThridPartIntegral();
			integral.setKey(String.valueOf(user.getUser_identity()));
			integral.setAd_name("财神榜第"+rank+"名");
			integral.setPoints(amount);
			integral.setClient_type(3);
			integral.setSource(Constants.INTEGAL_SOURCE_CAISHENBANG);
			integral.setUser_id(userid);
			integral.setOrder_id(String.valueOf(System.currentTimeMillis())+String.valueOf((int)(Math.random()*100)));
			userThridPartIntegralService.add(integral);
		}else{
			userIncomeService.addUserInviteFriendIncome(userid, amount, rank, "财神榜第"+rank+"名" );
		}
		userMessageService.addEffectiveInviteRewardMessage(userid,rank, remark);
		logger.info("addRewardIntegral:userid:{},amount:{},reason:{}",userid, amount, remark);
		
		try {
			//不与具体日期挂扣 前台也没有
			userFriendRedisCache.set(RedisUtil.buildUserEffectiveInviteReawrdKey(userid), String.valueOf(rank), Constants.DAY_SECONDS);
		} catch (Exception e) {
			
		}
		return true;
	}
	
	public boolean removeUserEffectiveInviteCacheKey (int user){
		try {
			userFriendRedisCache.remove(RedisUtil.buildUserEffectiveInviteReawrdKey(user));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean userHasEffectiveReward(int user){
		try {
			return userFriendRedisCache.exists(RedisUtil.buildUserEffectiveInviteReawrdKey(user));
		} catch (Exception e) {
			return false;
		}
	}
}
