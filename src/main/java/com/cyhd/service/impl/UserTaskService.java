package com.cyhd.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.AppTaskMapper;
import com.cyhd.service.dao.db.mapper.UserTaskFinishAuditMapper;
import com.cyhd.service.dao.db.mapper.UserTaskMapper;
import com.cyhd.service.dao.db.mapper.UserTaskReportMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserTask;
import com.cyhd.service.util.CacheUtil;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RedisUtil;
import com.cyhd.service.vo.UserTaskVo;
import com.cyhd.web.common.ClientInfo;


@Service
public class UserTaskService extends BaseService {

	@Resource
	private UserTaskMapper userTaskMapper;
	
	@Resource
	private AppTaskService appTaskService;
	
	@Resource
	private AppTaskMapper appTaskMapper;
	
	private CacheLRULiveAccessDaoImpl<ConcurrentHashMap<String,UserTaskVo>> cachedUserSystemTasks = new CacheLRULiveAccessDaoImpl<ConcurrentHashMap<String,UserTaskVo>>(Constants.minutes_millis * 1, 1024);
	
	@Resource(name = CacheUtil.MEMCACHED_RESOURCE)
	private CacheDao memcachedCacheDao;
	
	@Resource
	private UserTaskReportMapper userTaskReportMapper;
	
	@Resource
	private UserTaskFinishAuditMapper userTaskFinishMapper;
	
	//private FixSizeReverseList<UserTaskReport> taskReportsCacche = new FixSizeReverseList<UserTaskReport>(1000 * 200);
	
	@Resource
	private IdMakerService idMarkerService;
	
	@Resource
	private UserTaskNotifyService userTaskNotifyService;
	
	public UserTask getUserTask(int userId, int taskId) {
		return userTaskMapper.getUserTask(userId, taskId);
	}
	
	@Resource(name=RedisUtil.NAME_SELF)
	private IJedisDao userTaskCacheDao;

	@Resource
	private UserInstalledAppService userInstalledAppService;
	
	@Resource
	private UserFriendService userFriendService;
	
	//通过appid和设备号获取用户的任务
	public UserTask getUserTaskByDid(int appId, String did) {
		return userTaskMapper.getUserTaskByDid(appId, did);
	}
	
	public UserTask getUserTaskById(long id){
		return userTaskMapper.getUserTaskById(id);
	}
	
	/**
	 * 通过用户id和appId获取任务
	 * @param userId
	 * @param app_id
	 * @return
	 */
	public UserTask getUserTaskByAppId(int userId, int app_id) {
		return userTaskMapper.getUserTaskByAppId(userId, app_id);
	}
	
	
	/**
	 * 用户已完成任务日志列表
	 * 
	 * @param int userid
	 * @param int start
	 * @param int size
	 * 
	 * @return List<UserTaskVo>
	 */
	public List<UserTaskVo> getUserTasks(int userid, int start, int size){
		List<UserTask> userTasks = userTaskMapper.getUserFinishTaskLogs(userid, start, size);
		
		List<UserTaskVo> vos = new ArrayList<UserTaskVo>();
		
		String dateKey = new String();
		for(UserTask task : userTasks){
			UserTaskVo vo = new UserTaskVo();
			String temp = DateUtil.format(task.getStarttime(), "yyyyMMdd");
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
			
			vo.setUserTask(task);
			vo.setAppTask(appTaskService.getAppTask(task.getTask_id()));
			vo.setApp(appTaskService.getApp(task.getApp_id()));
			vos.add(vo);
		}
		return vos;
	}
	/**
	 * 获取试用应用总数
	 * 
	 * @param userid
	 * @return
	 */
	public int getUserTaskTotal(int userid) {
		return userTaskMapper.getUserTaskTotal(userid);
	}
	/**
	 * 获取用户所有正在进行中的任务列表
	 * @param id
	 * @return
	 */
	public List<UserTaskVo> getUserDoingTaskVos(int userId) {
		List<UserTask> userTasks = getUserDoingTasks(userId);
		
		List<UserTaskVo> vos = new ArrayList<UserTaskVo>();
		for(UserTask task : userTasks){
			UserTaskVo vo = new UserTaskVo();
			vo.setUserTask(task);
			vo.setAppTask(appTaskService.getAppTask(task.getTask_id()));
			vo.setApp(appTaskService.getApp(task.getApp_id()));
			vos.add(vo);
		}
		return vos;
	}
	
