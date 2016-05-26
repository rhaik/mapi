package com.cyhd.web.action.web;

import com.cyhd.common.util.AesCryptUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.Account;
import com.cyhd.service.dao.po.TransArticle;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.CookieUtil;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.IdEncoder;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.service.vo.UserIncomeLogVo;
import com.cyhd.web.common.BaseAction;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequestMapping("/www/new_user/article/")
public class NewUserTranArticleAction extends BaseAction{

	@Resource
	private UserArticleTaskService userArticleTaskService;
	
	@Resource
	private TransArticleTaskService transArticleTaskService;
	
	@Resource 
	private AccountService accountService;
	
	@Resource
	private TaskUpdateTimeHintService taskUpdateTimeHintService;

	@Resource
	private WeixinArticleService weixinArticleService;
	
	@Resource
	private  UserFriendService userFriendService;
	
	@Resource
	private UserIncomeService userIncomeService;
	
	private static final String AUTH_CODE = "AUTH_CODE";
	private static final String USER_TICKET = "USER_TICKET";
	
	///web/article/
	private final String prefix = "/web/article/";
	
//	@RequestMapping(value={"zhuangfa.html"})
//	public ModelAndView newUserBindMobileAndforward(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		
//		UserAgent ua = UserAgentUtil.getUserAgent(request);
//		if(ua.isWeixin()){
//			User u = null;
//			try{
//				u = getUser(request);
//				if(u == null){
//					return newUserBindMobileAndforwardCheck(request, response);	
//				}
//			}catch(Exception e){
//				logger.info("没有得到用户,去得到用户");
//			}
//		}
//		
//		ModelAndView mv = new ModelAndView();
//		
//		User user =getUser(request);
//		int invite = userFriendService.getInvitor(user.getId());
//		if(invite > 0){
//			User invitor = userService.getUserById(invite);
//			if(invitor != null){
//				List<UserIncomeLogVo> logs =  userIncomeService.getUserFriendInviteIncomeDetail(user.getId(), invite, 0, 1);
//				if(logs!= null && logs.isEmpty() == false){
//					mv.addObject("friendLog", logs.get(0));
//				}
//			}
//		}
//		//
//		
//		Account wxAccount = null;
//		if (GlobalConfig.isDeploy) {
//			wxAccount = accountService.getRandomAccount();
//		} else {
//			wxAccount = accountService.getAccountByHost(request.getHeader("Host"));
//		}
//		if(ua.isWeixin()){
//			
//			return mv;
//		}else if(ua.isInAppView()){
//			TransArticleTask task = transArticleTaskService.getSystemTask();
//			if(task != null){
//				UserArticleTaskVo vo =new UserArticleTaskVo();
//				vo.setTransArticleTask(task);
//				vo.setTransArticle(this.transArticleTaskService.getTransArticle(task.getArticle_id()));
//				vo.setReceived(user.isTranArticleComplete());
////				//获取分享的微信公众号信息，微信内和测试环境的分享根据域名来获取
//				String shareUrl = userArticleTaskService.getShareUrlByNewUserTask(user, wxAccount, vo);
//				mv.addObject("shareUrl",shareUrl );
//				String shareData = UserArticleTaskService.getShareData(user, wxAccount, vo);
//				mv.addObject("_data", shareData);
//				mv.addObject("logo", GlobalConfig.logo);
//			}
//		}
//		
//		mv.addObject("sign", AesCryptUtil.encrypt(user.getOpenid(), Constants.ARTICLE_AES_PASSWORD));
//		mv.addObject("user", user);
//		mv.setViewName(prefix+"zhuanfa.html.ftl");
//		return mv;
//	}
	
	@RequestMapping("auth")
	public ModelAndView zhuanfaAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		
		String accountIdStr = ServletRequestUtils.getStringParameter(request, "acc", "");
		//这里使用的User标识码 不是user_id
		String taskid = ServletRequestUtils.getStringParameter(request, "taskid");
		String userInfoId = ServletRequestUtils.getStringParameter(request, "u_id");
		String access_code = ServletRequestUtils.getStringParameter(request, "code");
		
		taskid = AesCryptUtil.decrypt(taskid, Constants.ARTICLE_AES_PASSWORD);
		userInfoId	= AesCryptUtil.decryptWithCaseInsensitive(userInfoId, Constants.ARTICLE_AES_PASSWORD);
		String query = RequestUtil.getQueryString(request);
		int accountid = IdEncoder.decode(accountIdStr);
		//logger.info(msg);
		if(StringUtils.isBlank(taskid) 
				|| StringUtils.isBlank(userInfoId)
				||accountid <= 0
				||StringUtils.isBlank(access_code)){
			logger.info("新手任务-转发微信认证,参数错误:{}",query);
			return getErrorView("参数错误！");
		}
		
		int uid = Integer.parseInt(userInfoId);
		
		if(logger.isInfoEnabled()){
			logger.info("Weixin callback auth, params:"+request.getQueryString());
		}
		
