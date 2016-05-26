package com.cyhd.web.action.wx;

import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.MoneyUtils;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.*;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.CookieUtil;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.LoginIDEncoder;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.service.util.UserAgentUtil.UserAgent;
import com.cyhd.service.vo.AppTaskVo;
import com.cyhd.service.vo.UserTaskVo;
import com.cyhd.web.common.BaseAction;
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
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = {"/weixin/user", "/ios/user"})
public class WeiXinUserAction extends BaseAction{
	
	@Resource
	private UserService userService;
	
	@Resource
	private AppTaskService appTaskService;
	
	@Resource
	private UserIncomeService userIncomeService;
	
	@Resource
	private UserEnchashmentService userEnchashmentService;
	
	@Resource
	private UserRankService userRankService;
	
	@Resource
	private SourceService sourceService;
	
	@Resource
	private UserFriendService userFriendService;
	
	@Resource
	private TransArticleTaskService transArticleTaskService;
	
	@Resource
	private AccountService accountService;
	
	private final String prefix="/weixin/user/";
	
	private static final String cookie_key = "safi_uuid";
	
	@RequestMapping(value="tasks.html",method=RequestMethod.GET)
	public ModelAndView taskList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		return new ModelAndView("forward:/www/downloads/tasks.html");
	}
	
	@RequestMapping(value="income.html",method=RequestMethod.GET)
	public ModelAndView incomeDetail(HttpServletRequest request,HttpServletResponse response) throws Exception{
		//如果在引用内，则重定向到应用内的提现页面
		UserAgent userAgent = UserAgentUtil.getUserAgent(request);
		if (userAgent.isInAppView() || request.getAttribute("fromSafari") != null){
			return  new ModelAndView("forward:/web/my/income.html");
		}

		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix+"income.html.ftl");
		
		User u = getUser(request);
		//添加收入判断, 暂时不在这里加，只在用户登录的时候加
