package com.cyhd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.cyhd.common.util.DateUtil;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.dao.po.*;
import com.cyhd.service.dao.po.UserDrawLog.UserDrawLogType;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.MoneyUtils;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.ECacheDao;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.UserAppMessageMapper;
import com.cyhd.service.dao.db.mapper.UserFriendMessageMapper;
import com.cyhd.service.dao.db.mapper.UserSysMessageMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.push.PushConstants;
import com.cyhd.service.push.PushService;
import com.cyhd.service.util.RedisUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@Service
public class UserMessageService extends BaseService {

	@Resource
	private UserAppMessageMapper userAppMessageMapper;
	
	@Resource
	private UserFriendMessageMapper userFriendMessageMapper;
	
	@Resource
	private UserSysMessageMapper userSysMessageMapper;
	
	@Resource
	private UserArticleMessageService userArticleMessageService;
	
	@Resource
	private UserTaskService userTaskService;
	
	@Resource
	private PushService pushService;
	
	@Resource
	private IdMakerService idMarkerService;
	
	
	private static final int max_size = 100;

	//线上环境试用redis缓存
	private static final boolean useRedis = GlobalConfig.isDeploy;
	
	@Resource(name=RedisUtil.NAME_ALIYUAN)
	private IJedisDao userMessageCacheDao;    //把用户的消息，保存到redis的列表里面，key是userid+type
	
	private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	private ECacheDao<UserMessagePage> pageCache = new CacheLRULiveAccessDaoImpl<UserMessagePage>(10 * Constants.minutes_millis, 128);
	
	private static String getAppMessageKey(int userId){
		return RedisUtil.buildUserMessageKey(userId, 1);
	}
	private static String getShareMessageKey(int userId){
		return RedisUtil.buildUserMessageKey(userId, 2);
	}
	private static String getSystemMessageKey(int userId){
		return RedisUtil.buildUserMessageKey(userId, 3);
	}
	private final static String getArticleMessageKey(int userId,int client_type){
		return RedisUtil.buildArticleMessagekey(userId,4, client_type);
	}
	private volatile boolean sending = false;
	//定时读取系统消息表，给用户发push
	public void loadAndSendSysPush(){
		try{
			if(sending){
				logger.warn("load system message warn, another is running!");
				return;
			}
			sending = true;
			logger.info("start load system message and push!!!!");
			Date d = new Date(System.currentTimeMillis() - 2 * Constants.hour_millis);
			//读取两小时内的新系统消息
			List<UserSystemMessage> messages = userSysMessageMapper.getUnSendSystemMessages(d, max_size);
			if(messages != null && messages.size() > 0){
				logger.info("load system message size:{}", messages.size());
				for(UserSystemMessage message : messages){
					if(message.getUser_id() == 0){
						pushService.notifyAllUsersSystemPrompt(message.getTitle(),message.getPush_client_type());
					}else{
						pushService.notifyUserSystemPrompt(message.getUser_id(), message.getTitle(),message.getPush_client_type());
					}
					userSysMessageMapper.setSended(message.getId());
				}
			}
			logger.info("end load system message and push!!!!");
		}catch(Exception e){
			logger.error("UserMessageService loadAndSendSysPush error!", e);
		}finally {
			sending = false;
		}
	}
	
	private volatile boolean notify = false;
	@Resource
	private AppTaskService appTaskService;
	
	@Resource
	private UserService userService;
	
	public void notifyExpiredAppMessage() {
		try{
			if(notify){
				logger.warn("load app will expire message warn, another is running!");
				return;
			}
			notify = true;
			logger.info("start load app will expire message and push!!!!");
			int expiredTime = (int) (Constants.TASK_EXPIRE_TIME - (10 * Constants.minutes_millis)) / 1000;
			
			logger.info("expireTime" + expiredTime);
			List<UserTask> userTask = userTaskService.getExpireTasks(expiredTime);
			if(userTask != null && userTask.size() > 0){
				logger.info("load app will expire message size:{}", userTask.size());
				for(UserTask ut : userTask){
					AppTask apptask= appTaskService.getAppTask(ut.getTask_id());
					App app = appTaskService.getApp(ut.getApp_id());
					User user = userService.getUserById(ut.getUser_id());
					
					if(userTaskService.updateWillExpire(ut.getId(), ut.getUser_id()) > 0)
						addUserAppMessage(user, app, apptask, ut, PushConstants.TYPE_APP_TASK_EXPIRE, null);
				}
			}
			logger.info("end load app will expire message and push!!!!");
		}catch(Exception e){
			logger.error("UserMessageService notifyExpiredAppMessage error!", e);
		}finally {
			notify = false;
		}
	}
	
	/**
	 * 应用试用消息列表
	 * 
	 * @return List<UserAppMessage>
	 */
	public List<UserAppMessage> getAppMessageList(int userid, long lastId, int size){
		int end = max_size;
		if(lastId <= 0){
//			lastId = this.getLastId(userid, 1);
			end = size;
		}
		
		if(lastId <= 0){
			lastId = Long.MAX_VALUE;
		}
		List<UserAppMessage> messages = new ArrayList<UserAppMessage>();
		
		if(useRedis){
			boolean error = false;
			try{
				String key = getAppMessageKey(userid);
				List<String> ls = userMessageCacheDao.getList(key, 0, end);
				if(ls != null && ls.size() > 0){
					for(String s : ls){
						
						UserAppMessage message = gson.fromJson(s, UserAppMessage.class);
						if(message.getStatus() == UserAppMessage.STATUS_APP_WILL_EXPIRE && message.getTask_id() == 0){
							int taskId = userTaskService.getUserTaskById(message.getUser_task_id()).getTask_id();
							message.setTask_id(taskId);
						}
						long sort = message.getSort_time();
						if(sort <= 0)
							sort = message.getId();
						if(sort < lastId && messages.size() < size){
							messages.add(message);
						}
					}
				}
				return fillContent(messages);
			}catch(Exception e){
				logger.error("UserMessageService getAppMessageList error!", e);
				error = true;
			}
		}
		
		return fillContent(userAppMessageMapper.getMessagesById(userid, lastId, size));
	}
	
