package com.cyhd.web.action.web;


import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.*;
import com.cyhd.service.impl.*;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/web/my")
public class MyAction extends BaseAction {

	@Resource
	UserService userService;
	
	@Resource
	AppTaskService appTaskService;
	
	@Resource
	UserTaskService userTaskService;

	@Resource
	UserCheckInService checkInService;

	@Resource
	private UserIntegalIncomeService userIntegalIncomeService;
	
	@Resource
	private UserShareService userShareService;
	
	private static final String prefix = "web/my/";

	/**
	 * 用户个人中心
	 *
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/home.html", method = RequestMethod.GET)
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "home.html.ftl");

		User user = getUser(request);
		mv.addObject("user", user);
		mv.addObject("title", "个人中心");

		return mv;
	}

	/**
	 * 应用试用日志列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/apps.html", method = RequestMethod.GET)
	public ModelAndView apps(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		

		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		int start = pageIndex * defaultPageSize;
		User u = getUser(request);
		
		int total = userTaskService.getUserTaskTotal(u.getId());
		if(total > 0) {
			mv.setViewName(prefix + "apps.html.ftl");
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage",totalPage);
			mv.addObject("usertasks", userTaskService.getUserTasks(u.getId(), start, defaultPageSize));
		} else {
			mv.addObject("description", "限时任务");
			mv.setViewName("common/nodata.html.ftl");
		}
		mv.addObject("title", "限时任务记录");
		return mv; 
	}
	/**
	 * 邀请记录列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@Resource
	UserFriendService userFriendServer;
	@RequestMapping(value = "/invites.html", method = RequestMethod.GET)
	public ModelAndView invites(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();

		int start = ServletRequestUtils.getIntParameter(request, "start", 0);
		User u = getUser(request);
		int total = userFriendServer.countUserFriends(u.getId());
		
		int uid  = userFriendServer.getInvitor(u.getId());
		if(uid > 0 || total > 0) {
			if(uid > 0) {
				mv.addObject("superiors", userService.getUserById(uid));
				mv.addObject("createTime", u.getCreatetime());
			}
			if(total > 0) {
				int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
				mv.addObject("totalPage",totalPage);
				mv.addObject("userFriends", userFriendServer.getUserFriends(u.getId(), start, defaultPageSize));
			}
			mv.setViewName(prefix + "invites.html.ftl");
		} else {
			mv.addObject("description", "收徒");
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", "收徒记录");
		return mv; 
	}
	/**
	 * 提现记录
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
//	@RequestMapping(value = "enchashments.html", method = RequestMethod.GET)
//	public ModelAndView enchashments(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ModelAndView mv = new ModelAndView();
//		User u = getUser(request);
//		UserIncome income = userIncomeService.getUserIncome(u.getId());
//
//		mv.addObject("user", u);
//		mv.addObject("income", income);
//		mv.setViewName(prefix + "paylog.html.ftl");
//		mv.addObject("title", "提现记录");
//		return mv;
//
//		return new ModelAndView("forward:");
//	}
	/**
	 * 提现列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = {"enchashmentlist.html", "enchashments.html"}, method = RequestMethod.GET)
	public ModelAndView enchashmentlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		int total = userEnchashmentService.getUserEnchashmentCount(u.getId());
		if(total > 0) {
			mv.setViewName(prefix + "enchashments.html.ftl");
			int start = ServletRequestUtils.getIntParameter(request, "start", 0);
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage",totalPage);
			mv.addObject("userEnchashments", userEnchashmentService.getUserEnchashmentList(u.getId(), start, defaultPageSize));
		} else {
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", "提现记录");
		return mv; 
	}
	/**
	 * 显示提现详情
	 * 
	 * @param id
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/enchashments/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView detail(@PathVariable("id")int id, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "enchashmentdetail.html.ftl");
		
		mv.addObject("userEnchashment", userEnchashmentService.getById(id));
		mv.addObject("title", "提现详情");
		return mv;
	}

	/**
	 * 我的收入
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@Resource
	UserIncomeService userIncomeService;
	@Resource
	UserEnchashmentService userEnchashmentService;
	@RequestMapping(value = "/income.html", method = RequestMethod.GET)
	public ModelAndView income(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "income.html.ftl");
		User u = getUser(request);
		UserIncome income = userIncomeService.getUserIncome(u.getId());
		
		UserEnchashmentAccount userAccount = userEnchashmentService.getUserEnchashmentAccount(u.getId());
		if(userAccount == null) userAccount = new  UserEnchashmentAccount();
		mv.addObject("user", u);
		mv.addObject("income", income);
		mv.addObject("userAccount", userAccount);
		mv.addObject("title", "我的收入");
		mv.addObject("enchashStages", UserEnchashmentService.EnchashmentStage.values());
		mv.addObject("inWeixinEnchashTime", userEnchashmentService.isInWeixinEnchashTime());
		mv.addObject("underWeixinLimit", userEnchashmentService.isUnderWeixinLimit());
		return mv; 
	}
	/**
	 * 邀请收入列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/inviteincome.html", method = RequestMethod.GET)
	public ModelAndView inviteincome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();

		int start = ServletRequestUtils.getIntParameter(request, "start", 0);
		int friend_level = ServletRequestUtils.getIntParameter(request, "friend_level", 1);
		friend_level = friend_level == 1 ? 1 : 2;
		User u = getUser(request);
		int userId = u.getId();
		
		int total = userIncomeService.countUserFriendInviteIncome(userId, 1);
		UserIncome income = userIncomeService.getUserIncome(userId);
		if(income.getShare_total() > 0 || total > 0) {
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage",totalPage);
			mv.addObject("income",income);
			mv.addObject("userIncome", userIncomeService.getUserFriendInviteIncome(userId, friend_level, start, defaultPageSize));
			mv.setViewName(prefix + "inviteincome.html.ftl");
		} else {
			mv.addObject("info", "学徒奖励记录");
			mv.addObject("description", "邀请好友");
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", "学徒奖励");
		return mv; 
	}
	/**
	 * 邀请收入明细
	 * 
	 * @param request
	 * @return
	 * @throws CommonException 
	 */
	@RequestMapping(value = "/inviteincome/{id:\\d+}", method = RequestMethod.GET)
	 public ModelAndView inviteIncomeDetail(@PathVariable("id")int from_user, HttpServletRequest request) throws CommonException {
		ModelAndView mv = new ModelAndView();

		int start = ServletRequestUtils.getIntParameter(request, "start", 0);
		if(from_user <= 0) {
			logger.error("from_user parameter error!!");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "缺少来源用户！");
		}
		User u = getUser(request);
		//u.setId(85);
		
