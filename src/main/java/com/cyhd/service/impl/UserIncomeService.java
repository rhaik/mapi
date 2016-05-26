package com.cyhd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.LiveAccess;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.ECacheDao;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.UserIncomeLogMapper;
import com.cyhd.service.dao.db.mapper.UserIncomeMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.dao.po.UserIncomeLog;
import com.cyhd.service.util.RedisUtil;
import com.cyhd.service.vo.UserIncomeLogVo;
import com.cyhd.service.vo.UserIncomeStatsVo;

@Service
public class UserIncomeService extends BaseService {

	@Resource
	private UserIncomeMapper userIncomeMapper;
	
	@Resource
	private UserIncomeLogMapper userIncomeLogMapper;
	
	@Resource
	private UserService userService;
	
	private LiveAccess<Long> total_incomes = new LiveAccess<Long>(3 * Constants.minutes_millis, null);
	
	private float rate = 2.25f;
	
	private LiveAccess<List<UserIncomeLogVo>> commonIncomes = new LiveAccess<List<UserIncomeLogVo>>(1 * Constants.minutes_millis, null); 
	
	private ECacheDao<UserIncomeStatsVo> stats = new CacheLRULiveAccessDaoImpl<UserIncomeStatsVo>(5 * Constants.minutes_millis, 1024);
	
	@Resource(name=RedisUtil.NAME_ALIYUAN)
	private IJedisDao userincomeRedisCache;
	
	//ECacheDao
	public long getTotalIncomes(){
		Long total = total_incomes.getElement();
		if(total == null){
			total = userIncomeMapper.getTotalIncomes();
			if(total == null){
				total = 0L;
			}
			total_incomes = new LiveAccess<Long>(5 * Constants.minutes_millis, total);
		}
		return total == null ? 0 : (long)(total * rate);
	}
	
	public long getMaskedTotalIncomes(){
		long income_total = this.getTotalIncomes();
		//大于10万的时候是四倍
		if(income_total >= 20000000){
			income_total = income_total * 3;
		}
		//最小是十万
		while(income_total < 20000000){
			income_total <<= 1;
		}
		return income_total;
	}
	
	
	public UserIncomeStatsVo getUserIncomeStats(int userId){
		String key = String.valueOf(userId);
		UserIncomeStatsVo vo = stats.get(key);
		if(vo == null){
			vo = new UserIncomeStatsVo();
			//本月
			Date currentMonth = DateUtil.getCurrentMonthFristDay();
			int currentMonthAmount = this.getUserAmountByUserIdAndDate(userId, currentMonth, new Date());
			vo.setCurrentMonthAmount(currentMonthAmount);
			//上月
			Date fristDay = DateUtil.getLastMonthFristDay();
			Date lastDay = DateUtil.getLastMonthLastDay();
			int lastMonthAmount = this.getUserAmountByUserIdAndDate(userId, fristDay, lastDay);
			vo.setLastMonthAmount(lastMonthAmount);
			
			//余额
			UserIncome income = this.getUserIncome(userId);
			vo.setBalance(income.getBalance());
			
			Date currentTime = new Date();
			Date today = DateUtil.getTodayStartDate();
			//昨日
			Date yestoday = DateUtil.getDateBefore(new Date(), 1);
			int yestodyAppAmount = this.getUserAppAmountByUserIdAndDate(userId, yestoday, today);
			vo.setYestodyAppAmount(yestodyAppAmount);
			
			int yestodyFriendAmount = this.getUserFriendAmountByUserIdAndDate(userId, yestoday, today);
			vo.setYestodyFriendAmount(yestodyFriendAmount);
			
			//7天
			Date sevenDay = DateUtil.getDateBefore(new Date(), 7);
			int sevenDayAppAmount = this.getUserAppAmountByUserIdAndDate(userId, sevenDay, currentTime);
			vo.setSevenDayAppAmount(sevenDayAppAmount);
			
			int sevenDayFriendAmount = this.getUserFriendAmountByUserIdAndDate(userId, sevenDay, currentTime);
			vo.setSevenDayFriendAmount(sevenDayFriendAmount);
			//30天
			Date thirtyDay = DateUtil.getDateBefore(new Date(), 30);
			int thirtyDayAppAmount = this.getUserAppAmountByUserIdAndDate(userId, thirtyDay, currentTime);
			vo.setThirtyDayAppAmount(thirtyDayAppAmount);
			
			int thirtyDayFriendAmount = this.getUserFriendAmountByUserIdAndDate(userId, thirtyDay, currentTime);
			vo.setThirtyDayFriendAmount(thirtyDayFriendAmount);
			stats.set(key, vo);
		}
		return vo;
	}
	
