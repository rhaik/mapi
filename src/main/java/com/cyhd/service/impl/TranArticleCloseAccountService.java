package com.cyhd.service.impl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.service.dao.po.TransArticle;
import com.cyhd.service.dao.po.TransArticleTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserArticleMessage;
import com.cyhd.service.dao.po.UserArticleTask;

@Service
public class TranArticleCloseAccountService extends BaseService{

	@Resource
	private UserArticleTaskService userArticleTaskService;
	
	private volatile boolean accounting = false;
	
	@Resource
	private UserIncomeService userIncomeService;
	
	@Resource
	private UserMessageService userMessageService;
	
	@Resource
	private TransArticleTaskService transArticleTaskService;
	
	@Resource
	private UserFriendService userFriendService;
	
	@Resource
	private UserService userService;
	//不采用forkjoin 采用线程池
	//private ForkJoinPool executor = null;
	
	private ExecutorService closeAccountES = null;
	private final int nThread =4;
	
	@PostConstruct
	public void init(){
		ThreadFactory factory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("close-account-job");
				return t;
			}
		};
		closeAccountES = Executors.newFixedThreadPool(nThread, factory);
	}
	
	/**
	 * 任务结束，处理奖励的job
	 */
	public void closeAccount(){
		if(accounting){
			logger.info("结算转发任务，已经处理中");
			return ;
		}
		logger.info("处理转发任务结算-开始");
		accounting = true;

		int size = 10;
		List<TransArticleTask> taskIdList = null;
		try {
			while(true){
				taskIdList = transArticleTaskService.getNotExecuteAccount(0, size);
				if(taskIdList == null||taskIdList.isEmpty()){
					logger.info("获取id为null,程序退出此时start:{},default_size:{}",0,size);
					break;
				}
				executeTaskIdList(taskIdList);
				if(taskIdList.size() < size) {
					logger.info("获取id为长度不足默认长度,程序退出此时start:{},default_size:{},taskSize:{}", 0, size, taskIdList.size());
					break;
				}
				try{ Thread.sleep(3000);}catch(Exception e){}
			}
		} catch (Exception e) {
			logger.error("处理程序出现未知错误:{}",e);
		}finally{
			accounting = false;
			logger.info("处理转发任务结算-结束");
		}
	}
	
	private void executeTaskIdList(List<TransArticleTask> taskIdList){
		//处理每一个任务Id
		for(TransArticleTask task:taskIdList){
			if(task == null ){
				continue;
			}
			logger.info("处理任务:taskId:{} 开始",task.getId());
			if(task.getExecute_flag() != TransArticleTask.EXECUTE_FLAG_NO_START){
				logger.info("状态不是开始状态taskId：{}",task.getId());
				continue;
			}

			//修改任务的操作状态
			if(transArticleTaskService.executeAccounting(task.getId()) == false){
				logger.info("修改数据为处理中失败:taskid:{}",task.getId());
				continue;
			}

			executeTask(task);
		}
	}
	
	public void executeTask(final TransArticleTask task){
		int size = 100;
		List<UserArticleTask> userTaskList = null;
		
		long start = 0;

		while(true){
			userTaskList = userArticleTaskService.getUserArticleTasksToCloseAccount(task.getId(), start, size);
			if(userTaskList == null || userTaskList.isEmpty()){
				logger.info("获取用户任务列表为null:{}",task.getId());
				break;
			}
			
			for(final UserArticleTask userTask : userTaskList){
				if(userTask == null){
					logger.error("获取用户任务为null:{}",task.getId());
					continue;
				}
				start = userTask.getId();
				
				final int amount = task.getRewardAmount(userTask.getView_num());
				logger.info("处理 user_taskid={},user_id:{},view_num:{},amount:{}",start, userTask.getUser_id(),userTask.getView_num(),amount);
				if(amount <= 0){
					//设置为过期状态
					userArticleTaskService.setExpired(userTask.getId());
					continue;
				}
				if(userTask.isReward()){
					logger.info("user_task_id:{},userId:{},已经获得奖励:",userTask.getId(),userTask.getUser_id());
					continue;
				}else{
					logger.info("user_task_id:{},userId:{},没有获得奖励:处理奖励",userTask.getId(),userTask.getUser_id());
				}
				
				closeAccountES.execute(new Runnable() {
					@Override
					public void run() {
						try{
							if(addReward(userTask.getId(),userTask.getUser_id())){
								addUserIncome(task, userTask.getUser_id(), userTask.getId(), userTask.getClient_type(),amount,userTask.getView_num());
							}
						}catch(Exception e){
							logger.error("处理收入出现异常,taskId:{},cause by:{}",userTask.getId(),e);
						}
					}
				});
			}
		}
		try{
			if(transArticleTaskService.executeAccounted(task.getId())){
				logger.info("修改状态为已处理,成功:{}",task.getId());
			}else{
				logger.error("修改状态为已处理,失败:{}",task.getId());
			}
		}catch(Exception e){
			logger.error("修改状态为完成出错,taskId:{}",task.getId());
		}
	}
	
	@PreDestroy
	public void shutdown(){
		if(closeAccountES != null){
			if(closeAccountES.isShutdown() ==  false){
				closeAccountES.shutdown();
			}
		}
	}
	/**
	 * 
	 * @param transArticleTask 转发任务
	 * @param task_user_id 做任务的user_id
	 * @param user_task_id 用户的任务id
	 * @param clientType 客户端类型
	 * @param view_num 有多少人看过
	 * @return
	 */
	private boolean addUserIncome(TransArticleTask transArticleTask,int task_user_id,long user_task_id,int clientType,int amount,int view_num){
		StringBuilder sb = new StringBuilder(200);
		sb.append(" 开始处理收入");
		sb.append(",user_id:").append(task_user_id);
		sb.append(",任务ID:").append(user_task_id);
		sb.append(",clientType:").append(clientType);
		sb.append(",amount:").append(amount);
		logger.info(sb.toString());
		
		//插入收入成功 发推送
		if(userIncomeService.addUserArticleIncome(task_user_id, user_task_id, transArticleTask.getName(), amount)){
			logger.info("增加UserId:{},收入:{},成功",task_user_id,amount);
			//send message
			userMessageService.notifyTranArticleMessage(task_user_id, transArticleTask.getName(),amount, 
					view_num, clientType,UserArticleMessage.MESS_TYPE_PASS,transArticleTask.getId(),user_task_id);
			//增加完成人数
			transArticleTaskService.onTaskFinished(transArticleTask.getId());
			//获取邀请人
			int invitorId = userFriendService.getInvitor(task_user_id);
			logger.info("有师傅-师傅ID:{}",invitorId);
			if(invitorId > 0){
				//增加朋友收入
				int shareShareAmount = userFriendService.getShareAmount(invitorId, amount);
				if(shareShareAmount <= 0){
					logger.info("朋友分成为0，userId:{},frendId:{}",task_user_id,invitorId);
					return false;
				}
				logger.info("增加师傅Id:{},收入:{}",invitorId,shareShareAmount);
				boolean addFriend  = userIncomeService.addArticleFriendShareIncome(invitorId, task_user_id, user_task_id, shareShareAmount, transArticleTask.getName(),1);
				if(addFriend == false){
					logger.error("转发任务中-增加师傅收入:用户ID:{},师傅ID:{},金额:{},任务Id:{}",task_user_id,invitorId,shareShareAmount,transArticleTask.getId());
					return addFriend;
				}
				//朋友消息 怎么处理 加字段么
				User task_user = userService.getUserById(task_user_id);
				TransArticle article = transArticleTaskService.getTransArticle(transArticleTask.getArticle_id());
				//发朋友信息
				userMessageService.addTranArticleShareMessage(invitorId, task_user,article, transArticleTask,amount,  task_user_id, shareShareAmount, 1, invitorId);
				logger.info("收入朋友成功");
				return true;
			}
		}else{
			logger.error("转发任务插入收入失败,userId:{},user_task_id:{},amount:{},clienttype:{}",task_user_id,user_task_id,transArticleTask.getAmount(),clientType);
		}
		return false;
	}
	
	/**
	 * 数据库插入奖励
	 * @param userArticleTask
	 * @return
	 */
	public boolean addReward(long taskId,int user_id ){
		// 发放奖励  奖励日志
		// 师傅奖励
		boolean onRewred = userArticleTaskService.onReward(taskId, user_id);
		if(onRewred == false){
			logger.error("user_task_id:{},user_id:{},数据库已经获得奖励，程序返回",taskId,user_id);
		}else{
			logger.info("user_task_id:{},userId:{},数据库记录奖励成功:",taskId,user_id);
		}
		return onRewred;
	}
}
