package com.cyhd.service.channelQuickTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.LiveAccess;
import com.cyhd.common.util.MD5Util;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppChannel;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.vo.AppTaskChannelVo;
import com.cyhd.service.vo.UserTaskVo;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("dianRuQuickTaskService")
public class DianRuQuickTaskService extends IQuickTaskService{

	private Map<String, AppTask> dianRuTaskCache = new ConcurrentHashMap<>();
	//存放半分钟
	int cacheTTl =  Constants.minutes_millis/2;
	private LiveAccess<List<AppTask>> cachedDianRuTasks = new LiveAccess<List<AppTask>>(cacheTTl, null);
	
	@Override
	public int getAppChannelID() {
		return AppChannel.CHANNEL_QUICK_DIANRU;
	}	
	
	@Override
	public String getAdidKey() {
		return "adid";
	}
	
	private List<AppTask> getAppTask(){
		List<AppTask> appTaskList = cachedDianRuTasks.getElement();
		if(appTaskList == null ){
			appTaskList = appTaskService.getAppQuickTaskByChannel(getAppChannelID());
			if(appTaskList != null){
				cachedDianRuTasks = new LiveAccess<List<AppTask>>(cacheTTl, appTaskList);
			}
		}
		return appTaskList;
	}
	
	public  List<UserTaskVo> getAllTaskApp( ClientInfo clientInfo,User user, Map<String, String > extraParams){
		if(GlobalConfig.isApiServer){
			List<AppTask> appTaskList = getAppTask();
			if(appTaskList != null && !appTaskList.isEmpty()){
				List<UserTaskVo> userTaskVOList = new ArrayList<>();
				UserTaskVo userTaskVO = null;
				for(AppTask appTask:appTaskList){
					userTaskVO = new UserTaskVo();
					userTaskVO.setApp(appTaskService.getApp(appTask.getApp_id()));
					userTaskVO.setAppTask(appTask);
					userTaskVO.setUserTask( userTaskService.getUserTaskByAppId(user.getId(), appTask.getApp_id()));
					userTaskVOList.add(userTaskVO);
				}
				return userTaskVOList;
			}
		}else if(GlobalConfig.runJob){
			//从有效任务中 找到是该渠道的快速任务
			//if(dianRuTaskCache.isEmpty()){
			List<AppTask> appTaskList = getAppTask();
			if(appTaskList != null && !appTaskList.isEmpty()){
				appTaskList.stream().forEach((AppTask appTask) ->{
					if(appTask.getIs_quick_task() == getAppChannelID()){
						App app = appTaskService.getApp(appTask.getApp_id());
						dianRuTaskCache.put(app.getAppstore_id(), appTask);
					}
				});
			}
//			}
			assembly(clientInfo, user);			
		}
		
		return null;
	}
	
