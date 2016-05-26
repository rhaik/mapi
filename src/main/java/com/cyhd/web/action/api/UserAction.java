package com.cyhd.web.action.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.common.util.*;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RequestUtil;

import com.cyhd.web.common.util.ClientInfoUtil;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.Source;
import com.cyhd.service.dao.po.TransArticleTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.dao.po.UserSystemMessage;
import com.cyhd.service.util.VersionUtil;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/api/v1")
public class UserAction extends BaseAction {

	@Resource
	UserService userService;
	
	@Resource
	UserIncomeService userIncomeService;
	
	@Resource
	MobileCodeService mobileCodeService;
	
	@Resource
	private UserShareService userShareService;
	
	@Resource
	private UserFriendService userFriendService;

	@Resource
	private BeginnerService beginnerService;
	
	@Resource
	private UserMessageService userMessageService;
	
	@Resource
	private SourceService sourceService;
	
	@Resource
	private AppTaskService appTaskService;
	
	@Resource
	private UserLoginRecordService userLoginRecordService;

	@Resource
	private UserCheckInService checkInService;
	
	@Resource
	private TransArticleTaskService transArticleTaskService;

	@Resource
	private AppUpdateService appUpdateService;
	
	private static final String prefix = "/api/v1/user/";

	private final int  ttlInMillis = Constants.minutes_millis*3;
	
	private  LiveAccess<Boolean> userLoginByIOS7Cache = new LiveAccess<Boolean>(ttlInMillis, null);
	