		int total = userIncomeService.countUserFriendInviteIncomeDetail(u.getId(), from_user);
		if(total > 0) {
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage",totalPage);
			mv.addObject("userIncomeDetail", userIncomeService.getUserFriendInviteIncomeDetail(u.getId(), from_user, start, defaultPageSize));
			mv.addObject("from_user",from_user);
			mv.setViewName(prefix + "inviteincomedetail.html.ftl");
		} else {
			mv.addObject("description", "邀请好友");
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", "学徒奖励明细");
		return mv; 
	}
	
	/**
	 * 其它收入列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/otherincome.html", method = RequestMethod.GET)
	public ModelAndView otherincome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		
		List<UserIncomeLog> otherIncome = userIncomeService.getUserOtherIncome(u.getId());
		if(otherIncome !=null && otherIncome.size() > 0) {
			mv.addObject("otherIncome", otherIncome); 
			mv.setViewName(prefix + "otherincome.html.ftl");
		} else {
			mv.addObject("info", "其它收入记录");
			mv.addObject("description", "新手教程");
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", "其它收入");
		return mv; 
	}
	@Resource
	UserExchangeIntegralService userExchangeIntegralService;
	/**
	 * 兑换金币列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/exchangelog.html", method = RequestMethod.GET)
	public ModelAndView exchangelog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		int clientType = clientInfo.isIos()?Constants.platform_ios:Constants.platform_android;
		int start = ServletRequestUtils.getIntParameter(request, "start", 0);
		int source = ServletRequestUtils.getIntParameter(request, "source", Constants.INTEGAL_SOURCE_WANPU);
		int total = userExchangeIntegralService.countUserExchangeIntegralLog(u.getId(), source, clientType);
		String integralType="金币";
		if(source == Constants.INTEGAL_SOURCE_YOUMI){
			integralType="积分";
		}
		if(total > 0) {
			int totalPage =  (total  +  defaultPageSize * 2  - 1) / (defaultPageSize * 2);
			mv.addObject("totalPage",totalPage);
			mv.addObject("exchangeLog", userExchangeIntegralService.getListByUserId(u.getId(), source, clientType, start, defaultPageSize * 2));
			mv.setViewName(prefix + "exchange.html.ftl");
			mv.addObject("source", source);
		} else {
			mv.addObject("description", integralType+"任务");
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", integralType+"兑换记录");
		mv.addObject("integralType", integralType);
		return mv; 
	}
	/**
	 * 联盟应用试用列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/thridpartlog.html", method = RequestMethod.GET)
	public ModelAndView thridpartlog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		int clientType = clientInfo.isIos()?Constants.platform_ios:Constants.platform_android;
		int start = ServletRequestUtils.getIntParameter(request, "start", 0);
		int source = ServletRequestUtils.getIntParameter(request, "source", Constants.INTEGAL_SOURCE_WANPU);
		int total = userExchangeIntegralService.countUserThridPartIntegral(u.getId(), source, clientType);
		String integralType="金币";
		if(source == Constants.INTEGAL_SOURCE_YOUMI){
			integralType="积分";
		}
		if(total > 0) {
			int totalPage =  (total  +  defaultPageSize  - 1) / (defaultPageSize);
			mv.addObject("totalPage",totalPage);
			mv.addObject("thridpartlog", userExchangeIntegralService.getUserThridPartIntegralList(u.getId(), source, clientType, start, defaultPageSize));
			mv.setViewName(prefix + "thridpartlog.html.ftl");
			mv.addObject("source", source);
		} else {
			mv.addObject("description", integralType+"任务");
			mv.setViewName("common/nodata.html.ftl");
		}

		mv.addObject("title", integralType+"任务记录");
		mv.addObject("integralType", integralType);
		return mv; 
	}
	
	/**
	 * 兑换金币列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/exchangelog_{id:[1-2]}.html", method = RequestMethod.GET)
	public ModelAndView exchangelogNew(@PathVariable("id")int source,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		int clientType = clientInfo.isIos()?Constants.platform_ios:Constants.platform_android;
		int start = ServletRequestUtils.getIntParameter(request, "start", 0);
		int total = userExchangeIntegralService.countUserExchangeIntegralLog(u.getId(), source, clientType);
		String integralType="金币";
		if(source == Constants.INTEGAL_SOURCE_YOUMI){
			integralType="积分";
		}
		if(total > 0) {
			int totalPage =  (total  +  defaultPageSize * 2  - 1) / (defaultPageSize * 2);
			mv.addObject("totalPage",totalPage);
			mv.addObject("exchangeLog", userExchangeIntegralService.getListByUserId(u.getId(), source, clientType, start, defaultPageSize * 2));
			mv.setViewName(prefix + "exchange.html.ftl");
			mv.addObject("source", source);
		} else {
			mv.addObject("description", integralType+"任务");
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", integralType+"兑换记录");
		mv.addObject("integralType", integralType);
		return mv; 
	}
	/**
	 * 联盟应用试用列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/thridpartlog_{id:[1-2]}.html", method = RequestMethod.GET)
	public ModelAndView thridpartlogNew(@PathVariable("id") int source,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		int clientType = clientInfo.isIos()?Constants.platform_ios:Constants.platform_android;
		int start = ServletRequestUtils.getIntParameter(request, "start", 0);
		int total = userExchangeIntegralService.countUserThridPartIntegral(u.getId(), source, clientType);
		String integralType="金币";
		if(source == Constants.INTEGAL_SOURCE_YOUMI){
			integralType="积分";
		}
		if(total > 0) {
			int totalPage =  (total  +  defaultPageSize  - 1) / (defaultPageSize);
			mv.addObject("totalPage",totalPage);
			mv.addObject("thridpartlog", userExchangeIntegralService.getUserThridPartIntegralList(u.getId(), source, clientType, start, defaultPageSize));
			mv.setViewName(prefix + "thridpartlog.html.ftl");
			mv.addObject("source", source);
		} else {
			mv.addObject("description", integralType+"任务");
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", integralType+"任务记录");
		mv.addObject("integralType", integralType);
		return mv; 
	}

	@RequestMapping(value = "/checkin.html", method = RequestMethod.GET)
	public ModelAndView myCheckinPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);

		//直接先签到
		checkInService.doCheckIn(u.getId(),clientInfo);


		//获取最近的签到记录和签到统计信息
		UserCheckInRecord lastRecord = checkInService.getLatestCheckInRecord(u.getId());
		UserCheckInStat checkInStat = checkInService.getUserCheckInStat(u.getId());

		boolean todayChecked = checkInService.isTodayRecord(lastRecord);
		boolean yesterdayCheecked = checkInService.isYesterdayRecord(lastRecord);


		int days = 0;  //已连续签到的天数
		int nextStageDays = 0; //还需签到多少天达到下一级别

		//昨天或者今天签到了，lastRecord肯定不为空，签到时间是连续的
		//计算今天的奖励金额
		UserCheckInService.CheckInStage todayStage = UserCheckInService.CheckInStage.FIRST;
		if (todayChecked){
			days = lastRecord.getDays();
			todayStage = checkInService.getCheckInStage(days);
		}else if (yesterdayCheecked){
			days = lastRecord.getDays();
			//如果今天还没有签到，则加一天再计算今天的奖励级别
			todayStage = checkInService.getCheckInStage(days + 1);
		}

		//获取下一个签到级别，如果为空，说明已经是最高级别
		UserCheckInService.CheckInStage nextStage = todayStage.nextStage();
		if (nextStage != null){
			nextStageDays = nextStage.getMinDays() - days;
		}

		//获取用户的签到奖励级别
		float checkinRate = checkInService.getUserCheckinRate(u.getId());

		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "checkin.html.ftl");
		mv.addObject("todayChecked", todayChecked);
		mv.addObject("yesterdayChecked", yesterdayCheecked);
		mv.addObject("days", days);
		mv.addObject("todayStage", todayStage);
		mv.addObject("checkinRate", checkinRate);
		mv.addObject("nextStageDays", nextStageDays);
		mv.addObject("checkInStages", UserCheckInService.CheckInStage.values());
		mv.addObject("lastRecord", lastRecord);
		mv.addObject("checkInStat", checkInStat);
		mv.addObject("title", "每日签到");
		return mv;
	}

	@RequestMapping(value = "/checkinlog.html", method = RequestMethod.GET)
	public ModelAndView myCheckinLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User u = getUser(request);

		UserCheckInStat checkInStat = checkInService.getUserCheckInStat(u.getId());
		List<UserCheckInRecord> checkInRecords = checkInService.getRecentRecords(u.getId());

		ModelAndView mv = new ModelAndView();
		mv.addObject("checkInStat", checkInStat);
		mv.addObject("checkInLog", checkInRecords);
		mv.addObject("totalPage", (int)Math.ceil(checkInStat.getTotal_days() * 1.0 / defaultPageSize));
		
		if(checkInRecords != null && checkInRecords.size() > 0){
			mv.setViewName(prefix + "checkinlog.html.ftl");
		} else {
			mv.addObject("description", "签到记录");
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", "签到记录");

		return mv;
	}

	@RequestMapping(value = {"giftscore.html", "goldcoin.html"}, method = RequestMethod.GET)
	public ModelAndView myGiftScore(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User u = getUser(request);

		int source = request.getRequestURI().endsWith("goldcoin.html") ? 1 : 2;
		ModelAndView mv = new ModelAndView();
		mv.addObject("title", "我的" + (source == 1? "金币" : "积分"));
		mv.addObject("user", u);
		mv.addObject("source", source);

		UserIntegalIncome income = userIntegalIncomeService.getIntegalIncomeBySource(u.getId(), source, Constants.platform_android);
		if (income == null){
			income = new UserIntegalIncome();
		}
		mv.addObject("income", income);

		mv.setViewName(prefix + "myscore.html.ftl");
		return mv;
	}

	@RequestMapping(value = "/to_bind_mobile.html")
	public ModelAndView toBindMobile(HttpServletRequest request){
		return new ModelAndView("forward:/ios/my/to_bind_mobile.html");
	}

	@RequestMapping(value = "/withdraw_account.html")
	public ModelAndView myWithdrawAccount(HttpServletRequest request){
		return new ModelAndView("forward:/ios/user/withdraw_account.html");
	}

	@RequestMapping(value = "/withdraw_type.html")
	public ModelAndView myWithdrawType(HttpServletRequest request){
		return new ModelAndView("forward:/ios/user/withdraw_type.html");
	}
	@RequestMapping("/show_order_image.html")
	public ModelAndView genShowOrderImage(HttpServletRequest request,HttpServletResponse response) throws CommonException{
		ModelAndView mv = new ModelAndView();
		User user = getUser(request);
		
		try {
			mv.addObject("url", userShareService.genShareOrderImage(user));
		} catch (Exception e) {
			logger.error("gen user share order image,user:{} ,cause ",user.getId(),e);
		}
		mv.addObject("title", "晒单分享收徒");
		mv.setViewName(prefix+"show_order_image.html.ftl");
		return mv;
	}
}
