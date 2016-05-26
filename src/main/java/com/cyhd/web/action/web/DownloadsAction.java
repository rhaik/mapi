package com.cyhd.web.action.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.common.util.NumberUtil;
import com.cyhd.service.dao.po.*;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.*;

import com.cyhd.service.vo.AppTaskVo;
import com.cyhd.service.vo.UserDrawLogVo;
import com.cyhd.service.vo.UserTaskVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.MoneyUtils;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.util.UserAgentUtil.UserAgent;
import com.cyhd.web.common.BaseAction;

@Controller
@RequestMapping("/www/downloads")
public class DownloadsAction extends BaseAction {

	@Resource
	UserService userService;
	
	@Resource
	private AppUpdateService appUpdateService;
	
	@Resource
	private UserShareService userShareService;
	
	@Resource
	UserIncomeService userIncomeService;
	
	@Resource
	private SourceService sourceService;
	
	@Resource
	private WeixinShareService weixinShareService;

	@Resource
	private AppTaskService appTaskService;

	@Resource
	HongbaoActivityService hongbaoService;
	
	@Resource
	private UserDrawService userDrawService;

	private static final String prefix = "/www/downloads/";
	
	/**
	 * 空白中转页，用于中转请求，跳转回APP
	 * @throws Exception
	 */
	@RequestMapping(value = "/jump")
	public ModelAndView jump(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		String uuid = CookieUtil.getCookieValue(Constants.INVITE_COOKIE_KEY, request);
		if(uuid == null)
			uuid="";
		mv.setViewName(prefix + "redirect.html.ftl");
		mv.addObject("uuid", uuid);
		return mv;
	}
	