	/**
	 * 微信登陆
	 * {"openid":"oVvAUt09_UJIHYnKzjRdIKn7oPUw","nickname":"大黄","sex":1,"language":"zh_CN",
	 * "city":"Haidian","province":"Beijing","country":"CN",
	 * "headimgurl":"http:\/\/wx.qlogo.cn\/mmopen\/PiajxSqBRaEI64LZ77Obo3maDiarLP3dWI2CAibwSQbtyAhwM8rkntSHx1AFGNqv933q0s0vAHfvAfBczUk0RLRibA\/0",
	 * "privilege":[],"unionid":"oKBEJuCc85TLwIY8ce0vFCH2aji4"}
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = {"/ab/ba"}, method = RequestMethod.POST, produces="text/json; charset=UTF-8")
	@ResponseBody
	public String login(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String loginInfo = ServletRequestUtils.getStringParameter(request, "info");
		int groupid = ServletRequestUtils.getIntParameter(request, "gid", 1);
		
		if (StringUtils.isEmpty(loginInfo)) {
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER);
		}
		JSONObject json = JSONObject.fromObject(loginInfo);
		if (JSONUtils.isNull(json)) {
			logger.error("user login info parameter not json !");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER);
		}
		ClientInfo clientInfo = getClientInfo(request);
		String os = clientInfo.getOs();
		if(StringUtils.isBlank(os)){
			logger.error("user login ,os is bank");
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTINFO);
		}else{
			if(clientInfo.isIos() && os.toLowerCase().contains("os7")){
				if (userLoginByIOS7Cache.getElement() == null) {
					userLoginByIOS7Cache = new LiveAccess<Boolean>(ttlInMillis, Boolean.TRUE);
				}else{
					logger.error("user login ios7 appear");
					throw new CommonException(ErrorCode.ERROR_CODE_UNKNOWN, "您的操作过于频繁，请稍后再试");
				}
			}
		}
		//add ip 限制
		if(userLoginRecordService.isExistIpByCache(clientInfo.getIpAddress())){
			logger.error("登陆时缓存中存在的Ip:{},idfa:{}",clientInfo.getIpAddress(),clientInfo.getIdfa());
			throw new CommonException(ErrorCode.ERROR_CODE_UNKNOWN, "您的操作过于频繁，请稍后再试");
		}
		
		String openId = json.getString("openid");
		String avatar = json.getString("headimgurl");
		String name = json.getString("nickname");
		int sex = 1;
		if(json.containsKey("sex"))
			sex = json.getInt("sex");
		
		String country = "CN";
		String province = "";
		String city = "";
		String unionid = "";
		if(json.containsKey("country"))
			country = json.getString("country");
		if(json.containsKey("province"))
			province = json.getString("province");
		
		if(json.containsKey("city"))
			city = json.getString("city");
		
		if(json.containsKey("unionid"))
			unionid = json.getString("unionid");

		String deviceId = clientInfo.getDid();
		if (StringUtils.isEmpty(deviceId)) {
			logger.error("did is null !");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER);
		}
		
		String idfa = clientInfo.getIdfa();
		
		User u = userService.getUserByUnionId(unionid);

		Date now = new Date();
		boolean isNew = true;

		String ticket = userService.generateTicket(openId);
		String invite_code = ServletRequestUtils.getStringParameter(request, "inid", null);
		if (u == null) {
			u = userService.getUserByDid(deviceId);
			if(u != null){
				throw new CommonException(ErrorCode.ERROR_CODE_USER_DUPLICATE, "账号异常，请联系工作人员处理！");
			}
			u = new User();
			u.setCreatetime(now);
			u.setUser_identity(userService.generateIdentityId());
			
			if(!StringUtils.isEmpty(invite_code)) {
				if(invite_code.startsWith(Constants.invite_code_prefix)){
					String identity = invite_code.substring(Constants.invite_code_prefix.length());
					Source s = sourceService.getSourceByIdentity(identity);
					u.setSource(s != null ? s.getId() : 0);
				}else if(invite_code.length() == 32){
					u.setSource(1);
				}else
					u.setSource(0);
			}
		} else {
			isNew = false;
			if(u.getDid() != null && deviceId != null && !u.getDid().equalsIgnoreCase(deviceId)){
				logger.error("warning!!! user {} did changed, old={}, new={}", u.getId(), u.getDid(), deviceId);
			}
			//添加用户被封判断  只有老用户才会被封禁
			if(u.isBlack()){
				logger.error("被封禁的用户登录：usre:{},clientInfo:{}",u,clientInfo);
				throw new CommonException(ErrorCode.ERROR_CODE_USER_MASKED);
			}
		}
		
		int devicetype = VersionUtil.getDeviceType(clientInfo.getClientType());
		
		if(!StringUtils.isEmpty(idfa)){
			u.setIdfa(idfa);
		}
		u.setGroupid(groupid);
		u.setOpenid(openId);
		u.setTicket(ticket);
		u.setDevicetype(devicetype);
		u.setDid(deviceId);
		u.setSex(sex);
		u.setUnionid(unionid);
		u.setInvite_code(MD5Util.getMD5(unionid));
		u.setCountry(country);
		u.setCity(city);
		u.setProvince(province);
		u.setAvatar(avatar);
		u.setLastlogintime(now);
		u.setName(name);
		
		boolean ret = userService.insertOrUpdate(u);
		if (ret) {
			User invitor = null;
			boolean hasInvitor = false;
			if(isNew){
				u = userService.getUserByIdentifyId(u.getUser_identity());
				userIncomeService.createNewUserIncome(u.getId());
				
				int clientType = clientInfo.isIos()?UserSystemMessage.PUSH_CLIENT_TYPE_IOS:UserSystemMessage.PUSH_CLIENT_TYPE_ANDROID;
				//发送新手任务消息
				userMessageService.notifyNewUserBeginnerMessage(u.getId(),clientType);
				
				if(!StringUtils.isEmpty(invite_code) && invite_code.length() == 32){
					logger.info("User login and has invitor,user={}, code={}", u.getId(), invite_code);
					invitor = userService.getUserByInviteCode(invite_code);
					if(invitor != null){
						if(invitor.getId() != u.getId() && invitor.getId() < u.getId()){
							userFriendService.onAddUserFriend(invitor, u,clientInfo.getIdfa());
						}else{
							invitor = null;
						}
					}else {
						logger.error("error invitor id when login, identify_id={}", invite_code);
					}
				}
			}

			//未奖励过和未完成过新手任务的用户，都进行发奖。
			//钥匙版本需要打开钥匙页面后才发奖，因为要处理收徒的逻辑
			boolean isYaoshi = clientInfo.isIos() && appUpdateService.isYaoshiBundle(clientInfo.getAppnm());
			if (!isYaoshi && invitor != null && userService.isNewUser(u)){ //有师傅的情况下才发奖
				userService.executeRewardNewUser(u, devicetype);
			}

			userLoginRecordService.add(u.getId(), name, avatar, country, province, city, deviceId, idfa,
					clientInfo.getAppVer(), devicetype, clientInfo.getModel(), clientInfo.getOs(), clientInfo.getNet(), ticket, RequestUtil.getIpAddr(request));
			

			Map<String, Object> userInfo = u.getBasicInfo();
			userInfo.put("is_new", isNew);
			userInfo.put("invitor", invitor == null ? "" : invitor.getName());

			Map<String, Object> results = new HashMap<>();
			results.put("ticket", u.getTicket());
			results.put("user", userInfo);
			return toJSONResult(results);
		} else {
			throw new CommonException(ErrorCode.ERROR_CODE_UNKNOWN);
		}
		//fillStatus(mv);
		//return mv;
	}
	
	@RequestMapping(value = {"/user/get_mobile_code","/ad/da"},  method = RequestMethod.POST)
	public ModelAndView sendMobileCodeToClient(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "result.json.ftl");

		ClientInfo clientInfo = getClientInfo(request);
		User user =  (User) request.getAttribute("userInfo");
		if (user == null){
			user = userService.getUserByIdfa(clientInfo.getIdfa());
		}

		String mobile = ServletRequestUtils.getStringParameter(request, "mobile", "");
		if(StringUtils.isEmpty(mobile) || !Validator.isMobile(mobile)){
			logger.error("mobile parameter error!!");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER);
		}
		
		User u2 = userService.getUserByMobile(mobile);
		if(u2 != null && user != null && u2.getId() != user.getId()){
			logger.error("mobile parameter error!!");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "该手机号已被别人绑定！");
		}
		
		if (mobileCodeService.sendMobileCode(mobile)) {
			fillStatus(mv);
		} else {
			logger.info("send code to client error");
			fillErrorStatus(mv, ErrorCode.ERROR_CODE_UNKNOWN, "拨打电话出错，请稍后重试！");
		}
		return mv;
	}

	@RequestMapping(value = {"/user/bind_mobile","/ae/ea"}, method = RequestMethod.POST)
	public ModelAndView bind(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String mobile = ServletRequestUtils.getStringParameter(request, "mobile", "");
		String code = ServletRequestUtils.getStringParameter(request, "code", "");
		if(StringUtils.isEmpty(mobile) || !Validator.isMobile(mobile) || StringUtils.isEmpty(code) || code.length() != 4 || !Validator.isDigital(code)){
			logger.error("Bind mobile parameter error!!");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER);
		}
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "result.json.ftl");
		User u = getUser(request);
		
		boolean newUser = StringUtils.isEmpty(u.getMobile());
		User u2 = userService.getUserByMobile(mobile);
		if(u2 != null && u2.getId() != u.getId()){
			logger.error("mobile parameter error!!");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "该手机号已被别人绑定！");
		}
		
		boolean codeValid = mobileCodeService.validMobileAndCode(mobile, String.valueOf(code));
		if(codeValid){
			userService.bindMobile(u.getId(), mobile);
			//绑定成功，处理邀请人
//			if(newUser){
//				int invitorid = ServletRequestUtils.getIntParameter(request, "inid", 0);
//				if(invitorid > 1000000){
//					User invitor = userService.getUserByIdentifyId(invitorid);
//					if(invitor != null && invitor.getId() != u.getId()){
//						ClientInfo clientInfo = getClientInfo(request);
//						userFriendService.onAddUserFriend(invitor, u,clientInfo.getIdfa());
//					}else{
//						logger.error("error invitor id when bind mobile, identify_id={}", invitorid);
//					}
//				}
//			}
			fillStatus(mv);
		}else{
			fillErrorStatus(mv, ErrorCode.ERROR_CODE_VERCODE);
		}
		return mv;
	}
	
	@RequestMapping(value = "/user/info")
	public ModelAndView userInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "user_info.json.ftl");
		User u = getUser(request);
		mv.addObject("user", u);
		fillStatus(mv);
		return mv;
	}
	
	@RequestMapping(value = "/user/share")
	public ModelAndView share(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "user_share.json.ftl");
		User u = getUser(request);
		
		String imageUrl = userShareService.makeShareImage(u);
		mv.addObject("imageUrl", imageUrl);
		fillStatus(mv);
		return mv;
	}
	
	//分享给好友 成功回调
	@RequestMapping(value = "/user/share_complete")
	public ModelAndView share_complete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "result.json.ftl");
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		if(u.isTaskShareComplete()){
			fillStatus(mv);
			return mv;
		}
		int clientType = clientInfo.isIos()?UserSystemMessage.PUSH_CLIENT_TYPE_IOS:UserSystemMessage.PUSH_CLIENT_TYPE_ANDROID;
		//beginnerService.onFirstShareTaskComplete(u,clientInfo.getIdfa(),clientType);
		fillStatus(mv);
		
		return mv;
	}
	
	
//	@RequestMapping(value = "/statistics", method = RequestMethod.POST)
//	public ModelAndView statistics(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ModelAndView mv = new ModelAndView();
//		mv.setViewName(prefix + "statistics.json.ftl");
//		User u = getUser(request);
//		
//		UserIncomeStatsVo vo = userIncomeService.getUserIncomeStats(u.getId());
//		//本月
//		mv.addObject("currentMonth", MoneyUtils.fen2yuanS(vo.getCurrentMonthAmount()));
//		//上月
//		mv.addObject("lastMonthAmount", MoneyUtils.fen2yuanS(vo.getLastMonthAmount()));
//		//余额
//		mv.addObject("balance", MoneyUtils.fen2yuanS(vo.getBalance()));
//		
//		//昨天
//		mv.addObject("todayAppAmount", MoneyUtils.fen2yuanS(vo.getYestodyAppAmount()));
//		mv.addObject("todayFriendAmount", MoneyUtils.fen2yuanS(vo.getYestodyFriendAmount()));
//		
//		//7天
//		mv.addObject("sevenDayAppAmount", MoneyUtils.fen2yuanS(vo.getSevenDayAppAmount()));
//		mv.addObject("sevenDayFriendAmount", MoneyUtils.fen2yuanS(vo.getSevenDayFriendAmount()));
//		
//		//30天
//		mv.addObject("thirtyDayAppAmount", MoneyUtils.fen2yuanS(vo.getThirtyDayAppAmount()));
//		mv.addObject("thirtyDayFriendAmount", MoneyUtils.fen2yuanS(vo.getThirtyDayFriendAmount()));
//		
//		fillStatus(mv);
//		return mv;
//	}
	/**
	 * 邀请好友列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/user/invites", method = RequestMethod.GET)
	public ModelAndView List(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "invites.json.ftl");
		
		User u = getUser(request);
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		int start = pageIndex * defaultPageSize;
		mv.addObject("userFriends", userFriendService.getUserFriends(u.getId(), start, defaultPageSize));
		
		mv.addObject("title", "收徒记录");
		return mv; 
	}
	
	/**
	 * 邀请收入列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/user/inviteincome", method = RequestMethod.GET)
	public ModelAndView inviteincome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "inviteincome.json.ftl");
		
		int friend_level = ServletRequestUtils.getIntParameter(request, "friend_level", 1);
		friend_level = friend_level == 1 ? 1 : 2;
		User u = getUser(request);
		//u.setId(85);
		int userId = u.getId();
		
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		int start = pageIndex * defaultPageSize;
		mv.addObject("userIncome", userIncomeService.getUserFriendInviteIncome(userId, friend_level, start, defaultPageSize));
		
		mv.addObject("title", "邀请收入记录");
		return mv; 
	}
	
	/**
	 * 邀请收入明细
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/user/inviteincome/detail", method = RequestMethod.GET)
	public ModelAndView inviteIncomeDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		
		int from_user = ServletRequestUtils.getIntParameter(request, "from_user", 0);
		if(from_user <= 0) {
			logger.error("from_user parameter error!!");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "缺少来源用户！");
		}
		User u = getUser(request);
		//u.setId(85);
		
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		int start = pageIndex * defaultPageSize;
		mv.addObject("userIncomeDetail", userIncomeService.getUserFriendInviteIncomeDetail(u.getId(), from_user, start, defaultPageSize));
		mv.setViewName(prefix + "inviteincomedetail.json.ftl");
		
		mv.addObject("title", "邀请收入明细");
		return mv; 
	}
	
	@Resource
	UserExchangeIntegralService userExchangeIntegralService;
	/**
	 * 兑换日志
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/user/exchangelog", method = RequestMethod.GET)
	public ModelAndView exchangelog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		ClientInfo clientInfo = getClientInfo(request);
		int clientType = clientInfo.isIos()?Constants.platform_ios:Constants.platform_android;
		User u = getUser(request);
		int source = ServletRequestUtils.getIntParameter(request, "source", Constants.INTEGAL_SOURCE_WANPU);
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		int start = pageIndex * (defaultPageSize * 2);
		mv.addObject("exchangeLog", userExchangeIntegralService.getListByUserId(u.getId(),source,clientType, start, defaultPageSize * 2));
		mv.setViewName(prefix + "exchange.json.ftl");
		
		mv.addObject("title", "兑换记录");
		return mv; 
	}
	/**
	 * 联盟应用试用日志
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/user/thridpartlog", method = RequestMethod.GET)
	public ModelAndView thridpartlog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		ClientInfo clientInfo = getClientInfo(request);
		int clientType = clientInfo.isIos()?Constants.platform_ios:Constants.platform_android;
		int source = ServletRequestUtils.getIntParameter(request, "source", Constants.INTEGAL_SOURCE_WANPU);
		
		User u = getUser(request);
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		int start = pageIndex * (defaultPageSize);
		mv.addObject("tasklog", userExchangeIntegralService.getUserThridPartIntegralList(u.getId(), source, clientType, start, defaultPageSize));
		mv.setViewName(prefix + "thridpartlog.json.ftl");
		
		mv.addObject("title", "金币任务记录");
		return mv; 
	}
	
	
	@RequestMapping(value={"/user/statistics","/ai/ia"},method=RequestMethod.POST)
	public ModelAndView homePageData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "homePageData.json.ftl");
		User u = getUser(request);
		
		ClientInfo info = super.getClientInfo(request);
		if(info.getAppVer() == null || info.getAppVer().startsWith("0.")){
			super.fillErrorStatus(mv, ErrorCode.ERROR_CODE_UNKNOWN, "版本过低，请在我->设置中检测新版本并升级！");
			return mv;
		}
		
		int todyIncome = userIncomeService.countUserTodyIncome(u.getId());
		mv.addObject("todyIncome", MoneyUtils.fen2yuanS(todyIncome));
		
		int  todyInvite = userFriendService.countTodyInviteFriendByuserId(u.getId());
		mv.addObject("todyInvite", todyInvite);

		int income = 0, balance = 0;
		UserIncome userIncome = userIncomeService.getUserIncome(u.getId());
		if (userIncome != null){
			income = userIncome.getIncome();
			balance = userIncome.getBalance();
		}else {
			//可能还没有收入数据
			userIncomeService.createNewUserIncome(u.getId());
		}

		mv.addObject("income", MoneyUtils.fen2yuanS2(income));
		
		mv.addObject("balance", MoneyUtils.fen2yuanS(balance));
		
		AppTask appTask = appTaskService.getLastAppTask();
		mv.addObject("appTask", appTask);
		
		TransArticleTask lastArticleTask = transArticleTaskService.getLastArticleTaskTask();
		mv.addObject("lastArticleTask", lastArticleTask);
		
		//今天是否已经签到过
		boolean hasCheckinToday = checkInService.isCheckinToday(u.getId());
		mv.addObject("hasCheckIn", hasCheckinToday? 1 : 0);

		return mv;
	}

	/**
	 * 用户签到
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/doCheckin", method = RequestMethod.POST)
	public ModelAndView doCheckIn(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);

		boolean result = checkInService.doCheckIn(u.getId(), clientInfo);

		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "result.json.ftl");

		if (result){
			fillStatus(mv);
		}else {
			fillErrorStatus(mv, -1);
		}
		return  mv;
	}


	/**
	 * 获取用户的签到记录
	 *
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/user/checkinlog", method = RequestMethod.GET)
	public ModelAndView getCheckInLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User u = getUser(request);
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		int start = pageIndex * (defaultPageSize);

		ModelAndView mv = new ModelAndView();
		mv.addObject("checkinLog", checkInService.getUserCheckInRecords(u.getId(), start, defaultPageSize));
		mv.setViewName(prefix + "checkinlog.json.ftl");

		mv.addObject("title", "签到记录");
		return mv;
	}

	/**
	 * 用户快速登录
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/be/eb", method = RequestMethod.POST, produces="text/json; charset=UTF-8")
	@ResponseBody
	public String getQuickLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ClientInfo clientInfo = getClientInfo(request);
		String idfa = clientInfo.getIdfa();
		/***
		 * 1、请求数据中idfa查询到用户，如果用户表里面idfa和did和客户端请求中数据一致，则用户登陆成功
		2、请求数据中idfa查询到用户，did不一致，则检查用户是否绑定过手机号，绑定过后给用户手机号发送验证码，如果未绑定过，则让用户绑定手机号。
		3、请求数据中idfa未查询到用户，则说明是新用户，让用户绑定手机号。
		 */

