package com.cyhd.service.impl;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.codehaus.groovy.classgen.genMathModification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.LiveAccess;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.UserDrawLogMapper;
import com.cyhd.service.dao.db.mapper.UserDrawMapper;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserDraw;
import com.cyhd.service.dao.po.UserDrawLog;
import com.cyhd.service.dao.po.UserDrawLog.UserDrawLogType;
import com.cyhd.service.impl.doubao.UserDuobaoCoinService;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RedisUtil;
import com.cyhd.service.vo.UserDrawLogVo;
import com.cyhd.web.common.ClientInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Transactional
@Service 
public class UserDrawService extends BaseService {

	@Resource(name=RedisUtil.NAME_SELF)
	private IJedisDao userDrawCache;
	
	@Resource
	private UserDrawMapper userDrawMapper;
	
	private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	@Resource
	private UserService userService;
	
	@Resource
	private UserFriendService userFriendService;
	
	@Resource
	private UserDrawLogMapper userDrawLogMapper;
	
	@Resource
	private UserIncomeService userIncomeService;
	
	@Resource
	private UserMessageService userMessageService;

	@Resource
	private UserIntegalIncomeService integalIncomeService;
	
	@Resource
	private UserDuobaoCoinService userDuobaoCoinService;
	@Resource
	private HongbaoActivityService hongbaoActivityService;
	
	//好友的红包数量
	private final static int FRIEND_HONGBAO_NUM = 5;
	
	private volatile boolean loading = false;
	private LiveAccess<UserDrawLogVo> todayMaxDrawLogCache = new LiveAccess<UserDrawLogVo>(1000, null);
	
	//用来作假数据的用户id
	public int[] fakeUsers = { 3, 29, 30, 31, 35, 39, 55, 57, 95, 97, 99, 167 };

//	@PostConstruct
//	private void loadTodyMaxUserDrawLog(){
//		/**目前只存放一个**/
//		if(loading || todayMaxDrawLogCache.isEmpty() == false){
//			return ;
//		}
//		loading = true;
//		try{
//			UserDrawLog userDrawLog = this.userDrawLogMapper.getTodyMaxDrawLog(GlobalConfig.ACTIVITY_ID, UserDrawLog.UserDrawLogType.DECREMENT.getType(), GenerateDateUtil.getCurrentDate());
//			if(userDrawLog != null){
//				UserDrawLogVo vo = new UserDrawLogVo();
//				vo.setUserDrawLog(userDrawLog);
//				vo.setUser(userService.getUserById(userDrawLog.getUser_id()));
//				todayMaxDrawLogCache.add(vo);
//			}
//		}catch(Exception e){
//			
//		}finally{
//			loading = false;
//		}
//	}

	/**
	 * 根据好友的id为师傅增加摇一摇的次数，传入的是徒弟的id
	 * @param friend_id
	 * @param reason
	 * @return
	 */
	public  boolean addUserDrawByFriend(int friend_id, String reason){
		User user = userService.getUserById(friend_id);
		logger.info("活动开始时间:{},结束时间:{},friend_id:{}",friend_id,GlobalConfig.ACTIVITY_START,GlobalConfig.ACTIVITY_END);
		//添加抽奖次数
		if(DateUtil.insideTwoTime(user.getCreatetime(),GlobalConfig.ACTIVITY_START, GlobalConfig.ACTIVITY_END) == false){
			logger.warn("邀请的用户不是在这个期间!userId：{},reason:{}",friend_id,reason);
			return false;
		}
			
		if(DateUtil.insideTwoTime(GenerateDateUtil.getCurrentDate(),GlobalConfig.ACTIVITY_START, GlobalConfig.ACTIVITY_END) == false){
			logger.warn("现在活动已经结束!userId：{},reason:{}",friend_id,reason);
			return false;
		}
		
		int invitorId = userFriendService.getInvitor(user.getId());
		if(invitorId > 0){
			logger.info("增加抽奖机会中,userId:{},存在师傅:{}",friend_id,invitorId);
			User invitor =  userService.getUserById(invitorId);
			//安卓用户 不参加抽奖
			if(invitor.isIos() == false){
				logger.info("师傅是Android,不给抽奖机会，邀请人ID:{},徒弟id:{}",invitorId,friend_id);
				return  false;
			}
			try{
				return  this.addUserDraw(invitor.getId(), friend_id, GlobalConfig.ACTIVITY_ID, reason);
			}catch(Exception e){
				logger.error("friend_id:{},invitor:{},插入抽奖次数出错:{}",friend_id,invitorId,e);
			}
		}else{
			logger.info("增加抽奖机会中,userId:{},不存在师傅。",friend_id);
		}
		return false;
	}
	
