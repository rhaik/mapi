package com.cyhd.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.cyhd.web.common.util.AESCoder;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.LiveAccess;
import com.cyhd.common.util.StringUtil;
import com.cyhd.common.util.structure.ConcurrentFIFOCache;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.AppMapper;
import com.cyhd.service.dao.db.mapper.AppTaskMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.vo.AppTaskVo;
import com.cyhd.service.vo.UserTaskVo;


@Service
public class AppTaskService extends BaseService {

	@Resource
	private AppMapper appMapper;
	
	@Resource
	private AppTaskMapper appTaskMapper;
	
	private ConcurrentHashMap<Integer, App> cachedApps = new ConcurrentHashMap<Integer, App>();
	
	int ttl_valids_tasks = 1000 * 30;

	private LiveAccess<List<AppTask>> cachedValidTasks = new LiveAccess<List<AppTask>>(ttl_valids_tasks, null);
	private LiveAccess<List<AppTask>> cachedFutureTasks = null;

	private LiveAccess<List<AppTask>> cachedAllChannelTasks = new LiveAccess<List<AppTask>>(ttl_valids_tasks, null);

	private CacheLRULiveAccessDaoImpl<AppTask> cachedTasks = new CacheLRULiveAccessDaoImpl<AppTask>(1000 * 10, 1024);
	
	private List<AppTaskVo>  cachedSystemTasks = new ArrayList<AppTaskVo>();
	
	private volatile boolean loading = false;
	
	private final int lastValidTaskTTL = 1000 * 30;
	//最新可用的apptask
	private LiveAccess<AppTask> lastValidTask = new LiveAccess<AppTask>(lastValidTaskTTL, null);
	
	private ConcurrentFIFOCache<Integer, LiveAccess<AppTask>> quickTaskMap = new ConcurrentFIFOCache<>(20, 20);
	
	public void reloadApps(){
		if(loading)
			return;
		try{
			loading = true;
			int start = 0;
			int size = 100;
			while(true){
				List<App> apps = appMapper.getApps(start, size);
				if(apps == null || apps.size() == 0)
					break;
				start += size;
				for(App app : apps){
					cachedApps.put(app.getId(), app);
				}
			}
			
		}finally{
			loading = false;
		}
	}
	
	public App getApp(int id){
		return this.cachedApps.get(id);
	}

	/**
	 * 根据名称获取App，如果重名，则取id更大的
	 * @param name
	 * @return
	 */
	public App getAppByName(String name){
		Collection<App> apps = cachedApps.values();
		return apps.stream().filter(app -> name != null && name.equalsIgnoreCase(app.getName())).reduce((app1, app2) ->
				app1.getId() > app2.getId() ? app1 : app2).orElse(null);
	}

	/**
	 * 根据进程名获取App，如果存在相同的，则取id更大的
	 * @param processName
	 * @return
	 */
	public App getAppByProcessName(String processName){
		Collection<App> apps = cachedApps.values();
		return apps.stream().filter(app -> processName != null && processName.equalsIgnoreCase(app.getProcess_name())).reduce((app1, app2) ->
				app1.getId() > app2.getId() ? app1 : app2 ).orElse(null);
	}

	/**
	 * 根据Appstore id获取App，如果存在相同的，则返回id更大的那个App
	 * @param appStoreId
	 * @return
	 */
	public App getAppByAppStoreId(String appStoreId){
		if (appStoreId == null){
			return null;
		}
		Collection<App> apps = cachedApps.values();
		return apps.stream().filter(app -> appStoreId != null && appStoreId.equalsIgnoreCase(app.getAppstore_id())).reduce((app1, app2) ->
				app1.getId() > app2.getId() ? app1 : app2 ).orElse(null);
	}

	/**
	 * 根据当前appId，获取之前相同AppstoreId,不同appId的App
	 * @param appId
	 * @return
	 */
	public App getTwinApp(int appId){
		App newApp = getApp(appId);
		if (newApp != null){
			Collection<App> apps = cachedApps.values();
			return apps.stream().filter(app -> app.getId() != appId && app.getAppstore_id() != null && app.getAppstore_id().equals(newApp.getAppstore_id())).findAny().orElse(null);
		}
		return null;
	}

	public List<AppTask> getAppTasksByApp(int appId){
		List<AppTask> tasks = getValidTasks();
		List<AppTask> results = new ArrayList<AppTask>();
		for(AppTask appTask : tasks){
			if(appTask.getApp_id() == appId){
				results.add(appTask);
			}
		}
		return results;
	}
	
