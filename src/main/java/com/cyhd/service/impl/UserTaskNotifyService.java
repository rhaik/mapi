package com.cyhd.service.impl;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.NumberUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.UserTaskNotifyMapper;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.UserTaskNotify;
import com.cyhd.service.util.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


@Service
public class UserTaskNotifyService extends BaseService {
	
	private final static Logger logger = LoggerFactory.getLogger("channel");
	@Resource
	private UserTaskNotifyMapper userTaskNotifyMapper;

    @Resource
    AppTaskService appTaskService;
    
    @Resource
    private UserTaskService userTaskService;
    /**上报*/
    private ExecutorService threadPartReportTask = null;    
	/**排重*/
    private ExecutorService threadDisctinctTask = null;  
    private ExecutorService threadDisctinctTaskNew = null;

    @Resource(name=RedisUtil.NAME_SELF)
	private IJedisDao userTaskCacheDao;
	
	@Resource
	private AppVendorService appVendorService;

	@Resource
	private UserInstalledAppService userInstalledAppService;
	
	@Resource
	private AntiCheatService antiCheatService;

	// callback reservation rate
	private final static int DEFAULT_RESERVE_RATE = 0;
	
	@PostConstruct
	private void initES(){
		ThreadFactory factory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("thread-report-task.job");
				return t;
			}
		};
		ThreadFactory disctinct = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("thread-disctinct.job");
				return t;
			}
		};
		ThreadFactory disctinctNew = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("thread-disctinct_new.job");
				return t;
			}
		};
		threadDisctinctTaskNew = Executors.newFixedThreadPool(4, disctinctNew);
		threadDisctinctTask = Executors.newFixedThreadPool(4, disctinct);
		threadPartReportTask = Executors.newFixedThreadPool(4, factory);
	}
	
	@PreDestroy
	private void shutdown(){
		if(threadPartReportTask != null){
			if(threadPartReportTask.isShutdown() == false){
				threadPartReportTask.shutdown();
			}
		}
		if(threadDisctinctTask != null){
			threadDisctinctTask.shutdown();
		}
	}
	
	public String isRevicedByIdfaArray(String[] idfas,int appId, int twinAppId, int channelId) {
		if(idfas.length == 1){
			return executeIsRevice(idfas[0], appId, twinAppId, channelId);
		}else{
			List<Callable<String>> tasks = new ArrayList<Callable<String>>(idfas.length);
			for(String idfa:idfas){
				tasks.add(new Callable<String>() {
					@Override
					public String call() throws Exception {
						return executeIsRevice(idfa, appId, twinAppId, channelId);
					}
				});
			}
			StringBuilder sb = new StringBuilder(500);
			try {
				List<Future<String>> futures = threadDisctinctTask.invokeAll(tasks);
				for(Future<String> future:futures){
					try {
						sb.append(future.get()).append(",");
					} catch (ExecutionException e) {
						logger.error("排重线程get出现错误:cause by:{}",e);
					}
				}
				int index = 0;
				if((index = sb.lastIndexOf(",")) != -1){
					sb.deleteCharAt(index);
				}
				return sb.toString();
			} catch (InterruptedException e) {
				logger.error("排重接口invokeAll出现错误:cause by:{}",e);
			}
			return null;
		}
	}

	private String executeIsRevice(String idfa, int appId, int twinAppId, int channelId) {
		boolean flag= isReceivedByIdfa(idfa, appId, twinAppId, channelId);
		StringBuilder sb = new StringBuilder();
		sb.append("\"").append(idfa).append("\"").append(":").append(flag);
		return sb.toString();
	}

	private IDFABean executeIsReviceBySignle(String idfa, int appId, int twinAppId, int channelId) {
		boolean flag= isReceivedByIdfa(idfa, appId, twinAppId, channelId);
		return new IDFABean(idfa,flag?1:0);
	}
	
	/***
	 * 新的排重接口
	 * @param idfa
	 * @param appId
	 * @param twinAppId
	 * @return
	 */
	public Map<String, Integer> isRevicedByIdfa(String[] idfas,int appId, int twinAppId, int channelId) throws Exception {
		
		
		Map<String, Integer> retv = new HashMap<>(idfas.length);
		//单一的
		if(idfas.length == 1){
			retv.put(idfas[0], executeIsReviceBySignle(idfas[0], appId, twinAppId, channelId).revice);
		}else{
			
			List<Callable<IDFABean>> tasks = new ArrayList<Callable<IDFABean>>(idfas.length);
			for(String tmp:idfas){
				tasks.add(new Callable<IDFABean>() {
					@Override
					public IDFABean call() throws Exception {
						return executeIsReviceBySignle(tmp, appId, twinAppId, channelId);
					}
				});
			}
			try{
				List<Future<IDFABean>> futures = threadDisctinctTaskNew.invokeAll(tasks);
				
				Iterator<Future<IDFABean>> iterator = futures.iterator();
				IDFABean bean = null;
				while(iterator.hasNext()){
					bean = iterator.next().get();
					retv.put(bean.idfa, bean.revice);
				}
				
			}catch(Exception e){
				//TODO 异常chu'li
				logger.error("新的排重中出错:{}",e);
				throw e;
			}
			
		}
		return retv;
	}

	class IDFABean{
		
		String idfa;
		int revice=1;
		
		public IDFABean(String idfa, int revice) {
			this.idfa = idfa;
			this.revice = revice;
		}
	}
	/**
	 * 判断第三方合作伙伴是否能接任务，会根据appId找到相同appstoreId的app，判断两次
	 * @param idfa
	 * @param appId
	 * @return
	 */
	public boolean isReceivedByIdfa(String idfa, int appId, int twinAppId, int channelId){
		boolean flag = userInstalledAppService.isPreFilteredByIDFA(appId, idfa) || userTaskService.isRevicedByIdfa(idfa, appId);
		if (flag){ //排重失败，看看是不是当前渠道接的渠道任务
			String idfaKey = RedisUtil.buildIDFAAppKey(idfa, appId);
			try {
				String value = userTaskCacheDao.get(idfaKey);
				if (NumberUtil.safeParseInt(value) == channelId){ //表示渠道任务进行中
					flag = false; //当前渠道进行中再次排重，可以通过
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}

		//检查是否由相同AppstoreId的另一个app接过
		if (flag == false && twinAppId > 0){
			flag = userInstalledAppService.isPreFilteredByIDFA(twinAppId, idfa) || userTaskService.isRevicedByIdfa(idfa, twinAppId);
		}
		return flag;
	}


	/**
	 * 判断这个idfa是不是由第三方接过改任务
	 * @param idfa
	 * @param appId
	 * @return
	 */
	public boolean isReceivedByPartner(String idfa, int appId){
		String idfaKey = RedisUtil.buildIDFAAppKey(idfa, appId);
		try{
			String value = userTaskCacheDao.get(idfaKey);
			if(StringUtils.isNotBlank(value)){
				return true;
			}
		}catch(Exception e){
			logger.info("get idfa value form db ,cause by:{}",e);
		}
		
		UserTaskNotify ut3 = getByIdfaAndAppId(idfa, appId);

		//根据其不同的状态，设定不同的过期时间
		if(ut3 != null){
			boolean result = true;
			int expireTime = Constants.MONTH_SECOND_TIME;
			int status = 1; //1表示已经完成

			if (ut3.getStatus() == 1) {
				if(ut3.isVilid()){ //进行中，则一直到过期时间，都不能接
					result = true;
					status = ut3.getChannel();  //直接缓存为channel id
					expireTime = (int)(ut3.getExpiretime().getTime() - System.currentTimeMillis()) / 1000;
				}else { //任务已经过期，可以通过排重
					result = false;
				}
			}

			if (result) { //排重未通过，加入缓存
				try {
					userTaskCacheDao.set(idfaKey, "" + status, expireTime);
				} catch (Exception e) {
				}
			}
			return result;
		}
		return false;
	}


	public UserTaskNotify getByIdfaAndAppId(String idfa,int appId){
		return userTaskNotifyMapper.getByIdfaAndAppId(idfa, appId);
	}
	public boolean addTask(UserTaskNotify utn) {
		return userTaskNotifyMapper.addTask(utn) > 0;
	}

	public boolean restartTask(UserTaskNotify utn){
		return userTaskNotifyMapper.restartTask(utn) > 0;
	}


	/**
	 * 厂商回调渠道任务
	 * @param utn
	 * @return
	 */
	public boolean onVendorCallback(UserTaskNotify utn){
		boolean flag = false;
		//特殊情况 有厂商 但是是上报任务
		AppTask appTask = appTaskService.getAppTask(utn.getTask_id());
		if(utn.getVendor() == 0 && appTask != null) {
			//如果是厂商任务，则完成厂商回调
			flag = userTaskNotifyMapper.finishVendorCallback(utn.getId()) > 0;
			logger.info("onVendorCallback, finish vendor callback, utn:{}, flag:{}", utn, flag);

			if (flag) {
				//任务的激活数和完成数+1
				appTaskService.onActiveTask(utn.getTask_id());
				appTaskService.onTaskFinished(utn.getTask_id());

				//任务有效且不是直接奖励的任务，才回调渠道
				if (utn.isVilid() && !appTask.isDirectReward()) {
					//必须是任务未过期期间，才回调渠道 并且不是直接给奖励的
					int rnd = ThreadLocalRandom.current().nextInt(100);
					int reserve = getReserveRate(utn.getChannel());
					if (rnd > (reserve - 1) || "test".equalsIgnoreCase(appTask.getKeywords())) { //关键词为test时，肯定回调
						//回调任务时，向渠道发起回调
						String callbackUrl = utn.getCallbackurl();
						if (StringUtil.isNotBlank(callbackUrl)) {
							try {
								userTaskNotifyMapper.finishChannelCallback(utn.getId());
								String rs = HttpUtil.get(callbackUrl, null);

								logger.info("onVendorCallback,response:{}, request url:{}",rs, callbackUrl);
								JSONObject json = new JSONObject(rs);

								if (json != null && json.has("success") && json.optBoolean("success")) {
									userTaskNotifyMapper.channelCallbackSuccess(utn.getId());
								} else {
									userTaskNotifyMapper.channelCallbackFail(utn.getId());
								}
								flag = true;
							} catch (Exception e) {
								userTaskNotifyMapper.channelCallbackFail(utn.getId());
								logger.info("onVendorCallback url:{} is error:{}", callbackUrl, e);
								flag = false;
							}
						}
					}
				} else {
					logger.warn("onVendorCallback, utn is expired or direct reward. utn:{}", utn);
				}
			}
		}
		return flag;
	}


	/**
	 * 渠道上报已激活
	 * 
	 * @return
	 */
	public boolean onChannelReport(UserTaskNotify utn) {
		boolean flag = false;
		//特殊情况 有厂商 但是是上报任务
		AppTask appTask = appTaskService.getAppTask(utn.getTask_id());
		if(utn.getReward() == 0 && appTask != null) {
			//如果是上报任务，直接设置为已奖励
			if (utn.isReportTask() || appTask.isDirectReward()) {
				flag = userTaskNotifyMapper.finishReportTask(utn.getId()) > 0;
			}else {
				logger.warn("onChannelCallback, utn is expired or is not direct reward. utn:{}", utn);
			}

			if (flag) {
				//任务的激活数和完成数+1
				appTaskService.onActiveTask(utn.getTask_id());
				appTaskService.onTaskFinished(utn.getTask_id());

				//修改排重的缓存，设置为1个月
				String idfaKey = RedisUtil.buildIDFAAppKey(utn.getIdfa(), utn.getApp_id());
				try {
					userTaskCacheDao.set(idfaKey, "1", Constants.DAY_SECONDS * 30);
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}

		return flag;
	}
	
	public String executorThreeRepotTask(String[] idfas,AppTask appTask,  int channelId,String ip,String mac,App app){
		
		if(idfas.length ==1){
		 	return executeIdfaTask(idfas[0], appTask,app,ip,channelId);
		}
		
		List<Callable<String>> callable = new ArrayList<Callable<String>>(idfas.length);
		
		for(String idfa:idfas){
			callable.add(new Callable<String>() {
				@Override
				public String call() throws Exception {
					return executeIdfaTask(idfa, appTask,app,ip,channelId);
				}
			});
		}
		StringBuilder sb = new StringBuilder(300);
		try {
			List<Future<String>> futures = threadPartReportTask.invokeAll(callable);
			String tmp = null;
			for(Future<String> future:futures){
				try{
					tmp = future.get();
					if(tmp != null){
						sb.append(tmp).append(",");
					}
				}catch(ExecutionException ee){
					logger.error("处理第三方上报任务,cause by:",ee);
				}
			}
		} catch (InterruptedException e) {
			logger.error("处理第三方上报中,处理后的:{},cause by:",sb.toString(),e);
		}
		int index = 0;
		if((index = sb.lastIndexOf(",")) != -1){
			sb.deleteCharAt(index);
		}
		return sb.toString();
	}

	private String executeIdfaTask(String idfa, AppTask appTask,App app,String ip,int channelId) {
		UserTaskNotify ut = getByIdfaAndAppId(idfa, appTask.getApp_id());
		boolean flag = false;
		if(ut == null){
			logger.error("没有获得相应的UserTaskNotify:idfa:{},appId:{},channel:{}",idfa,appTask.getApp_id(),channelId);
			flag = false;
		}else if(ut.getChannel() != channelId){
			//渠道对不对
			logger.error("渠道来源不对！channel:{},ut_channel:{},idfa:{},taskId:{}",channelId,ut.getChannel(),idfa,appTask.getApp_id());
			flag = false;
		}else if(ut.isVilid() == false){
			//任务过期没有
			logger.error("任务已过期！channel:{},idfa:{},taskId:{}",channelId,idfa,appTask.getApp_id());
			flag = false;
		}else if(appTask.isDirectReward() == false){
			//回调任务不准上报 (有的厂商不回调)
			logger.error("回调任务不准上报!,channel:{},idfa:{},taskId:{}",channelId,idfa,appTask.getApp_id());
			flag = false;
		}else if(ut.getStatus() > 2){
			//状态不是1的话 就是修改过的
			logger.error("任务已操作！channel:{},idfa:{},taskId:{}",channelId,idfa,appTask.getApp_id());
			flag = false;
		}else{
			boolean doreport = true;
			//厂商判断激活没有
			if(appTask.isVendorTask()){
				Map<String, Integer> distinctMap = this.appVendorService.onDisctinctNew(app, idfa);
				if(distinctMap != null){
					Integer result = distinctMap.get(idfa);
					if(result != null && result == 0){
						logger.warn("任务未激活！channel:{},idfa:{},appId:{}",channelId,idfa,appTask.getApp_id());
						flag =  false;
						//任然允许上报
						doreport = true;
					}
				}
			}
			if(doreport){
				flag = onChannelReport(ut);
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("\"").append(idfa).append("\"").append(":").append(flag);
		return sb.toString();
	}


	//根据渠道获取ReserveRate
	private int getReserveRate(int channel){
		if (channel == 53802){ //趣米
			return 18;
		}

		if (channel == 36298 || channel == 80236 || channel == 43919){ //无锡飞梦, 聚有钱, 馅饼儿
			return 0;
		}
		return DEFAULT_RESERVE_RATE;
	}
}