	private void assembly(ClientInfo clientInfo,User user){
		JSONArray allAppTable = getAppTasks(clientInfo);
		JSONObject json = null;
		Map<String,UserTaskVo> assemblyVoList = new HashMap<>();
		if(allAppTable != null){
			for(int i = 0; i < allAppTable.size(); i++){
				try{
					json = allAppTable.getJSONObject(i);
					int price = json.optInt("price", 0);
					int pos = json.optInt("aso_pos");
					logger.info("appName:{},price:{},pos:{}",json.optString("title",""),price,pos);
					if(price < QuickChannelConfig.MIN_DINARU_PRICE || price  >= QuickChannelConfig.MAX_DIANRU_PRICE){
						logger.info("{} 积分的不接",QuickChannelConfig.MIN_DINARU_PRICE);
						continue;
					}else if(pos >= 100){
						logger.info("排名大于:{}的不接",100);
						continue;
					}
					UserTaskVo userTaskVo = assemblyUserTaskVO(json,  json.getString(getAdidKey()), user);
					if(userTaskVo != null && userTaskVo.getApp() != null && userTaskVo.getAppTask() != null){
						assemblyVoList.put(userTaskVo.getApp().getAppstore_id(),userTaskVo);
					}
				}catch(Exception e){
					logger.error("组装UsersTask",e);
				}
			}
		}
		//缓存中有的 组装中没有的标记为 无效任务
		//组装中有的 缓存中没有 加到缓存中
		Set<String> cacheKeys = new HashSet<String>();
		if( !dianRuTaskCache.isEmpty()){
			dianRuTaskCache.entrySet().stream().forEach((Entry<String,AppTask> entey) -> {
				cacheKeys.add(entey.getKey());
			});
		}
		Set<String> assemplyKyes = assemblyVoList.keySet();
		AppTask appTask = null;
		logger.info("组装中的itunes_ID:{},接口中的itunes_Id:{}",cacheKeys,assemplyKyes);
		for(String cacheKey:cacheKeys){
			if(!assemplyKyes.contains(cacheKey)){
				//dianRuTaskCache.remove(cacheKey);
				//修改任务数 没有取到任务 那就证明任务下线啦 修改任务数
				try{
					appTask = dianRuTaskCache.get(cacheKey);
					appTaskService.updateQuickTaskInvalid(appTask.getId());
					logger.info("修改任务为无效name:{},keywords:{},task_id:{}",appTask.getName(),appTask.getKeywords(),appTask.getId());
				}catch(Exception e){
					logger.error("修改任务无效出错,cacheKey:{}",cacheKey,e);
					continue;
				}
			}
		}
		
		if(!assemplyKyes.isEmpty() ){
			for(String assemplyKey:assemplyKyes){
				if(!cacheKeys.contains(assemplyKey)){
					dianRuTaskCache.put(assemplyKey, assemblyVoList.get(assemplyKey).getAppTask());
				}
			}
		}
}
	protected  UserTaskVo assemblyUserTaskVO(JSONObject object,String ad_id,User user){
		App srcApp  = assemblyApp(object);
		List<String> filterAppList = getFilterAppIdList();
		if(filterAppList != null && filterAppList.isEmpty() == false){
			if(filterAppList.contains(srcApp.getAppstore_id())){
				logger.info("app:{} is filter,",srcApp.getName()); 
				return null;
			}
		}
		
		AppTask appTask = null;
		//判断这个存在不 有时候 app和apptask存在的 但是adid变化 导致任务也变化？ 现在默认都不变 
		App app  = appTaskService.getAppByBundleID(srcApp.getBundle_id());
		boolean isNewApp = app==null;
		if(isNewApp){
			//设置app为已审核 没有创建人
			srcApp.setAuditor(1);
			srcApp.setAudit_time(GenerateDateUtil.getCurrentDate());
			srcApp.setCreatetime(srcApp.getAudit_time());
			srcApp.setStatus(1);
			
			appTaskService.addAppByQuickTaskApp(srcApp);
			logger.info("app:{} is new App!!",srcApp.getName());
			app = srcApp;
		}else{
			//判断app的版本是不是有变化导致进程等变化 
			if(srcApp.getProcess_name().equals(app.getProcess_name()) == false){
				app.setProcess_name(srcApp.getProcess_name());
				appTaskService.updateAppProcess(srcApp.getProcess_name(), srcApp.getDownload_size(), app.getId());
			}
		}
		//组装出来的appTask 需判断关键词 剩余数修改没  
		AppTask assemblyTask = assemblyAppTask(app, object);
		UserTaskVo userTaskVo = new UserTaskVo();
		userTaskVo.setApp(app);
		
		boolean isDirectStartTask = isNewApp;
		
		if(isNewApp == false){
			// 首先去这个adid的 再取今天的任务 中有没有这个app任务 没有的话 上线
			appTask = appTaskService.getQuickTask(app.getId(),ad_id);
			if(appTask == null){
				isDirectStartTask = ! appTaskService.getAppTasksByAppIdForQuickTask(app.getId());
			}
		}
		
		if(isDirectStartTask){
			//直接上任务
			logger.info("直接上任务，app:{},keyword:{},to_user_amount:{}",app.getName(),assemblyTask.getKeywords(),assemblyTask.getAmount());
			assemblyTask.setState(Constants.ESTATE_Y);
		}
		
		if(appTask == null){
			logger.info("获取到的任务为null，app:{},keyword:{},to_user_amount:{}",app.getName(),assemblyTask.getKeywords(),assemblyTask.getAmount());
			//此时还没有任务 所以任务也就没有审核 所以没必要加到用户vo里面去
			appTaskService.addAppTask(assemblyTask);
			createChannel(ad_id, assemblyTask.getId(), assemblyTask.isDirectReward()?1:0);
			if(isNewApp ){
				userTaskVo.setAppTask(assemblyTask);
			}
			
		}else if(appTask.getState() == Constants.ESTATE_N ){
			logger.info("任务无效:app:{},appTask:{}",app.getName(),appTask.getId());
			//系统中存在的任务是无效的 就不用管
			//not do it
			return null;
		}
		//app不变 变的是app任务中的任务剩余数、关键词等
		//看看关键词是不是变化 剩余数变化没
		else if(!appTask.getKeywords().equals(assemblyTask.getKeywords())
				||((appTask.getAmount() != assemblyTask.getAmount()) )
				|| !appTask.isHasLeftTasks()){
			logger.info("app有变化，name:{},src_keyword:{},now_keyword:{},src_rank:{},now_rank:{},src_amount:{},now_amount:{},剩余:{}",
					app.getName(),appTask.getKeywords(),assemblyTask.getKeywords(),
					appTask.getCurrent_rank(),assemblyTask.getCurrent_rank()
					,appTask.getAmount(),assemblyTask.getAmount(),appTask.getLeftTasks());
			//判断任务数
			assemblyTask.setCurrent_task(assemblyTask.getCurrent_task()+appTask.getReceived_task());
			assemblyTask.setReceived_task(appTask.getReceived_task());
			assemblyTask.setId(appTask.getId());
			appTaskService.updateAppTask(assemblyTask);
			userTaskVo.setAppTask(assemblyTask);
			
		}else if(appTask.getAd_id().equals(assemblyTask.getAd_id()) == false){
			//宝宝是懵逼的 假如adid变化 我怎么办 ？要不来个冗余字段 adid加在task中？还是先把adid也加在appTask
			//把原来的置为无效  新增一个
			logger.error("app的adid有变化,app:{},src_adid:{},assembly_adid:{}",app.getName(),appTask.getAd_id(),assemblyTask.getAd_id());
			appTaskService.addAppTask(assemblyTask);
			createChannel(assemblyTask.getAd_id(), assemblyTask.getId(), assemblyTask.isDirectReward()?1:0);
			userTaskVo.setAppTask(assemblyTask);
		}else{
			userTaskVo.setAppTask(appTask);
		}
		return userTaskVo;
		//dianRuTaskCache.put(app.getAppstore_id(), userTaskVo);
	}

