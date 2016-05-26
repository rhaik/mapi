package com.cyhd.service.impl;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.po.*;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RedisUtil;
import com.cyhd.service.vo.AppTaskChannelVo;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 处理客户端上报的任务监控信息
 * @author luckyee
 *
 */
@Service
public class UserTaskReportService extends BaseService {
	
	private static final Logger logger = LoggerFactory.getLogger("report");

	@Resource
	private UserTaskCalculateService userTaskCalculateService;
	
	@Resource
	private UserTaskService userTaskService;
	
	@Resource
	private UserTaskReportPersistService userTaskReportPersistService;
	
	@Resource
	private AppTaskService appTaskService;
	
	@Resource
	IdMakerService idMarkerService;
	
	@Resource
	private UserInstalledAppService userInstalledAppService;
	
	private ExecutorService executor = null;

	private int executor_size = 10;

	@Resource(name=RedisUtil.NAME_SELF)
	private IJedisDao userTaskCacheDao;
	
	@Resource
	private ChannelService channelService;

	@Resource
	private AntiCheatService antiCheatService;
	
	@Resource
	private AppChannelQuickTaskService appChannelQuickTaskService;
	
	private ExecutorService reportChannelExecutor = null;
	
	@Resource
	private UserService userService;
	
	@Resource
	private AppVendorService appVendorService;
	
