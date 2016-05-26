package com.cyhd.web.action.web;

import com.cyhd.service.dao.po.TransArticleTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserEnchashmentAccount;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.CookieUtil;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.service.vo.AppTaskVo;
import com.cyhd.service.vo.UserArticleTaskVo;
import com.cyhd.service.vo.UserTaskVo;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/discovery")
public class DiscoveryAction extends BaseAction {

	@Resource
	UserService userService;
	
	@Resource
	AppTaskService appTaskService;
	
	@Resource
	UserTaskService userTaskService;
	
	@Resource
	UserRankService userRankService;
	
	@Resource
	UserIncomeService userIncomService;
	
	@Resource
	UserFriendService userFriendService;
	
	@Resource
	private TaskUpdateTimeHintService taskUpdateTimeHintService;

	@Resource
	private TransArticleTaskService transArticleTaskService;
	
	@Resource
	private AccountService accountService;
	
	@Resource
	private WeixinArticleService weixinArticleService;
	
	@Resource
	private AppChannelQuickTaskService appChannelQuickTaskService;

	@Resource
	UserIncomeService userIncomeService;

	@Resource
	UserEnchashmentService userEnchashmentService;
	
	private static final String prefix = "/web/discovery/";

	@RequestMapping(value = "/discover.html")
	public ModelAndView discover(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "discover.html.ftl");
		