	// type: (1:完成试用 2:好友分成,3:邀请好友,4、其他)
	public boolean addAppTaskIncome(int userId, long taskId, int sum, String appName){
		int i = userIncomeMapper.addAppTaskIncome(userId, sum);
		boolean b = i > 0;
		if(b){
			addUserIncomeLog(userId, 0, taskId, sum, UserIncome.INCOME_TYPE_TASK, appName, 0);
		}
		return b;
	}
	// level=1 :徒弟贡献，level=2：徒孙贡献
	public boolean addFriendShareIncome(int userId, int friendId, long friendTaskId, int sum, String appName, int level){
		int i = 0;
		if(level == 1)
			i = userIncomeMapper.addFriendShareIncomeLevel1(userId, sum);
		else if(level == 2)
			i = userIncomeMapper.addFriendShareIncomeLevel2(userId, sum);
		boolean b = i > 0;
		if(b){
			addUserIncomeLog(userId, friendId, friendTaskId, sum, UserIncome.INCOME_TYPE_SHARE, appName, level);
		}
		return b;
	}
	//新手任务：邀请好友任务完成
	public boolean addInviteTaskIncome(int userId, long taskId, int sum){
		int i = userIncomeMapper.addOtherIncome(userId, sum);
		boolean b = i > 0;
		if(b){
			addUserIncomeLog(userId, 0, taskId, sum, UserIncome.INCOME_TYPE_BEGINNER, "新手任务-邀请第一个好友", 0);
		}
		return b;
	}
	
	//新手任务：第一个试用完成
	public boolean addFirstTryTaskIncome(int userId, long taskId, int sum){
		int i = userIncomeMapper.addOtherIncome(userId, sum);
		boolean b = i > 0;
		if(b){
			addUserIncomeLog(userId, 0, taskId, sum, UserIncome.INCOME_TYPE_BEGINNER, "新手任务-第一次试用", 0);
		}
		return b;
	}

	//新手任务：分享给朋友
	public boolean addShareToFriendsIncome(int userId, long taskId, int sum){
		int i = userIncomeMapper.addOtherIncome(userId, sum);
		boolean b = i > 0;
		if(b){
			addUserIncomeLog(userId, 0, taskId, sum, UserIncome.INCOME_TYPE_BEGINNER, "新手任务-分享给好友", 0);
		}
		return b;
	}
	//新手任务：第一个试用完成
	public boolean addFirstTranArticleTaskIncome(int userId, long taskId, int sum){
		int i = userIncomeMapper.addOtherIncome(userId, sum);
		boolean b = i > 0;
		if(b){
			addUserIncomeLog(userId, 0, taskId, sum, UserIncome.INCOME_TYPE_BEGINNER, "新手任务-转发任务", 0);
		}
		return b;
	}

	//新手任务：完成新手任务的奖励，通过taskName传入任务名称，描述为："新手任务-" + taskName
	public boolean addBeginnerTaskIncome(int userId, long taskId, int amount, String taskName) {
		int i = userIncomeMapper.addOtherIncome(userId, amount);
		boolean b = i > 0;
		if(b){
			addUserIncomeLog(userId, 0, taskId, amount, UserIncome.INCOME_TYPE_BEGINNER, "新手任务-" + taskName, 0);
		}
		return b;
	}
		