	public void removeCache(int userId){
		this.memcachedCacheDao.remove(CacheUtil.getUserTasksKey(userId));
	}
	
	public List<UserTask> getUserDoingTasks(int userId){
		List<UserTask> userTasks = (List<UserTask>)memcachedCacheDao.get(CacheUtil.getUserTasksKey(userId));
		if(userTasks == null){
			userTasks = userTaskMapper.getDoingTasks(userId);
			if(userTasks != null){
				memcachedCacheDao.set(CacheUtil.getUserTasksKey(userId), userTasks, 5 * Constants.minutes_millis);
			}
		}
		return userTasks;
	}
	
	public List<Integer> getAppIdByUserVOList(List<UserTaskVo> userTaskVO){
		if(userTaskVO != null && userTaskVO.isEmpty() == false){
			List<Integer> appIdList = new ArrayList<Integer>();
			for(UserTaskVo vo:userTaskVO){
				appIdList.add(vo.getApp().getId());
			}
			return appIdList;
		}
		return null;
	}
	
	public List<UserTask> getUserDoingAppTasks(int userId, int appId){
		 List<UserTask> userTasks = getUserDoingTasks(userId);
		 List<UserTask> userTasks2 = new ArrayList<UserTask>();
		 if(userTasks != null){
			 for(UserTask ut:userTasks){
				 if(ut.getApp_id() == appId){
					 userTasks2.add(ut);
				 }
			 }
		 }
		 return userTasks2;
	}
	
	/**
	 * 获取用户新手任务信息
	 * 
	 * @param userid
	 * @return
	 */
	public ConcurrentHashMap<String,UserTaskVo> getUserSystemAppTask(int userid) {
		String cacheKey = String.valueOf(userid);
		
		ConcurrentHashMap<String,UserTaskVo> rs = cachedUserSystemTasks.get(cacheKey);
		
		if(rs == null) {
			List<UserTask> userTasks = userTaskMapper.getSystemUserTaskByUserId(userid);
			
			ConcurrentHashMap<String,UserTaskVo> vos = new ConcurrentHashMap<String,UserTaskVo>();
			for(UserTask task : userTasks) {
				UserTaskVo vo = new UserTaskVo();
				vo.setUserTask(task);
				vo.setAppTask(appTaskService.getAppTask(task.getTask_id()));
				vo.setApp(appTaskService.getApp(task.getApp_id()));
				vos.put(String.valueOf(task.getTask_id()), vo);
			}
			
			cachedUserSystemTasks.set(cacheKey, vos);
			return vos;
		}
		return rs;
		
	}
//	public List<UserTask> getUserTasksByTaskIds(int userId, List<Integer> taskIds){
//		return userTaskMapper.getUserTasksByTaskIds(userId, taskIds);
//	}
	