		Map<String, Object> results = new HashMap<>();
		results.put("state", Constants.QUICK_LOGIN_FAIL);

		if(StringUtils.isBlank(idfa)||StringUtils.isBlank(clientInfo.getDid())){
			return toJSONResult(results);
		}

		User user = userService.getUserByIdfa(idfa);

		//未查到用户，直接返回失败
		if(user == null){
			return toJSONResult(results);
		}else if(clientInfo.getDid().equals(user.getDid()) == false){ //did不相同，仍然失败
			if(StringUtils.isNotBlank(user.getMobile())){
				results.put("mobile", user.getMobile());
				return toJSONResult(results);
			}else{
//				mv.addObject("state", Constants.QUICK_LOGIN_BAD_DID);
				return toJSONResult(results);
			}
		}

		//idfa和did都相同，则修改user ticket
		//返回ticket
		String ticket = userService.generateTicket(user.getOpenid());
		user.setLastlogintime(new Date());
		user.setTicket(ticket);
		boolean ret = userService.insertOrUpdate(user);
		if(ret){
			//新增登录记录
			userLoginRecordService.add(user.getId(), null, null, null, null, null, clientInfo.getDid(), clientInfo.getIdfa(),
					clientInfo.getAppVer(), VersionUtil.getDeviceType(clientInfo.getClientType()), clientInfo.getModel(), clientInfo.getOs(), clientInfo.getNet(), ticket, RequestUtil.getIpAddr(request));
		}