	private boolean addUserIncomeLog(int userId, int friendId, long taskId, int sum,int action, String remarks, int friend_level){
		UserIncomeLog log = new UserIncomeLog();
		log.setAction(action);
		log.setUser_id(userId);
		log.setUser_task_id(taskId);
		log.setOperator_time(new Date());
		log.setAmount(sum);
		log.setFrom_user(friendId);
		log.setFriend_level(friend_level);
		int type = 1;
		if (action == UserIncome.INCOME_TYPE_ENCASH || action == UserIncome.INCOME_TYPE_DOUBAOCOIN){
			type = 0;
		}
		log.setType(type);
		log.setRemarks(remarks);
		return userIncomeLogMapper.add(log) > 0;
	}
	
	public boolean createNewUserIncome(int userId){
		UserIncome ui = new UserIncome();
		ui.setUser_id(userId);
		return userIncomeMapper.add(ui) >= 0;
	}
	
	public UserIncome getUserIncome(int userId){
		return userIncomeMapper.getUserIncome(userId);
	}

	/**
	 * 更新提现中数据，将余额全部提现
	 * @param userId
	 * @return
	 */
	public boolean addUserEncashing(int userId){
		return userIncomeMapper.addUserEncashing(userId) > 0;
	}

	/**
	 * 更新提现中的金额，指定提现的金额
	 * @param userId
	 * @return
	 */
	public boolean addUserEncashingAmount(int userId, int amount){
		return userIncomeMapper.addUserEncashingAmount(userId, amount) > 0;
	}
	
	/**
	 * 根据日期获取总金额
	 * @param userid
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getUserAmountByUserIdAndDate(int userid, Date startTime, Date endTime) {
		Integer i = userIncomeLogMapper.getUserAmountByUserIdAndDate(userid, startTime, endTime);
		return i == null ? 0:i;
	}
	/**
	 * 用户应用试用金额
	 * 
	 * @param userid
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getUserAppAmountByUserIdAndDate(int userid, Date startTime, Date endTime) {
		Integer i =  userIncomeLogMapper.getUserAppAmountByUserIdAndDate(userid, startTime, endTime);
		return i == null ? 0:i;
	}
	/**
	 * 用户好友分成金额
	 * @param userid
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getUserFriendAmountByUserIdAndDate(int userid, Date startTime, Date endTime) {
		Integer i = userIncomeLogMapper.getUserFriendAmountByUserIdAndDate(userid, startTime, endTime);
		return i == null ? 0:i;
	}
	/**
	 * 获取好友收入记录
	 * @param num
	 * @return
	 */
	public synchronized List<UserIncomeLogVo> getUserIncomeLogs(int num) {
		List<UserIncomeLogVo> l = commonIncomes.getElement();
		if(l == null){
			List<UserIncomeLog> logs  =  userIncomeLogMapper.getUserIncomeLogs(num);
			
			l = new ArrayList<UserIncomeLogVo>();
			for(UserIncomeLog log : logs) {
				if (log.getAction() == UserIncome.INCOME_TYPE_DOUBAOCOIN){
					continue;
				}
				UserIncomeLogVo vo = new UserIncomeLogVo();
				vo.setUser(userService.getUserById(log.getUser_id()));
				if(log.getFrom_user() > 0)
					vo.setFromUser(userService.getUserById(log.getFrom_user()));
				vo.setUserIncomeLog(log);
				l.add(vo);
			}
			commonIncomes = new LiveAccess<List<UserIncomeLogVo>>(2 * Constants.minutes_millis, l);
		}
		return l;
	}
	/**
	 * 获取好友收入列表
	 * @param userId	用户ID
	 * @param friend_level	级别 1:徒弟2:徒孙
	 * @return
	 */
	public List<UserIncomeLogVo> getUserFriendInviteIncome(int userId,int friend_level, int start, int size) {
		List<UserIncomeLog> logs  =  userIncomeLogMapper.getUserFriendInviteIncome(userId, friend_level,start,size);
		
		List<UserIncomeLogVo> l = new ArrayList<UserIncomeLogVo>();
		String dateKey = new String();
		for(UserIncomeLog log : logs) {
			UserIncomeLogVo vo = new UserIncomeLogVo();
			
			String temp = DateUtil.format(log.getOperator_time(), "yyyyMMdd");
			vo.setDisplayDate(false);
			if(dateKey.isEmpty()) {
				dateKey = temp;
				vo.setDisplayDate(true);
			} else if(!dateKey.equals(temp)){
				if(l.size() > 0) {
					dateKey = temp;
					vo.setDisplayDate(true);
				}
			}
			if(log.getFrom_user() > 0)
				vo.setFromUser(userService.getUserById(log.getFrom_user()));
			vo.setUserIncomeLog(log);
			l.add(vo);
		}
		return l;
	}
	public int countUserFriendInviteIncome(int userid,int friend_level) {
		return userIncomeLogMapper.countUserFriendInviteIncome(userid, friend_level);
	}
	/**
	 * 获取好友收入明细列表
	 * @param userId	用户ID
	 * @param from_user	来源用户
	 * @return
	 */
	public List<UserIncomeLogVo> getUserFriendInviteIncomeDetail(int userId,int from_user, int start, int size) {
		List<UserIncomeLog> logs  =  userIncomeLogMapper.getUserFriendInviteIncomeDetail(userId, from_user,start,size);
		
		List<UserIncomeLogVo> l = new ArrayList<UserIncomeLogVo>();
		for(UserIncomeLog log : logs) {
			UserIncomeLogVo vo = new UserIncomeLogVo();
			 
			if(log.getFrom_user() > 0)
				vo.setFromUser(userService.getUserById(log.getFrom_user()));
			vo.setUserIncomeLog(log);
			l.add(vo);
		}
		return l;
	}
	public int countUserFriendInviteIncomeDetail(int userid,int from_user) {
		return  userIncomeLogMapper.countUserFriendInviteIncomeDetail(userid, from_user);
	}
	