	public List<UserTask> getUserTasksByAppIds(int userId, List<Integer> appIds){
		return userTaskMapper.getUserTasksByAppIds(userId, appIds);
	}
	/**
	 * 应用试用页面，排序：
	 * @param userId
	 * @param tasks
	 * @return
	 */
	public List<UserTaskVo> getTasks(User user, List<UserTaskVo> tasks){
		if(tasks == null || tasks.isEmpty()){
			tasks = new ArrayList<UserTaskVo>();
			return new ArrayList<UserTaskVo>();
		}
		
		List<Integer> appIds = new ArrayList<Integer>();
		
		for(UserTaskVo vo : tasks){
			appIds.add(vo.getAppTask().getApp_id());
		}
		
		List<UserTask> userTasks = new ArrayList<UserTask>();
		if(!appIds.isEmpty()){
			userTasks = this.getUserTasksByAppIds(user.getId(), appIds);
		}
		
		//得到用户已经安装过的app
		Set<Integer> installList = userInstalledAppService.getListByUserId(user.getId());

		List<UserTaskVo> receiveds = new ArrayList<UserTaskVo>();

		List<UserTaskVo> canReceives = new ArrayList<UserTaskVo>();
		List<UserTaskVo> others = new ArrayList<UserTaskVo>();
		//加上现在进行中的任务
		try{
			List<UserTaskVo> doingTasks = this.getUserDoingTaskVos(user.getId());
			if(doingTasks != null && !doingTasks.isEmpty()){
				//这个里面的app没有包含现在进行中的就把进行中的加到里面qu
				List<Integer> taskAppLists = this.getAppIdByUserVOList(tasks);
				
				List<UserTaskVo> tmp = new ArrayList<UserTaskVo>();
				for(int i = 0; i < doingTasks.size(); i++){
					if(! taskAppLists.contains(doingTasks.get(i).getApp().getId())){
						tmp.add(doingTasks.get(i));
					}
				}
				if(tmp != null){
					tasks.addAll(tmp);
				}
				taskAppLists = null;
				tmp = null;
			}
		}catch(Exception e){
			logger.error("将用户正在做的任务",e);
		}
		for(UserTaskVo vo : tasks){
			UserTask ut = findUserTask(user.getId(), vo.getAppTask().getApp_id(), userTasks);;
			vo.setUserTask(ut);
			
			if(vo.isCanReceive()){
				//在能接收任务中判断 用户是否安装过该app
				if(userInstalledAppService.isPreFilteredByIDFA(vo.getAppTask().getApp_id(), user.getIdfa())
						||  (installList != null && installList.contains(vo.getAppTask().getApp_id())) ){
					vo.setInstalledApp(true);
					others.add(vo);//其他 
				}else{
					canReceives.add(vo);
				}
			}else if(vo.isApping() || vo.isWaitingCallback()){
				receiveds.add(vo);
			}else{
				others.add(vo);
			}
		}

		//可接的任务，按照奖励金额/剩余数量倒序
		Collections.sort(canReceives, (t1, t2) -> {
			int num = t2.getAppTask().getAmount() - t1.getAppTask().getAmount();
			if (num == 0) {
				return t2.getAppTask().getLeftTasks() - t1.getAppTask().getLeftTasks();
			}
			return num;
		});

		//不可接得任务，正在进行中的排在前面，然后根据剩余数量倒序
		Collections.sort(others, (t1, t2) -> {
			int w1 = (t1.getAppTask().isValid()?1 : 0) * 10 + (t1.isValid()? 1: 0);
			int w2 = (t2.getAppTask().isValid()?1 : 0) * 10 + (t2.isValid()? 1: 0);

			if (w2 == w1){
				return t2.getAppTask().getLeftTasks() - t1.getAppTask().getLeftTasks();
			}else {
				return w2 - w1;
			}
		});

		receiveds.addAll(canReceives);
		receiveds.addAll(others);
		
		return receiveds;
	}
	
	/**
	 * 一个用户一个app只能玩儿一次<br/>
	 * 对于渠道任务或厂商回调的任务，只能玩一次
	 * @param userId
	 * @param appId
	 * @param userTasks
	 * @return
	 */
	private static UserTask findUserTask(int userId, int appId, List<UserTask> userTasks){
		for(UserTask ut : userTasks){
		if((ut.getUser_id() == userId && ut.getApp_id()== appId)){
				return ut;
			}
		}
		return null;
	}

	/**
	 * 同一个app，支持多次接任务，但是只能完成一次<br/>
	 * 要找出来用户当前的任务或者已经完成的任务，这样的情况下认为用户已经试用过该app
	 *
	 * @param appId
	 * @param taskId
	 * @param userTasks
	 * @return
	 */
	private static UserTask findUserNotExpiredTask(int appId, int taskId, List<UserTask> userTasks){
		return userTasks.stream()
				.filter(userTask -> appId == userTask.getApp_id() && (userTask.getTask_id() == taskId || !userTask.isExpired()) )
				.findAny().orElse(null);
	}
	