	private List<UserAppMessage> fillContent(List<UserAppMessage> messages){
		if(messages == null || messages.size() == 0){
			return messages;
		}
		for(UserAppMessage message : messages){
			if(StringUtils.isEmpty(message.getBundle_id())){
				App app = appTaskService.getAppByProtocol(message.getAgreement());
				if(app != null){
					message.setBundle_id(app.getBundle_id());
				}
			}
		}
		return messages;
	}
	
	/**
	 * 获取总数
	 * @param userid
	 * @return
	 */
	public int getAppMessageCount(int userid) {
		if(useRedis){
			try{
				String key = getAppMessageKey(userid);
				return (int)userMessageCacheDao.getListLen(key);
			}catch(Exception e){
				logger.error("UserMessageService getAppMessageCount error!", e);
			}
		}
		return userAppMessageMapper.getCount(userid);
	}
	/**
	 * 更新已读
	 * 
	 * @param id
	 * @return
	 */
	public boolean updateAppMessageReadStatus(int id) {
		return userAppMessageMapper.updateReadStatus(id) >= 1;
	}
	
	public void addUserShareMessage(int userId, User friend, App app, AppTask appTask, UserTask ut, int shareAmount, int level,int parentId){
		UserFriendMessage message = new UserFriendMessage();
		message.setId(idMarkerService.getIncreaseId());
		message.setSort_time(System.currentTimeMillis());
		message.setAmount(shareAmount);
		message.setApp_name(app.getName());
		message.setApp_icon(app.getIcon());
		message.setCreate_time(new Date());
		message.setFriend_amount(appTask.getAmount());
		message.setFriend_avater(friend.getAvatar());
		message.setFriend_id(friend.getId());
		message.setFriend_name(friend.getName());
		message.setFriend_task_id(ut.getTask_id());
		message.setUser_id(userId);
		message.setFriend_level(level);
		message.setMiddle_friend_id(parentId);
		message.setSource(UserFriendMessage.SOURCE_APP_TASK);
		if(saveFriendMessage(message)){
			 pushService.notifyFriendPrompt(userId, app, appTask, ut, shareAmount, level);
		}
	}
	
	public void addUserAppMessage(User user, App app, AppTask appTask, UserTask ut, int type){
		this.addUserAppMessage(user, app, appTask, ut, type, null);
	}
	public void addUserAppMessage(User user, App app, AppTask appTask, UserTask ut, int type, String extra){
		//int amount = userTaskService.getAmountByActivity(appTask.getAmount(), user.getId());
		int amount = appTask.getAmount();
		UserAppMessage message = new UserAppMessage();
		message.setId(idMarkerService.getIncreaseId());
		message.setSort_time(System.currentTimeMillis());
		message.setAgreement(app.getAgreement());
		message.setAmount(amount);
		message.setApp_icon(app.getIcon());
		message.setApp_name(app.getName());
		message.setCreate_time(new Date());
		message.setKeyword(appTask.getKeywords()); 
		message.setTask_description(appTask.getDescription());
		message.setUser_id(user.getId());
		message.setTask_id(appTask.getId());
		message.setUser_task_id(ut.getId());
		message.setStatus(type);
		message.setFinish_time(ut.getFinishtime());
		message.setTrial_time(appTask.getDuration()/60);
		message.setExtra_info(extra);
		message.setExpired_time(ut.getExpiretime());
		if(saveAppMessage(message)) {
			// 只有成功完成时才发推送
			if (type == UserAppMessage.STATUS_APP_AUDIT_SUCCESS){
				String content = app.getName() + "App试用奖励" + MoneyUtils.fen2yuanS(amount) + "元已经发放！";
				pushService.notifyUserSystemPrompt(user.getId(), content, Constants.platform_ios);
			}
		}
	}
	/**
	 * 保存
	 * 
	 *
	 * @return boolean
	 */
	private boolean saveAppMessage(UserAppMessage message){
		try{
			String key = getAppMessageKey(message.getUser_id());
			userMessageCacheDao.addToList(key, gson.toJson(message));
			userMessageCacheDao.keepLen(key, max_size);
			userMessageCacheDao.expire(key, 7 * Constants.DAY_SECONDS); //最多保持7天
		}catch(Exception e){
			
		}
		return userAppMessageMapper.add(message) >=1;
	}
//	/**
//	 * 获取APP消息模板
//	 *  
//	 * @return String
//	 */
//	public String getPushAppTemplate(UserAppMessage message) {
//		StringBuilder str = new StringBuilder(); 
//		str.append("《");
//		str.append(message.getApp_name());
//		str.append("》搜索下载完成，试玩后获得奖励");
//		str.append(MoneyUtils.fen2yuan(message.getAmount()));
//		str.append("元");
//		return str.toString();
//	}
//	/**
//	 * 获取好友消息模板
//	 * 
//	 * @return String
//	 */
//	public String getPushFriendTemplate(UserFriendMessage message) {
//		StringBuilder str = new StringBuilder(); 
//		str.append("我完成了《");
//		str.append(message.getApp_name());
//		str.append("》的试用，并获得收益");
//		str.append(MoneyUtils.fen2yuan(message.getAmount()));
//		str.append("元，给你提供分成");
//		str.append(MoneyUtils.fen2yuan(message.getFriend_amount()));
//		str.append("元");
//		return str.toString();
//	}
	/**
	 * 好友消息列表
	 * 
	 * @return List<UserFriendMessage>
	 */
	public List<UserFriendMessage> getFriendMessageList(int userid, long lastId, int size){
		int end = max_size;
		if(lastId <= 0){
			lastId = Long.MAX_VALUE;
//			lastId = this.getLastId(userid, 2);
			end = size;
		}
	
		if(useRedis){
			boolean error = false;
			try{
				String key = getShareMessageKey(userid);
				List<String> ls = userMessageCacheDao.getList(key, 0, end);
				List<UserFriendMessage> messages = new ArrayList<UserFriendMessage>(size);
				if(ls != null && ls.size() > 0){
					for(String s : ls){
						UserFriendMessage message = gson.fromJson(s, UserFriendMessage.class);
						long sort = message.getSort_time();
						if(sort <= 0)
							sort = message.getId();
						if(sort < lastId && messages.size() < size){
							messages.add(message);
						}
					}
				}
				return messages;
			}catch(Exception e){
				logger.error("UserMessageService getAppMessageList error!", e);
				error = true;
			}
		}
		return userFriendMessageMapper.getMessagesById(userid, lastId, size);
	} 
	
