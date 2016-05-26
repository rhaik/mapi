package com.cyhd.service.push;

import static com.cyhd.service.push.PushConstants.TYPE_FRIEND_PROMOT;
import static com.cyhd.service.push.PushConstants.TYPE_SYSTEM_PROMOT;
import static com.cyhd.service.push.PushConstants.TYPE_SYS_PROMOT;
import static com.cyhd.service.push.PushConstants.TYPE_TRAN_ARTICLE_PROMOT;
import static com.cyhd.service.push.PushConstants.newPushParam;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.Helper;
import com.cyhd.common.util.MoneyUtils;
import com.cyhd.common.util.job.FeedbackAsyncJob;
import com.cyhd.common.util.job.JobHandler;
import com.cyhd.common.util.job.JobResult;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserFriendMessage;
import com.cyhd.service.dao.po.UserTask;
import com.cyhd.service.impl.BaseService;
import com.cyhd.service.impl.DeviceService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.util.CollectionUtil;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.IdEncoder;

@Service
public class PushService extends BaseService {

	protected static Logger logger = LoggerFactory.getLogger("push");

	@Resource
	AppPushDelegate pushDelegate;
	
	@Resource
	UserService userService;

	private ExecutorService matchPushExecutor = null;

	private ExecutorService pushExecutor = null;

	FeedbackAsyncJob<SysPush> asyncJob = null;

	private int push_executor_size = 20;
	
	//每一次通知的同步数量
	private final int sysPushBlockSize = 100;
	
	private ForkJoinPool pool = null;
	
	@Resource
	private DeviceService deviceService;
	
	@PostConstruct
	public void init() {
		ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("match_push_job_thread");
				return t;
			}
		};
		ThreadFactory threadFactory2 = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("push_job_thread");
				return t;
			}
		};
		if (GlobalConfig.server_id == 216) {
			push_executor_size = 30;
		}
		matchPushExecutor = Executors.newFixedThreadPool(push_executor_size, threadFactory);
		pushExecutor = Executors.newFixedThreadPool(5, threadFactory2);
		
		//使用Runtime.getRuntime().availableProcessors()
		pool = new ForkJoinPool();
	}

	@PreDestroy
	public void shutdown() {
		if (asyncJob != null)
			asyncJob.shutdown();
		if (matchPushExecutor != null)
			matchPushExecutor.shutdown();
		if (pushExecutor != null)
			pushExecutor.shutdown();
	}

	/**
	 * 
	 * @param async
	 *            是否异步发送
	 * @return
	 */
	private PushResult pushDo(final long userId, final long fromId, final String alertBody, final JSONObject params, final int pushType, final long targetId, final boolean async) {
		if (async) {
			pushExecutor.execute(new Runnable() {
				@Override
				public void run() {
					pushDoIt(userId, fromId, alertBody, params, pushType, targetId);
				}
			});
			return new PushResult(true);
		} else {
			return pushDoIt(userId, fromId, alertBody, params, pushType, targetId);
		}
	}

	

	/**
	 * 全部的push发送方法<br/>
	 * @param params :<b style="color:red;">需要向Android发送需要加上clientType,UserSystemMessage.PUSH_CLIENT_TYPE_ANDROID</b>
	 * @param userId
	 * @param alertBody
	 *
	 * @param pushType
	 * @param driver
	 * @param targetId
	 * @return
	 * @see AppPushDelegate#push(long, String, JSONObject, boolean)
	 */
	private PushResult pushDoIt(long userId, long fromId, String alertBody, JSONObject params, int pushType, long targetId) {
		String logmessage = "PushService.pushDo：userid:" + userId + ",fromId:" + fromId + ",params:" + params + ",pushType:" + pushType
				+ ",targetid:" + targetId + ",alertBody:" + alertBody;
		try {
			long pushRecordId = 0;//idMakerService.getIncreaseId();
			params.put("id", pushRecordId);
			logger.info(logmessage + " start invoke push!");
			// 给用户push,处理push结果
			PushResult result = pushDelegate.push(userId, alertBody, params);
			// 写入用户的历史机型
			// User user = userService.getUserById(userId);
			// if (!StringUtils.isEmpty(user.getClienttype())) {
			// result.setClientType(user.getClienttype());
			// }
			result.setPushRecordId(pushRecordId);
			logger.info(logmessage + " end invoke push! pushRecordId:" + pushRecordId + ",result:" + result);

			// 添加push 记录
			if (!result.isSuccess()) {
				// pushRecordService.add(pushRecordId, userId, fromId, targetId,
				// driver, pushType, alertBody, result.getClientType(),
				// PushRecord.STATE_FAIL,
				// result.getErrMessage(), result.getErrcode());
			} else {
				// pushRecordService
				// .add(pushRecordId, userId, fromId, targetId, driver,
				// pushType, alertBody, result.getClientType(),
				// PushRecord.STATE_SEND, "", 0);
			}
			return result;
		} catch (Exception e) {
			logger.error(logmessage + " invoke error.", e);
			return AppPushDelegate.getDefaultPushResult();
		}
	}
	
	/**
	 * 用户试用消息
	 * @param user
	 * @param app
	 * @param appTask
	 * @param ut
	 * @param type
	 */