	/**
	 * 添加 新手任务 到user_task表。完成的时候，直接添加
	 * @param userId
	 * @param appTask
	 * @return
	 */
	public UserTask addSystemTask(int userId, AppTask appTask, String did,String idfa) {
		UserTask ut = getUserTaskByAppId(userId, appTask.getApp_id());
		if( ut != null){
			logger.warn("AddSystemTask error! has Exist! userid={}, taskid={}", userId, appTask.getId());
			return null;
		}
	    ut = new UserTask();
		long userTaskId = idMarkerService.getTimedId();
		Date now = new Date();
		ut.setId(userTaskId);
		ut.setDid(did);
		ut.setUser_id(userId);
		ut.setApp_id(appTask.getApp_id());
		ut.setStarttime(now);
		ut.setType(UserTask.TYPE_SYSTEM);
		ut.setStatus(UserTask.STATUS_COMPLETED);
		ut.setTask_id(appTask.getId());
		ut.setFinishtime(now);
		ut.setReward(1);
		ut.setRewardtime(now);
		ut.setExpiretime(new Date(System.currentTimeMillis() + Constants.day_millis * 10));
		ut.setIdfa(idfa);
		int i = userTaskMapper.addTask(ut);
		if(i > 0){
			removeCache(userId);
			return ut;
		}
		return null;
	}

	/**
	 * 用户接任务，添加任务记录
	 * @param userId
	 * @param appTask
	 * @param clientInfo
	 * @return
	 */
	public UserTask addTask(int userId, AppTask appTask, ClientInfo clientInfo) {
		Date now = new Date();
		UserTask ut = new UserTask();
		long userTaskId = idMarkerService.getTimedId();
		ut.setDid(clientInfo.getDid());
		ut.setId(userTaskId);
		ut.setUser_id(userId);
		ut.setApp_id(appTask.getApp_id());
		ut.setStarttime(now);
		ut.setType(UserTask.TYPE_COMMON);
		ut.setStatus(UserTask.STATUS_INIT);
		ut.setTask_id(appTask.getId());
		ut.setReward(0);
		ut.setIdfa(clientInfo.getIdfa());
		ut.setUser_ip(clientInfo.getIpAddress());
		ut.setBattery_id(clientInfo.getShortBid());
		long expiretime = Constants.TASK_EXPIRE_TIME;
		if(appTask.isQuicktask()){
			expiretime  = Constants.QUICK_TASK_EXPIRE_TIME;;
		}
		ut.setExpiretime(new Date(System.currentTimeMillis() +expiretime ));
		int i = 0;
		i = userTaskMapper.addTask(ut);
		if (i > 0) {
			removeCache(userId);
			return ut;
		}
		return null;
	}
	
	public boolean onFinishDownload(int userId, long id){
		int i = userTaskMapper.finishDownload(id, userId);
		if(i >0){
			this.removeCache(userId);
		}
		return i > 0;
	}
	
	public boolean onFinishTask(int userId, long id, boolean reward){
		int i = 0;
		if(reward)
			i = userTaskMapper.finishTaskAndReward(id, userId);
		else
			i = userTaskMapper.finishTaskAndNoReward(id, userId);
		if(i >0){
			this.removeCache(userId);
		}
		return i > 0;
	}
	public boolean onTaskReward(int userId, long taskId){
		int i = userTaskMapper.reward(taskId, userId);
		if(i >0){
			this.removeCache(userId);
		}
		return i > 0;
	}
	
	
	/**
	 * 上报处理
	 * @param u
	 * @param deviceType
	 * @param did
	 * @param content
	 */
	public void report(User u, int deviceType, String did, String content){
		
	}

	public List<UserTask> getExpireTasks(int expiredTime) {
		return userTaskMapper.getExpireTask(expiredTime);
	}

	public int updateWillExpire(long id, int user_id) {
		return userTaskMapper.updateWillExpire(id, user_id);
	}

	/**
	 * 根据idfa和appId获取用户任务，不区分任务状态
	 * @param idfa
	 * @param appId
	 * @return
	 */
	public UserTask getUserTaskByIdfaAndAppId(String idfa,int appId){
		return userTaskMapper.getUserTaskByIdfaAndAppId(idfa, appId);
	}
	
