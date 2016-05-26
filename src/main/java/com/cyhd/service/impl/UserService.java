package com.cyhd.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.cyhd.common.util.StringUtil;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.MD5Util;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.db.mapper.UserMapper;
import com.cyhd.service.dao.impl.CacheDualAccessDaoImpl;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.util.CacheUtil;


@Service
public class UserService extends BaseService {
	
	@Resource(name = CacheUtil.MEMCACHED_RESOURCE)
	private CacheDao memcachedCacheDao;
	
	private CacheLRULiveAccessDaoImpl<Integer> ticketCache = new CacheLRULiveAccessDaoImpl<Integer>(Constants.minutes_millis * 3, 1024);

	private CacheDualAccessDaoImpl<User> cacheDao;
	
	private ExecutorService pushExecutor = null;
	
	@Resource
	private UserIncomeService userIncomeService;
	
	@Resource
	private UserFriendService userFriendService;
	
	@Resource
	private UserMessageService userMessageService;


	/**
	 * 为用户秒赚大钱公众号对应openID增加缓存，防止用户不断刷新
	 */
	private CacheLRULiveAccessDaoImpl<String> openIDCache = new CacheLRULiveAccessDaoImpl<>(Constants.minutes_millis, 512);
	
	@PostConstruct
	public void init() {
		cacheDao = new CacheDualAccessDaoImpl<User>(10 * 1000, 2048, memcachedCacheDao);
		ThreadFactory threadFactory2 = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("update_user_job_thread");
				return t;
			}
		};
		pushExecutor = Executors.newFixedThreadPool(1, threadFactory2);
	}
	
	@PreDestroy
	public void close(){
		if(pushExecutor != null){
			pushExecutor.shutdown();
			pushExecutor = null;
		}
	}
	
	@Resource
	private UserMapper userMapper;
	
	
	public int getMaxUserId() {
		return userMapper.getMaxUserId();
	}
	
	public User getUserByInviteCode(String code){
		return userMapper.getUserByInviteCode(code);
	}
	
	public User getUserByOpenId(String openId) {
		return userMapper.getUserByOpenId(openId);
	}
	public User getUserByIdentifyId(int id){
		return userMapper.getUserByIdentifyId(id);
	}
	
	public User getUserByMobile(String mobile){
		return userMapper.getUserByMobile(mobile);
	}
	
	public User getUserByUnionId(String unionid) {
		return userMapper.getUserByUnionId(unionid);
	}
	
	
	public boolean updateDeviceType(final int userId, final int type){
		pushExecutor.execute(new Runnable() {
			@Override
			public void run() {
				int i = userMapper.updateDeviceType(userId, type);
				if(i >= 1){
					clearUserCache(userId);
				}
			}
		});
		return true;
	}
	
	public boolean bindMobile(int userId, String mobile){
		int i = userMapper.bindMobile(userId, mobile);
		if(i >= 1){
			this.clearUserCache(userId);
			return true;
		}
		return false;
	}
	
	public boolean setTaskInviteComplete(int userId){
		int i = userMapper.updateTaskProperty(userId, User.TASK_INVITE);
		if(i >= 1){
			this.clearUserCache(userId);
			return true;
		}
		return false;
	}
	
	public boolean setTaskShareComplete(int userId){
		int i = userMapper.updateTaskProperty(userId, User.TASK_SHARE);
		if(i >= 1){
			this.clearUserCache(userId);
			return true;
		}
		return false;
	}
	
	public boolean setTaskAppComplete(int userId){
		int i = userMapper.updateTaskProperty(userId, User.TASK_APP);
		if(i >= 1){
			this.clearUserCache(userId);
			return true;
		}
		return false;
	}
	
	public boolean setMasked(int userId, boolean masked){
		int mask = 0;
		if(masked){
			mask = 1;
		}
		int i = userMapper.setMasked(userId, mask);
		if(i >= 1){
			this.clearUserCache(userId);
			return true;
		}
		return false;
	}
	
	public User getUserByTicket(String ticket) {
		Integer id = (Integer) ticketCache.get(ticket);
		if (id == null) {
			User user = userMapper.getUserByTicket(ticket);
			if (user != null) {
				ticketCache.set(ticket, user.getId());
				return user;
			}
		} else {
			return this.getUserById(id);
		}
		return null;
	}
	
	public User getUserById(int id) {
		if (id == 0) {
			return null;
		}
		User u = this.getUserFromCache(id);
		if (u == null) {
			u = userMapper.getUserById(id);
			if (u != null) {
				this.addUserToCache(id, u);
			}
		}
		return u;
	}
	
	/**
	 * 获取用户列表
	 * @param row
	 * @return
	 */
	public List<User> getList(int start, int size) {
		return userMapper.getList(start, size);
	}
	public User getUserByDid(String deviceId) {
		return userMapper.getUserByDid(deviceId);
	}
	
	public String generateTicket(String openId) {
		return MD5Util.getMD5(openId) + MD5Util.getMD5(System.currentTimeMillis() + "");
	}
	
	public int generateIdentityId(){
		int id = 10000000 + new Random().nextInt(90000000);
		User u = this.getUserByIdentifyId(id);
		if(u != null){
			return generateIdentityId();
		}
		return id;
	}
	public boolean insertOrUpdate(User u) {
		int i = userMapper.addOrUpdate(u);
		if(i >= 1 && u.getId() > 0){
			this.clearUserCache(u.getId());
		}
		return i >= 1;
	}
	private String buildCacheKey(int userid) {
		return CacheUtil.getUserKey(userid);
	}

	private User getUserFromCache(int userid) {
		try {
			String key = buildCacheKey(userid);
			return (User) cacheDao.get(key);
		} catch (Exception e) {
			logger.error("get from cache error!", e);
		}
		return null;
	}

	private void addUserToCache(int userid, User user) {
		try {
			String key = buildCacheKey(userid);
			cacheDao.set(key, user);
		} catch (Exception e) {
			logger.error("add cache error!", e);
		}
	}

	public void clearUserCache(int userid) {
		try {
			String key = buildCacheKey(userid);
			cacheDao.remove(key);
		} catch (Exception e) {
			logger.error("clear cache error!", e);
		}
	}
	
	public boolean setGenSharePic(int userid){
		int i = userMapper.updateGenSharePic(userid);
		if(i >= 1){
			this.clearUserCache(userid);
			return true;
		}
		return false;
	}
	
	public User getUserByIdfa(String idfa){
		return userMapper.getUserByIdfa(idfa);
	}
	
	/***
	 * 封掉用户并且写log
	 * @param userId
	 * @param masked
	 * @param reason
	 * @return
	 */
	public boolean setMaskedUser(int userId,boolean masked,String reason){
		if(this.setMasked(userId, masked)){
			//写日志
			this.userMapper.addUserMaskedLog(userId, masked?1:0, reason, -1);
			return true;
		}
		return false;
	}
	public boolean setTranArticleComplete(int userId){
		int i = userMapper.updateTaskProperty(userId, User.TASK_TRAN_ARTICLE);
		if(i >= 1){
			this.clearUserCache(userId);
			return true;
		}
		return false;
	}
	
	public boolean setRewardNewUserComplete(int userId){
		int i = userMapper.updateTaskProperty(userId, User.NEW_USER_FLAG);
		if(i >= 1){
			this.clearUserCache(userId);
			return true;
		}
		return false;
	}


	/**
	 * 给新用户发奖
	 * @param u
	 * @param clientType
	 * @return
	 */
	public int executeRewardNewUser(User u,int clientType){
		if(u.isRewardNewUserComplete()){
			return 0;
		}

		//默认是1块钱
		int amount = 100;

		//获取用户的邀请人
		User invitor = null;
		int invitorId = userFriendService.getInvitor(u.getId());
		
		if (invitorId > 0) {
			
			invitor = getUserById(invitorId);
		}

		//ios用户的奖励金额不同
		if (clientType == Constants.platform_ios) {
			amount = invitor == null? 150 : 200;
		}

		if(userIncomeService.addNewUserRewardIncome(u, amount)){
			userMessageService.addNewUserRewardMessage(u.getId(), amount, clientType, invitor);
			logger.info("新用户发放奖励,处理成功,userId:{},amount:{},师傅Id:{}", u.getId(), amount, invitorId);
		}else{
			logger.error("新用户发放奖励失败, userId:{}, amount:{}", u.getId(), amount);
			amount = 0;
		}

		setRewardNewUserComplete(u.getId());

		return amount;
	}

	/**
	 * 没有做过新手任务的都是新用户
	 */
	public boolean isNewUser(User u){
		return !(u.isRewardNewUserComplete()
				||u.isTaskAppComplete() 
				||u.isTaskInviteComplete()
				||u.isTaskShareComplete()
				||u.isTranArticleComplete());
	}

	/**
	 * 用户完成钥匙版安装入口的任务
	 * @param userId
	 * @return
	 */
	public boolean setYaoshiClipComplete(int userId){
		int i = userMapper.updateTaskProperty(userId, User.TASK_YAOSHI_CLIP);
		if(i >= 1){
			this.clearUserCache(userId);
			return true;
		}
		return false;
	}


	/**
	 * 获取用户指定公众号对应的openId
	 * @param appId
	 * @param userId
	 * @return
	 */
	public String getUserOpenID(String appId, int userId){
		String openId = openIDCache.get(appId + userId);
		if (openId == null) {
			openId = userMapper.getUserOpenID(appId, userId);
			if (StringUtil.isNotBlank(openId)){
				openIDCache.set(appId + userId, openId);
			}
		}
		return openId;
	}

	/**
	 * 修改名称
	 * @param userId
	 * @param name
	 */
	public boolean setName(int userId,String name) {
		int rows = userMapper.updateName(userId, name);
		if(rows >= 1) {
			this.clearUserCache(userId);
			return true;
		}
		return false;
	}

	/**
	 * 修改头像
	 * @param userId
	 * @param avatar
	 * @return
	 */
	public boolean setAvatar(int userId, String avatar) {
		int rows = userMapper.updateAvatar(userId, avatar);
		if(rows >= 1) {
			this.clearUserCache(userId);
			return true;
		}
		return false;
	}
	public boolean updateUserCreateTime(int userId,Date createTime){
		int rows = userMapper.updateUserCreateTime(userId, createTime);
		if(rows >= 1) {
			this.clearUserCache(userId);
			return true;
		}
		return false;
	}
}
