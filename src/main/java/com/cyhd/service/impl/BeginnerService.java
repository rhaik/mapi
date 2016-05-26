package com.cyhd.service.impl;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.*;
import com.cyhd.service.push.PushService;
import com.cyhd.service.vo.AppTaskVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 新手任务
 * @author luckyee
 *
 */
@Service
public class BeginnerService extends BaseService {

	@Resource
	private UserIncomeService userIncomeService;
	
	@Resource
	private UserTaskService userTaskService;
	
	@Resource
	private AppTaskService appTaskService;
	
	@Resource
	private PushService pushService;
	
	@Resource
	private UserMessageService userMessageService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserFriendService userFriendService;
	
	@Resource
	private UserArticleTaskService userArticleTaskService;
	
	@Resource
	private TransArticleTaskService transArticleTaskService;
	
	@Resource 
	private UserDrawService userDrawService;
	/**
	 * “邀请好友”任务完成
	 * @param userId
	 */
	private void onInviteTaskComplete(User u,String idfa,int clienttype) {
		try {
			int userId = u.getId();
			AppTaskVo vo = appTaskService.getSystemInviteTask();
			AppTask appTask = vo.getAppTask();
			App app = vo.getApp();

			UserTask ut = userTaskService.addSystemTask(userId, appTask, u.getDid(),idfa); // 添加任务
			if (ut == null) { // 已完成过
				userService.setTaskInviteComplete(userId);
				return;
			}
			userIncomeService.addInviteTaskIncome(userId, ut.getId(), appTask.getAmount()); // 给钱
			try{
				userMessageService.addUserBeginnerTaskMessage(userId, app, appTask, ut,clienttype); // 消息通知
			}catch(Exception e){
				logger.error("UserMessageService addUserBeginnerTaskMessage error!", e);
			}

			userService.setTaskInviteComplete(userId);
		} catch (Exception e) {
			logger.error("BeginnerService onInviteTaskComplete error!", e);
		}
	}
	
	/**
	 * 
	 * @param userId
	 */
	private void onFirstTryTaskComplete(User u,String idfa,int clientType){
		try{
			int userId = u.getId();
			AppTaskVo vo = appTaskService.getSystemAppTryTask();
			AppTask appTask = vo.getAppTask();
			App app = vo.getApp();
			
			UserTask ut = userTaskService.addSystemTask(userId, appTask, u.getDid(),idfa);  //添加任务
			if(ut == null){ //已完成过
				userService.setTaskAppComplete(userId);
				return;
			}
			userIncomeService.addFirstTryTaskIncome(userId, ut.getId(), appTask.getAmount());  //给钱
			userMessageService.addUserBeginnerTaskMessage(userId, app, appTask, ut,clientType);  //消息通知
			
			userService.setTaskAppComplete(userId);
		}catch(Exception e){
			logger.error("BeginnerService onFirstTryTaskComplete error!", e);
		}
	}

	/**
	 * 分享链接到社交网络
	 * @param userId
	 */
	public void onFirstShareTaskComplete(User u,String idfa,int clientType){
		try{
			int userId = u.getId();
			AppTaskVo vo = appTaskService.getSystemShareTask();
			AppTask appTask = vo.getAppTask();
			App app = vo.getApp();
			
			UserTask ut = userTaskService.addSystemTask(userId, appTask, u.getDid(),idfa);  //添加任务
			if(ut == null){ //已完成过
				userService.setTaskShareComplete(userId);
				return;
			}
			userIncomeService.addShareToFriendsIncome(userId, ut.getId(), appTask.getAmount());  //给钱
			userMessageService.addUserBeginnerTaskMessage(userId, app, appTask, ut,clientType);  //消息通知
			
			userService.setTaskShareComplete(userId);
		}catch(Exception e){
			logger.error("BeginnerService onFirstTryTaskComplete error!", e);
		}
	}
	/**
	 * 两个限时任务
	 * @param u
	 * @param idfa
	 */
	public void addPreTwoApptaskExtraReward(User u,String idfa){
		if(u.isTaskAppComplete()){
			logger.info("userId:{}已经完成过以前的限时任务",u.getId());
			userService.setTaskAppComplete(u.getId());
			return ;
		}

		int count = userTaskService.getUserFinshTaskNum(u.getId());
		if(count > 2){
			userService.setTaskAppComplete(u.getId());
			logger.info("userId:{}已经完成两个以上的限时任务",u.getId());
			return ;
		}
		
		if(count == 1){
			//新用户完成第一次显示任务 增加抽奖机会
			//userDrawService.addUserDraw(u.getId(), "您徒弟"+u.getName()+"完成第一次限时任务");
			userFriendService.onUserFriendFinshFirstAppTask(u);
		}

		AppTaskVo vo = appTaskService.getSystemAppTryTask();
		AppTask appTask = vo.getAppTask();
		boolean isNewUser = u.isRewardNewUserComplete();
		int amount = 20;

		//修改 状态 和 添加完成log
		long  taskId = 0;
		UserTask ut = null;
		try{
			if(count == 2||(isNewUser && count == 1)){
				logger.info("userId:{}已经完成两个限时任务",u.getId());
			    ut = userTaskService.addSystemTask(u.getId(), appTask, u.getDid(),idfa);  //添加任务
				if(ut == null){
					logger.info("userId:{},插入数据库为null,应该是已经完成了",u.getId());
					userService.setTaskAppComplete(u.getId());
					return ;
				}
				userService.setTaskAppComplete(u.getId());
			}
			if(userIncomeService.addPreTwoAppTaskIncome(u.getId(), taskId, amount, appTask.getName())){
				logger.info("userId:{},给钱成功-成前两个限时任务-发送消息",u.getId());
				try{
					userMessageService.notifyPreTwoAppTask(u.getId(), amount ,Constants.platform_ios,count,isNewUser); // 消息通知
				}catch(Exception e){
					logger.error("新手任务:-两个限时任务，cause By:{}",e);
				}
				}else{
				logger.error("userId:{},给钱失败--完成前两个限时任务",u.getId());
			}
		}catch(Exception e){
			logger.error("完成两个限时任务奖励出现异常,cause by:{}",e);
		}
	}
	