	/***
	 * 快速任务中 判断这个app是不是已经有今日任务啦
	 * @param appId
	 * @return
	 */
	public boolean getAppTasksByAppIdForQuickTask(int appId){
		if(getAppTasksByApp(appId).size() > 0){
			return true;
		}
		//去数据库判断
		Integer id = appTaskMapper.getExistTodayTask(appId);
		return id != null ;
	}

	/**
	 * 根据url协议获取App，如果存在相同的，则返回id更大的那个App
	 * @param protocol
	 * @return
	 */
	public App getAppByProtocol(String protocol){
		if(protocol == null){
			return null;
		}

		Collection<App> apps = cachedApps.values();
		return apps.stream().filter(app -> protocol != null && protocol.equalsIgnoreCase(app.getAgreement())).reduce((app1, app2) ->
				app1.getId() > app2.getId() ? app1 : app2 ).orElse(null);
	}

	/**
	 * 获取所有的App
	 * @return
	 */
	public List<App> getAllApps(){
		return new ArrayList<App>(cachedApps.values());
	}
	
	public AppTask getAppTask(int taskId){
		AppTask at = cachedTasks.get(String.valueOf(taskId));
		if(at == null){
			at = appTaskMapper.getAppTask(taskId);
			if(at != null){
				cachedTasks.set(String.valueOf(taskId), at);
			}
		}
		
		return at;
	}
	
	public List<AppTask> getValidTasks(){
		List<AppTask> tasks = cachedValidTasks.getElement();
		if(tasks == null){
			tasks = appTaskMapper.getValidTasks();
			if(tasks != null){
				Map<Integer, AppTask> appTaskMap = new LinkedHashMap<>();
				tasks.stream().forEach(appTask -> {
					AppTask currentTask = appTaskMap.get(appTask.getApp_id());
					//同一App已经有任务，则比较开始
					if (currentTask != null){
						if (appTask.getStart_time().after(currentTask.getStart_time())){ //后开始的任务优先展示
							//不是快速任务 就加入
							//if(appTask.isQuicktask() == false){
								appTaskMap.put(appTask.getApp_id(), appTask);
//							}else{
//								quickTaskMap.put(appTask.getApp_id(), appTask);
//							}
						}
					}else{
						//不是快速任务 就加入
						//if(appTask.isQuicktask() == false){
							appTaskMap.put(appTask.getApp_id(), appTask);
//						}else{
//							quickTaskMap.put(appTask.getApp_id(), appTask);
//						}
					}
				});
				tasks = new ArrayList<>(appTaskMap.values());
				cachedValidTasks = new LiveAccess<List<AppTask>>(ttl_valids_tasks, tasks);
			}
		}
		if(tasks == null){
			tasks = new ArrayList<AppTask>();
		}
		return tasks;
	}
	
	public List<UserTaskVo> getValidTaskVos(){
		return getUserTaskVos(getValidTasks());
	}


	public List<UserTaskVo> getUserTaskVos(List<AppTask> appTasks){
		List<UserTaskVo> vos = new ArrayList<UserTaskVo>();
		for(AppTask task : appTasks){
			UserTaskVo vo = new UserTaskVo();
			vo.setApp(this.getApp(task.getApp_id()));
			vo.setAppTask(task);
			vos.add(vo);
		}
		return vos;
	}


	public List<UserTaskVo> getFutrueTaskVos(){
		return getUserTaskVos(getFutureTasks());
	}

	//获取24小时内待上线的任务
	public List<AppTask> getFutureTasks(){
		List<AppTask> futureTasks = null;
		if (cachedFutureTasks == null || cachedFutureTasks.getElement() == null){
			futureTasks = appTaskMapper.getFutureTasks();
			if (futureTasks != null){
				cachedFutureTasks = new LiveAccess<>(Constants.minutes_millis, futureTasks);
			}
		}else {
			futureTasks = cachedFutureTasks.getElement();
		}

		if (futureTasks == null){
			futureTasks = new ArrayList<>();
		}
		return futureTasks;
	}

	/**
	 * 获取系统任务
	 * 
	 * @return List<AppTaskVo>
	 */
	public List<AppTaskVo> getSystemAppTasks() {
		if (cachedSystemTasks.size() == 0) {
			List<AppTask> tasks = appTaskMapper.getSystemAppTask();
			for (AppTask task : tasks) {
				AppTaskVo vo = new AppTaskVo();
				vo.setApp(this.getApp(task.getApp_id()));
				vo.setAppTask(task);
				cachedSystemTasks.add(vo);
			}
		}

		return cachedSystemTasks;
	}
	
