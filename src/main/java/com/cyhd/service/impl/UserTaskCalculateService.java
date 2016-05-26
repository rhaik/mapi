package com.cyhd.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.service.dao.db.mapper.UserTaskFinishAuditMapper;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserAppMessage;
import com.cyhd.service.dao.po.UserSystemMessage;
import com.cyhd.service.dao.po.UserTask;
import com.cyhd.service.vo.AppTaskChannelVo;
import com.cyhd.web.common.ClientInfo;


@Service
public class UserTaskCalculateService extends BaseService {

	@Resource
	private UserTaskService userTaskService;
	
	@Resource
	private AppTaskService appTaskService;
	
	@Resource
	UserMessageService userMessageService;
	
	@Resource
	UserFriendService userFriendService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserIncomeService userIncomeService;
	
	@Resource
	private UserTaskFinishAuditMapper userTaskFinishAuditMapper;
	
	@Resource
	private BeginnerService beginnerService;
	

	@Resource
	private ChannelService channelService;
	
	@Resource
	private AppChannelQuickTaskService appChannelQuickTaskService;
	
	public void onFinishTask(User user, App app, AppTask appTask, UserTask ut, boolean warning, String reason,int ClientType) {
		boolean isCallBack = false;
		if(appTask.getIschannel() > 0 ) {
			//改到检测到任务上报处
			//检查是否有回调
			AppTaskChannelVo appTaskChannel = channelService.getAppTaskChannel(appTask.getId());
			isCallBack = appTaskChannel.getAppChannel().isCallBack();
//			//该任务是不是要上报
//			if(!isCallBack && appTaskChannel.getAppTaskChannel().isNeedReport()){
//				logger.info("有渠道回调,userId:{},idfa:{},taskId:{},appName:{}",user.getId(),user.getIdfa(),appTask.getId(),app.getName());
//				ClientInfo clientInfo = new ClientInfo();
//				clientInfo.setIpAddress(ut.getUser_ip());
//				clientInfo.setIdfa(ut.getIdfa());
//				boolean  callBack = channelService.reportChannel(appTask,appTaskChannel,clientInfo);
//				logger.info("有渠道回调,userId:{},idfa:{},taskId:{},appName:{},上报任务:{}",user.getId(),user.getIdfa(),appTask.getId(),app.getName(),callBack);
//			}
		}else if (appTask.isVendorTask()){ //需要厂商回调
			isCallBack = true;
		}else if(appTask.isQuicktask()){
			//if(ut.getReport_status() == 0){
				//快速任务都不回调
				AppTaskChannelVo appTaskChannel = channelService.getAppTaskChannel(appTask.getId());
				isCallBack = appTaskChannel.getAppTaskChannel().isNeedReport()?false:true;
//				if(!isCallBack){
//					ClientInfo clientInfo = new ClientInfo();
//					clientInfo.setIdfa(ut.getIdfa());
//					clientInfo.setIpAddress(ut.getUser_ip());
//					if(clientInfo.getIdfa() == null){
//						logger.info("clientinfo not has idfa");
//						clientInfo.setIdfa(user.getIdfa());
//					}
//					appChannelQuickTaskService.reportTaskFinsh(clientInfo, appTask,ut,user);
//				}
//			}
		}

		boolean isReward = !warning;

		//如果是回调，则检查是否已经发过奖励
		if(isCallBack) {
			if (ut.getReward() == 1){
				isReward = false;
			}else if (ut.getConfirm_finish() == 1 || appTask.getDirect_reward() == 1){
				isReward = true;
			}else {
				isReward = false;
			}
		}

		//1. 修改用户任务表
		boolean result = userTaskService.onFinishTask(user.getId(), ut.getId(), isReward);
		if(!result){
			logger.error("User Task finish Task error!");
			return;
		}
		//2 修改任务统计（完成数+1）
		appTaskService.onTaskFinished(ut.getTask_id());  
		
		//3.如果疑似作弊，则进入人工审核
		if(warning){
			userTaskFinishAuditMapper.addAudit(user.getId(), ut.getId(), reason, 0);
		}

		//4.如果奖励了，则执行发奖的逻辑
		if (isReward){
			userMessageService.addUserAppMessage(user, app, appTask, ut, UserAppMessage.STATUS_APP_COMPLETE);  //任务完成
			onUserTaskFinishCheckOK(user, app, appTask, ut,ClientType);
		}
	}
	/**
	 * 第三方回调确认完成任务
	 * 
	 * @param user
	 * @param app
	 * @param appTask
	 * @param ut
	 */
	public void onConfirmFinishTask(User user, App app, AppTask appTask, UserTask ut) {
		//只判断是否已经奖励过，不判断完成状态。如果用户放弃了任务，也不给他发奖
		boolean isReward = (ut.getReward() == 0 && ut.getStatus() != UserTask.STATUS_ABORTED);

		boolean result = userTaskService.confirmFinishTask(ut.getId(), user.getId(), isReward);
		if(!result){
			logger.error("User Confirm Task finish Task error!");
			return;
		}
		//给用户发放奖励
		if(isReward) {
			onUserTaskFinishCheckOK(user, app, appTask, ut, UserSystemMessage.PUSH_CLIENT_TYPE_IOS);
		}
	}
	