	private  boolean addUserDraw(int user_id,int friend_id,int activity_id,String reason){
		//先插log user_id friend_id,activity_id 是唯一约束
		UserDrawLog log = new UserDrawLog(user_id, friend_id, reason, activity_id, 0, UserDrawLogType.INCREMENT);
		log.setDraw_times(FRIEND_HONGBAO_NUM);
		boolean flag = this.userDrawLogMapper.addUserDrawLog(log) > 0;
		if(flag){
			//每个徒弟增加5次摇奖机会
			flag = incrDrawBalance(user_id, activity_id, FRIEND_HONGBAO_NUM);
			if(flag){
				userMessageService.notifyActivityMessage(user_id, log, Constants.platform_ios);
				//remove cache
				this.removeCache(user_id, activity_id);
			}
		}
		return flag;
	}
	
	public UserDraw getUserDraw(int user_id,int activity_id){
		UserDraw userDraw = null;
		String key = RedisUtil.buildActiveUserKey(user_id, activity_id);
		String data = null;
		try {
			data = this.userDrawCache.get(key);
			if(StringUtil.isNotBlank(data)){
				userDraw =  gson.fromJson(data, UserDraw.class);
				if(userDraw != null){
					if(hongbaoActivityService.getUserHongbaoNum(user_id) == userDraw.getBalance_times()){
						return userDraw;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		userDraw = this.getUserDrawFromDB(user_id, activity_id);
		try {
			//存放五分钟 gson 似乎很傻 如果对象是null 它存入的就是 “"null"”
			if(userDraw !=null){
				this.userDrawCache.set(key, gson.toJson(userDraw), 300);
			}
		} catch (Exception e) {
			logger.error("用户抽奖对象存入redis出现异常,cause by:{}",e.getMessage());
		}
		return userDraw;
	}


	/***
	 * 用户进行现金抽奖
	 * @param user_id
	 * @param activity_id
	 * @param reason
	 * @param drawAmount 抽奖获得的奖励
	 * @return
	 */
	public boolean decreUserDrawTimes(int user_id,int activity_id,String reason,int drawAmount,int clientType){
		boolean flag = decrDrawBalance(user_id, activity_id);
		if(flag && drawAmount > 0){
			//添加钱包
			flag = userIncomeService.addDrawIncome(user_id, drawAmount, reason);
			if(flag){
			//Add log
				UserDrawLog log = new UserDrawLog(user_id, 0, reason, activity_id,drawAmount, UserDrawLogType.DECREMENT);
				addDrawLog(log);

				userMessageService.notifyActivityMessage(user_id, log, clientType);
			}
		}
		return flag;
	}


	/**
	 * 增加抽奖次数
	 * @param userId
	 * @param activityId
	 * @param balance
	 * @return
	 */
	public boolean incrDrawBalance(int userId, int activityId, int balance){
		boolean flag =  userDrawMapper.addOrUpdateUserDrawTimes(userId, activityId, balance) > 0;
		if(flag){
			this.removeCache(userId, activityId);
		}
		return flag;
	}

	/**
	 * 增加抽奖次数
	 * @param userId
	 * @param activityId
	 * @param balance
	 * @return
	 */
	public boolean updateDrawBalance(int userId, int activityId, int balance){
		boolean flag =  userDrawMapper.updateDrawBalance(userId, activityId, balance) > 0;
		if(flag){
			this.removeCache(userId, activityId);
		}
		return flag;
	}


	/**
	 * 减少抽奖次数
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public boolean decrDrawBalance(int userId, int activityId){
		boolean flag =  userDrawMapper.decreBalance(userId, activityId) > 0;
		if(flag){
			removeCache(userId, activityId);
		}
		return flag;
	}
	
	private void removeCache(int userId, int activityId){
		try {
			this.userDrawCache.remove(RedisUtil.buildActiveUserKey(userId, activityId));
		} catch (Exception e) {}
	}

	/**
	 * 从数据库中读取
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public UserDraw getUserDrawFromDB(int userId, int activityId){
		return userDrawMapper.getUserDraw(userId, activityId);
	}


	/**
	 * 增加抽奖日志
	 * @param log
	 * @return
	 */
	public boolean addDrawLog(UserDrawLog log){
		boolean flag = userDrawLogMapper.addUserDrawLog(log) > 0;
		if(flag && log.getType() == UserDrawLogType.DECREMENT.getType()){
			String key = RedisUtil.builderActivityKey(log.getActivity_id());
			try {
				this.userDrawCache.addToList(key, gson.toJson(log));
				userDrawCache.keepLen(key, 50);

			} catch (Exception e) {
				logger.error("addDrawLog exception" + e.getMessage(), e);
			}
		}
		return flag;
	}

	
	/**
	 * 获得用户抽奖的奖励log 
	 * @param activity
	 * @param start
	 * @param size
	 * @return
	 */
	public List<UserDrawLog> getRewardedLog(int activity, int start, int size){
		String key = RedisUtil.builderActivityKey(activity);
		List<UserDrawLog> userDrawlods = Collections.EMPTY_LIST;
		try {
			List<String> strLogs = this.userDrawCache.getList(key, start, size);
			if(strLogs != null && strLogs.isEmpty() == false){
				userDrawlods = strLogs.stream().map(log -> hadleUserDrawLog(log)).collect(Collectors.toList());
			}
			return userDrawlods;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		userDrawlods = this.userDrawLogMapper.getUserDrawLogsToRoll(activity,UserDrawLogType.DECREMENT.getType(),start,size);
		try {
			if(userDrawlods != null && userDrawlods.isEmpty() == false){
				String[] arrays = userDrawlods.stream().map((UserDrawLog userDrawLog) -> gson.toJson(userDrawLog)).toArray(String[]::new);
				this.userDrawCache.addToList(key, arrays);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return userDrawlods;
	}

	
	public UserDrawLog hadleUserDrawLog(String logStr){
		return gson.fromJson(logStr, UserDrawLog.class);
	}


	public List<UserDrawLogVo> getUserDrawLogVos(List<UserDrawLog> logs){
		List<UserDrawLogVo> logVos = new LinkedList<>();
		if(logs != null && logs.isEmpty() == false){
			UserDrawLogVo logVo = null;
			for(UserDrawLog log:logs){
				logVo = new UserDrawLogVo();
				logVo.setUser(userService.getUserById(log.getUser_id()));
				logVo.setUserDrawLog(log);
				logVos.add(logVo);
			}
		}
		return logVos;
	}
	
//	public int getRandomDrawAmount(int user_id,int seed){
//		return getRandomDrawAmount(userService.getUserById(user_id), seed);
//	}
	
//	/***
//	 * 现在产生的随机数跟用户无关 <br/>
//	 * 10~100
//	 * @param user
//	 * @param seed
//	 * @return
//	 */
//	public int getRandomDrawAmount(User user,int seed){
//		assert(user != null);
//		int userId = user.getId();
//		int ran = 0;
//		//大黄 老板娘
//		if(userId == 1 || userId == 92){
//			ran = 500 + ThreadLocalRandom.current().nextInt(500);
//		}else{
//			ran = ThreadLocalRandom.current().nextInt(40);
//
//			//如果大于15，则再随机一次
//			if (ran > 15){
//				ran = ThreadLocalRandom.current().nextInt(40);
//			}
//
//			//如果大于25，则再次随机
//			if (ran > 25){
//				ran = ThreadLocalRandom.current().nextInt(40);
//			}
//			ran = 10 + ran;
//		}
//		logger.info("抽奖中,user:{},抽中的金额:{} 分",user,ran);
//		return ran;
//	}



	public void addFakeDrawLog(){

		List<Integer> userList = new ArrayList<>(Constants.defaultUserIds);
		for (int u : fakeUsers){
			userList.add(u);
		}

		//获取一个随机的用户
		int user_id = userList.get(ThreadLocalRandom.current().nextInt(userList.size()));

		//获取随机的金额
		int drawAmount = 50 + ThreadLocalRandom.current().nextInt(500);

		UserDrawLog log = new UserDrawLog(user_id, 0, "双旦活动拆红包", GlobalConfig.ACTIVITY_ID, drawAmount, UserDrawLogType.DECREMENT);
		String key = RedisUtil.builderActivityKey(GlobalConfig.ACTIVITY_ID);
		try {
			this.userDrawCache.addToList(key, gson.toJson(log));
		} catch (Exception e) {
			logger.error("在减少抽奖次数中,操作redis,cause by:{}",e.getMessage());
		}
	}

	/**
	 * 获取用户的抽奖次数
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public int countUserDrawTimes(int userId, int activityId, Date startTime){
		return userDrawLogMapper.countUserDrawLog(userId, activityId, UserDrawLogType.DECREMENT.getType(), startTime);
	}
	
	@Transactional
	public DrawNum getRandomAmount(User user,ClientInfo clientInfo){
		String orderId = user.getId()+"_"+(System.currentTimeMillis()/1000);
		DrawNum drawNum = getRandomDrawNum(user, clientInfo);
		logger.info("user:{},draw:{}",user,drawNum);
		boolean flag = decrDrawBalance(user.getId(), GlobalConfig.ACTIVITY_ID);
		//减少次数
		//插入收入
		//插入日志
		//发送消息
		String reason =String.valueOf(drawNum.getCount())+drawNum.getType().getName() ; 
		logger.info("descrease user:{}, draw num,flag:{}",user.getId(),flag);
		if(flag){
			hongbaoActivityService.userHongbaoNumCache.remove(String.valueOf(user.getId()));
			if(drawNum.getType() == DrawType.JingBi){
				flag = integalIncomeService.addRewardedIntegral(user.getId(), drawNum.getCount(),"摇一摇赢iPhone", clientInfo.getPlatform(), orderId);
			}else if(drawNum.getType() == DrawType.DuoBaoBi){
				flag = userDuobaoCoinService.addDuobaoCoinByDraw(user.getId(), drawNum.getCount(), "摇一摇赢iPhone" );
			}
			logger.info("income user:{}, draw:{},flag:{}",user.getId(),drawNum,flag);
			if(flag){
				UserDrawLog log = new UserDrawLog(user.getId(), 0, reason, GlobalConfig.ACTIVITY_ID, drawNum.getCount(), UserDrawLogType.DECREMENT);
				 //增加抽奖记录 上面的已经发送信息的啦
				if(this.addDrawLog(log) && drawNum.getType() == DrawType.DuoBaoBi){
					//发系统消息
					userMessageService.notifyActivityMessage(user.getId(), log, clientInfo.getPlatform());
					logger.info("add hongbao income for user:{}, type:{}, amount:{}", user.getId(), drawNum.getType().getName() ,drawNum.getCount());
				    
				}
			}
		}
		return drawNum;
	}
	
	public boolean addMaxDrawLog(UserDrawLog log,boolean max){
		boolean flag = addDrawLog(log);
		return flag;
	}
	
	private DrawNum getRandomDrawNum(User user,ClientInfo clientInfo){
		
		//80% 出金币,20% 出夺宝比
		if(clientInfo.isIos() == false){
			return DrawNum.defult;
		}
		//0.4*0.4=0.16
		DrawNum draw = getRandomDrawNum();
		return draw;
	}
	/**抽奖**/
	private DrawNum getRandomDrawNum(){
		DrawNum draw = new DrawNum();
		int random = ThreadLocalRandom.current().nextInt(100);

		int count = 0;
		if(random > 3){ //单次摇奖，96%概率出金币
			 draw.setType(DrawType.JingBi);
			 int rnd = ThreadLocalRandom.current().nextInt(100);
			 if (rnd > 95){
				 count = 100 + ThreadLocalRandom.current().nextInt(201);
			 }else if (rnd > 80){
				 count = 30 + ThreadLocalRandom.current().nextInt(71);
			 }else if (rnd > 60){
				 count = 20 + ThreadLocalRandom.current().nextInt(61);
			 }else {
				 count = 10 + ThreadLocalRandom.current().nextInt(41);
			 }
			 draw.setCount(count);
		}else{ //单次摇奖，4%的概率出夺宝币
			draw.setType(DrawType.DuoBaoBi);
			int rnd = ThreadLocalRandom.current().nextInt(100);
			if (rnd > 95){
				count = 3;
			}else if (rnd > 80){
				count = 2;
			}else {
				count = 1;
			}
			draw.setCount(count);
		}
		return draw;
	}
	
	/**获得今日最大奖**/
	public UserDrawLogVo getTodyMaxDrawLog(){
		UserDrawLogVo vo = todayMaxDrawLogCache.getElement();
		if(vo != null){
			return vo;
		}else{
			try{
				UserDrawLog userDrawLog = this.userDrawLogMapper.getTodyMaxDrawLog(GlobalConfig.ACTIVITY_ID, UserDrawLog.UserDrawLogType.DECREMENT.getType(), GenerateDateUtil.getCurrentDate());
				if(userDrawLog != null){
					vo = new UserDrawLogVo();
					vo.setUserDrawLog(userDrawLog);
					vo.setUser(userService.getUserById(userDrawLog.getUser_id()));
					//设置过期时间为到晚上23:59:59
					int ttl = DateUtil.getSecondToMidnight() * 1000;
					todayMaxDrawLogCache = new LiveAccess<UserDrawLogVo>(ttl, vo);
					return vo;
				}
			}catch(Exception e){}
		}
		
		return null;
	}
	
	public static class DrawNum{
		private DrawType type;
		private int count;

		public DrawNum() {

		}

		public DrawNum(DrawType type, int count) {
			this.type = type;
			this.count = count;
		}
		public DrawType getType() {
			return type;
		}
		public void setType(DrawType type) {
			this.type = type;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public static DrawNum defult = new DrawNum(DrawType.JingBi, 0);
		
		@Override
		public String toString() {
			return "type=" + type.name + ", count=" + count;
		}
		
		
	}
	
	public static enum DrawType{
		JingBi("金币"),XianJin("现金"),DuoBaoBi("夺宝币"),Other("其他");
		private String name;
		private DrawType(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setOtherName(String name){
			DrawType.Other.name = name;
		}
	}
}