	/**
	 * 判断这个idfa是不是接过改任务，同时检查本系统用户和合作伙伴用户
	 * @param idfa
	 * @param appId
	 * @return
	 */
	public boolean isRevicedByIdfa(String idfa,int appId){
		if(userTaskNotifyService.isReceivedByPartner(idfa, appId)) {
			return true;
		}
		String idfaKey = RedisUtil.buildIDFAAppKey(idfa,appId);
		try{
			String value = userTaskCacheDao.get(idfaKey);
			if(StringUtils.isNotBlank(value)){
				return true;
			}
		}catch(Exception e){
			logger.info("get idfa value form db ,cause by:{}",e);
		}
		
		UserTask ut3 = getUserTaskByIdfaAndAppId(idfa, appId);

		//存在，并且不是已放弃的状态
		if(ut3 != null && !ut3.isAborted()){
			try {
				userTaskCacheDao.set(idfaKey, "1", Constants.MONTH_SECOND_TIME);
			} catch (Exception e) {
			}
			return true;
		}
		return false;
	}
	
	public boolean onActive(int userId, long id){
		int i = userTaskMapper.finishActive(id, userId);
		if(i >0){
			this.removeCache(userId);
		}
		return i > 0;
	}
	 
	/**
	 * 回调确认完成任务
	 * @param id
	 * @param userId
	 * @return
	 */
	public boolean confirmFinishTask(long id, int userId, boolean reward) {
		int i = 0;
		if(reward)
			i = userTaskMapper.confirmFinishTaskReward(id, userId);
		else
			i = userTaskMapper.confirmFinishTask(id, userId);
		if(i >0){
			this.removeCache(userId);
		}
		return i > 0;
	}

	/**
	 * 用户放弃任务，将过期时间设置为现在，状态设置为放弃
	 * @param utid
	 * @param userId
	 * @return
	 */
	public boolean abortTask(long utid, int userId){
		boolean flag = userTaskMapper.abortTask(userId, utid) > 0;
		if (flag){
			removeCache(userId);
		}
		return flag;
	}

	/**
	 * 重新开启已放弃的任务，修改了原先接过的任务的数据
	 * @param ut
	 * @return
	 */
	public UserTask restartTask(UserTask ut, AppTask appTask, ClientInfo clientInfo){
		Date now = new Date();
		ut.setStarttime(now);

		long expireTime = Constants.TASK_EXPIRE_TIME;
		if(appTask.isQuicktask()){
			expireTime = Constants.QUICK_TASK_EXPIRE_TIME;
		}

		ut.setExpiretime(new Date(System.currentTimeMillis() + expireTime));
		ut.setTask_id(appTask.getId());
		ut.setUser_ip(clientInfo.getIpAddress());
		ut.setStatus(UserTask.STATUS_INIT);

		boolean flag = userTaskMapper.restartTask(ut) > 0;
		if (flag){
			removeCache(ut.getUser_id());
			return ut;
		}
		return null;
	}
	 
	
	/**
	 * 设置当前任务对应的app首次打开
	 * @param userId
	 * @param id
	 * @return
	 */
	public boolean setOpened(int userId, long id){
		int i = userTaskMapper.setAppOpened(id, userId);
		if(i > 0){
			this.removeCache(userId);
		}
		return i > 0;
	}
	public int getUserFinshTaskNum(int userId){
		return userTaskMapper.countFinshTask(userId);
	}