//	public boolean notifyUserApp(User user, App app, AppTask appTask, UserTask ut, int type, String extra) {
//		String logInfo = "PushService.notifyUserApp(toId=" + user.getId() +")";
//		if (logger.isInfoEnabled())
//			logger.info(logInfo);
//		try {
//			JSONObject params = newPushParam(type);
//			params.put("task_id", ut.getId());
//			params.put("en_task_id", IdEncoder.encode(appTask.getId()));
//			StringBuffer sb = new StringBuffer();
//			String appName = app.getName();
//			switch(type){
//				case PushConstants.TYPE_APP_TASK_START:
//					sb.append(appName).append("试用任务已参加，请尽快完成拿奖励！");
//				break;
//				case PushConstants.TYPE_APP_TASK_DOWNLOAD:
//					sb.append(appName).append("已经下载成功，请点击“打开试玩”，试用").append(appTask.getDuration()/60)
//					.append("分钟，即可获得").append(MoneyUtils.fen2yuanS(appTask.getAmount())).append("元奖励。");
//				break;
//				case PushConstants.TYPE_APP_TASK_FINISHED:
//					sb.append(appName).append("已试用完成，请等待奖励发放！");
//				break;
//				case PushConstants.TYPE_APP_TASK_SUCCESS:
//					sb.append(appName).append("App试用奖励").append(MoneyUtils.fen2yuanS(appTask.getAmount())).append("元已经发放！");
//				break;
//				case PushConstants.TYPE_APP_TASK_FAILED:
//					sb.append(appName).append("试用未通过审核！");
//					if(extra!=null && extra.length() > 0){
//						sb.append("原因：").append(extra);
//					}
//				break;
//				case PushConstants.TYPE_APP_TASK_EXPIRE :
//					sb.append(appName).append("试用即将过期，请尽快完成以便顺利拿到奖励。"); 
//					break;
//			}
//			
//			PushResult result = pushDo(user.getId(), 0l, sb.toString(), params, type, 0, true);
//			return result.isSuccess();
//		} catch (Exception e) {
//			logger.error(logInfo, e);
//			return false;
//		}
//	}