		mv.addObject("title", "发现");
		return mv;
	}


	@RequestMapping(value = "/earn.html")
	public ModelAndView earn(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "earn.html.ftl");

		User user = getUser(request);
		ClientInfo info = super.getClientInfo(request);

		int todayIncome = userIncomeService.countUserTodyIncome(user.getId());

		int income = 0, balance = 0, enchashing = 0;
		UserIncome userIncome = userIncomeService.getUserIncome(user.getId());
		if (userIncome != null){
			income = userIncome.getIncome();
			balance = userIncome.getBalance();
			enchashing = userIncome.getEncashing();
		}else {
			//可能还没有收入数据
			userIncomeService.createNewUserIncome(user.getId());
		}

		mv.addObject("user", user);
		mv.addObject("todayIncome", todayIncome);
		mv.addObject("income", income);
		mv.addObject("balance", balance);
		mv.addObject("enchashing", enchashing);

		UserEnchashmentAccount userAccount = userEnchashmentService.getUserEnchashmentAccount(user.getId());
		if(userAccount == null) userAccount = new  UserEnchashmentAccount();
		mv.addObject("income", income);
		mv.addObject("userAccount", userAccount);
		mv.addObject("enchashStages", UserEnchashmentService.EnchashmentStage.values());
		mv.addObject("inWeixinEnchashTime", userEnchashmentService.isInWeixinEnchashTime());
		mv.addObject("underWeixinLimit", userEnchashmentService.isUnderWeixinLimit());


		Calendar cal = GregorianCalendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if(hour < 5){
			return mv;
		}

		//获取限时任务列表
		List<UserTaskVo> futureTasks = appTaskService.getFutrueTaskVos();
		Map<String, String> extraParams = new HashMap<String, String>(2);
		extraParams.put("ua", request.getHeader("user-agent"));
		List<UserTaskVo > quickTaskList = appChannelQuickTaskService.getUserTask(info, user, extraParams);
		if(user.getId() == 1){
			logger.info("快速任务列表：{}",quickTaskList);
		}
		List<UserTaskVo> discoveryTaskList = new ArrayList<>();
		if(quickTaskList != null){
			discoveryTaskList.addAll(quickTaskList);
		}
		discoveryTaskList.addAll(appTaskService.getValidTaskVos());
		List<UserTaskVo> tasks = userTaskService.getTasks(user, discoveryTaskList);

		mv.addObject("inAppView", true);
		if(tasks != null && tasks.size() > 0) {
			//进行中的任务，获取其加密后的app信息
			Map<String, String> appInfoMap = tasks.stream().filter(t -> t.isApping() || t.isWaitingCallback()).collect(Collectors.toMap(t1 -> t1.getApp().getBundle_id(), t2 -> appTaskService.getEncryptedAppInfo(t2.getApp(), user.getUser_identity())));

			mv.addObject("appInfoMap", appInfoMap);
			mv.addObject("tasks", tasks);
			mv.addObject("futureTasks", futureTasks);
			int inviteNums = userFriendService.countUserFriends(user.getId());
			float shareRate = userFriendService.getExtraShareRateByAppTask(user.getId(),inviteNums)+1;

			String hintText = taskUpdateTimeHintService.getAppTaskUpdateTimehint();
			if (shareRate > 1){
				hintText = String.format("您收徒%d人，任务单价奖励为%.2f倍", inviteNums, shareRate);
			}
			mv.addObject("hint_text", hintText);
			mv.addObject("shareRate",shareRate);
		}
		mv.addObject("title", "手机试客");
		return mv;
	}
	
	@RequestMapping(value = "/tasks.html")
	public ModelAndView tasks(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		
		User user = getUser(request);
		
		ClientInfo info = super.getClientInfo(request);
		if(info.getAppVer() == null || info.getAppVer().startsWith("0.")){
			mv.addObject("tasks", "true");
			mv.setViewName("common/nodata.html.ftl");
			mv.addObject("description", "版本过低，请检测新版本并升级！");
			mv.addObject("title", "限时任务");
			return mv;
		}
		
		Calendar cal = GregorianCalendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if(hour < 5){
			mv.addObject("tasks", "true");
			mv.setViewName("common/nodata.html.ftl");
			mv.addObject("title", "限时任务");
			return mv;
		}

		List<UserTaskVo> futureTasks = appTaskService.getFutrueTaskVos();
		Map<String, String> extraParams = new HashMap<String, String>(2);
		extraParams.put("ua", request.getHeader("user-agent"));
		List<UserTaskVo > quickTaskList = appChannelQuickTaskService.getUserTask(info, user, extraParams);
		if(user.getId() == 1){
			logger.info("快速任务列表：{}",quickTaskList);
		}
		List<UserTaskVo> discoveryTaskList = new ArrayList<>();
		if(quickTaskList != null){
			discoveryTaskList.addAll(quickTaskList);
		}
		discoveryTaskList.addAll(appTaskService.getValidTaskVos());
		List<UserTaskVo> tasks = userTaskService.getTasks(user, discoveryTaskList);
//		if(user.getId() == 1){
//			logger.info("最后的数据列表：{}",tasks);
//		}
		if(tasks != null && tasks.size() > 0) {
			String viewName = "/safari/new_tasks.html.ftl";
			if (request.getAttribute("fromSafari") != null){
				CookieUtil.setNewCookie("last_app_time", "" + System.currentTimeMillis() / 1000, response);
				viewName = "/safari/new_tasks.html.ftl";
			}else {
				mv.addObject("inAppView", true);
			}
			mv.setViewName(viewName);

			//进行中的任务，获取其加密后的app信息
			Map<String, String> appInfoMap = tasks.stream().filter(t -> t.isApping() || t.isWaitingCallback()).collect(Collectors.toMap(t1 -> t1.getApp().getBundle_id(), t2 -> appTaskService.getEncryptedAppInfo(t2.getApp(), user.getUser_identity())));

			mv.addObject("appInfoMap", appInfoMap);
			mv.addObject("user", user);
			mv.addObject("tasks", tasks);
			mv.addObject("futureTasks", futureTasks);
			int inviteNums = userFriendService.countUserFriends(user.getId());
			float shareRate = userFriendService.getExtraShareRateByAppTask(user.getId(),inviteNums)+1;
			String hintText = taskUpdateTimeHintService.getAppTaskUpdateTimehint();
			if (shareRate > 1){
				hintText = String.format("您收徒%d人，任务单价奖励为%.2f倍", inviteNums, shareRate);
			}
			mv.addObject("hint_text", hintText);
			mv.addObject("shareRate",shareRate);
		} else {
			mv.addObject("tasks", "true");
			mv.setViewName("common/nodata.html.ftl");
		}
		mv.addObject("title", "限时任务");
		return mv;
	}
	
	@RequestMapping(value = "/ranks.html")
	public ModelAndView ranks(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "ranks.html.ftl");
		
		User u = this.getUser(request);
		
		int allRank = userRankService.getUserAllRank(u.getId());
		int monthRank = userRankService.getUserMonthRank(u.getId());
		
		mv.addObject("topUsers", userRankService.getAllTopUsers());
		mv.addObject("monthTopUsers", userRankService.getMonthTopUsers());
		mv.addObject("user", u);
		mv.addObject("allRank", allRank);
		mv.addObject("monthRank", monthRank);
		mv.addObject("userIncome", userIncomService.getUserIncome(u.getId()));
		
		mv.addObject("title", "收入排行榜");
		return mv;
	}
	
	/**
	 * 收学徒
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/share.html")
	public ModelAndView share(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "share.html.ftl");
		
		User u = this.getUser(request);
		
		UserIncome ui = userIncomService.getUserIncome(u.getId());
		mv.addObject("userIncome", ui);
		mv.addObject("userFriendCount", userFriendService.countUserFriends(u.getId()));
		mv.addObject("userFriendAmount", ui.getShare_level1_total());
		
		mv.addObject("userGrandsonCount", userFriendService.countUserGrandson(u.getId()));
		mv.addObject("userGrandsonAmount", ui.getShare_level2_total());

		mv.addObject("todayInviteCount", userFriendService.countTodyInviteFriendByuserId(u.getId()));
		mv.addObject("user", u);
		mv.addObject("title", "收徒");

		UserAgentUtil.UserAgent userAgent = new UserAgentUtil.UserAgent(request);
		mv.addObject("isIOS", userAgent.isIPad() || userAgent.isIPhone());
		mv.addObject("isInApp", userAgent.isInAppView());
		return mv;
	}
	
//	/**
//	 * 新手教程
//	 * 
//	 * @param request
//	 * @param response
//	 * @return ModelAndView
//	 */
//	@RequestMapping(value = "/beginner.html", method = RequestMethod.GET)
//	public ModelAndView beginner(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ModelAndView mv = new ModelAndView();
//		mv.setViewName(prefix + "beginner.html.ftl");
//		User u = getUser(request);
//		
//		ConcurrentHashMap<String,UserTaskVo>  userTask = userTaskService.getUserSystemAppTask(u.getId());
//		
//		List<AppTaskVo> systemTask = appTaskService.getSystemAppTasks();
//		List<AppTaskVo> systemTask2 = new ArrayList<AppTaskVo>();
//		for(AppTaskVo st : systemTask) {
//			UserTaskVo ut = userTask.get(String.valueOf(st.getAppTask().getId()));
////			if (ut != null) {
////				st.setStatus(ut.getUserTask().getStatus());
////			}
//			AppTaskVo st2 = new AppTaskVo();
//			st2.setApp(st.getApp());
//			st2.setAppTask(st.getAppTask());
//			if (ut != null) {
//				st2.setStatus(ut.getUserTask().getStatus());
//			}
//			systemTask2.add(st2);
//		}
//		
//		mv.addObject("mytasks", systemTask2);
//		mv.addObject("user", u);
//		mv.addObject("title", "新手任务");
//		return mv; 
//	}
	/**
	 * 新手教程
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/beginner.html", method = RequestMethod.GET)
	public ModelAndView beginner(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "beginner.html.ftl");
		User u = getUser(request);
		
		ConcurrentHashMap<String,UserTaskVo>  userTask = userTaskService.getUserSystemAppTask(u.getId());
		
		List<AppTaskVo> systemTask = appTaskService.getSystemAppTasks();
		List<AppTaskVo> systemTask2 = new ArrayList<AppTaskVo>();
		int count = 0;
		boolean isNew = u.isRewardNewUserComplete();
		mv.addObject("isNew",isNew);
		for(AppTaskVo st : systemTask) {
			UserTaskVo ut = userTask.get(String.valueOf(st.getAppTask().getId()));
			AppTaskVo st2 = new AppTaskVo();
			st2.setApp(st.getApp());
			st2.setAppTask(st.getAppTask());
			//完成任务里面 
			if (ut != null) {
				if(st.getAppTask().getId()== 1){
					count = userFriendService.countUserFriends(u.getId());
					mv.addObject("inviteText", "已邀请"+count+"人");
				}
				st2.setStatus(ut.getUserTask().getStatus());
			}else{
				int taskId = st.getAppTask().getId();
				//1 邀请任务3 :app限时任务
				if(taskId== 1){
					count = userFriendService.countUserFriends(u.getId());
					st2.setProStatusText("已邀请"+count+"人");
					mv.addObject("inviteText", "已邀请"+count+"人");
				}else if(taskId == 3){
					//老版显示 打开 
//					if(isNew == false){
//						count = userTaskService.getUserFinshTaskNum(u.getId());
//						st2.setProStatusText("已试用"+count+"个");
//					}
				}
			}
			if(ut != null){
				systemTask2.add(st2);
			}else{
				systemTask2.add(0, st2);
			}
		}
		TransArticleTask task = transArticleTaskService.getSystemTask();
		if(task != null){
			UserArticleTaskVo vo =new UserArticleTaskVo();
			vo.setTransArticleTask(task);
			vo.setTransArticle(this.transArticleTaskService.getTransArticle(task.getArticle_id()));
			vo.setReceived(u.isTranArticleComplete());
			mv.addObject("tranTask", vo);
//			if(u.isTranArticleComplete() == false){
////				//获取分享的微信公众号信息，微信内和测试环境的分享根据域名来获取
//				Account wxAccount = null;
//				if (GlobalConfig.isDeploy) {
//					wxAccount = accountService.getRandomAccount();
//				} else {
//					wxAccount = accountService.getAccountByHost(request.getHeader("Host"));
//				}
//				String shareUrl = UserArticleTaskService.getShareUrl(u, wxAccount, vo);
//				mv.addObject("shareUrl",shareUrl );
//				String shareData = UserArticleTaskService.getShareData(u, wxAccount, vo);
//				mv.addObject("_data", shareData);
//			}
			
		}
	
		mv.addObject("mytasks", systemTask2);
		mv.addObject("user", u);
		mv.addObject("title", "新手任务");
		return mv;
	}

}