//		if(userService.isNewUser(u)){
//			userService.executeRewardNewUser(u, UserSystemMessage.PUSH_CLIENT_TYPE_ALL);
//		}
		UserIncome income = userIncomeService.getUserIncome(u.getId());
		if(income == null){
			income = new UserIncome();
		}
		UserEnchashmentAccount userAccount = userEnchashmentService.getUserEnchashmentAccount(u.getId());
		
		if(userAccount == null) {
			userAccount = new  UserEnchashmentAccount();
		}
		
		mv.addObject("user", u);
		mv.addObject("income", income);
		mv.addObject("userAccount", userAccount);
		mv.addObject("title", "我的收入");
		mv.addObject("enchashStages", UserEnchashmentService.EnchashmentStage.values());
		mv.addObject("inWeixinEnchashTime", userEnchashmentService.isInWeixinEnchashTime());
		mv.addObject("underWeixinLimit", userEnchashmentService.isUnderWeixinLimit());
		mv.addObject("code", u.getInvite_code());
		return mv;
	}
	
	@RequestMapping(value="rank_list.html",method=RequestMethod.GET)
	public ModelAndView rankList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix+"ranks.html.ftl");
		
		User u = this.getUser(request);
		
		int allRank = userRankService.getUserAllRank(u.getId());
		int monthRank = userRankService.getUserMonthRank(u.getId());
		
		mv.addObject("topUsers", userRankService.getAllTopUsers());
		mv.addObject("monthTopUsers", userRankService.getMonthTopUsers());
		mv.addObject("user", u);
		mv.addObject("allRank", allRank);
		mv.addObject("monthRank", monthRank);
		mv.addObject("userIncome", userIncomeService.getUserIncome(u.getId()));
		mv.addObject("code", u.getInvite_code());
		mv.addObject("title", "收入排行榜");
		
		return mv;
	}
	
	@RequestMapping(value = "/share/{id:[A-Za-z0-9_]+}")
	public ModelAndView showShare(@PathVariable("id")String unionIdMd5, HttpServletRequest request, HttpServletResponse response) throws Exception {
		long start = System.currentTimeMillis();
		ModelAndView mv = new ModelAndView();
		//共用原来的代码
		mv.setViewName("/www/downloads/share.html.ftl");
		
		String shareImg = "";
		boolean isGuest = true,defaultPage = true;
		if(unionIdMd5.startsWith(Constants.invite_code_prefix)) {
			String identity = unionIdMd5.substring(Constants.invite_code_prefix.length());
			Source s = sourceService.getSourceByIdentity(identity);
			if(s != null) {
				mv.addObject("source", s.getTitle());
				defaultPage = false;
			}
		} else if(unionIdMd5.length() == 32){
			User u = userService.getUserByInviteCode(unionIdMd5);
			if(u != null) {
				mv.addObject("user", u);
				isGuest = defaultPage = false;
				shareImg = u.getHeadImg();
			}
		} 
		if(defaultPage) {
			mv.addObject("source", sourceService.getSourceByIdentity("default").getTitle());
		}
		UserAgent ua = UserAgentUtil.getUserAgent(request);
		if(unionIdMd5 != null){
			if(ua != null && (ua.isIPhone()|| ua.isIPad())){
				CookieUtil.setNewCookie(cookie_key, unionIdMd5, response);
			}
		}
//		try{
//			Map<String, String> shareMap = weixinShareService.sign(destUrl) ;
//			mv.addObject("sharemap", shareMap) ;
//			if(StringUtils.isEmpty(shareImg)){
//				shareImg = shareMap.get("logo");
//			}
//		}catch(Exception e){
//			logger.error("weixin share error!",e);
//		}
		
		mv.addObject("share_img", shareImg);
		
		mv.addObject("userFriendIncome", userIncomeService.getUserIncomeLogs(20));
		mv.addObject("isInAppView", ua.isInAppView());
		//mv.addObject("isInAppView", true);
		mv.addObject("isSafari", ua.isSafari() + "");
		mv.addObject("isWeixin", ua.isWeixin() + "");
		mv.addObject("isGuest", isGuest);
		
		mv.addObject("ios",(ua.isIPhone() || ua.isIPad()) + "");
		
		boolean isNotDownload = ua.isQzone() || ua.isWeibo() || (ua.isQq() && !ua.isMQQBrowser());
		mv.addObject("isNotDownload", isNotDownload + "");
		mv.addObject("unionIdMd5", unionIdMd5);
		
		mv.addObject("income_total", MoneyUtils.fen2yuanS(userIncomeService.getMaskedTotalIncomes()));
		
		mv.addObject("title", ua.isInAppView() ? "收徒" : "下载赚大钱");
		System.out.println(System.currentTimeMillis() - start);
		return mv;
	}
	
	@RequestMapping(value="center.html",method=RequestMethod.GET)
	public ModelAndView center(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix+"PersonalCenter.html.ftl");
		
		User u = getUser(request);
		mv.addObject("u", u);
		
		Date qrTime = u.getQrcode_time();
		
		//生成的二维码 大于四天需要在重新生成 
		//生成的二维码大于七天就不要显示啦
		boolean isCreateQR = true;
		Date current = GenerateDateUtil.getCurrentDate();
		int day = 0;
		if(qrTime != null){
			long tmp =current.getTime() - qrTime.getTime() ;
			if( Constants.QR_CREATE_MIN_TTL- tmp > 0){
				isCreateQR = false;
			}
			
			day = (int) ((Constants.QR_CREATE_MAX_TTL-tmp)/Constants.day_millis);
			if(day <= 0){
				day = 1;
			}
		}
		
		mv.addObject("ttl", day);
		mv.addObject("isCreateQR", isCreateQR);
		mv.addObject("title", "我");
		return mv;
	}
	
	//选择设置提现账号页面
	@RequestMapping(value="withdraw_type.html", method=RequestMethod.GET)
	public ModelAndView withdrawType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "withdrawType.html.ftl");
		mv.addObject("title", "提现账号设置");
		mv.addObject("user", getUser(request));
		return mv;
	}
	
	//进入提现账号页面
	@RequestMapping(value="withdraw_account.html", method=RequestMethod.GET)
	public ModelAndView withdrawAccount(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "withdrawAccount.html.ftl");
		
		int type = ServletRequestUtils.getIntParameter(request, "type", UserEnchashment.ACCOUNT_TYPE_WX);

		User user = getUser(request);
		//微信提现，检查是否已关注公众号
		if (type == UserEnchashment.ACCOUNT_TYPE_WX){
			String openId = userService.getUserOpenID(GlobalConfig.weixin_pay_appid, user.getId());
			if (StringUtil.isBlank(openId)){
				UserAgent userAgent = new UserAgent(request);

				//未关注秒赚大钱公众号，提示用户去关注
				if (userAgent.isInAppView()){
					mv = new ModelAndView("common/redirect.html.ftl");
					mv.addObject("title", "微信提现账号");
					mv.addObject("url", "/static/html/tixian_yindao.html");
					mv.addObject("isAndroid", userAgent.isAndroid());
					mv.addObject("timeout", 0);
					return mv;
				}else if (request.getAttribute("fromSafari") != null){
					return new ModelAndView("redirect:/static/html/safari_frm.html?pg=/static/html/tixian_yindao.html");
				}else {
					return new ModelAndView("redirect:/static/html/tixian_yindao.html");
				}
			}
		}

		mv.addObject("type", type);
		mv.addObject("title", type == UserEnchashment.ACCOUNT_TYPE_ALIPAY? "支付宝提现账号" : "微信提现账号");
		
		UserEnchashmentAccount ue = userEnchashmentService.getUserEnchashmentAccount(user.getId());
		
		mv.addObject("ue", ue == null? new UserEnchashmentAccount() : ue);

		return mv;
	}
	
	//设置提现账号
	@RequestMapping(value="withdraw_setting.html", method=RequestMethod.POST)
	public ModelAndView withdrawSetting(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("forward:/api/v1/enchashment/setting");
	}
	
	//保存提现
	@RequestMapping(value="withdraw_action.html", method=RequestMethod.POST)
	public ModelAndView withdrawAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("forward:/api/v1/enchashment/save");
	}
	
	//关于我们页面
	@RequestMapping(value="aboutus.html", method=RequestMethod.GET)
	public ModelAndView aboutUs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("forward:/web/setting/aboutus.html");
	}

	/**
	 * safari版本首页
	 * @param request
	 * @return
	 */
	@RequestMapping("/index.html")
	public ModelAndView safariIndex(HttpServletRequest request) throws Exception{
		UserAgent userAgent = UserAgentUtil.getUserAgent(request);
		User user = (User) request.getAttribute("userInfo");
		if (userAgent == null){
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "参数错误");
		}

		if (!(userAgent.isIPhone() || userAgent.isIPad())){
			return new ModelAndView("redirect:/www/downloads/app/wxgongzhonghao");
		}

		//去往safari首页
		if (userAgent.isWeixin()){
			return new ModelAndView("redirect:/ios/enter.html?code=" + LoginIDEncoder.encode(user.getId()));
		}

		return new ModelAndView("redirect:/www/downloads/app/wxgongzhonghao");
	}
}