	public JSONArray getAppTasks(ClientInfo clientInfo) {
		StringBuilder querySB = new StringBuilder(320);
		querySB.append("http://api.mobile.dianru.com/ads_fast_api/show");
		querySB.append("?source=").append(getAppChannel().getChannel_id());
		querySB.append("&device=").append(clientInfo.getIOSDeviceName());
		querySB.append("&osver=").append(clientInfo.getOSVersion());
		querySB.append("&idfa=").append(clientInfo.getIdfa());
		try{
			String queryURL = querySB.toString();
			logger.info("请求点入快速任务列表开始;idfa:{},osVersion:{}",clientInfo.getIdfa(),clientInfo.getOSVersion());
			String response = HttpUtil.get(queryURL, null);
			//响应数据太多 就不要打印结果集啦
			if(logger.isDebugEnabled()){
				logger.debug("请求点入快速任务列表结束;idfa:{},osVersion:{},response:{}",clientInfo.getIdfa(),clientInfo.getOSVersion(),response);
			}
			if(StringUtil.isNotBlank(response) && response.length() > 10){
				JSONObject json = JSONObject.fromObject(response);
				if(json != null){
					return json.getJSONArray("table");
				}
			}
		}catch(Exception e){
			logger.info("请求点入快速任务列表异常,idfa:{},osVersion:{},cause by:{}",clientInfo.getIdfa(),clientInfo.getOSVersion(),e);
		}
		return null;
	}
	public boolean doClick(ClientInfo clientInfo, User user, App app,AppTask appTask,AppTaskChannelVo appTaskChannelVo, Map<String, String> extraParams) {
		StringBuilder signSB = new StringBuilder(160);
		signSB.append("adid=").append(appTaskChannelVo.getAppTaskChannel().getThird_id());
		signSB.append("&device=").append(clientInfo.getIOSDeviceName());
		signSB.append("&over=").append(clientInfo.getOSVersion());
		signSB.append("&idfa=").append(clientInfo.getIdfa());
		signSB.append("&client_ip=").append(clientInfo.getIpAddress());
		signSB.append("&time=").append(System.currentTimeMillis());
		signSB.append("&source=").append(appTaskChannelVo.getAppChannel().getChannel_id());
		
		String sign = MD5Util.getMD5(signSB.toString()+QuickChannelConfig.DIANRU_SALT);
	
		StringBuilder querySB = new StringBuilder();
		querySB.append(appTaskChannelVo.getAppChannel().getClick_url());
		querySB.append('?').append(signSB);
		querySB.append("&checksum=").append(sign);
		
		String queryURL = querySB.toString();
		try{
			logger.info("请求点入点击开始,idfa:{},app:{}",clientInfo.getIdfa(),app.getName());
			String response = HttpUtil.get(queryURL, null);
			logger.info("请求点入点击结束,idfa:{},app:{},response:{}",clientInfo.getIdfa(),app.getName(),response);
			JSONObject json =JSONObject.fromObject(response);
			boolean success = json != null && json.optBoolean("success");
			//TODO 失败是不是要修改任务数？
			return success;
		}catch(Exception e){
			logger.error("请求点入点击异常,idfa:{},app:{}",clientInfo.getIdfa(),app.getName());
			logger.error("cause by:",e);
		}
		return false;
	}