//	public boolean notifySysPrompt(long userId, String content, String url, long targetid) {
//		String logInfo = "PushService.notifySysPrompt(toId=" + userId + ", message=" + content + ", target=" + url + ")";
//		if (logger.isInfoEnabled())
//			logger.info(logInfo);
//		try {
//			JSONObject params = newPushParam(TYPE_SYS_PROMOT);
//			params.put("target", url);
//			String alertBody = content;
//
//			PushResult result = pushDo(userId, 0l, alertBody, params, TYPE_SYS_PROMOT,  targetid, true);
//			return result.isSuccess();
//		} catch (Exception e) {
//			logger.error(logInfo, e);
//			return false;
//		}
//	}
	
	public boolean notifyFriendPrompt(int userId, App app, AppTask appTask, UserTask ut, int shareAmount, int level) {
		String logInfo = "PushService.notifySysPrompt(toId=" + userId  + ")";
		if (logger.isInfoEnabled())
			logger.info(logInfo);
		try {
			JSONObject params = newPushParam(TYPE_FRIEND_PROMOT);
			params.put("task_id", ut.getId());
			String appName = app.getName();
			String target = "徒弟"; 
			if(level > 1){
				target = "徒孙";
			}
			
			String alertBody = String.format("您的%s刚刚试用%s，赚了%s元，秒赚大钱给您奖励%s元。", target, appName, MoneyUtils.fen2yuanS(appTask.getAmount()), MoneyUtils.fen2yuanS(shareAmount));

			PushResult result = pushDo(userId, 0l, alertBody, params, TYPE_FRIEND_PROMOT, 0, true);
			return result.isSuccess();
		} catch (Exception e) {
			logger.error(logInfo, e);
			return false;
		}
	}
	/**
	 * 给所有用户发系统push
	 * @param content
	 * @return
	 */
	public boolean notifyAllUsersSystemPrompt(String content,int pushClientType) {
		content = Helper.truncate(content, 30);
		
//		int minUserId = 1;
//		int maxUserId = userService.getMaxUserId();
		Long[] userArr = null;
		int page = 1;
		while(true){
		    userArr = deviceService.getUserIdList(page);
				
			if(userArr == null || userArr.length == 0){
				break;
			}
			
			for(long uid : userArr){
				notifyUserSystemPrompt(uid, content,pushClientType);
			}
			//pool.execute(new AllUserSysPushTask(userArr, content) );
				
			if(userArr.length < Constants.DEFAULT_DEVICE_PAGE_SIZE){
				break;
			}
			try{
				Thread.sleep(page * 2 * 1000);
			}catch(Exception e){
				
			}
				
			page ++;
		}
		logger.info("notifyAllUsersSystemPrompt  method end");
		return true;
	}
	
	/**
	 * 系统消息
	 * 
	 * @param userId
	 * @param content
	 * @return
	 */
	public boolean notifyUserSystemPrompt(long userId, String content,int pushClientType) {
		String logInfo = "PushService.notifySysPrompt(toId=" + userId + ", message=" + content + ")";
		if (logger.isInfoEnabled())
			logger.info(logInfo);
		try {
			JSONObject params = newPushParam(TYPE_SYSTEM_PROMOT);
			String alertBody = content;
			if(pushClientType > 0){
				params.put("clientType", pushClientType);
			}
			PushResult result = pushDo(userId, 0l, alertBody, params, TYPE_SYSTEM_PROMOT, 0, true);
			return result.isSuccess();
		} catch (Exception e) {
			logger.error(logInfo, e);
			return false;
		}
	}
	/**转发任务的通知**/
//	public boolean notifyTranArticlePrompt(long userId,String content,int pushClientType){
//		String logInfo = "PushService.notifyTranArticlePrompt(toId=" + userId + ", message=" + content + ")";
//		if (logger.isInfoEnabled())
//			logger.info(logInfo);
//		try {
//			JSONObject params = newPushParam(TYPE_TRAN_ARTICLE_PROMOT);
//			String alertBody = content;
//			if(pushClientType > 0){
//				params.put("clientType", pushClientType);
//			}
//			PushResult result = pushDo(userId, 0l, alertBody, params, TYPE_TRAN_ARTICLE_PROMOT, 0, true);
//			return result.isSuccess();
//		} catch (Exception e) {
//			logger.error(logInfo, e);
//			return false;
//		}
//	}
//	
//	public boolean notifyTranArticleFriendPrompt(int userId, String articleName, int amount,long user_task_id,  int shareAmount, int level){
//		String logInfo = "PushService.notifyTranArticleFriendPrompt(toId=" + userId  + ")";
//		if (logger.isInfoEnabled())
//			logger.info(logInfo);
//		try {
//			JSONObject params = newPushParam(TYPE_FRIEND_PROMOT);
//			params.put("task_id", user_task_id);
//			String target = "徒弟"; 
//			if(level > 1){
//				target = "徒孙";
//			}
//			
//			String alertBody = String.format("您的%s刚刚试用%s，赚了%s元，赚大钱给您奖励%s元。", target, articleName, MoneyUtils.fen2yuanS(amount), MoneyUtils.fen2yuanS(shareAmount));
//
//			PushResult result = pushDo(userId, 0l, alertBody, params, TYPE_FRIEND_PROMOT, 0, true);
//			return result.isSuccess();
//		} catch (Exception e) {
//			logger.error(logInfo, e);
//			return false;
//		}
//	}
	