	/**
	 * job执行，异步
	 * @param userTaskId
	 * @throws Exception
	 */
	public void onUserTaskFinishCheckOK(long userTaskId) throws Exception {
		try{
			UserTask ut = userTaskService.getUserTaskById(userTaskId);
			User user = userService.getUserById(ut.getUser_id());
			App app = appTaskService.getApp(ut.getApp_id());
			AppTask appTask = appTaskService.getAppTask(ut.getTask_id());
			//设置给用户结算
			userTaskService.onTaskReward(user.getId(), ut.getId());
			
			onUserTaskFinishCheckOK(user, app, appTask, ut,UserSystemMessage.PUSH_CLIENT_TYPE_IOS);
		}catch(Exception e){
			logger.error("Audit Job Finish error!", e);
			throw e;
		}
	}
	
	public void onUserTaskFinishCheckFail(long userTaskId, String reason){
		//消息
		UserTask ut = userTaskService.getUserTaskById(userTaskId);
		User user = userService.getUserById(ut.getUser_id());
		App app = appTaskService.getApp(ut.getApp_id());
		AppTask appTask = appTaskService.getAppTask(ut.getTask_id());
		userMessageService.addUserAppMessage(user, app, appTask, ut, UserAppMessage.STATUS_APP_AUDIT_FAIL, reason);  //审核不通过
	}
	
	private void onUserTaskFinishCheckOK(User user, App app, AppTask appTask, UserTask ut,int clientType){
		//自己的收入 加上额外的
		int userIncomeAmount  = userTaskService.getAmountByActivity(appTask.getAmount(), user.getId());
		//int userIncomeAmount  =  appTask.getAmount();
		userTaskService.updateEarned_amount(user.getId(),ut.getId(), userIncomeAmount);
		//钱包
		userIncomeService.addAppTaskIncome(user.getId(), ut.getId(),userIncomeAmount, app.getName());
		//消息
		userMessageService.addUserAppMessage(user, app, appTask, ut, UserAppMessage.STATUS_APP_AUDIT_SUCCESS);  //审核通过
		
		//给好友分成
		int invitorId = userFriendService.getInvitor(user.getId());
		if(invitorId > 0){
//			float shareRate = appTask.getShare_rate();
//			//防止填写错误
//			if (shareRate <= 0 || shareRate > 0.5){
//				shareRate = 0.1f;
//			}
			User invitor = userService.getUserById(invitorId);
			//判断师傅是否被封禁
			if(!invitor.isBlack()){
	//			//4月分成修改成0.2，上任务的时候修改
	//			int shareAmount = Math.round(appTask.getAmount() * shareRate);
				//新增给师傅发钱 前五个完成的显示任务 每一个1块
				int shareAmount = userTaskService.getAmountOptimizeInvite(appTask, user, invitorId);
				// 给好友分钱
				userIncomeService.addFriendShareIncome(invitorId, user.getId(), ut.getId(), shareAmount, app.getName(), 1);
				// 给好友发消息
				this.userMessageService.addUserShareMessage(invitorId, user, app, appTask, ut, shareAmount,1, 0);
	
				//徒孙分成
				int invitorsInvitorId = userFriendService.getInvitor(invitorId);
				if(invitorsInvitorId > 0){
					float shareShareRate = appTask.getShare_rate() >= 0.2 ? 0.05f : 0.01f; //徒弟分成比例提高到20%及以上，徒孙为5%，否则为1%
					int shareShareAmount = Math.round(appTask.getAmount() * shareShareRate);
	
					if (shareShareAmount > 0) {
						userIncomeService.addFriendShareIncome(invitorsInvitorId, user.getId(), ut.getId(), shareShareAmount, app.getName(), 2);
						// 给好友发消息
						userMessageService.addUserShareMessage(invitorsInvitorId, user, app, appTask, ut, shareShareAmount, 2, invitorId);
					}
				}
			}
		}
		
		// 如果用户新手任务没有完成
		if(!user.isTaskAppComplete()){
			//beginnerService.onFirstTryTaskComplete(user,ut.getIdfa(),clientType);
			//完成两次限时任务
			beginnerService.addPreTwoApptaskExtraReward(user,ut.getIdfa());
		}
	}

	public void onFinishDownload(User user, App app, AppTask appTask, UserTask ut) {
		userTaskService.onFinishDownload(user.getId(), ut.getId());
		userMessageService.addUserAppMessage(user, app, appTask, ut, UserAppMessage.STATUS_APP_DOWNLOADS);
	}
	
	
	
}