		results.put("user", user.getBasicInfo());
		results.put("mobile", user.getMobile());
		results.put("ticket", ticket);
		results.put("state", Constants.QUICK_LOGIN_IS_EXIST_USER);

		return toJSONResult(results);
	}

	/**
	 * 使用手机号和验证码进行登录
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/bf/fb"}, method=RequestMethod.POST, produces="text/json; charset=UTF-8")
	@ResponseBody
	public String loginByCode(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String mobile = request.getParameter("mobile");
		String code = request.getParameter("code");
		String invite_code = ServletRequestUtils.getStringParameter(request, "inid", "");
		
		if(StringUtils.isBlank(mobile)||StringUtils.isBlank(code)){
			return toJSONResult(-1, "请求参数错误");
		}
		
		if(mobile.length() != 11 || code.length() != 4){
			return toJSONResult(-1, "请求参数错误");
		}
		
		boolean valid = mobileCodeService.validMobileAndCode(mobile, code);
		if(valid == false){
			return toJSONResult(-1, "验证码错误");
		}

		ClientInfo clientInfo = getClientInfo(request);
		if (StringUtil.isBlank(clientInfo.getIdfa()) ){
			logger.error("用户快速登陆，mobile={}, code={}, idfa is empty", mobile, code);
			throw new CommonException(ErrorCode.ERROR_CODE_USER_MASKED, "请在手机“设置”->“隐私”->“广告”中关闭“限制广告跟踪”！");
		}


		Date now = new Date();
		User user = userService.getUserByMobile(mobile);
		String ticket = userService.generateTicket(clientInfo.getDid());

		boolean isNew = false;

		//通过手机号查到用户
		if(user != null){

			//判断IDFA是否匹配
			if (StringUtil.isNotBlank(user.getIdfa()) && !user.getIdfa().equals(clientInfo.getIdfa())){
				logger.error("用户的IDFA异常，禁止登录，User：{}，新的IDFA：{}", user, clientInfo.getIdfa());
				throw new CommonException(ErrorCode.ERROR_CODE_USER_DUPLICATE, "账号或手机号码异常，请更换手机号重试或联系客服人员处理！");
			}

			//如果之前用户的idfa为空，则重设
			if (StringUtil.isBlank(user.getIdfa())){
				user.setIdfa(clientInfo.getIdfa());
			}

			user.setLastlogintime(now);
			user.setDid(clientInfo.getDid());
			user.setTicket(ticket);

			userService.insertOrUpdate(user);
		}else {
			user = userService.getUserByIdfa(clientInfo.getIdfa());

			//根据IDFA获取到用户
			if(user != null){
				//进入这里，用户的IDFA肯定是匹配的

				//检查手机号是否匹配
				String oldMobile = user.getMobile();

				//手机号不为空，且不匹配，抛出异常
				if(StringUtils.isNotBlank(oldMobile) && !mobile.equals(oldMobile)){
					logger.error("获取的用户已绑定电话,userId:{},bindMobile:{},mobile:{}",user.getId(),oldMobile,mobile);
					throw new CommonException(ErrorCode.ERROR_CODE_USER_DUPLICATE, "账号或手机号码异常，请更换手机号重试或联系客服人员处理！");
				}

				//原手机号为空，则重新绑定
				if (StringUtil.isBlank(oldMobile)){
					userService.bindMobile(user.getId(), mobile);
					logger.info("quick login, bind mobile={} for user={}", mobile, user);
				}

				user.setLastlogintime(now);
				user.setDid(clientInfo.getDid());
				user.setTicket(ticket);
				userService.insertOrUpdate(user);
			}else {
				//通过IDFA未查到用户则是新用户
				//创建user 构建初始化资源
				isNew = true;

				user = new User();
				user.setCreatetime(now);
				user.setUser_identity(userService.generateIdentityId());
				user.setIdfa(clientInfo.getIdfa());
				user.setLastlogintime(now);
				user.setDid(clientInfo.getDid());
				user.setMobile(mobile);
				user.setBindtime(now);
				user.setTicket(ticket);

				if(StringUtil.isNotBlank(invite_code)) {
					if(invite_code.startsWith(Constants.invite_code_prefix)){
						String identity = invite_code.substring(Constants.invite_code_prefix.length());
						Source s = sourceService.getSourceByIdentity(identity);
						user.setSource(s != null ? s.getId() : 0);
					}else if(invite_code.length() == 32){
						user.setSource(1);
					}else
						user.setSource(0);
				}

				boolean ret = userService.insertOrUpdate(user);

				if (ret) {
					user = userService.getUserByIdentifyId(user.getUser_identity());
					userIncomeService.createNewUserIncome(user.getId());

					int clientType = clientInfo.isIos() ? UserSystemMessage.PUSH_CLIENT_TYPE_IOS : UserSystemMessage.PUSH_CLIENT_TYPE_ANDROID;
					//发送新手任务消息
					userMessageService.notifyNewUserBeginnerMessage(user.getId(), clientType);

					User invitor = null;
					if (StringUtil.isNotBlank(invite_code) && invite_code.length() == 32) {
						logger.info("User login and has invitor,user={}, code={}", user.getId(), invite_code);
						invitor = userService.getUserByInviteCode(invite_code);
						if (invitor != null) {
							if (invitor.getId() != user.getId() && invitor.getId() < user.getId()) {
								userFriendService.onAddUserFriend(invitor, user, clientInfo.getIdfa());
							} else {
								invitor = null;
							}
						} else {
							logger.error("error invitor id when login, identify_id={}", invite_code);
						}
					}
				}else { //创建用户失败
					throw new CommonException(ErrorCode.ERROR_CODE_UNKNOWN);
				}
			}
		}

		//增加登录记录
		userLoginRecordService.add(user.getId(), null, null, null, null, null, clientInfo.getDid(), clientInfo.getIdfa(),
				clientInfo.getAppVer(), VersionUtil.getDeviceType(clientInfo.getClientType()), clientInfo.getModel(), clientInfo.getOs(), clientInfo.getNet(), ticket, RequestUtil.getIpAddr(request));

		Map<String, Object> results = new HashMap<>();
		results.put("isnew", isNew);
		results.put("ticket", ticket);
		results.put("user", user.getBasicInfo());

		return toJSONResult(results);
	}


	/**
	 * 验证用户的邀请码
	 * @param request
	 * @param response
	 * @return
	 * @throws CommonException
	 */
	@RequestMapping(value={"/bh/hb"},produces={"text/json; charset=UTF-8"})
	@ResponseBody
	public String invite(HttpServletRequest request,HttpServletResponse response) throws CommonException{
		//rtv: {"code":%d,"message":"%s","data":{"amount":%3.1f}}
		String rtvFormatMessage = "{\"code\":%d,\"message\":\"%s\",\"data\":{\"amount\":%3.1f}}";
		int amount = 0;
		int code = -2;
		String message = "系统异常,请稍后重试";
		User u = getUser(request);
		//未给用户发过新用户奖励，则处理收徒和发奖的逻辑
		try {
			//有的用户很久以前就注册的了 但是没有下载过app
			if(u.getCreatetime().before(GlobalConfig.NEW_USER_REGIST_START_) || !userService.isNewUser(u)){
				return String.format(rtvFormatMessage, code,"亲,您已经是老用户了。",MoneyUtils.fen2yuan(amount));
			}
			//这哥们是不是完成新手任务奖励 这哥们是不是有师傅
			int invitorId= userFriendService.getInvitor(u.getId());
			boolean reward = true;
			if(u.isRewardNewUserComplete()){
				message = "亲,你已经是资深用户了";
			}else if(invitorId  > 0){
				message = "亲,你已经是有师傅的人啦";
			}else{
				// 1 输入师傅的邀请码 2 没有输入邀请码
				int  inviteType = ServletRequestUtils.getIntParameter(request, "invite_type", 0);
				//处理收徒的逻辑，理论上只会进行一次
				String invite_code = ServletRequestUtils.getStringParameter(request, "invite_code", "");
				if(inviteType == 1) {
					if (invite_code.length() != 8 || NumberUtil.safeParseLong(invite_code) <= 0) {
						reward = false;
						code = -1;
						message = "邀请码错误";
						logger.info("safari invite, user login invite code wrong,user={}, code={}", u.getId(), invite_code);
					} else {
						logger.info("safari invite, user login and has invitor,user={}, code={}", u.getId(), invite_code);

						//处理用户邀请的逻辑
						User invitor = null;
						if (invite_code.length() == 32) {
							invitor = userService.getUserByInviteCode(invite_code);
						} else if (invite_code.length() == 8) {
							invitor = userService.getUserByIdentifyId(NumberUtil.safeParseInt(invite_code));
						}

						if (invitor != null) {
							if(invitor.isBlack()){
								logger.error("输入的邀请人已经被拉黑:user:{},invitor:{}",u.getId(),invitorId);
							}else if (invitor.getId() != u.getId()) {
								if (invitor.getId() < u.getId()) {
									logger.warn("safari invite,存在师傅,userId:{},invitor_id:{}", u.getId(), invitor);
									userFriendService.onAddUserFriend(invitor, u, u.getIdfa());
								} else {
									reward = false;
									code = -1;
									message = "邀请码错误";
								}
							} else {
								message = "邀请人不能是自己";
							}
						} else {
							reward = false;
							code = -1;
							message = "邀请码错误";
							logger.warn("safari invite, error invitor id when login, identify_id={}", invite_code);
						}
					}
				}

				if(userService.isNewUser(u)){
					if(reward){
						ClientInfo clientInfo = ClientInfoUtil.getClientInfo(request);
						//保险期间，先给用户创建收入记录，再发奖
						userIncomeService.createNewUserIncome(u.getId());
						amount = userService.executeRewardNewUser(u, clientInfo.getPlatform());
						code = 0;
						message="ok";
					}
				}else{
					message="亲,你已经是资深用户了";
				}
			}
		}catch (Exception exp){
			logger.error("invite safari error:", exp);
			code = -1;
		}

		return String.format(rtvFormatMessage, code,message,MoneyUtils.fen2yuan(amount));
	}
	public static final Date REGIST_DATE= DateUtil.parseDate("2016-05-10","yyyy-MM-dd");
	@Resource
	private UserTaskService userTaskService;
	@Resource
	private RepairService repairService;
	
	@RequestMapping(value={"/invite_code/re_input_invitor"},produces={"text/json; charset=UTF-8"})
	@ResponseBody
	public String reInputCode(HttpServletRequest request,HttpServletResponse response) throws CommonException{
		String rtvFormatMessage = "{\"code\":%d,\"message\":\"%s\",\"data\":{\"amount\":%3.1f}}";
		User u = getUser(request);
		int code = -1;
		String message = "error";
		int amount = 0;
		if(u.isBlack()){
			message = "非法的请求";
		}else if( !repairService.isNewInvitedUser(u.getId())
				|| !DateUtil.isSameDay(u.getCreatetime(), REGIST_DATE)){
			repairService.removeNewUser(u.getId());
			message = "亲,您不是5月10号注册的新用户";
		}else{
			//判断是不是有师傅
			int invite = userFriendService.getInvitor(u.getId());
			if(invite > 0){
				message = "亲,您已经有师傅啦!";
				repairService.removeNewUser(u.getId());
				return String.format(rtvFormatMessage, code,message,MoneyUtils.fen2yuan(amount));
			}
			
			User invitor = null;
			// 1 输入师傅的邀请码 2 没有输入邀请码
			int  inviteType = ServletRequestUtils.getIntParameter(request, "invite_type", 0);
			if(inviteType == 2){
				message = "ok";
				code = 0;
				repairService.removeNewUser(u.getId());
				return String.format(rtvFormatMessage, code,message,MoneyUtils.fen2yuan(amount));
			}
			
			String invite_code = ServletRequestUtils.getStringParameter(request, "invite_code","");
			if(StringUtil.isBlank(invite_code)||invite_code.length() != 8){
				message = "邀请码无效";
				return String.format(rtvFormatMessage, code,message,MoneyUtils.fen2yuan(amount));
			}
			invitor = userService.getUserByIdentifyId(NumberUtil.safeParseInt(invite_code));
			if (invitor == null || invitor.isBlack()) {
				code = -1;
				message = "邀请码错误";
				logger.warn("safari invite,重新邀请, error invitor id when login, identify_id={}", invite_code);
				return String.format(rtvFormatMessage, code,message,MoneyUtils.fen2yuan(amount));
			}
			
			if (invitor.getId() != u.getId()) {
				if (invitor.getId() < u.getId()) {
					logger.warn("safari invite,重新邀请存在师傅,userId:{},invitor_id:{}", u.getId(), invitor.getId());
					boolean addSuccess = userFriendService.onAddUserFriend(invitor, u, u.getIdfa());
					if(addSuccess){
						if(u.isTaskAppComplete()
								||userTaskService.getUserFinshTaskNum(u.getId()) >= 1){
							userFriendService.addEffectiveInvite(invitor.getId(), u.getId(), 1, DateUtil.getTodayStr());
						}else{
							userService.updateUserCreateTime(u.getId(),GenerateDateUtil.getCurrentDate());
						}
						if(!u.isRewardNewUserComplete()){
							//保险期间，先给用户创建收入记录，再发奖
							userIncomeService.createNewUserIncome(u.getId());
						}
						amount = 100;
						//发奖
						ClientInfo clientInfo = ClientInfoUtil.getClientInfo(request);
						if(userIncomeService.addUserReInputInviteCodeIncome(u.getId(), amount)){
							userMessageService.addNewUserRewardMessage(u.getId(), amount, clientInfo.getPlatform(), invitor);
							logger.info("重新邀请 新用户发放奖励,处理成功,userId:{},amount:{},师傅Id:{}", u.getId(), amount, invitor.getId());
						}else{
							logger.error("重新邀请 新用户发放奖励失败, userId:{}, amount:{}", u.getId(), amount);
							amount = 0;
						}

						userService.setRewardNewUserComplete(u.getId());
						repairService.removeNewUser(u.getId());
						code = 0;
						message = "ok";
					}
				}else{
					message = "邀请码错误";
					logger.warn("safari invite,重新邀请的好友有问题,userId:{},invitor_id:{}", u.getId(), invitor.getId());
				}
			} else {
				message = "邀请人不能是自己";
			}
		} 
		
		return String.format(rtvFormatMessage, code,message,MoneyUtils.fen2yuan(amount));
	}
}