//	public boolean notifyIntergalFriendPrompt(int userId,int user_task_id, String appName, int amount, int shareAmount, int level,int messageSource){
//		String logInfo = "PushService.notifyTranArticleFriendPrompt(toId=" + userId  + ")";
//		if (logger.isInfoEnabled())
//			logger.info(logInfo);
//		try {
//			JSONObject params = newPushParam(TYPE_FRIEND_PROMOT);
//			params.put("task_id", user_task_id);
//			String target = "徒弟"; 
//			if(level > 1){
//				target = "徒孙";
//			}
//			//TODO 修改
//			String alert = null;
//			if(messageSource == UserFriendMessage.SOURCE_INTEGAL_QIANDAO){
//				alert = "您的%s刚刚完成%s任务，赚了%s元，赚大钱给您奖励%s元。";
//			}else if(messageSource == UserFriendMessage.SOURCE_INTEGAL_JIFEN){
//				alert = "您的%s刚刚完成积分任务(%s)，赚了%s元，赚大钱给您奖励%s元。";
//			}else{
//				alert = "您的%s刚刚完成金币任务(%s)，赚了%s元，赚大钱给您奖励%s元。";
//			}
//
//			String alertBody = String.format(alert, target, appName, MoneyUtils.fen2yuanS(amount), MoneyUtils.fen2yuanS(shareAmount));
//			
//			PushResult result = pushDo(userId, 0l, alertBody, params, TYPE_FRIEND_PROMOT, 0, true);
//			return result.isSuccess();
//		} catch (Exception e) {
//			logger.error(logInfo, e);
//			return false;
//		}
//	}
//	/**
//	 * 批量发布push
//	 * 
//	 * @return
//	 */
//	public int batchPush(int cityid, String key, List<Long> uids, String content, String target, String remark, UserMisInfo userMis) {
//		if (uids == null || uids.size() == 0)
//			return 0;
//		int pushBatchId = pushBatchService.add(cityid, CollectionUtil.fromLongListToString(uids), content, target, remark, uids.size(), userMis.getUsername(),
//				PushBatch.ctype_push);
//		List<SysPush> pushes = new ArrayList<SysPush>();
//		for (long uid : uids) {
//			SysPush syspush = new SysPush(uid, content, target, pushBatchId);
//			pushes.add(syspush);
//		}
//
//		if (asyncJob == null) {
//			asyncJob = new FeedbackAsyncJob<SysPush>("sys_push", 2);
//		}
//		asyncJob.submit(pushBatchId, key, pushes, handler);
//
//		return pushes.size();
//	}

	public JobResult getPushResults(String name) {
		return asyncJob.getJobResult(name);
	}

	private SysPushHandler handler = new SysPushHandler();

	private class SysPushHandler implements JobHandler<SysPush> {
		@Override
		public boolean handle(SysPush t) {
			//return notifySysPrompt(t.getUserid(), t.getContent(), t.getTarget(), t.getPushBatchId());
			return true;
		}
	}

	private static class SysPush {
		private long userid;
		private String content;
		private String target;
		private int pushBatchId;

		public SysPush(long userid, String content, String target, int pushBatchId) {
			super();
			this.userid = userid;
			this.content = content;
			this.target = target;
			this.pushBatchId = pushBatchId;
		}

		public long getUserid() {
			return userid;
		}

		public String getContent() {
			return content;
		}

		public String getTarget() {
			return target;
		}

		public int getPushBatchId() {
			return pushBatchId;
		}
	}
	
//	private class AllUserSysPushTask extends RecursiveAction{
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 4483913539981224676L;
//		private Long[] userIds;
//		private String content;
//		
//		public AllUserSysPushTask(Long[] userIds, String content) {
//			this.userIds = userIds;
//			this.content = content;
//		}
//		
//		public void doPush(){
//			for(Long value:userIds){
//				notifyUserSystemPrompt(value, content);
//			}
//		}
//		
//		@Override
//		protected void compute() {
//			if(userIds.length < sysPushBlockSize){
//				doPush();
//			}else{
//				int len = userIds.length;
//				int middle = (len)/2;
//				AllUserSysPushTask t1 = new AllUserSysPushTask(Arrays.copyOfRange(userIds, 0, middle), content);
//				AllUserSysPushTask t2 = new AllUserSysPushTask(Arrays.copyOfRange(userIds,middle, len), content);
//				invokeAll(t1, t2);
//				
//			}
//		}
//	}
}