	public List<AppTaskVo> getSystemAppTasksCopy() {
		List<AppTaskVo> temp = new ArrayList<AppTaskVo>();
		
		List<AppTaskVo> ls = getSystemAppTasks();
		if(ls != null)
			temp.addAll(ls);
		return temp;
	}
	/***邀请任务（新手任务）*/
	public AppTaskVo getSystemInviteTask(){
		return getSystemTask(AppTask.SYS_INVITE_TASK);
	}
	//分享任务（新手任务）
	public AppTaskVo getSystemShareTask(){
		return getSystemTask(AppTask.SYS_SHARE_TASK);
	}
	/***试用一个app任务（新手任务）*/
	public AppTaskVo getSystemAppTryTask(){
		return getSystemTask(AppTask.SYS_APP_TASK);
	}
//	/***试用两个个app任务（新手任务）*/
//	public AppTaskVo getSystemPreTwoAppTask(){
//		return getSystemTask(4);
//	} 
//	/**邀请好友 -五个*/
//	public AppTaskVo getSystemInviteFiveFriendAppTask(){
//		return getSystemTask(5);
//	}

	/**
	 * 获取钥匙版入口的任务
	 * @return
	 */
	public AppTaskVo getSystemYaoshiTask() {
		return getSystemTask(AppTask.SYS_YAOSHI_TASK);
	}

	private AppTaskVo getSystemTask(int taskId){
		List<AppTaskVo> tasks  = getSystemAppTasks();
		for(AppTaskVo task : tasks){
			if(task.getAppTask().getId() == taskId){
				return task;
			}
		}
		return null;
	}

	/**
	 * 用户添加任务成功，则处理任务相关信息，给总接收任务数+1，当前接收数+1
	 * @param taskId
	 */
	public boolean onUserReceiveTask(int taskId) {
		int i = appTaskMapper.updateReceiveNum(taskId);
		if(i > 0){
			this.cachedTasks.remove(String.valueOf(taskId));
		}
		return i > 0;
	}

	/**
	 * 用户添加任务成功，则处理任务相关信息，给总接收任务数+1，当前接收数+1
	 * @param taskId
	 */
	public boolean onUserAbortTask(int taskId) {
		int i = appTaskMapper.reduceReceiveNum(taskId);
		if(i > 0){
			this.cachedTasks.remove(String.valueOf(taskId));
		}
		return i > 0;
	}


	/**
	 * 用户激活任务数加1
	 * @param taskId
	 * @return
	 */
	public boolean onActiveTask(int taskId){
		return appTaskMapper.updateActiveNum(taskId) > 0;
	}
	public boolean onTaskFinished(int taskId){
		int i = appTaskMapper.updateCompleteNum(taskId);
		if(i > 0){
			//this.cachedTasks.remove(String.valueOf(taskId));
		}
		return i > 0;
	}
	/**
	 * 獲取最新的可用的
	 * @return
	 */
	public AppTask getLastAppTask(){
		AppTask  task = lastValidTask.getElement();
		
		if(task== null){
			task = appTaskMapper.getLastValidTask();
			lastValidTask = new LiveAccess<AppTask>(lastValidTaskTTL, task);
		}
		
		return task;
	}
	
	/**
	 * 根据应用id和渠道id获取当前可用的任务
	 * @return
	 */
	public AppTask getTaskByAppIdAndChannelId(int appId, int channelId){
		String key = "CHANNEL_APP_TASK_" + appId + "_" + channelId;
		AppTask  task =  cachedTasks.get(key);
		if(task == null) {
			task = appTaskMapper.getTaskByAppIdAndChannelId(appId, channelId);
			cachedTasks.set(key, task);
		}
		return task;
	}

	/**
	 * 网页版对app的协议和bundle id进行加密
	 * @param app
	 * @param userIdentity
	 * @return
	 */
	public String getEncryptedAppInfo(App app, int userIdentity){
		String encrypted = "";
		String data = app.getAgreement() + "&" + app.getBundle_id();

		try {
			encrypted = AESCoder.encrypt(data, ("mzdqweb2015" + userIdentity).substring(0, 16));
		} catch (Exception e) {
			logger.error("app info encrypt error", e);
		}
		return encrypted;
	}
	
	public App getAppByBundleID(String bunleID){
		if(StringUtil.isBlank(bunleID)){
			return null;
		}
		App rtvApp =  cachedApps.values().stream().filter(
				app -> bunleID.equalsIgnoreCase(app.getBundle_id()))
				.reduce((app1, app2) ->
				app1.getId() > app2.getId() ? app1 : app2 ).orElse(null);
		if(rtvApp == null){
			rtvApp = appMapper.getAppByBundleID(bunleID);
			if(rtvApp != null){
				cachedApps.put(rtvApp.getId(), rtvApp);
			}
		}
		return rtvApp;
	}
	