	@PostConstruct
	public void init() {
		ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("report_job_thread");
				return t;
			}
		};
		if(GlobalConfig.isDeploy){
			executor_size = 50;
		}
		ThreadFactory reqortFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("report_channel_finsh.job");
				return t;
			}
		};
		executor = Executors.newFixedThreadPool(executor_size, threadFactory);
		reportChannelExecutor = Executors.newFixedThreadPool(executor_size, reqortFactory);
	}

	@PreDestroy
	public void shutdown() {
		if (executor != null)
			executor.shutdown();
	}
	
	/**
	 * 需要异步处理
	 */
	public void report(final User u, final int appDate,final  String network, final String battery_level, final String mobile_network, final String screen_brightness, final JSONArray appArray,final ClientInfo clientInfo) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				Date appReportDate = toDate(appDate);
				for(int i = 0; i < appArray.size(); i++){
					JSONObject appObject  = appArray.getJSONObject(i);
					innerReport(u, appReportDate, network, battery_level, mobile_network, screen_brightness, appObject, clientInfo);
				}
			}
		});
	}
	
	private void innerReport(User user, Date appReportDate, String network, String battery_level, String mobile_network, String screen_brightness, JSONObject appObject,ClientInfo clientInfo){
		try{
			String did = clientInfo.getDid();
			String idfa = clientInfo.getIdfa();
			int deviceType = clientInfo.getPlatform();

			int userId = user.getId();
			String processName = appObject.getString("name");  //app名
			int startDate = (int)appObject.getDouble("start_date");  //启动时间
			int status = appObject.getInt("status");  // 进程状态
			
			App app = appTaskService.getAppByProcessName(processName);
			if(app == null){
				logger.error("process report, userid={},app name={}, app is null!", userId, processName);
				return;
			}
			List<UserTask> tasks = userTaskService.getUserDoingAppTasks(userId, app.getId()); 
			if(tasks.isEmpty()){
				logger.warn("process report, userid={},app name={}, tasks is empty!", userId, processName);
				return;
			}
			Date appStartTime = toDate(startDate);
			
			logger.info(
					"start process report, userid={}, appid={}, appname={}, startdate={}, status={}, reportdate={}, network={}, battery={}, mobile_network={}, bright={}",
					userId, app.getId(), app.getName(), DateUtil.format(appStartTime), status, DateUtil.format(appReportDate), network, battery_level,
					mobile_network, screen_brightness);

			for(UserTask ut : tasks){
				if(!ut.isValid()){
					logger.warn("process report, userid={},app name={}, task={} is completed or expired!", userId, processName, ut.getId());
					userTaskReportPersistService.remove(ut.getId());
					continue;
				}
				
				logger.warn("process report, userid={},app name={}, task={}, status={}", userId, processName, ut, status);
				AppTask appTask = appTaskService.getAppTask(ut.getTask_id());
				if(appTask == null){
					logger.error("process report, userid={},app name={}, apptask={} is null!", userId, processName, ut.getTask_id());
					continue;
				}
				ut.setIdfa(idfa);

				//fix: ios中文进程名称修复，防止客户端未检测到中文进程信息
				if (status == 10 && ut.isOpened() && (processName.startsWith("NES_") || StringUtil.isContainsChinese(processName))){
					status = 1;
					appStartTime = ut.getOpen_time();
					logger.warn("ios process fix, change status to 1, processName:{}, ut:{}", processName, ut);
				}

				//用户只安装了应用，还没有打开
				if(status == 10){
					if(ut.getDownload() == 0){  //给用户一个提醒（下载完成）
						userTaskCalculateService.onFinishDownload(user, app, appTask, ut);
						//添加已下载入库
						userInstalledAppService.insert(userId, app.getId(), did, app.getAgreement());
					}
					continue;
				}
				int index = 1;
				boolean canFinish = false;
				boolean warning = false; //是否异常请求，如果异常，则需要人工审核
				StringBuffer cause = new StringBuffer();
				boolean first = false;
				int duration = 0;
				int report_gap = 0;
				Date now = new Date();
				
				UserTaskReport lastReport = userTaskReportPersistService.getLastReport(ut.getId());
				if(lastReport != null){
					index = lastReport.getReport_index()+1;
					duration = lastReport.getDuration();
					canFinish = true; //至少有两次上报才能完成任务，然后看时长是否满足
					report_gap = this.secondsGap(lastReport.getReporttime(), now);
					int newDuration = report_gap;  //如果不是第一次上报，则计算使用时长的办法，用上报时间。
					if(newDuration > 60){
//						if(newDuration > 200){
//							warning = true;
//							cause.append("上报间隔过长：").append(newDuration).append("||");
//						}
						newDuration = 60;
					}
					duration += newDuration;
				}else{
					first = true; // 第一次上报
					duration = this.secondsGap(appStartTime, appReportDate);
					if(duration > 60){   //如果第一次上报，两个时间差太多，则设置为1分钟
						duration = 60;
					}
				}
				
				UserTaskReport report = new UserTaskReport();
				report.setId(idMarkerService.getUniqRandomId());
				report.setReport_index(index);
				report.setDuration(duration);
				report.setReport_gap(report_gap);
				report.setReporttime(now);
				report.setUser_id(userId);
				report.setUser_task_id(ut.getId());
				report.setDevicetype(deviceType);
				report.setDid(did);
				
				userTaskReportPersistService.addReport(report);
				
				if(ut.getActive() == 0){
					//首先检查ip是否激活太频繁，30s内只允许一个激活
//					if(antiCheatService.isFrequentTaskIp(clientInfo.getIpAddress())){
//						continue;
//					}
//					antiCheatService.cacheIPForTTL(clientInfo.getIpAddress(), 30);

					//激活用户
					try{
						//防止重复激活
						boolean success = userTaskService.onActive(userId, ut.getId());
						if (success) {
							appTaskService.onActiveTask(appTask.getId());
							//新增快熟任务上报
							if(appTask.isQuicktask() ){
								//Map<String, String> extraParams  = new HashMap<String, String>();
								appChannelQuickTaskService.reportTaskFinsh(clientInfo, appTask, ut, user);
							}
							if(appTask.getIschannel() > 0){
								channelService.reportTaskToChannel(appTask, ut, clientInfo);
							}
						}else {
							logger.warn("on active user task not success, userid={}, appid={}, apptask={}, utid={}!", userId, ut.getApp_id(), ut.getTask_id(), ut.getId());
						}
					}catch(Exception e){
						logger.error("on active error!",e);
					}
				}
				
				if(canFinish){
					if(index <= 3){  //如果上报次数太少就完成任务，则可能有问题
						warning = false;
						cause.append("上报间隔次数太少：").append(index);
					}
					if(appTask.getDuration() <= duration){  //完成任务！！！
						//检查是否奖励台频繁，30s内只会给同一ip一个奖励
//						if (antiCheatService.isFrequentTaskIp(clientInfo.getIpAddress())){
//							logger.error("该ip奖励太频繁:{}, ut:{}", clientInfo.getIpAddress(), ut);
//							continue;
//						}
//						antiCheatService.cacheIPForTTL(clientInfo.getIpAddress(), 30);
						
						//是不是同地区的ip 
						boolean sameAddr = antiCheatService.isSameAddressIp(clientInfo.getIpAddress(), ut.getUser_ip());
						boolean ignoreReport =  !sameAddr && !( appTask.isQuicktask() || appTask.getIschannel() > 0);
						logger.info("usertask ignore report:{},ut:{},processName:{}",ignoreReport,ut.getId(),processName);
						if(ignoreReport){
							continue;
						}
						int clientType = (deviceType==Constants.platform_android)?UserSystemMessage.PUSH_CLIENT_TYPE_ANDROID:UserSystemMessage.PUSH_CLIENT_TYPE_IOS;
						//厂商任务 还得判断激活没有
						boolean finsh = true;
//						if(appTask.isVendorTask()){
//							Map<String, Integer> distinctMap =  this.appVendorService.onDisctinctNew(app, ut.getIdfa());
//							if(distinctMap != null){
//								Integer distinct = distinctMap.get(ut.getIdfa());
//								if(distinct != null && distinct == 0){
//									finsh = false;
//								}
//							}
//						}
						//如果没有激活 就继续
						if(!finsh){
							logger.warn("用户上报任务未激活,idfa:{},ut:{},app:{},user:{},appTask:{}",ut.getIdfa(),ut.getId(),app.getName(),user.getId(),appTask.getId());
							// 厂商接口返回未激活，则不给奖励
							continue;
						}
					
						userTaskCalculateService.onFinishTask(user, app, appTask, ut, warning, cause.toString(),clientType );
						userTaskReportPersistService.remove(ut.getId());
						//添加到缓存 缓存时间一个月
						String idfaKey = RedisUtil.buildIDFAAppKey(idfa, appTask.getApp_id());
						try{
							userTaskCacheDao.set(idfaKey, "1", Constants.MONTH_SECOND_TIME);
						}catch(Exception e){
							logger.debug("add idfa into cache ，causeby:{}",e);
						}
					}
					
				}else if(first && ut.getDownload() == 0){  //第一次上报，给用户一个提醒（下载完成）
					userTaskCalculateService.onFinishDownload(user, app, appTask, ut);
					//添加已下载入库
					userInstalledAppService.insert(userId, app.getId(), did, app.getAgreement());
				}
			}
		}catch(Exception e){
			logger.error("report process error!",e);
		}
	}
	
	private int secondsGap(Date d1, Date d2){
		if (d1 == null || d2 == null) {
			return 0;
		}
		if(d2.before(d1)){
			return 0;
		}
		return (int)((d2.getTime() - d1.getTime())/1000);
	}
	
	private static Date toDate(int second){
		return new Date((long)second * 1000);
	}
	
	private volatile boolean reported = false;
	
	public void reportChannelTaskFinsh(){
		if(reported){
			logger.info("report task finsh to channel ing .......");
			return ;
		}
		reported = true;
		logger.info("report task finsh to channel start .......");
		 //得到正在进行中的任务 
		Date now = GenerateDateUtil.getCurrentDate();
		//获取用户的激活开始时间和结束时间 后台取的是任务开始时间来做判断
		Date start = DateUtil.getAddDate(now, Calendar.MINUTE, -60);
		try{
			List<AppTask> doingAppList = appTaskService.getDoingAppTaskList();
			AppTaskChannelVo vo = null;
			for(AppTask appTask:doingAppList){
				try{
					vo = channelService.getAppTaskChannel(appTask.getId());
					if(!vo.getAppTaskChannel().isNeedReport()){
						logger.info("task is not support report ,apptask:{},",appTask.getId());
						 continue;
					}
					int duration = appTask.getDuration();
					duration = duration == 0?130:duration+15;
					Date end =  DateUtil.getAddDate(now, Calendar.SECOND, -duration);
					logger.info("report user task finsh to channel ,taskId:{}",appTask.getId());
					executorReportTask(appTask, userTaskService.getReportUserTaskList(appTask.getId(),start,end));
				}catch(Exception ee){
					logger.error("report task:{} finsh to channel!!! error",appTask.getId(),ee);
				}
			}
		}catch(Exception e){
			logger.error("report task finsh to channel!!! error",e);
		}finally{
			reported = false;
		}
		logger.info("report task finsh to channel end .......");
	 }
	
	private void executorReportTask(AppTask appTask,List<UserTask> userTaskList){
		if(userTaskList != null && !userTaskList.isEmpty() ){
			for(UserTask userTask:userTaskList){
				reportChannelExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try{
							ClientInfo clientInfo = new ClientInfo();
							clientInfo.setIdfa(userTask.getIdfa());
							clientInfo.setIpAddress(userTask.getUser_ip());
							boolean report = false;
							logger.info("report user task finsh,start,userTask:{}",userTask.getId());
							if(appTask.isQuicktask() ){
								report = appChannelQuickTaskService.reportTaskFinsh(clientInfo, appTask,userTask,userService.getUserById(userTask.getUser_id()));
							}
//							else{
//								report = channelService.reportTaskToChannel(appTask, userTask, clientInfo);
//							}
							logger.info("report user task finsh,end,userTask:{},report status:{}",userTask.getId(),report);
							clientInfo = null;
						}catch(Exception e){
							logger.error("上报渠道任务异常:usertask:{},cause:",userTask.getId(),e);
						}
					}
				});
			}
		}else{
			logger.info("appTask not has user task,appTask:{} ",appTask.getId());
		}
	}
	
	public static void main(String[] args) {
		 //得到正在进行中的任务 
		Date now = GenerateDateUtil.getCurrentDate();
		//获取用户的激活开始时间和结束时间
		Date start = DateUtil.getAddDate(now, Calendar.MINUTE, -60);
		Date end =  DateUtil.getAddDate(now, Calendar.SECOND, -120);
		int duration = 120;
		Date end2 =  DateUtil.getAddDate(now, Calendar.SECOND, -duration);
		System.out.println(start.toLocaleString() +" -> "+end.toLocaleString()+" -> "+end2.toLocaleString());
	}

}