	@Override
	public boolean reportTaskFinsh(ClientInfo clientInfo, User user, AppTask appTask, App app,AppTaskChannelVo appTaskChannelVo) {
		//虽然和点击点击接口是一样的 但是还是copy 各有所职
		try{
			StringBuilder signSB = new StringBuilder(160);
			signSB.append("adid=").append(appTaskChannelVo.getAppTaskChannel().getThird_id());
			signSB.append("&device=").append(clientInfo.getModel());
			signSB.append("&over=").append(clientInfo.getOSVersion());
			signSB.append("&idfa=").append(clientInfo.getIdfa());
			signSB.append("&client_ip=").append(clientInfo.getIpAddress());
			signSB.append("&time=").append(System.currentTimeMillis());
			signSB.append("&source=").append(appTaskChannelVo.getAppChannel().getChannel_id());//miaozhuan
			
			String sign = MD5Util.getMD5(signSB.toString()+QuickChannelConfig.DIANRU_SALT);
		
			StringBuilder querySB = new StringBuilder();
			querySB.append("http://api.mobile.dianru.com/ads_fast_api/callback.do");
			querySB.append('?').append(signSB);
			querySB.append("&checksum=").append(sign);
			
			String queryURL = querySB.toString();
			logger.info("上报点入开始,idfa:{},app:{}",clientInfo.getIdfa(),app.getName());
			String response = HttpUtil.get(queryURL, null, 10000);
			logger.info("上报点入结束,app:{},idfa:{},response:{}",app.getName(),clientInfo.getIdfa(),response);
			JSONObject json =JSONObject.fromObject(response);
			boolean success = json != null && json.optBoolean("success");
			//TODO 失败是不是要修改任务数？
			return success;
		}catch(Exception e){
			logger.error("上报点入异常,idfa:{},app:{},cause:",clientInfo.getIdfa(),app.getName(),e);
		}
		return false;
		
	}
	
	
	App assemblyApp(JSONObject appTaskMessage) {
		App app = new App();
		app.setAppstore_id(appTaskMessage.getString("appstoreid"));
		app.setDescription(appTaskMessage.getString("url"));
		app.setBundle_id(appTaskMessage.getString("bundleid"));
		String  processName = appTaskMessage.getString("processname");
		if(StringUtil.isBlank(processName) ){
			throw new IllegalArgumentException(app.getAppstore_id()+" process name is null");
		}
		//只支持16位的长度
		processName = processName.length()>16?processName.substring(0, 15):processName;
		
		app.setProcess_name(processName);
		app.setIcon(appTaskMessage.getString("icon"));
		app.setUrl(app.getDescription());
		app.setName(appTaskMessage.getString("title"));
		app.setDownload_size(appTaskMessage.optString("psize","1.1"));
		app.setCreatetime(GenerateDateUtil.getCurrentDate());
		return app;
	}
	