	/**
	 * 获取好友收入明细列表
	 * @param userId	用户ID
	 * @param from_user	来源用户
	 * @return
	 */
	public List<UserIncomeLog> getUserOtherIncome(int userId) {
		return userIncomeLogMapper.getUserOtherIncome(userId);
	}
	
	/**
	 * 得到用户今日的收入
	 * TODO 先不用缓存 测试通过加上
	 * @param userId
	 * @return
	 */
	public int countUserTodyIncome(int userId){
		String key = RedisUtil.buildUserTodyIncome(userId);
		Integer income = 0;
		try {
			String data =  userincomeRedisCache.get(key);
			if(data != null){
				return Integer.parseInt(data);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("redis get User homePage:cause by :{}",e);
			}
		}
		
		Date startTime = DateUtil.getTodayStartDate();
		income = this.userIncomeLogMapper.getUserAmountByUserIdAndDate(userId, startTime , new Date());
		income = income == null?0:income;
		try {
			//缓存一定的时间
			userincomeRedisCache.set(key, Integer.toString(income), 3);
		} catch (Exception e) {
		}
		
		return income;
	}
	
	public boolean addExchangeIntegal(int userId,int income,String remark){
		if(this.userIncomeMapper.addExchangeIntegalIncome(userId, income) > 0){
			return this.addUserIncomeLog(userId, 0, 0, income, UserIncome.INCOME_TYPE_EXCHANGE, remark, 0);
		}
		return false;
	}
	
	public boolean addExchangeIntegalYouMi(int userId,int income,String remark){
		if(this.userIncomeMapper.addExchangeIntegalIncomeYouMi(userId, income) > 0){
			return this.addUserIncomeLog(userId, 0, 0, income, UserIncome.INCOME_TYPE_EXCHANGE_YOUMI, remark, 0);
		}
		return false;
	}
	
	public boolean addUserRecharge(int userId,int income){
		return this.userIncomeMapper.addUserRecharge(userId, income) > 0;
	}
	public boolean returnRechargeUserBalance(int userId,int income){
		return this.userIncomeMapper.returnRechargeUserBalance(userId, income) > 0;
	}
	public boolean addUserRechargeTotal(int userId,int income){
		return this.userIncomeMapper.addUserRechargeTotal(userId, income) > 0;
	}
	