	/**
	 * app 推广页
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/app/{id:[A-Za-z0-9_]+}")
	public ModelAndView app(@PathVariable("id")String unionIdMd5, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "downloads.html.ftl");
		UserAgent ua = UserAgentUtil.getUserAgent(request);
		
		if(unionIdMd5 != null){
			if(ua != null && ua.isIPhone()){
				CookieUtil.setNewCookie(Constants.INVITE_COOKIE_KEY, unionIdMd5, response);
			}
		}
		boolean isSafari = ua != null && ua.isSafari();
		mv.addObject("isSafari", isSafari);
		mv.addObject("uuid", unionIdMd5);
		mv.addObject("title", "下载");
		return mv;
	}
	
	@RequestMapping(value = "/ysurl")
	public ModelAndView showUrlByKey(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("下载钥匙：{},", request.getQueryString());
		request.setAttribute("comeYS", true);
		return buildDownLoad(request, response);
	}
	@RequestMapping(value = "/url")
	public ModelAndView showUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return buildDownLoad(request, response);
	}
	
	
	/**
	 * app 推广页
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/share/{id:[A-Za-z0-9_]+}")
	public ModelAndView showShare(@PathVariable("id")String unionIdMd5, HttpServletRequest request, HttpServletResponse response) throws Exception {

		//先检查UA，目前只支持IOS分享收徒
		UserAgent ua = UserAgentUtil.getUserAgent(request);
		if(!ua.isIPhone() && !ua.isIPad() && !"80000001".equals(unionIdMd5)){
			return new ModelAndView("redirect:/www/downloads/android_share/" + unionIdMd5);
		}

		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "share.html.ftl");
		if ("80000001".equals(unionIdMd5)){
			mv.setViewName(prefix + "share_baidu.html.ftl");
		}

		String shareImg = "";
		boolean isGuest = true,defaultPage = true;
		User user = null;
		if(unionIdMd5.startsWith(Constants.invite_code_prefix)) {
			String identity = unionIdMd5.substring(Constants.invite_code_prefix.length());
			Source s = sourceService.getSourceByIdentity(identity);
			if(s != null) {
				mv.addObject("source", s.getTitle());
				defaultPage = false;
			}
		} else if(unionIdMd5.length() == 32){
			user = userService.getUserByInviteCode(unionIdMd5);
		} else if (unionIdMd5.length() == 8){ //user identity
			user = userService.getUserByIdentifyId(NumberUtil.safeParseInt(unionIdMd5));
		}

		if (user != null){
			mv.addObject("user", user);
			int withdrawTotal = 0;
			UserIncome userIncome = userIncomeService.getUserIncome(user.getId());
			if (userIncome != null){
				withdrawTotal = userIncome.getEncash_total();
			}
			mv.addObject("withdrawTotal", withdrawTotal);
			isGuest = defaultPage = false;
			shareImg = user.getHeadImg();

			CookieUtil.setNewCookie(Constants.INVITE_COOKIE_KEY, user.getInvite_code(), response);
			CookieUtil.setNewCookie(Constants.INVITE_COOKIE_KEY, user.getInvite_code(),  30 * 24 * 60 * 60, GlobalConfig.isDeploy?".miaozhuandaqian.com" : ".lieqicun.cn", response);
		}

//		if(defaultPage) {
//			mv.addObject("source", sourceService.getSourceByIdentity("default").getTitle());
//		}

		if (ua.isWeixin()) {
			String destUrl = RequestUtil.getFullUrl(request);
			//如果是从钥匙首页forward过来，则将RequestURI替换成根路径，这会导致：/ios/index.html这样的路径无法微信分享
			if (request.getAttribute("fromYaoshiHome") != null){
				destUrl = destUrl.replace(request.getRequestURI(), "/");
			}

			try {
				Map<String, String> shareMap = weixinShareService.sign(destUrl, request.getHeader("Host"));
				mv.addObject("sharemap", shareMap);
				if (StringUtils.isEmpty(shareImg)) {
					shareImg = shareMap.get("logo");
				}
			} catch (Exception e) {
				logger.error("weixin share error!", e);
			}
		}

		mv.addObject("share_url", (GlobalConfig.isDeploy? "https://m.miaozhuandaqian.com" : "http://mapi.lieicun.cn")  + "/?u=" + (user == null? 0 : user.getUser_identity()));
		mv.addObject("share_img", shareImg);
		if(!ua.isAndroid()){
			mv.addObject("userFriendIncome", userIncomeService.getUserIncomeLogs(25));
		}
//		mv.addObject("userFriendIncome", userIncomeService.getUserIncomeLogsNew(20));
		mv.addObject("isInAppView", ua.isInAppView());
		//mv.addObject("isInAppView", true);
		mv.addObject("isSafari", ua.isSafari());
		mv.addObject("isWeixin", ua.isWeixin());
		mv.addObject("isGuest", isGuest);
		
		mv.addObject("ios",(ua.isIPhone() || ua.isIPad()));

		String uaStr = request.getHeader("user-agent");
		mv.addObject("isIOS9", uaStr != null && (uaStr.contains("Version/9.") || uaStr.toLowerCase().contains("os 9")));
		
		boolean isNotDownload = ua.isQzone() || ua.isWeibo() || (ua.isQq() && !ua.isMQQBrowser());
		mv.addObject("isNotDownload", isNotDownload );
		mv.addObject("unionIdMd5", unionIdMd5);
		//修改显示的累计分成
		long income_total  = userIncomeService.getMaskedTotalIncomes();
		mv.addObject("income_total", MoneyUtils.fen2yuanS(income_total));
		
		mv.addObject("title", ua.isInAppView() ? "收徒" : "下载秒赚大钱");
		mv.addObject("showHeader", false);
		return mv;
	}
	@RequestMapping(value = {"/android_share/{id:[A-Za-z0-9_]+}"})
	public ModelAndView androidShowShare(@PathVariable("id")String unionIdMd5, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "share_android.html.ftl");

		UserAgent ua = UserAgentUtil.getUserAgent(request);

		if (request.getRequestURI().endsWith("/gj")){
			mv.setViewName(prefix + "ganji.html.ftl");
		}else if ("80000001".equals(unionIdMd5)){
			mv.setViewName(prefix + "share_baidu.html.ftl");
		}else if( (ua.isIPhone()|| ua.isIPad()) && ua.isSafari()){
			mv.setViewName("redirect:/www/downloads/app/" + unionIdMd5);
			return mv;
		}

		String destUrl = Constants.share_wx_pre_link + unionIdMd5;
		
		if(request.getQueryString() != null) {
			destUrl += "?" + request.getQueryString() ;
		}
		String shareImg = "";
		boolean isGuest = true,defaultPage = true;
		if(unionIdMd5.startsWith(Constants.invite_code_prefix)) {
//			String identity = unionIdMd5.substring(Constants.invite_code_prefix.length());
//			Source s = sourceService.getSourceByIdentity(identity);
//			if(s != null) {
//				mv.addObject("source", s.getTitle());
//				defaultPage = false;
//			}
				defaultPage = false;
			
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
		
		if(unionIdMd5 != null){
			if(ua != null && (ua.isIPhone()|| ua.isIPad())){
				CookieUtil.setNewCookie(Constants.INVITE_COOKIE_KEY, unionIdMd5, response);
				CookieUtil.setNewCookie(Constants.INVITE_COOKIE_KEY, unionIdMd5,  30 * 24 * 60 * 60, GlobalConfig.isDeploy?".miaozhuandaqian.com" : ".lieqicun.cn", response);

			}
		}
		try{
			Map<String, String> shareMap = weixinShareService.sign(destUrl, request.getHeader("Host")) ;
			mv.addObject("sharemap", shareMap) ;
			if(StringUtils.isEmpty(shareImg)){
				shareImg = shareMap.get("logo");
			}
		}catch(Exception e){
			logger.error("weixin share error!",e);
		}
		
		mv.addObject("share_img", shareImg);
		
		mv.addObject("isInAppView", ua.isInAppView());
		mv.addObject("isSafari", ua.isSafari());
		mv.addObject("isWeixin", ua.isWeixin());
		mv.addObject("isGuest", isGuest);
		
		mv.addObject("ios",(ua.isIPhone() || ua.isIPad()));

		String uaStr = request.getHeader("user-agent");
		mv.addObject("isIOS9", uaStr != null && (uaStr.contains("Version/9.") || uaStr.toLowerCase().contains("os 9")));
		
		mv.addObject("unionIdMd5", unionIdMd5);
		//修改显示的累计分成
		long income_total  = userIncomeService.getMaskedTotalIncomes();
		mv.addObject("income_total", MoneyUtils.fen2yuanS(income_total));
		
		mv.addObject("title", "下载秒赚大钱");
		mv.addObject("showHeader", false);
		return mv;
	}

	@RequestMapping(value = "/tasks.html")
	public ModelAndView tasks(HttpServletRequest request, HttpServletResponse response){
		ModelAndView mv = new ModelAndView();
		UserAgent agent = UserAgentUtil.getUserAgent(request);

		if(agent.isAndroid()){
			mv.setViewName("redirect:/www/downloads/android_share/bigmoney_wxgongzhonghao");
			return mv;
		}
		mv.addObject("ios", agent.isIPhone() || agent.isIPad());
		List<UserTaskVo> tasks = appTaskService.getValidTaskVos();

		if(tasks == null || tasks.isEmpty()){
			List<AppTaskVo> appTasks = appTaskService.getSystemAppTasks();
			for(AppTaskVo task : appTasks){
				UserTaskVo vo = new UserTaskVo();
				vo.setAppTask(task.getAppTask());
				vo.setApp(task.getApp());
				tasks.add(vo);
			}
		}

		if(tasks != null && tasks.size() > 0) {
			mv.setViewName(prefix + "tasks.html.ftl");
			mv.addObject("tasks", tasks);
		} else {
			mv.addObject("tasks", "true");
			mv.setViewName("common/nodata.html.ftl");
		}
		mv.addObject("title", "任务列表");
		mv.addObject("isWeixin", agent.isWeixin());
		mv.addObject("isSafari", agent.isSafari());
		mv.addObject("yaoshiScheme", GlobalConfig.yaoshi_scheme);

		return mv;
	}

	@RequestMapping(value = "/safari")
	public ModelAndView downloadForSafari(HttpServletRequest request, HttpServletResponse response){
		String uaStr = request.getHeader("user-agent");
		UserAgent ua = UserAgentUtil.getUserAgent(request);
		boolean isWeixin = ua.isWeixin();
		if ((ua.isIPhone()|| ua.isIPad()) ){
			String viewName = prefix + "safari.html.ftl";
			if(isWeixin){
				viewName = "/safari/weixin.html.ftl";
			}
			ModelAndView mv = new ModelAndView();
			mv.addObject("yaoshiScheme", GlobalConfig.yaoshi_scheme);
			mv.addObject("isIOS9", uaStr != null && (uaStr.contains("Version/9.") || uaStr.toLowerCase().contains("os 9")));
			mv.setViewName(viewName);
			return mv;
		}
		return new ModelAndView("redirect:/www/downloads/android_share/safari");
	}
	//http://192.168.1.10:8080/www/downloads/safari
	private ModelAndView buildDownLoad(HttpServletRequest request, HttpServletResponse response){
		logger.info("getDownload url! and start download!");
		ModelAndView mv = new ModelAndView();
		UserAgent agent = UserAgentUtil.getUserAgent(request);

		try {
			if (agent.isIPhone() || agent.isIPad()) {
				String ip = RequestUtil.getIpAddr(request);
				String downloadId = CookieUtil.getCookieValue("sharedownload", request);
				Boolean comeYS = (Boolean) request.getAttribute("comeYS");
				boolean isFormYS = comeYS != null && comeYS.booleanValue();
				AppUpdateIos ios = appUpdateService.getAppUpdateIos(downloadId);
				
				//如果是从safari中的不是钥匙的要去下载钥匙
				if(isFormYS && ios != null &&  ios.isKeyApp() == false ){
					//是要下载钥匙 
					ios = null;
				}
				
				if (downloadId.isEmpty() || ios == null) {
					//String ref = request.getHeader("Referer");
					if(isFormYS){
						//是要下载钥匙 
						ios = appUpdateService.selectAppUpdateIosKey(ip);
					}else{
						ios = appUpdateService.selectAppUpdateIos(ip);
					}

					downloadId = MD5.getMD5(ios.getId() + AppUpdateService.ID_HASH_SALT);
					logger.info("user download ios:{}, new cookie:{}", ios, downloadId);
				}

				CookieUtil.setNewCookie("sharedownload", downloadId, 365 * 24 * 60 * 60, null, response);
				CookieUtil.setNewCookie("sharedownload", downloadId, 365 * 24 * 60 * 60, GlobalConfig.isDeploy? ".miaozhuandaqian.com" : ".lieqicun.cn", response);
				mv.setViewName("redirect:" + appUpdateService.getNewestDownloadUrl(ios));
			}else {
				mv.setViewName("redirect:" + appUpdateService.getAndroidVersion().getUrl());
			}
		}catch (Exception e){
			logger.error("", e);
		}
		return mv;
	}


	/**
	 * app 推广页
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/hongbao/{id:[A-Za-z0-9_]+}")
	public ModelAndView hongbaoShare(@PathVariable("id")String unionIdMd5, HttpServletRequest request, HttpServletResponse response) throws Exception {

		//先检查UA，目前只支持IOS分享收徒
		UserAgent ua = UserAgentUtil.getUserAgent(request);
		if(!ua.isIPhone() && !ua.isIPad()){
			return new ModelAndView("redirect:/www/downloads/android_share/" + unionIdMd5);
		}

		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "hongbao.html.ftl");

		String shareImg = "";
		boolean isGuest = true,defaultPage = true;
		User user = null;
		if(unionIdMd5.startsWith(Constants.invite_code_prefix)) {
			String identity = unionIdMd5.substring(Constants.invite_code_prefix.length());
			Source s = sourceService.getSourceByIdentity(identity);
			if(s != null) {
				mv.addObject("source", s.getTitle());
				defaultPage = false;
			}
		} else if(unionIdMd5.length() == 32){
			user = userService.getUserByInviteCode(unionIdMd5);
		} else if (unionIdMd5.length() == 8){ //user identity
			user = userService.getUserByIdentifyId(NumberUtil.safeParseInt(unionIdMd5));
		}

		if (user != null){
			CookieUtil.setNewCookie(Constants.INVITE_COOKIE_KEY, user.getInvite_code(), response);
			CookieUtil.setNewCookie(Constants.INVITE_COOKIE_KEY, user.getInvite_code(),  30 * 24 * 60 * 60, GlobalConfig.isDeploy?".miaozhuandaqian.com" : ".lieqicun.cn", response);
			UserDraw userDraw = userDrawService.getUserDraw(user.getId(), GlobalConfig.ACTIVITY_ID);
			if(userDraw != null && userDraw.getBalance_times() > 0){
				mv.addObject("balance", userDraw.getBalance_times());
			}
		}

//		if(defaultPage) {
//			mv.addObject("source", sourceService.getSourceByIdentity("default").getTitle());
//		}

		String destUrl = RequestUtil.getFullUrl(request);
		if (ua.isWeixin()) {
			try {
				Map<String, String> shareMap = weixinShareService.sign(destUrl, request.getHeader("Host"));
				mv.addObject("sharemap", shareMap);
				if (StringUtils.isEmpty(shareImg)) {
					shareImg = shareMap.get("logo");
				}
			} catch (Exception e) {
				logger.error("weixin share error!", e);
			}
		}

		mv.addObject("share_url", destUrl);
		mv.addObject("share_img", shareImg);
		mv.addObject("isInAppView", ua.isInAppView());
		mv.addObject("isSafari", ua.isSafari());
		mv.addObject("isWeixin", ua.isWeixin());
		mv.addObject("isGuest", isGuest);

		mv.addObject("ios",(ua.isIPhone() || ua.isIPad()));

		boolean isNotDownload = ua.isQzone() || ua.isWeibo() || (ua.isQq() && !ua.isMQQBrowser());
		mv.addObject("isNotDownload", isNotDownload );
		mv.addObject("unionIdMd5", unionIdMd5);

		mv.addObject("title", ua.isInAppView() ? "收徒" : "摇一摇红包");

		List<UserDrawLogVo> drawLogVoList = hongbaoService.getLatestHongbaoList();
		mv.addObject("hongbaoList", drawLogVoList);

		return mv;
	}
}