	/**
	 * 获取总数
	 * @param userid
	 * @return int
	 */
	public int getFriendMessageCount(int userid) {
		if(useRedis){
			try{
				String key = getShareMessageKey(userid);
				return (int)userMessageCacheDao.getListLen(key);
			}catch(Exception e){
				logger.error("UserMessageService getFriendMessageCount error!", e);
			}
		}
		return userFriendMessageMapper.getCount(userid);
	}
	/**
	 * 保存
	 * 
	 * @return boolean
	 */
	private boolean saveFriendMessage(UserFriendMessage message){
		try{
			String key = getShareMessageKey(message.getUser_id());
			userMessageCacheDao.addToList(key, gson.toJson(message));
			userMessageCacheDao.keepLen(key, max_size);
			userMessageCacheDao.expire(key, 7 * Constants.DAY_SECONDS); //最多保持7天
		}catch(Exception e){
			
		}
		return userFriendMessageMapper.add(message) >=1;
	}
	/**
	 * 更新好友消息已读
	 * 
	 * @param id
	 * @return boolean
	 */
	public boolean updateFriendMessageReadStatus(int id) {
		return userFriendMessageMapper.updateReadStatus(id) >= 1;
	}
	
	
	public void addUserBeginnerTaskMessage(int userId, App app, AppTask appTask, UserTask ut,int clientType) {
		UserSystemMessage message = makeDefaultSystemMessage();
		message.setUser_id(userId);
		String title = "您已完成“"+ appTask.getName() +"”新手任务";
		message.setTitle(title);
		message.setType(UserSystemMessage.TYPE_BEGINNER_TASK);
		message.setContent(title + "，奖励"+ MoneyUtils.fen2yuanS(appTask.getAmount())+ "元");
		message.setDescription(message.getContent());
		message.setPush_client_type(clientType);
		try{
		if(saveUserSystemMessage(message)){
			//pushService.notifyUserSystemPrompt(userId, title,clientType);
		}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}
	
	public void notifyNewUserBeginnerMessage(int userId,int clientType) {
		UserSystemMessage message = makeDefaultSystemMessage();
		message.setUser_id(userId);
		String title = "恭喜您注册成功";
		message.setTitle(title);
		message.setType(UserSystemMessage.TYPE_BEGINNER_TASK);
		message.setContent("恭喜注册成功，先去新手任务看看吧！");
		message.setTarget_url("/web/discovery/beginner.html");
		message.setDescription(message.getContent());
		message.setPush_client_type(clientType);
		try{
		if(saveUserSystemMessage(message)){
//			pushService.notifyUserSystemPrompt(userId, title,clientType);
		}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}
	public void notifyMobileRechargeMessage(int userId, String mobile, int value) {
		UserSystemMessage message = makeDefaultSystemMessage();
		message.setUser_id(userId);
		String title = "手机充值成功";
		message.setTitle(title);
		message.setType(UserSystemMessage.TYPE_RECHARGE_TASK);
		message.setContent("手机:"+mobile+" 充值成功，充值金额为"+value+"元！");
		message.setDescription(message.getContent());
		try{
		if(saveUserSystemMessage(message)){
			//pushService.notifyUserSystemPrompt(userId, title,UserSystemMessage.PUSH_CLIENT_TYPE_ALL);
		}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}
	private UserSystemMessage makeDefaultSystemMessage(){
		UserSystemMessage message = new UserSystemMessage();
		//message.setId(idMarkerService.getIncreaseId());
		message.setSort_time(System.currentTimeMillis());
		message.setCreate_time(new Date());
		message.setSend_time(new Date());
		message.setSend(1);
		return message;
	}
	
	/**
	 * 系统消息列表
	 * 
	 * @return List<UserSystemMessage>
	 */
	public List<UserSystemMessage> getSysMessageList(int userid, Date createtime, long lastId, int size,int clientType){
//		if(lastId <= 0){

//			lastId = Long.MAX_VALUE;
//			lastId = this.getLastId(userid, 3);
//		}
//		return userSysMessageMapper.getMessagesById(userid, createtime, lastId, size,clientType);


		int end = max_size;
		if(lastId <= 0){
			lastId = Long.MAX_VALUE;
//			lastId = this.getLastId(userid, 2);
			end = size;
		}

		if(useRedis){
			boolean error = false;
			try{
				String key = getSystemMessageKey(userid);
				List<String> ls = userMessageCacheDao.getList(key, 0, end);
				List<UserSystemMessage> messages = new ArrayList<>(size);
				if(ls != null && ls.size() > 0){
					for(String s : ls){
						UserSystemMessage message = gson.fromJson(s, UserSystemMessage.class);
						long sort = message.getSort_time();
						if(sort <= 0)
							sort = message.getId();
						if(sort < lastId && messages.size() < size){
							messages.add(message);
						}
					}
				}
				return messages;
			}catch(Exception e){
				logger.error("UserMessageService getSysMessageList error!", e);
				error = true;
			}
		}
		return userSysMessageMapper.getMessagesById(userid, createtime, lastId, size, clientType);
	}
	
	/**
	 * 获取总数
	 * @param userid
	 * @return int
	 */
	public int getSysMessageCount(int userid, Date createtime,int clientType) {
		if(useRedis){
			try{
				String key = getSystemMessageKey(userid);
				return (int)userMessageCacheDao.getListLen(key);
			}catch(Exception e){
				logger.error("UserMessageService getAppMessageCount error!", e);
			}
		}

		return userSysMessageMapper.getCount(userid, createtime,clientType);
	}
	
	/**
	 * 获取系统详情页
	 * @param id
	 * @return UserMessagePage
	 */
	public UserMessagePage getSysMessagePageById(int id) {
		String key = String.valueOf(id);
		UserMessagePage page = pageCache.get(key);
		if(page == null){
			page = userSysMessageMapper.getSysMessagePageById(id);
			if(page != null && page.getEstate() == 1){
				
			}else {
				page = new UserMessagePage();
			}
			pageCache.set(key, page);
		}
		return page;
	}
	
	public Map<String, Object> getSysMessageNotReadCount(int userid, Date createtime, long lastId, int clientType){
//		if(lastid <= 0){
//			lastid = this.getLastId(userid, 3);
//		}


		Map<String, Object> map = new HashMap<String, Object>();
		int end = max_size;
		if(lastId <= 0){
//			lastId = this.getLastId(userid, 1);
			//lastId = Long.MAX_VALUE;
			end = 1;
		}

		UserSystemMessage message = null;
		int total = 0;

		if(useRedis){
			try{
				String key = getSystemMessageKey(userid);
				List<String> ls = userMessageCacheDao.getList(key, 0, end);

				if(ls != null && ls.size() > 0){
					message = gson.fromJson(ls.get(0), UserSystemMessage.class);
					map.put("sysMessage", message);

					if(lastId > 0){
						for(String s : ls){
							//转化成类在这里有点多余
							//TODO 需要改善
							message = gson.fromJson(s, UserSystemMessage.class);
							long sort = message.getSort_time();
							if(sort <= 0)
								sort = message.getId();
							if(sort <= lastId){
								break;
							}
							total ++;
						}
					}
				}
				map.put("sys_total", total);
				return map;
			}catch(Exception e){
				logger.error("UserMessageService getSysMessageNotReadCount error!", e);
			}
		}

		//去数据库查询
		if(lastId > 0){
			total = userSysMessageMapper.getCountByLastId(userid, createtime, lastId, clientType);
		}

		message = getUserLastSystemMessage(userid, createtime, clientType);

		map.put("sys_total", total);
		map.put("sysMessage", message);

		return map;
	}

	/**
	 * 获取用户最新的系统消息
	 * @param userid
	 * @param createTime
	 * @param clientType
	 * @return
	 */
	public UserSystemMessage getUserLastSystemMessage(int userid, Date createTime, int clientType){
		UserSystemMessage systemMessage = null;
		String key = getSystemMessageKey(userid);
		try {
			List<String> ls = userMessageCacheDao.getList(key, 0, 1);
			if (ls != null && ls.size() > 0){
				systemMessage = gson.fromJson(ls.get(0), UserSystemMessage.class);
			}else {
				systemMessage = userSysMessageMapper.getSysMessageLastByUserId(userid, createTime, clientType);
			}

		} catch (Exception e) {
			logger.error("UserMessageService getUserLastSystemMessage error!", e);
		}
		return systemMessage;
	}

	/**
	 * 如果lastId为0 取最新的一条
	 * @param userid
	 * @param lastId
	 * @return
	 */
	public Map<String, Object> getAppMessageNotReadCount(int userid,long lastId){
		Map<String, Object> map = new HashMap<String, Object>();
		int end = max_size;
		if(lastId <= 0){
//			lastId = this.getLastId(userid, 1);
			//lastId = Long.MAX_VALUE;
			end = 1;
		}

		UserAppMessage message = null;
		int total = 0;
		
		if(useRedis){
			try{
				String key = getAppMessageKey(userid);
				List<String> ls = userMessageCacheDao.getList(key, 0, end);
				
				if(ls != null && ls.size() > 0){
					message = gson.fromJson(ls.get(0), UserAppMessage.class);
					map.put("appMessage", message);
					
					if(lastId > 0){
						for(String s : ls){
							//转化成类在这里有点多余 
							//TODO 需要改善 
						    message = gson.fromJson(s, UserAppMessage.class);
						    long sort = message.getSort_time();
							if(sort <= 0)
								sort = message.getId();
							if(sort <= lastId){
								break;
							}
							total ++;
						}
					}
				}
				map.put("app_total", total);
				return map;
			}catch(Exception e){
				logger.error("UserMessageService getAppMessageList error!", e);
			}
		}
		
		//去数据库查询 
		if(lastId > 0){
			total = userAppMessageMapper.getCountByLastId(userid, lastId);
		}
		
		message = getUserLastAppMessage(userid);

		map.put("app_total", total);
		map.put("appMessage", message);
		
		return map;
	}

	/**
	 * 获取用户最新的应用消息
	 * @param userid
	 * @return
	 */
	public UserAppMessage getUserLastAppMessage(int userid){
		UserAppMessage appMessage = null;
		String key = getAppMessageKey(userid);
		try {
			List<String> ls = userMessageCacheDao.getList(key, 0, 1);
			if (ls != null && ls.size() > 0){
				appMessage = gson.fromJson(ls.get(0), UserAppMessage.class);
			}else {
				appMessage = userAppMessageMapper.getLastAppMessageByUserId(userid);
			}

		} catch (Exception e) {
			logger.error("UserMessageService getUserLastAppMessage error!", e);
		}
		return appMessage;
	}
	/**
	 * 如果lastId为0 取最新的
	 * @param userid
	 * @param lastId
	 * @return
	 */
	public Map<String, Object> getFriendMessageNotReadCount(int userid,long lastId){
		Map<String, Object> map = new HashMap<String, Object>();
		int end = max_size;
		if(lastId <= 0){
//			lastId = this.getLastId(userid, 2);
			end = 1;
		}
//		//TODO 这里需要注意  第一次就是0 
//		if(lastId <= 0){
//			lastId = Long.MAX_VALUE;
//		}
		UserFriendMessage message = null;
		int total = 0;
		if(useRedis){
			try{
				String key = getShareMessageKey(userid);
				List<String> ls = userMessageCacheDao.getList(key, 0, end);
				
				if(ls != null && ls.size() > 0){
					message = gson.fromJson(ls.get(0), UserFriendMessage.class);
					map.put("friendMessage", message);
					
					if(lastId > 0){
						for(String s : ls){
							message = gson.fromJson(s, UserFriendMessage.class);
							long sort = message.getSort_time();
							if(sort <= 0)
								sort = message.getId();
							if(sort <= lastId ){
								break;
							}
							total ++;
						}
					}
					
				}
				map.put("friend_total", total);
				return map;
			}catch(Exception e){
				logger.error("UserMessageService getAppMessageList error!", e);
			}
		}
		
		//去数据库查询 没有再加一层else 
		if(lastId > 0){
			total = userFriendMessageMapper.getCountByLastId(userid, lastId);
		}
		
		message = getUserLastFirendMessage(userid);
		
		map.put("friend_total", total);
		map.put("friendMessage", message);
		
		return map;
	}

	/**
	 * 获取用户最新的好友消息
	 * @param userid
	 * @return
	 */
	public UserFriendMessage getUserLastFirendMessage(int userid){
		UserFriendMessage friendMessage = null;
		String key = getShareMessageKey(userid);
		try {
			List<String> ls = userMessageCacheDao.getList(key, 0, 1);
			if (ls != null && ls.size() > 0){
				friendMessage = gson.fromJson(ls.get(0), UserFriendMessage.class);
			}else {
				friendMessage = userFriendMessageMapper.getUserLastFirendMessage(userid);
			}

		} catch (Exception e) {
			logger.error("UserMessageService getUserLastFirendMessage error!", e);
		}
		return friendMessage;
	}
	
//	public long getLastId(int userid,int type){
////		MessageLastId messageLastId = this.messageLastIdMapper.selectByUseridAndType(userid, type);
////		if(messageLastId != null){
////			return messageLastId.getLastid();
////		}
//		return 0;
//	}
	
	/***
	 * 
	 * @param userId
	 * @param ad_name
	 * @param points
	 * @param source <h2 color='red'>是金币还是积分。默认是积分，如果是金币source=Constants.INTEGAL_SOURCE_WANPU<h2>
	 * @param clientType
	 */
	public void notifyIntegralMessage(int userId,String ad_name,int points,int source,int clientType) {
		UserSystemMessage message = makeDefaultSystemMessage();
		message.setUser_id(userId);
		String type = "积分";
		if(source == Constants.INTEGAL_SOURCE_WANPU){
			type="金币";
		}
		
		String title = "恭喜您获得"+points+type+"!";
		message.setTitle(title);
		message.setType(UserSystemMessage.TYPE_INTEGAL);
		message.setContent("恭喜您完成赚"+type+"中的"+ad_name+"任务，获得"+points+type+",可在我的"+type+"中进行现金兑换。");
		message.setDescription(message.getContent());
		message.setPush_client_type(clientType);
		try{
		if(saveUserSystemMessage(message)){
			if(clientType == Constants.platform_android){
				pushService.notifyUserSystemPrompt(userId, message.getContent(),message.getPush_client_type());
			}
		}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}
	
	public void notifyTranArticleMessage(int userId,String articleName,int income,int view_num,int clientType,int type,int task_id,long user_task_id){
		UserArticleMessage message = new UserArticleMessage();
		message.setUser_id(userId);
		String yuan = MoneyUtils.fen2yuanS(income);
		message.setAmount(income);
		if(type == UserArticleMessage.MESS_TYPE_WILL_EXPRIED){
			message.setTask_description("您转发任务中的<"+articleName+">,即将过期,其阅读人数"+view_num+"人,未达到目标人数.");
		}else{
			message.setTask_description("恭喜您完成转发任务中的<"+articleName+">其阅读人数达到"+view_num+"人，获得"+yuan+"元收入");
		}
		message.setClient_type(clientType);
		message.setTask_name(articleName);
		message.setTask_id(task_id);
		message.setType(type);
		message.setSort_time(System.currentTimeMillis());
		message.setUser_task_id(user_task_id);
		message.setCreate_time(new Date());
		try{
			String key = getArticleMessageKey(message.getUser_id(),clientType);
			userMessageCacheDao.addToList(key, gson.toJson(message));
			userMessageCacheDao.keepLen(key, max_size);
			userMessageCacheDao.expire(key, 7 * Constants.DAY_SECONDS); //最多保持7天
		}catch(Exception e){
			
		}
		try{
			if(userArticleMessageService.addUserArticleMessage(message)){
				if(clientType == Constants.platform_android){
					pushService.notifyUserSystemPrompt(userId, message.getTask_description(),message.getClient_type());
				}else { //ios
					if (type == UserArticleMessage.MESS_TYPE_PASS){ //only push when success
						pushService.notifyUserSystemPrompt(userId, message.getTask_description(),message.getClient_type());
					}
				}
			}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}
	
	public void addTranArticleShareMessage(int userId, User friend,TransArticle article, TransArticleTask  articleTask, int amount, long user_task_id, int shareAmount, int level,int parentId){
		UserFriendMessage message = new UserFriendMessage();
		message.setId(idMarkerService.getIncreaseId());
		message.setSort_time(System.currentTimeMillis());
		message.setAmount(shareAmount);
		message.setApp_name(articleTask.getName());
		message.setApp_icon(article.getImg());
		message.setCreate_time(new Date());
		message.setFriend_amount(amount);
		message.setFriend_avater(friend.getAvatar());
		message.setFriend_id(friend.getId());
		message.setFriend_name(friend.getName());
		message.setFriend_task_id(user_task_id);
		message.setUser_id(userId);
		message.setFriend_level(level);
		message.setMiddle_friend_id(parentId);
		message.setSource(UserFriendMessage.SOURCE_TRAN_ARTICLE);
		if(saveFriendMessage(message)){
			 //pushService.notifyTranArticleFriendPrompt(userId, article.getName(), amount, user_task_id, shareAmount, level);
		}
	}
	
	public void addFirstTranArticleMessage(int user_id,int task_id,long user_task_id,int client_type,int amount,int view_num,String task_name,Date expired_time,int type,String amount_des){
		UserArticleMessage message = new UserArticleMessage();
		message.setSort_time(System.currentTimeMillis());
		message.setUser_id(user_id);
		message.setAmount(amount);
		message.setTask_id(task_id);
		message.setView_num(view_num);
		message.setTask_name(task_name);
		message.setExpired_time(expired_time);
		message.setType(type);
		message.setTask_description("您已成功分享任务<" + task_name + ">待过期时间-" + DateUtil.getBeforeDateTimeShow(expired_time) + "您的" + view_num + "个不同好友查看后,系统奖励您" + amount_des + "元");
		message.setClient_type(client_type);
		message.setUser_task_id(user_task_id);
		message.setAmount_des(amount_des);
		message.setCreate_time(new Date());
		try{
			String key = getArticleMessageKey(message.getUser_id(),client_type);
			userMessageCacheDao.addToList(key, gson.toJson(message));
			userMessageCacheDao.keepLen(key, max_size);
			userMessageCacheDao.expire(key, 7 * Constants.DAY_SECONDS); //最多保持7天
		}catch(Exception e){
			
		}
		if(userArticleMessageService.addUserArticleMessage(message)){
			//pushService.notifyTranArticlePrompt(user_id, message.getTask_description(), client_type);
		}
	}
	public int getArticleMessageCount(int userid,int client_type) {
		
		if(useRedis){
			try{
				String key = getArticleMessageKey(userid,client_type);
				return (int)userMessageCacheDao.getListLen(key);
			}catch(Exception e){
				logger.error("UserMessageService getAppMessageCount error!", e);
			}
		}
		return userArticleMessageService.getCount(userid,client_type);
	}
	
	public List<UserArticleMessage> getArticleMessageList(int userid, long lastId, int size,int client_type){
		int end = max_size;
		if(lastId <= 0){
			end = size;
		}
		if(lastId <= 0){
			lastId = Long.MAX_VALUE;
		}
		if(useRedis){
			boolean error = false;
			try{
				String key = getArticleMessageKey(userid,client_type);
				List<String> ls = userMessageCacheDao.getList(key, 0, end);
				List<UserArticleMessage> messages = new ArrayList<UserArticleMessage>(size);
				if(ls != null && ls.size() > 0){
					for(String s : ls){
						UserArticleMessage message = gson.fromJson(s, UserArticleMessage.class);
						long sort = message.getSort_time();
						if(sort <= 0)
							sort = message.getId();
						if(sort < lastId && messages.size() < size){
							messages.add(message);
						}
					}
				}
				return messages;
			}catch(Exception e){
				logger.error("UserMessageService getArticleMessageList error!", e);
				error = true;
			}
		}
		return userArticleMessageService.getMessagesByUserId(userid, lastId, size,client_type);
	}
	
	/**
	 * 如果lastId为0 取最新的
	 * @param userid
	 * @param lastId
	 * @return
	 */
	public Map<String, Object> getArticleMessageNotReadCount(int userid,long lastId,int client_type){
		Map<String, Object> map = new HashMap<String, Object>();
		int end = max_size;
		if(lastId <= 0){
			end = 1;
		}
		UserArticleMessage message = null;
		int total = 0;
		if(useRedis){
			try{
				String key = getArticleMessageKey(userid, client_type);
				List<String> ls = userMessageCacheDao.getList(key, 0, end);
				if(ls != null && ls.size() > 0){
					message = gson.fromJson(ls.get(0), UserArticleMessage.class);
					map.put("articleMessage", message);
					
					if(lastId > 0){
						for(String s : ls){
							message = gson.fromJson(s, UserArticleMessage.class);
							long sort = message.getSort_time();
							if(sort <= 0)
								sort = message.getId();
							if(sort <= lastId ){
								break;
							}
							total ++;
						}
					}
				}
				map.put("total", total);
				return map;
			}catch(Exception e){
				logger.error("UserMessageService getArticleMessageNotReadCount error!", e);
			}
		}
		//去数据库查询 没有再加一层else 
		if(lastId > 0){
			total = userArticleMessageService.getCountByLastId(userid, lastId,client_type);
		}
		message = userArticleMessageService.getLastArticleMessageByUserId(userid,client_type);
		map.put("total", total);
		map.put("articleMessage", message);
		
		return map;
	}

	/**
	 * 获取用户最新的文章消息
	 * @param userid
	 * @return
	 */
	public UserArticleMessage getUserLastArticleMessage(int userid){
		UserArticleMessage articleMessage = null;
		String key = getArticleMessageKey(userid, Constants.platform_ios);
		try {
			List<String> ls = userMessageCacheDao.getList(key, 0, 1);
			if (ls != null && ls.size() > 0){
				articleMessage = gson.fromJson(ls.get(0), UserArticleMessage.class);
			}else {
				articleMessage = userArticleMessageService.getLastArticleMessageByUserId(userid, Constants.platform_ios);
			}

		} catch (Exception e) {
			logger.error("UserMessageService getUserLastArticleMessage error!", e);
		}
		return articleMessage;
	}
	
//	public void addIntegarShareMessage(User friend,int userId,String appName,int amount,int shareAmount,int level,int messageSource){
//		UserFriendMessage message = new UserFriendMessage();
//		message.setId(idMarkerService.getIncreaseId());
//		message.setSort_time(System.currentTimeMillis());
//		message.setAmount(shareAmount);
//		message.setApp_name(appName);
//		message.setCreate_time(new Date());
//		message.setFriend_amount(amount);
//		message.setFriend_avater(friend.getAvatar());
//		message.setFriend_id(friend.getId());
//		message.setFriend_name(friend.getName());
//		message.setUser_id(userId);
//		message.setFriend_level(level);
//		message.setSource(messageSource);
//		if(saveFriendMessage(message)){
//			 pushService.notifyIntergalFriendPrompt(userId,friend.getId(),appName, amount, shareAmount, level,messageSource);
//		}
//	}
	
	public void addIntegarShareMessage(User friend,int userId,String appName,int amount,int shareAmount,int level,int messageType,int clientType){
		UserSystemMessage message = makeDefaultSystemMessage();
		message.setUser_id(userId);
		String title = "恭喜您获得"+shareAmount+"金币分成！";
		message.setTitle(title);
		message.setType(messageType);
		StringBuilder sb = new StringBuilder();
		sb.append("恭喜您的徒弟 ").append(friend.getName()).append(" 完成");
		if(messageType == UserSystemMessage.TYPE_INTEGAL_SHARE_QIANDAO){
			sb.append("签到任务");
		}else if(clientType == Constants.platform_android){
			sb.append("快速任务");
		}else{
			sb.append("赚金币任务");
		}
		sb.append("，给您分成").append(shareAmount).append("金币,请在我的金币中查看或兑换");
		
		message.setContent(sb.toString());
		message.setDescription(message.getContent());
		message.setPush_client_type(UserSystemMessage.PUSH_CLIENT_TYPE_ALL);
		try{
		if(saveUserSystemMessage(message)){
			pushService.notifyUserSystemPrompt(userId, title,message.getPush_client_type());
		}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}

	/**
	 * 一元夺宝成功发通知
	 * @param user
	 * @param productName
	 */
	public void addDuobaoSuccessMessage(User user, String productName){
		UserSystemMessage message = makeDefaultSystemMessage();
		message.setUser_id(user.getId());
		message.setTitle("恭喜您一元夺宝成功，获得奖品“" + productName + "”");
		message.setContent("恭喜您一元夺宝成功，获得奖品“"+ productName +"”请您3日内至一元夺宝个人中心-夺宝记录确认收货地址");
		message.setType(UserSystemMessage.TYPE_DUOBAO_SUCCESS);
		message.setPush_client_type(UserSystemMessage.PUSH_CLIENT_TYPE_ALL);
		message.setDescription(message.getContent());

		try{
			if(saveUserSystemMessage(message)){
				pushService.notifyUserSystemPrompt(user.getId(), message.getTitle(), message.getPush_client_type());
			}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}

	
	public void notifyInvitePreFriend(int userId,String taskName,int count,int amount,int clientType){
			UserSystemMessage message = makeDefaultSystemMessage();
			message.setUser_id(userId);
			String title = "恭喜您参加邀请五人的新手任务,已邀请"+count+"人";
			if(count == 5){
				title = "恭喜您完成邀请五人的新手任务,已邀请"+count+"人";
			}
			message.setTitle(title);
			message.setType(UserSystemMessage.TYPE_BEGINNER_TASK);
			message.setContent(title + ",奖励"+ MoneyUtils.fen2yuanS(amount)+ "元");
			message.setDescription(message.getContent());
			message.setPush_client_type(clientType);
			try{
			if(saveUserSystemMessage(message)){
				//pushService.notifyUserSystemPrompt(userId, title,clientType);
			}
			}catch(Exception e){
				logger.error("UserMessageservice error!", e);
			}
	}
	
	public void notifyPreTwoAppTask(int userId,int mount,int clientType,int count,boolean isNewUser){
		UserSystemMessage message = makeDefaultSystemMessage();
		message.setUser_id(userId);
		String title = null;
		if(count==2||isNewUser){
			title = "恭喜您完成“限时任务”的新手任务";
		}else{
			title = "谢谢您参加“限时任务”的新手任务";
			message.setTitle(title+",已完成"+count+"个");
		}
		message.setType(UserSystemMessage.TYPE_BEGINNER_TASK);
		message.setContent(title + ",奖励"+ MoneyUtils.fen2yuanS(mount)+ "元");
		message.setDescription(message.getContent());
		message.setPush_client_type(clientType);
		try{
		if(saveUserSystemMessage(message)){
			//pushService.notifyUserSystemPrompt(userId, title,clientType);
		}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}
	public void addReissueIntegalMessage(int userId, int amount,String reason,int clientType) {
		UserSystemMessage message = makeDefaultSystemMessage();
		message.setUser_id(userId);
		String title = reason;
		message.setTitle(title);
		message.setType(UserSystemMessage.TYPE_BEGINNER_TASK);
		message.setContent(title + ",给您发放"+amount+ "金币");
		message.setDescription(message.getContent());
		message.setPush_client_type(clientType);
		try{
		if(saveUserSystemMessage(message)){
			//pushService.notifyUserSystemPrompt(userId, title,clientType);
		}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}
	
	public void addBeginnerTranArticleMessage(int userId,int amount,int clientType){
		UserSystemMessage message = makeDefaultSystemMessage();
		message.setUser_id(userId);
		String title = new StringBuilder(20).append("恭喜您完成“转发“的新手任务").append("系统奖励您")
				.append(MoneyUtils.fen2yuanS(amount)).append("元").toString();
		message.setTitle(title);
		message.setType(UserSystemMessage.TYPE_BEGINNER_TASK);
		message.setContent(title);
		message.setDescription(message.getContent());
		message.setPush_client_type(clientType);
		try{
		if(saveUserSystemMessage(message)){
			//pushService.notifyUserSystemPrompt(userId, title,clientType);
		}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}
	
	public void addNewUserRewardMessage(int userId,int amount,int clientType,User invitor){
		UserSystemMessage message = makeDefaultSystemMessage();
		message.setUser_id(userId);
		StringBuilder sb = new StringBuilder(20);
		sb.append("恭喜您");
		if(invitor != null){
			sb.append("通过“").append(invitor.getName()).append("”邀请");
		}
		
		sb.append(",成为我们的新用户");
		message.setTitle( sb.toString());
		sb.append(",系统奖励您").append(MoneyUtils.fen2yuanS(amount)).append("元");
		message.setType(UserSystemMessage.TYPE_BEGINNER_TASK);
		message.setContent(sb.toString());
		message.setDescription(message.getContent());
		message.setPush_client_type(clientType);
		try{
		if(saveUserSystemMessage(message)){
			//pushService.notifyUserSystemPrompt(userId, message.getContent(),clientType);
		}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}


	/**
	 * 获取应用试用消息的内容
	 * @param appMessage
	 * @return
	 */
	public static String getAppMessageContent(UserAppMessage appMessage){
		switch (appMessage.getStatus()) {
			case 10:
				return "您刚刚开启了" + appMessage.getApp_name() + "任务，在1小时内完成可获得" + MoneyUtils.fen2yuanS(appMessage.getAmount()) + "元奖励！";
			case 11:
				return appMessage.getApp_name() +  "已经下载成功，请打开试用";
			case 12:
				return appMessage.getApp_name() +  "已试用完成，请等待奖励发放！";
			case 13:
				return appMessage.getApp_name() + "试用奖励" + MoneyUtils.fen2yuanS(appMessage.getAmount())  + "元已经发放，请查询您的收入";
			case 14:
				return appMessage.getApp_name() +  "试用未通过审核，如有疑问可咨询客服";
			case 15:
				return appMessage.getApp_name() + "试用任务，还有几分钟就要过期了，请尽快完成，以便顺利拿到" + MoneyUtils.fen2yuanS(appMessage.getAmount())  + "元奖励！";
			default:
				return "";
		}
	}

	/**
	 * 获取用户转发消息的内容
	 * @param articleMessage
	 * @return
	 */
	public static String getArticleMessageContent(UserArticleMessage articleMessage){
		switch (articleMessage.getType()) {
			case 10:
				return "您刚刚开启了“" + articleMessage.getTask_name() + "”任务，在任务结束前按要求完成阅读人数即获得相应奖励！";
			case 13:
				return "“" + articleMessage.getTask_name() + "”任务已结束，转发奖励" + MoneyUtils.fen2yuanS(articleMessage.getAmount())  + "元已经发放, 请查询您的收入！";
			default:
				return "";
		}
	}

	/**
	 *
	 * @param friendMessage
	 * @return
	 */
	public static String getFriendMessageContent(UserFriendMessage friendMessage){
		return "你的好友“" + friendMessage.getFriend_name()
				+ "”完成限时任务“" + friendMessage.getApp_name()+ "”，为你分成"
				+ MoneyUtils.fen2yuanS(friendMessage.getAmount()) + "元";
	}
	/***抽奖活动发消息**/
	public void notifyActivityMessage(int userId,UserDrawLog log,int clientType){
		UserSystemMessage message = makeDefaultSystemMessage();
		message.setUser_id(userId);
		String title = null;
		String content = null;
		int times = log.getDraw_times()<=0?1:log.getDraw_times();
		if(log.getType() ==UserDrawLogType.INCREMENT.getType()){
			title = "恭喜您获得"+times+"个专属红包";
			content = log.getReason() + "，您获得"+times+"个专属红包，赶紧去摇一摇吧";
		}else{
			title = "恭喜您摇一摇获得" + log.getReason();
			content = "恭喜您摇一摇获得" + log.getReason();
		}
		message.setTitle(title);
		message.setType(UserSystemMessage.TYPE_ACTIVEITY_DRAW);
		message.setContent(content);
//		message.setTarget_url("/static/html/apprentice.html");
		message.setDescription(message.getContent());
		message.setPush_client_type(clientType);
		try{
		if(saveUserSystemMessage(message)){
//			pushService.notifyUserSystemPrompt(userId, title,clientType);
		}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}

	/**
	 * 保存系统消息
	 * @param message
	 * @return
	 */
	public boolean saveUserSystemMessage(UserSystemMessage message){
		try{
			String key = getSystemMessageKey(message.getUser_id());
			userMessageCacheDao.addToList(key, gson.toJson(message));
			userMessageCacheDao.keepLen(key, max_size);
			userMessageCacheDao.expire(key, 3 * Constants.DAY_SECONDS); //最多保持3天
		}catch(Exception e){

		}
		return userSysMessageMapper.add(message) > 0;
	}
	
	public void addEffectiveInviteRewardMessage(int userId,int rank, String remark){
		UserSystemMessage message = makeDefaultSystemMessage();
		message.setUser_id(userId);
		message.setTitle("恭喜您参加财神榜，获得第“" + rank + "”名");
		message.setContent(remark);
		message.setType(UserSystemMessage.TYPE_SYS);
		message.setPush_client_type(UserSystemMessage.PUSH_CLIENT_TYPE_ALL);
		message.setDescription(message.getContent());

		try{
			if(saveUserSystemMessage(message)){
				pushService.notifyUserSystemPrompt(userId, message.getTitle(), message.getPush_client_type());
			}
		}catch(Exception e){
			logger.error("UserMessageservice error!", e);
		}
	}

}