	public boolean exchangeDuobaoCoin(int userId, int amount){
		boolean flag =  userIncomeMapper.exchangeDuobaoCoin(userId, amount) > 0;
		if (flag){
			//记录余额变更日志
			addUserIncomeLog(userId, 0, 0, amount, UserIncome.INCOME_TYPE_DOUBAOCOIN, "充值夺宝币", 0);
		}
		logger.info("exchangeDuobaoCoin, userId:{}, amount:{}, result:{}", userId, amount, flag);
		return flag;
	}
	
	private void onUserIncomeChanged(int userId){
		flushRedisByUserTodyIncome(userId);
	}
	
	private void flushRedisByUserTodyIncome(int userId){
		String key = RedisUtil.buildUserTodyIncome(userId);
		
		try {
			userincomeRedisCache.remove(key);
		} catch (Exception e) {
		}
	}
	
	public boolean addUserArticleIncome(int userId,long taskId,String ArticleName,int income){
		int i = userIncomeMapper.addArticleIncome(userId, income);
		boolean b = i > 0;
		if(b){
			addUserIncomeLog(userId, 0, taskId, income, UserIncome.INCOME_TYPE_TRAN_ARTICLE, ArticleName, 0);
		}
		return b;
	}
	
	public boolean addArticleFriendShareIncome(int userId, int friendId, long friendTaskId, int sum, String articleName, int level){
		int i = 0;
		if(level == 1)
			i = userIncomeMapper.addFriendShareIncomeLevel1(userId, sum);
		else if(level == 2)
			i = userIncomeMapper.addFriendShareIncomeLevel2(userId, sum);
		boolean b = i > 0;
		if(b){
			addUserIncomeLog(userId, friendId, friendTaskId, sum, UserIncome.INCOME_TYPE_TRAN_ARTICLE_SHARE, articleName, level);
		}
		return b;
	}
	public boolean addPreTwoAppTaskIncome(int userId, long taskId, int sum, String appName){
		int i = userIncomeMapper.addOtherIncome(userId, sum);
		boolean b = i > 0;
		if(b){
			addUserIncomeLog(userId, 0, taskId, sum, UserIncome.INCOME_TYPE_BEGINNER, appName, 0);
		}
		return b;
	}
	public boolean addPreFiveFriendTaskIncome(int userId, long taskId, int sum, String appName){
		int i = userIncomeMapper.addOtherIncome(userId, sum);
		boolean b = i > 0;
		if(b){
			addUserIncomeLog(userId, 0, taskId, sum, UserIncome.INCOME_TYPE_BEGINNER, appName, 0);
		}
		return b;
	}
	
	public boolean addNewUserRewardIncome(User u,int amount){
		if(u.isRewardNewUserComplete()){
			return true;
		}
		int i = userIncomeMapper.addOtherIncome(u.getId(), amount);
		boolean b = i > 0;
		if(b){
			addUserIncomeLog(u.getId(), 0, 0, amount, UserIncome.INCOME_TYPE_BEGINNER, "新用户红包", 0);
		}
		return b;
	}
	
	public boolean addDrawIncome(int userId,int income,String remark){
		if(this.userIncomeMapper.addOtherIncome(userId, income) > 0){
			return this.addUserIncomeLog(userId, 0, 0, income, UserIncome.INCOME_TYPE_OTHER, remark, 0);
		}
		return false;
	}
	
	public boolean addUserInviteFriendIncome(int userId,int amount,int rank,String remark){
		if(this.userIncomeMapper.addOtherIncome(userId, amount) > 0){
			return this.addUserIncomeLog(userId, 0, 0, amount, UserIncome.INCOME_TYPE_OTHER, remark, 0);
		}
		return false;
	}
	
	public boolean addUserReInputInviteCodeIncome(int userId,int amount){
		if(this.userIncomeMapper.addOtherIncome(userId, amount) > 0){
			return this.addUserIncomeLog(userId, 0, 0, amount, UserIncome.INCOME_TYPE_OTHER, "系统补发", 0);
		}
		return false;
	}
}