	public App addAppByQuickTaskApp(App app){
		//缓存中有的话 
		App appTmp = this.getAppByBundleID(app.getBundle_id());
		if(appTmp != null){
			return app;
		}
		
		appMapper.addAppByQucikTask(app);
		if(app.getId() > 0){
			cachedApps.put(app.getId(), app);
			return app;
		}
		return null;
	}
	
	public AppTask addAppTask(AppTask appTask){
		appTaskMapper.addAppTask(appTask);
		if(appTask.getId() > 0){
			//cachedTasks.set(String.valueOf(appTask.getId()), appTask);
			quickTaskMap.remove(appTask.getApp_id());
		}
		return appTask;
	}
	
	public boolean updateAppTask(AppTask appTask){
		return appTaskMapper.updateQuickAppTask(appTask) > 0;
	}
	
	public boolean updateQuickTaskInvalid(int task_id){
		return appTaskMapper.updateQuickTaskInvalid(task_id) > 0;
	}
	
	public boolean addAppCurrentTaskNumByQuickTask(int taskId,int appId,int num){
		boolean flag = appTaskMapper.updateCurrTaskNum(taskId, num) > 0;
		if(flag){
			//remove cache
			quickTaskMap.remove(appId);
		}
		return flag;
	}
	
	public AppTask getQuickTask(int app_id,String adid){
		Date currentDate =  GenerateDateUtil.getCurrentDate();
		LiveAccess<AppTask> appTaskLive = quickTaskMap.get(app_id);
		AppTask appTask =appTaskLive == null? null:appTaskLive.getElement();
		
		if(appTask != null && (appTask.getStart_time().after(currentDate) 
				|| appTask.getEnd_time().before(currentDate))){
			appTask = null;
			quickTaskMap.remove(app_id);
		}
		if(appTask == null || adid.equals(appTask.getAd_id()) == false){
			//获得任务 这个任务是今天有效 还要是第三方快速任务 
			appTask = appTaskMapper.getQuickTaskByAppId(app_id, GenerateDateUtil.getCurrentDate(),adid);
			if(appTask != null){
				//放58秒
				quickTaskMap.put(app_id, new LiveAccess<>(Constants.minutes_millis-2000, appTask));
			}
		}
		
		return appTask;
	}
	
	public boolean updateAppProcess(String process,String size,int appid){
		boolean flag =  appMapper.updateProcessName(process, size, appid) > 0;
		if(flag){
			cachedApps.remove(appid);
		}
		return flag;
	}
	
	public List<AppTask> getDoingAppTaskList(){
		return appTaskMapper.getQuickDoingAppTask();
	}
	
	public List<AppTask> getAppQuickTaskByChannel(int channel){
		List<AppTask> appTaskList = appTaskMapper.getQuickTaskTasks(channel);
		if(appTaskList != null && !appTaskList.isEmpty()){
			Map<Integer, AppTask> appTaskMap = new LinkedHashMap<>();
			appTaskList.stream().forEach(appTask -> {
				AppTask currentTask = appTaskMap.get(appTask.getApp_id());
				//同一App已经有任务，则比较开始
				if (currentTask != null){
					if (appTask.getStart_time().after(currentTask.getStart_time()) || appTask.getState() == Constants.ESTATE_Y){ //后开始的任务优先展示
						appTaskMap.put(appTask.getApp_id(), appTask);
					}
				}else{
					appTaskMap.put(appTask.getApp_id(), appTask);
				}
			});
			return new ArrayList<>(appTaskMap.values());
			}
		return null;
	}

	/**
	 * 获取所有的分发任务
	 * @return
	 */
	public List<AppTask> getAllValidChannelTasks(){
		List<AppTask> allTasks = cachedAllChannelTasks.getElement();
		if (allTasks == null){
			allTasks = appTaskMapper.getValidChannelTasks();
			cachedAllChannelTasks = new LiveAccess<>(ttl_valids_tasks, allTasks);
		}

		return allTasks;
	}

	/**
	 * 获取当前渠道所有的分发任务
	 * @param channelId
	 * @return
	 */
	public List<AppTask> getChannelTasks(int channelId){
		return getAllValidChannelTasks().stream().filter(appTask -> appTask.getDistribution_id() == channelId).collect(Collectors.toList());
	}
}