	AppTask assemblyAppTask(App app, JSONObject appTaskMessage) {
		AppTask appTask = new AppTask();
		//将任务的开始时间设置为当天的开始 结束时间和显示时间设置为当天结束 
		//那后台怎么可控的？ 如果total_task为0那就是没有上线的  哈哈 用户不可见
		appTask.setStart_time(GenerateDateUtil.getCurrentDate());
		appTask.setEnd_time(DateUtil.getTodayEndDate());
		appTask.setShow_end_time(appTask.getEnd_time());
		appTask.setCreatetime(GenerateDateUtil.getCurrentDate());
		appTask.setAmount(this.convert(appTaskMessage.optInt("price", 0)));
		appTask.setCurrent_rank(appTaskMessage.optInt("aso_pos"));
		appTask.setDuration(appTaskMessage.optInt("runtime", 120));
		appTask.setDescription(appTaskMessage.getString("text2"));
		appTask.setAd_id(appTaskMessage.getString("adid"));
		//任务总数为0 就是需要后台审核 
		appTask.setCurrent_task(appTaskMessage.optInt("remain"));
		appTask.setTotal_task(appTask.getCurrent_task());
		appTask.setDirect_reward(1);
		//结算类型
		appTask.setSettlement_method(3);
		appTask.setTask_type(0);
		appTask.setState(0);
		appTask.setIs_quick_task(getAppChannelID());
		appTask.setDirect_reward(1);
		appTask.setName(app.getName());
		appTask.setApp_id(app.getId());
		String keyword = appTaskMessage.getString("keywords");
		//关键字为空 是直接下载
		if(StringUtil.isBlank(keyword)){
			appTask.setRequire_type(AppTask.REQUIRE_TYPE_DIRECT);
			keyword = appTask.getName();
		}	
		appTask.setKeywords(keyword);
		return appTask;
	}
	
	/***
	 * 将原来的奖励转化为我们系统的
	 * //最低一块
	 * @param src_amount
	 * @return
	 */
	public int convert(int src_amount){
		//如何去掉最后一位
		int amount =(int)(src_amount*QuickChannelConfig.REWARD_RADIOS);
		if(amount < QuickChannelConfig.REWARD_MIN_AMOUNT){
			return QuickChannelConfig.REWARD_MIN_AMOUNT;
		}
		if(amount % 10 != 0){
			int inte  = amount/10;
			amount = inte*10;
		}
		//小于1.3元的都给1元
		if(amount <= 140){
			return QuickChannelConfig.REWARD_MIN_AMOUNT; 
		}
		return amount;
	}


	@Override
	public boolean distinct(ClientInfo clientInfo,User user, App app, AppTaskChannelVo vo) {
		String url = "http://api.mobile.dianru.com/channel/proxy.do?dr_adid="+vo.getAppTaskChannel().getThird_id();
		Map<String, String> parameters = new HashMap<String, String>(1);
		parameters.put("idfa_list", clientInfo.getIdfa());
		try {
			logger.info("请求点入快速任务排重开始,idfa:{},app:{}",clientInfo.getIdfa(),app.getName());
			String response = HttpUtil.postByForm(url, parameters);
			logger.info("请求点入快速任务排重结束,idfa:{},app:{},response:{}",clientInfo.getIdfa(),app.getName(),response);
			JSONObject json = JSONObject.fromObject(response);
			if(json != null){
				int status = json.optInt(clientInfo.getIdfa(),2);
//				if(status == 1){
//					try {
//						//userInstalledAppService.addPreFilteredIDFA(app.getId(), clientInfo.getIdfa());
//					} catch (Exception e) {}
//				}
				return status == 0;
			}
		} catch (Exception e) {
			logger.error("请求点入快速任务排重异常,idfa:{},app:{}",clientInfo.getIdfa(),app.getName(),e);
		}
		return false;
	}
}