	public void addPreFiveFriendExtraReward(User user,String idfa){
		//以前的邀请好友 如果完成就不要往下走啦
		if(user.isTaskInviteComplete()){
			logger.info("userId:{}已经完成原有的邀请好友任务",user.getId());
			userService.setTaskInviteComplete(user.getId());
			return;
		}
		int count = userFriendService.countUserFriends(user.getId());
		if(count <= 0){
			logger.error("邀请人数为0,userId:{}",user.getId());
			return ;
		}
		if(count > 5){
			logger.info("userId:{}邀请人数超过五人",user.getId());
			userService.setTaskInviteComplete(user.getId());
			return;
		}
		userFriendService.removeFriendCountCache(user.getId());
		
		AppTaskVo vo = appTaskService.getSystemInviteTask();
		AppTask appTask = vo.getAppTask();
		try{
			if(count == 5){
				UserTask ut = userTaskService.addSystemTask(user.getId(), appTask, user.getDid(),idfa);  //添加任务
				if(ut == null){
					userService.setTaskInviteComplete(user.getId());
					logger.info("userId:{}插入数据库为null",user.getId());
					return ;
				}
				userService.setTaskInviteComplete(user.getId());
			}

			int amount = 20;
			if(userIncomeService.addPreTwoAppTaskIncome(user.getId(), 0, amount, appTask.getName())){
				logger.info("userId:{},给钱成功-邀请五个好友-发送消息",user.getId());
				try{
					userMessageService.notifyInvitePreFriend(user.getId(), "", count, amount, UserSystemMessage.PUSH_CLIENT_TYPE_ALL);
				}catch(Exception e){
					logger.error("邀请五个好友push,cause by:{}",e);
				}
			}else{
				logger.info("userId:{},给钱失败-邀请五个好友",user.getId());
			}
		}catch(Exception e){
			logger.error("邀请五个好友奖励:cause by：{}",e);
		}
	}
	
	public void addTranArticleTaskReward(User u,TransArticleTask task){
		if(u.isTranArticleComplete()){
			logger.info("用户已经完成新手任务-转发任务,userId:{},taskId:{}",u.getId(),task.getId());
			return ;
		}
		try{
			UserArticleTask userTask = userArticleTaskService.getArticleTask(u.getId(), task.getId());
			if(userTask == null || userTask.getArticle_id() > 10){
				logger.error("获取的转发任务有问题");
				userService.setTranArticleComplete(u.getId());
				return ;
			}
			
			if(userIncomeService.addFirstTranArticleTaskIncome(u.getId(),userTask.getId(), task.getAmount())){  //给钱
				userMessageService.addBeginnerTranArticleMessage(u.getId(), task.getAmount(), userTask.getClient_type());;  //消息通知
				userService.setTranArticleComplete(u.getId());
			}
		}catch(Exception e){
			logger.error("新手任务中-第一次转发任务,userId:{},TranArticleId:{},cause by:{}",u.getId(),task.getId(),e.getMessage());
		}
	}


	/**
	 * 钥匙版安装入口的奖励
	 * @param user
	 */
	public void addYaoshiClipReward(User user){
		if (user.isYaoshiClipComplete()){
			logger.warn("用户已经完成新手任务-钥匙入口任务,userId:{}", user.getId());
			return ;
		}
		int userId = user.getId();
		AppTaskVo vo = appTaskService.getSystemYaoshiTask();

		if (vo == null){
			logger.warn("没有钥匙入口的任务，不给用户发奖");
			return;
		}

		AppTask appTask = vo.getAppTask();
		App app = vo.getApp();

		UserTask ut = userTaskService.addSystemTask(userId, appTask, user.getDid(), user.getIdfa());  //添加任务
		if(ut == null){ //已完成过
			userService.setYaoshiClipComplete(userId);
			return;
		}
		userIncomeService.addBeginnerTaskIncome(userId, ut.getId(), appTask.getAmount(), appTask.getName());  //给钱
		userMessageService.addUserBeginnerTaskMessage(userId, app, appTask, ut, Constants.platform_ios);  //消息通知

		userService.setYaoshiClipComplete(userId);

		logger.info("用户完成安装钥匙入口的任务，用户：{}, 新手任务-{}, 奖励：{}", user.getId(), appTask.getName(), appTask.getAmount());
	}
}
