package com.cyhd.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.LiveAccess;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.channelQuickTask.IQuickTaskService;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserTask;
import com.cyhd.service.util.AppContext;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.vo.AppTaskChannelVo;
import com.cyhd.service.vo.UserTaskVo;
import com.cyhd.web.common.ClientInfo;

@Service
public class AppChannelQuickTaskService extends BaseService{

	protected static Logger logger = LoggerFactory.getLogger("quicktask");
	
	@Resource
	private UserService userService;
	
	@Resource
	private ChannelService channelService;
	
	@Resource
	private AppTaskService appTaskService;
	
	private ExecutorService loadQuickTaskDataES = null;
	@Resource 
	private UserTaskService userTaskService;
	
	private String[] serviceNames = null;
	 
	BlockingQueue<LiveAccess<ReportBean>> reportTaskQueue = null; 
	
	volatile boolean running = true;
	
	ExecutorService reportES;
	
	@PostConstruct
	private void initReportPool(){
		loadReportTaskQueueCache();
		reportES = Executors.newFixedThreadPool(1, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("report_quick_channel_thread");
				return t;
			}
		});
		
		reportES.execute(new Runnable() {
			@Override
			public void run() {
				report();
			}
		});
	}
	
	@PreDestroy
	private void destory(){
		running = false;
		saveReportTaskQueueToFile();
		reportES.shutdown();
		
		loadQuickTaskDataES.shutdown();
	}
	
	
	private void report(){
		while(running){
			try{
				LiveAccess<ReportBean> headReportBean = reportTaskQueue.take();
				List<LiveAccess<ReportBean>> reportList = new ArrayList<>();
				reportList.add(headReportBean);
				//移除所有可用元素到目标集合中去
				reportTaskQueue.drainTo(reportList);
				
				for(LiveAccess<ReportBean> liveBean:reportList){
					if(liveBean.getElement() != null){
						reportTaskQueue.offer(liveBean);
						continue;
					}
					ReportBean reportBean = liveBean.getElementIfNecessary();
					Date now = GenerateDateUtil.getCurrentDate();
					if((now.getTime() - reportBean.getInitDate().getTime()) > Constants.QUICK_TASK_EXPIRE_TIME ){
						logger.info("任务已过期不用重复上报:idfa:{}",reportBean.getClientInfo().getIdfa());
						continue;
					}
					UserTask ut = reportBean.getUserTask();
					if(ut.getReport_status()>0){
						logger.info("状态已改变:idfa:{}",reportBean.getClientInfo().getIdfa());
						continue;
					}
					reportTaskFinsh(reportBean.getClientInfo(), appTaskService.getAppTask(ut.getTask_id()), reportBean.getUserTask(), userService.getUserById(ut.getUser_id()));
//					if(!success){
//						reportTaskQueue.offer(new LiveAccess<AppChannelQuickTaskService.ReportBean>(30000, reportBean));
//					}
					if(reportTaskQueue.size() > 0){
						TimeUnit.SECONDS.sleep(5);
					}
				}
			}catch(Exception e){
				logger.error("通知快速任务的上游渠道异常,",e);
			}
		}
	}
	
	@PostConstruct
	private void initPool(){
		String channelQuicktask = GlobalConfig.channel_quick_task;
		int size = 0;
		if(StringUtil.isNotBlank(channelQuicktask)){
			serviceNames = channelQuicktask.split(",");
			size = serviceNames.length;
		}
		if(size > 1){
			ThreadFactory factory = new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					t.setName("get_quick_task.thred");
					return t;
				}
			};
			loadQuickTaskDataES = Executors.newFixedThreadPool(4, factory);
		}
	}
	
	public List<UserTaskVo> getUserTask(ClientInfo clientInfo,User user, Map<String, String > extraParams){
		if(StringUtil.isBlank(clientInfo.getIdfa())){
			clientInfo.setIdfa(user.getIdfa());
			//return null;
		}
		//都没有idfa 就不要去请求啦 
		if(StringUtil.isBlank(clientInfo.getIdfa())){
			return null;
		}
		List<UserTaskVo> quickTaskData = new LinkedList<UserTaskVo>();
		if(serviceNames == null ){
			if(logger.isInfoEnabled()){
				logger.info("not quick task channel");
			}
		}else{
			if(serviceNames.length == 1){
				return getUserTaskByQuickChannel(clientInfo, user, extraParams, serviceNames[0]);
			}else{
				List<Callable<List<UserTaskVo>>> taskList = new ArrayList<Callable<List<UserTaskVo>>>(serviceNames.length);
				for (String serviceName : serviceNames) {
					taskList.add(new Callable<List<UserTaskVo>>() {
						@Override
						public List<UserTaskVo> call() throws Exception {
							return getUserTaskByQuickChannel(clientInfo, user, extraParams, serviceName);
						}
					});
				}
				try {
					List<Future<List<UserTaskVo>>> futureList = loadQuickTaskDataES.invokeAll(taskList);
					List<UserTaskVo> tmp = null;
					for(Future<List<UserTaskVo>> future:futureList){
						if(future.isDone()){
							try{
								tmp = future.get();
								if(tmp != null){
									quickTaskData.addAll(tmp);
								}
							}catch(Exception e){
								logger.error("cause by:{}",e);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return quickTaskData;
	}
	
	private List<UserTaskVo> getUserTaskByQuickChannel(ClientInfo clientInfo,User user, Map<String, String > extraParams,String serviceName){
		if(StringUtil.isBlank(serviceName)){
			return null;
		}
		//没有必要打印这个日志 
		//logger.info("get quick task start,serviceName:{},user:{}",serviceName,user);
		try {
			IQuickTaskService iQuickTaskService = AppContext.getAppContext().getBean(serviceName.trim(), IQuickTaskService.class);
			if(iQuickTaskService != null){
				return iQuickTaskService.getAllTaskApp(clientInfo, user, extraParams);
			}
		} catch (Exception e) {
			logger.error("get quick task data by:{}",serviceName);
			logger.error("cause by:",e);
		}
		return null;
	}
	
	public boolean click(ClientInfo clientInfo,User user,AppTask appTask,Map<String, String > extraParams){
		try {
			AppTaskChannelVo vo = channelService.getAppTaskChannel(appTask.getId());
			App app =appTaskService.getApp(appTask.getApp_id());
			IQuickTaskService iQuickTaskService = AppContext.getAppContext().getBean(vo.getAppChannel().getService_name(), IQuickTaskService.class);
			if(iQuickTaskService != null){
				boolean rtv = iQuickTaskService.click(clientInfo, user, app, appTask,vo, extraParams);
				logger.info("请求:{}的快速任务点击,idfa:{},user:{},接任务状态:{}",vo.getAppChannel().getService_name(),clientInfo.getIdfa(),user.getId(),rtv);
				return rtv;
			}
		} catch (Exception e) {
			logger.error("请求快速任务点击,idfa:{},user:{},cause by:{}",clientInfo.getIdfa(),user.getId(),e);
		}
		
		return false;
	}
	
	public boolean reportTaskFinsh(ClientInfo clientInfo,AppTask appTask,UserTask ut,User user){
		try{
			if(appTask.isQuicktask()){
				AppTaskChannelVo vo = channelService.getAppTaskChannel(appTask.getId());
				if(vo.getAppTaskChannel().isNeedReport() == false){
					//任务不上报
					logger.info("task is not support report, appid:{},user:{},idfa:{}",appTask.getApp_id(),ut.getUser_id(),clientInfo.getIdfa());
					return true;
				}
				
//				int runtime = appTask.getDuration();
//				Date activeTime = ut.getActive_time();
//				Date now = GenerateDateUtil.getCurrentDate();
				//不一定要runtime这么多时间
//				if((now.getTime() - activeTime.getTime()) < runtime*800){
//					logger.warn("Run time not specified time,activeTime:{},now:{},runtime:{}",activeTime,now,runtime);
//					return false;
//				}
				//do report 
				
				//判断任务的试玩时间
				Date now = GenerateDateUtil.getCurrentDate();
				//不足 试玩时间+5s的下载时间 加入待上报队列
				boolean isfinshDuration = (now.getTime()-ut.getStarttime().getTime()-(appTask.getDuration()+5)*1000) > 0;
				boolean reportStatus  = false;
				//必须达到试玩时间 才去上报
				if(isfinshDuration && (reportStatus=doReporTaskFinsh(clientInfo, user, appTask, vo))){
					logger.info("上报任务状态,idfa:{},status:{}",clientInfo.getIdfa(),reportStatus);
					userTaskService.updateReportStatus(user.getId(), ut.getId());
				}else{
					ReportBean reportBean = new ReportBean(clientInfo, ut);
					boolean addToQueue = reportTaskQueue.offer(new LiveAccess<AppChannelQuickTaskService.ReportBean>(30000, reportBean));
					logger.info("finsh duration:{},add to queue,idfa:{},status:{}",isfinshDuration,clientInfo.getIdfa(),addToQueue);
				}
				return reportStatus;
			}
		}catch(Exception e){
			logger.error("上报快速任务完成，ut:{}",ut.getId(),e);
		}
		return false;
	}

	private boolean doReporTaskFinsh(ClientInfo clientInfo, User user, AppTask appTask,
			AppTaskChannelVo vo) {
		App app =appTaskService.getApp(appTask.getApp_id());
		try {
			IQuickTaskService iQuickTaskService = AppContext.getAppContext().getBean(vo.getAppChannel().getService_name(), IQuickTaskService.class);
			if(iQuickTaskService != null){
				boolean rtv = iQuickTaskService.reportTaskFinsh(clientInfo, user, appTask, app,vo);
				logger.info("上报:{}的快速任务,idfa:{},user:{},上报状态:{}",vo.getAppChannel().getService_name(),clientInfo.getIdfa(),user.getId(),rtv);
				return rtv;
			}
		} catch (Exception e) {
			logger.error("上报:{}的快速任务,idfa:{},user:{},",vo.getAppChannel().getService_name(),clientInfo.getIdfa(),user.getId(),e);
		}
		return false;
	}
	
	//构造请求所需参数
	ClientInfo jobClientInfo = null;
	User jobDefaultUser = null;
	Map<String, String> jobExtraParams = null;
	
	private void initParameters(){
		jobClientInfo = new ClientInfo();
		jobClientInfo.setIpAddress("202.108.31.60");
		jobClientInfo.setOs("iPhone OS8.4.1");
		jobClientInfo.setModel("iPhone");
		jobClientInfo.setIdfa("WSHSYDBB-WSHZ-03Z3-HHXX-TTXSWXYZZZZZ");
		int userId = 1;
		
		jobDefaultUser = userService.getUserById(userId);
		
		jobExtraParams = new HashMap<String, String>();
		jobExtraParams.put("ua", "Mozilla/5.0 (iPhone; CPU iPhone OS 8_4_1 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Version/8.0 Mobile/12H321 Safari/600.1.4");
	}
	
	public void loadByJob(){
		if(jobClientInfo == null){
			initParameters();
		}
		this.getUserTask(jobClientInfo, jobDefaultUser, jobExtraParams);
	}
	
	private final  String filePath = System.getProperty("java.io.tmpdir")+"/reportTaskQueue.cache.db";
	private final void loadReportTaskQueueCache(){
		File file = new File(filePath);
		if(file.exists()){
			if(file.canRead() == false)
				file.setReadable(true);
			try( ObjectInputStream is = new ObjectInputStream(new FileInputStream(  
					file));){
				try {
					reportTaskQueue = (BlockingQueue<LiveAccess<ReportBean>>) is.readObject();
					logger.info("read object for file success ,filePath:{}",filePath);
					file.delete();
				} catch (ClassNotFoundException e) {
					logger.error("read object for file fail ,filePath:{},cause :{}",filePath,e);
				}
			} catch ( IOException e) {
				logger.error("read object for file fail,filePath:{},cause :{}",filePath,e);
			}
		}else{
			logger.error("reportTaskQueue.cache.db not exist,filePath:{}",filePath);
		}
		if(reportTaskQueue == null){
			reportTaskQueue = new LinkedBlockingQueue<>(5000);
		}
	}
	
	private final void saveReportTaskQueueToFile(){
		File file = new File(filePath);
		if(file.exists() == false){
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("create reportTaskQueue.cache.db fail,filePath:{},cause :{}",filePath,e);
			}
		}
		if(file.canWrite() == false)
			file.setWritable(true);
		
		try( ObjectOutputStream os = new ObjectOutputStream(  
                new FileOutputStream(file))){
			os.writeObject(reportTaskQueue);
			logger.info("write file reportTaskQueue.cache.db success,filePath:{}",filePath);
		}catch (IOException e) {
			logger.error("write file reportTaskQueue.cache.db fail,filePath:{},cause :{}",filePath,e);
		}
	}
	
	
	static class ReportBean implements java.io.Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -7590135882953554740L;
		
		ClientInfo clientInfo;
		UserTask userTask;
		Date intDate;
		public ReportBean() {
			intDate = GenerateDateUtil.getCurrentDate();
		}
		
		public ReportBean(ClientInfo clientInfo, UserTask userTask) {
			this();
			this.clientInfo = clientInfo;
			this.userTask = userTask;
		}

		public ClientInfo getClientInfo() {
			return clientInfo;
		}

		public void setClientInfo(ClientInfo clientInfo) {
			this.clientInfo = clientInfo;
		}

		public UserTask getUserTask() {
			return userTask;
		}

		public void setUserTask(UserTask userTask) {
			this.userTask = userTask;
		}

		public Date getInitDate() {
			return intDate;
		}
		
	}
	public static void main(String[] args) {
		LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
		boolean add =queue.offer("a");
		System.out.println(add);
	}
}