		int articleId = 0;
		try {
			articleId = Integer.parseInt(taskid);
		} catch (Exception e) {
		}
		
		if( articleId <= 0){
			return getErrorView("参数错误！");
		}
		TransArticle article = transArticleTaskService.getTransArticle(articleId);
		if(article == null){
			return getErrorView("参数错误！");
		}

		Account account = accountService.getAccount(accountid);
		if(account == null){
			return getErrorView("参数错误！");
		}

		String url = "/www/new_user/article/zhuangfa2.html";//article.getUrl();

		if(StringUtils.isEmpty(access_code)){
			if(logger.isWarnEnabled()){
				logger.warn("access code is null, user not author!");
			}
			return getErrorView("认证失败");
		}

		String appId = account.getWxappid();
		String appSecret = account.getWxappsecret();
		
		//获取token
		StringBuilder sb = new StringBuilder(500);
		sb.append("https://api.weixin.qq.com/sns/oauth2/access_token");
		sb.append("?appid=").append(appId);
		sb.append("&secret=").append(appSecret);
		sb.append("&code=").append(access_code);
		sb.append("&grant_type=").append("authorization_code");
		String accessUrl =sb.toString();
		//清空 下一个用
		sb.delete(0, sb.length());
		String content = HttpUtil.get(accessUrl, null, "utf-8");
		if(logger.isInfoEnabled()){
			logger.info("CallbackController.auth get access token from wx, url={}, returns: {} ", accessUrl, content);
		}
		try{
			JSONObject retJson =JSONObject.fromObject(content);
			if(retJson.has("errcode")){
				if(logger.isErrorEnabled()){
					logger.error("appId={}, response error:{}",appId, content);
				}
				return mv;
			}
/***
 {
 "access_token":"ACCESS_TOKEN",
 "expires_in":7200,
 "refresh_token":"REFRESH_TOKEN",
 "openid":"OPENID",
 "scope":"SCOPE",
 "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
 }
 */
			String openId = retJson.getString("openid");
			String accessToken = retJson.getString("access_token");
			String refreshToken = retJson.getString("refresh_token");

			String unionid=retJson.containsKey("unionid")?retJson.getString("unionid"):null;
			//needUserInfo,taskid
			Integer article_task_id = IdEncoder.decode(taskid);
			if(GlobalConfig.isDeploy){
				if(StringUtils.isEmpty(unionid)){
					logger.error("appId={}, get unionid error!",appId);
					return mv;
				}
			}
			//获取用户信息
			//?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
			sb.append("https://api.weixin.qq.com/sns/userinfo");
			sb.append("?access_token=").append(accessToken);
			sb.append("&openid=").append(openId);
			sb.append("&lang=").append("zh_CN");
			accessUrl = sb.toString();
			sb.delete(0, sb.length());
			logger.info("请求微信获取用户信息:url:{}",accessUrl);
			content = HttpUtil.get(accessUrl, null, "utf-8");
			logger.info("微信返回"+content);
			retJson =JSONObject.fromObject(content);
			if(retJson.has("errcode")){
				if(logger.isErrorEnabled()){
					logger.error("appId={}, response error:{}",appId, content);
				}
				//怎么办 不让访问
				return getErrorView("认证失败");
			}
			User u = userService.getUserByUnionId(unionid);
			User invitor= null;
			logger.info("user->"+u);
			if(u == null){
				u = genNewUser(retJson,uid);
			}
			
			CookieUtil.setNewCookie(USER_TICKET, IdEncoder.encode(u.getId()), response);
			int invite = userFriendService.getInvitor(u.getId());
			invitor = userService.getUserById(invite);
			
			if(invitor != null){
				List<UserIncomeLogVo> logs =  userIncomeService.getUserFriendInviteIncomeDetail(u.getId(), invitor.getId(), 0, 1);
				if(logs!= null && logs.isEmpty() == false){
					mv.addObject("friendLog", logs.get(0));
				}
			}
			CookieUtil.setNewCookie(AUTH_CODE, u.getInvite_code(), response);
			sb.delete(0, sb.length());
			sb.append("redirect:"+url);
			sb.append("?wxId=").append(accountIdStr);
			sb.append("&invite=").append(AesCryptUtil.encryptWithCaseInsensitive(String.valueOf(u.getUser_identity()), Constants.ARTICLE_AES_PASSWORD));
			url = sb.toString();
			mv.setViewName(url);
			logger.info("微信认证重定向:{}",url);
		}catch(Exception e){
			if(logger.isErrorEnabled())
				logger.error("parse callback content error", e);
			return getErrorView("认证失败");
		}
		if(logger.isInfoEnabled())
			logger.info("redirect to:" + url);
		return mv;
	}
	
	@RequestMapping(value={"zhuangfa.html"})
	public ModelAndView newUserBindMobileAndforwardCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//invite wxId
		ModelAndView mv = new ModelAndView();
		String wxIdStr = ServletRequestUtils.getStringParameter(request,"wxId","");
		String inviteStrSrc = ServletRequestUtils.getStringParameter(request,"invite","");
		
		Integer wxid = IdEncoder.decode(wxIdStr);
		String	inviteStr = AesCryptUtil.decryptWithCaseInsensitive(inviteStrSrc, Constants.ARTICLE_AES_PASSWORD);
		
		if(wxid == null||StringUtils.isBlank(inviteStr)){
			return getErrorView("来源不对");
		}
		Account wxAccount = accountService.getAccount(wxid);
		
		mv.addObject("isWeiXin", true);
		StringBuilder sb = new StringBuilder(300);
		String host = wxAccount.getHost();
		sb.append(host);
		if(!host.endsWith("/")){
			sb.append("/");
		}
		sb.append("www/new_user/article/auth?acc=").append(wxIdStr);
		sb.append("&u_id=").append(inviteStrSrc);
		sb.append("&taskid=").append(AesCryptUtil.encrypt("1", Constants.ARTICLE_AES_PASSWORD));
		String url = sb.toString();
		//清空sb
		sb.delete(0, sb.length());
		
		String redirectUrl = URLEncoder.encode(url, "utf-8");
		String appId = wxAccount.getWxappid();
		String appSecret = wxAccount.getWxappsecret();
		String scope = "snsapi_userinfo";
		
		sb.append("https://open.weixin.qq.com/connect/oauth2/authorize");
		sb.append("?appid=").append(appId);
		sb.append("&redirect_uri=").append(redirectUrl);
		sb.append("&response_type=").append("code");
		sb.append("&scope=").append(scope);
		sb.append("&state=").append(wxAccount.getId());
		sb.append("#wechat_redirect");
		
		String jumpUrls = sb.toString();
		if(logger.isInfoEnabled()){
			logger.info("新手任务：jump to weixin auth url: "  + jumpUrls);
		}
		mv.setViewName("redirect:" +jumpUrls);
		
		return mv;
	}

	@RequestMapping(value={"zhuangfa2.html"})
	public ModelAndView newUserBindMobileAndforward(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String inviteCode = CookieUtil.getCookieValue(AUTH_CODE, request);
		String ticket = CookieUtil.getCookieValue(USER_TICKET, request);
		logger.info("得到的cookie:inviteCode:{},ticket:{}",inviteCode,ticket);
		User user = null;
		//用户未登录，优先用cookie值进行登录，否则用cookie中ticket进行登陆
		if (StringUtils.isNotBlank(inviteCode)) {
			user = userService.getUserByInviteCode(inviteCode);
		}else if(StringUtils.isNotBlank(ticket)){ //old user
			Integer uid = IdEncoder.decode(ticket);
			if(uid != null && uid.intValue() > 0){
				user = userService.getUserById(uid.intValue());
			}
		}
		logger.info("获得用户:user:{}",user);
		if(user == null){
			return new ModelAndView("forward:/www/new_user/article/zhuangfa.html");
		}else{
			ModelAndView mv = new ModelAndView();
			mv.addObject("sign", AesCryptUtil.encryptWithCaseInsensitive(user.getOpenid(), Constants.ARTICLE_AES_PASSWORD));
			mv.addObject("se_id", user.getInvite_code());
			mv.addObject("isWexin", true);
			List<UserIncomeLogVo> list = userIncomeService.getUserIncomeLogs(20);
			mv.addObject("friendLogList", list);
			mv.setViewName("/web/article/zhuanfa.html.ftl");
			mv.addObject("user", user);
			return mv;
		}
	}
	public User genNewUser(JSONObject retJson,int uid){
		String openId = retJson.getString("openid");
		String avatar = retJson.getString("headimgurl");
		String name = retJson.getString("nickname");
		int sex = 1;
		if(retJson.containsKey("sex"))
			sex = retJson.getInt("sex");
		
		String country = "CN";
		String province = "";
		String city = "";
		String unionid = "";
		if(retJson.containsKey("country"))
			country = retJson.getString("country");
		if(retJson.containsKey("province"))
			province = retJson.getString("province");
		
		if(retJson.containsKey("city"))
			city = retJson.getString("city");
		
		if(retJson.containsKey("unionid"))
			unionid = retJson.getString("unionid");
		
		//创建一个新的用户
		User u = new User();
		u.setOpenid(openId);
		u.setTicket(userService.generateTicket(openId));
		u.setSex(sex);
		u.setUnionid(unionid);
		u.setInvite_code(MD5Util.getMD5(unionid));
		u.setCountry(country);
		u.setCity(city);
		u.setProvince(province);
		u.setAvatar(avatar);
		u.setName(name);
		u.setUser_identity(userService.generateIdentityId());
		u.setCreatetime(GenerateDateUtil.getCurrentDate());
		if(userService.insertOrUpdate(u)){
			//增加徒弟
			//公众号没有绑在一个下面的话 就得不到unionid
			//TODO  上线修改
			u = userService.getUserByOpenId(openId);
			User invitor = userService.getUserByIdentifyId(uid);
			userFriendService.onAddUserFriend(invitor, u, null);
			return u;
		}
		return null;
	}
}