	/**
	 *自己应该得到的 活动中增加额外收入
	 * @param apptaskAmount
	 * @param userId
	 * @return
	 */
	public int getAmountByActivity(int apptaskAmount,int userId){
		return apptaskAmount;
//		int inviteNums = userFriendService.countUserFriends(userId);
//		float shareRate = userFriendService.getExtraShareRateByAppTask(userId,inviteNums)+1;
//		return (int)(apptaskAmount*shareRate);
	}
	/****
	 * 前五个完成的任务每一个给师傅一块钱 
	 * @param apptaskAmount
	 * @param user
	 * @param invitor
	 * @return
	 */
	public int getAmountOptimizeInvite(AppTask appTask,User user,int invitor){
		float rate = appTask.getShare_rate();
		int friendAmount = (int)(rate*appTask.getAmount());
		if(user.getCreatetime().after(GlobalConfig.OPTIMIZE_INVITE_START)){
			String key = RedisUtil.buildUserFinshFiveTaskNumKey(user.getId());
			try {
				if(userTaskCacheDao.exists(key )){
					return friendAmount;
				}
			} catch (Exception e) {}
			
			int num = userTaskMapper.countFinshTask(user.getId());
			if(num <= 5){
				friendAmount= 100;
			}else{
				try {
					userTaskCacheDao.set(key, "1", Constants.MONTH_SECOND_TIME);
				} catch (Exception e) {}
			}
		}
		return friendAmount;
	}
	
	public boolean updateEarned_amount(int userId,long userTaskId,int earned_amount){
		return userTaskMapper.updateEarned_amount(userId,userTaskId, earned_amount) > 0;
	}
	
	public boolean updateReportStatus(int userId,long userTaskId){
		return  userTaskMapper.updateReportStatus(userId, userTaskId) > 0;
	}
	
	public List<UserTask> getReportUserTaskList(int appTaskId,Date start,Date end){
		return userTaskMapper.getReportTaskList(appTaskId,start,end);
	}
	
	/***
	 * 
	 * @param battery_id
	 * @param app_id
	 * @return
	 * {@link true} 存在今日
	 */
	public boolean existTodayUserTaskByBatteryIdAndAppId(String battery_id,int app_id){
		if(StringUtil.isBlank(battery_id)){
			return false;
		}
		String key = RedisUtil.buildBatteryIdAndAppIdTaskKey(battery_id, app_id);
		try {
			return userTaskCacheDao.exists(key);
		} catch (Exception e) {
			//异常怎么办
		}
		return false;
	}
	/***
	 * 将今日的app下的电池id加到缓存下
	 * @param battery_id
	 * @param app_id
	 * @return
	 */
	public boolean addTodayBatteryTaskToRedis(String battery_id,int app_id){
		if(StringUtil.isBlank(battery_id)){
			return false;
		}
		String key = RedisUtil.buildBatteryIdAndAppIdTaskKey(battery_id, app_id);
		try {
			Date now = GenerateDateUtil.getCurrentDate();
			Date todayEndDate = DateUtil.getTodayEndDate();
			int ttl = (int)((todayEndDate.getTime() - now.getTime())/1000);
			return userTaskCacheDao.set(key, "1",ttl);
		} catch (Exception e) {
			
		}
		return false;
	}
	
	public boolean removeTodayBatteryTaskToRedis(String battery_id,int app_id){
		if(StringUtil.isBlank(battery_id)){
			return false;
		}
		String key = RedisUtil.buildBatteryIdAndAppIdTaskKey(battery_id, app_id);
		try {
			return userTaskCacheDao.remove(key);
		} catch (Exception e) {
		}
		return false;
	}
	
	public boolean existBatteryTaskByAppId(String battery_id,int app_id){
		if(StringUtil.isBlank(battery_id)){
			return false;
		}
		String key = RedisUtil.buildBatteryIdAndAppIdTaskNumKey(battery_id, app_id);
		int times = 0;
		int allowTimes = Constants.BATTERY_APP_ID_TIMES;
		try {
			String data = userTaskCacheDao.get(key);
			if(StringUtil.isNotBlank(data)){
				times = Integer.parseInt(data);
			}
		} catch (Exception e) {}
		
		boolean rtv = times >= allowTimes;
		if(!rtv){
			times = userTaskMapper.countUserTaskNumByBatterIdAndAppId(battery_id, app_id);
			rtv = times >= allowTimes;
			if(rtv){
				try {
					userTaskCacheDao.set(key, String.valueOf(times),Constants.MONTH_SECOND_TIME);
				} catch (Exception e) {}
			}
		}
		return rtv; 
	}
	
}
