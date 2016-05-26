package com.cyhd.service.impl;

import javax.annotation.Resource;

import com.cyhd.service.impl.doubao.ProductActivityCalculateService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.impl.doubao.ThirdShishicaiService;
import com.cyhd.service.util.GlobalConfig;

/**
 * 公用job service
 *
 */
@Service
public class CommonJobsService extends BaseService{

	@Resource
	private AppTaskService appTaskService;
	
	@Resource
	private UserRankService userRankService;
	
	@Resource
	private AppUpdateService appUpdateService;
	
	@Resource
	private UserTaskFinishJobService userTaskFinishJobService;
	
	@Resource
	private UserMessageService userMessageService;
	
	@Resource
	private TransArticleTaskService transArticleTaskService;
	@Resource
	private ProductActivityCalculateService activityCalculateService;
	
	@Resource
	private AccountService accountService;
	
	@Resource
	private UserArticleTaskService userArticleTaskService ;
	
	@Resource
	private TranArticleCloseAccountService tranArticleCloseAccountService;
	
	@Resource
	private UserTaskReportService userTaskReportService;
	
	@Resource
	private GameService gameService;

	@Resource
	private UserDrawService userDrawService;
	
	@Resource
	private ThirdShishicaiService thirdShishicaiService;

	@Resource
	HongbaoActivityService hongbaoService;
	
	@Resource
	private AppChannelQuickTaskService appChannelQuickTaskService;
	
	@Resource
	private UserFriendService userFriendService;
	
	@Scheduled(fixedDelay = Constants.minutes_millis * 1)
	public void reloadApps(){
		appTaskService.reloadApps();
	}
	
	@Scheduled(fixedDelay = Constants.minutes_millis * 1)
	public void reloadArticles(){
		transArticleTaskService.reloadArticles();
	}
	
	@Scheduled(fixedDelay = Constants.minutes_millis * 5)
	public void reloadRanks(){
		if(GlobalConfig.isApiServer)
			userRankService.reload();
	}
	
	@Scheduled(fixedDelay = Constants.minutes_millis * 1)
	public void reloadUpdate(){
		if(GlobalConfig.isApiServer)
			appUpdateService.reload();
	}
	
	@Scheduled(fixedDelay = Constants.minutes_millis * 30, initialDelay=20 * 1000)
	public void startLoad(){
		if(GlobalConfig.runJob)
			userTaskFinishJobService.startLoad();
	}
	
	@Scheduled(fixedDelay = Constants.minutes_millis * 1, initialDelay=20 * 1000)
	public void startLoadSysMessage(){
		if(GlobalConfig.runJob)
			userMessageService.loadAndSendSysPush();
	}
	@Scheduled(fixedDelay = Constants.minutes_millis * 1)
	public void notifyExpiredAppMessage(){
		if(GlobalConfig.runJob)
			userMessageService.notifyExpiredAppMessage();
	}
	@Scheduled(fixedDelay = Constants.minutes_millis * 10)
	public void reloadAccountId(){
		if(GlobalConfig.isApiServer){
			accountService.loadIds();
		}
	}
	
	/*@Scheduled(fixedDelay = Constants.minutes_millis * 10)
	public void notifyNearExpireArticleTaskmessage(){
		if(GlobalConfig.runJob){
			userArticleTaskService.NotifyNearExpireTask();
		}
	}*/
	
	/**一个分钟执行一次**/
	@Scheduled(fixedDelay = Constants.minutes_millis)
	public void executeArticleTaskAmount(){
		if(GlobalConfig.runJob){
			tranArticleCloseAccountService.closeAccount();
		}
	}
	/**一个分钟执行一次**/
	@Resource
	private UserTaskDistinctService userTaskDistinctService;
	@Scheduled(fixedDelay = Constants.minutes_millis)
	public void executeTaskDistinct(){
		if(GlobalConfig.runJob){
			//userTaskDistinctService.startDistinct();
		}
	}

	@Scheduled(fixedDelay = Constants.minutes_millis * 5)
	public void reloadShareHosts(){
		if (GlobalConfig.isApiServer) {
			transArticleTaskService.reloadShareHosts();
		}
	}
	
	@Scheduled(fixedDelay = Constants.minutes_millis * 5)
	public void loadGameList(){
		if(GlobalConfig.isApiServer){
			gameService.loadGames();
		}
	}


	/**
	 * 一元夺宝活动开奖<br/>
	 * 每五分钟开奖一次
	 */
	@Scheduled(cron = "0 0/5 * * * *")
	public void announceProductActivity(){
		if (GlobalConfig.runJob) {
			activityCalculateService.announceProductActivity();
		}
	}


	/**
	 * 根据规则，自动创建一元夺宝活动
	 */
	@Scheduled(fixedDelay = Constants.minutes_millis * 10, initialDelay= Constants.minutes_millis)
	public void buildProductActivity(){
		if (GlobalConfig.runJob) {
			activityCalculateService.buildProductActivity();
		}
	}

	/**
	 * 处理过期的夺宝活动
	 */
	@Scheduled(fixedDelay = Constants.hour_millis, initialDelay = Constants.minutes_millis * 10)
	public void expireDuobaoActivity(){
		if (GlobalConfig.runJob){
			activityCalculateService.expireProductActivity();
		}
	}

	/**
	 * 定时获取时时彩开奖数据<br/>
	 * 第3分钟开始，每5分钟抓取一次时时彩数据
	 */
	@Scheduled(cron = "0 3/5 * * * *")
	public void fetchShishicaiData(){
		if (GlobalConfig.runJob) {
			thirdShishicaiService.fetchShishicaiData();
		}
	}

	/**
	 * 重设红包数量
	 */
	//@Scheduled(cron = "0 0 11,15,20 * * *")
	public void resetSystemHongbaoNum(){
		if (GlobalConfig.runJob){
			hongbaoService.resetSystemHongbaoNum();
		}
	}

	/***
	 * 定时加载第三方墙上的数据 到我们的本地种
	 */
	@Scheduled(cron="*/30 * 5-23 * * *")
	public void loadChanelQuick(){
		if(GlobalConfig.runJob){
			appChannelQuickTaskService.loadByJob();
		}
	}
	
//	@Scheduled(cron="0 */1 * * * *")
//	public void reportChannelTaskFinsh(){
//		if(GlobalConfig.runJob){
//			userTaskReportService.reportChannelTaskFinsh();
//		}
//	}
	
	//@Scheduled(cron="0 0 0 * * ?")
	public void addInviteRankData(){
		if(GlobalConfig.runJob){
			//userFriendService.addCheatInviteData();
		}
	}
}
