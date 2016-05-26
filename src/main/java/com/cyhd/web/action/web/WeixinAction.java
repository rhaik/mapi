package com.cyhd.web.action.web;

import java.net.URLEncoder;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.common.util.NumberUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.common.util.UUIDUtil;
import com.cyhd.service.dao.po.TransArticleTask;
import com.cyhd.service.dao.po.UserArticleTask;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.*;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.service.dao.po.Account;
import com.cyhd.service.dao.po.TransArticle;
import com.cyhd.web.common.BaseAction;

@Controller
public class WeixinAction extends BaseAction {

	@Resource
	AccountService accountService;

	@Resource
	private UserArticleTaskService userArticleTaskService;

	@Resource
	private TransArticleTaskService transArticleTaskService;

	@Resource
	private UserViewArticleRecordService userViewArticleRecordService;

	private static Logger logger = LoggerFactory.getLogger("wechat");
	
	@RequestMapping(value = "/wec{rd:\\d+}/chc{td:\\d+}/{id:\\w+}")//加密后的任务ID
	public ModelAndView check2(@PathVariable("rd")int random,@PathVariable("td")int random2, @PathVariable("id")String taskEncodeId,HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(logger.isInfoEnabled()){
			logger.info("enter check2, random1={}, random2={}, encodeId={}, url={}", random, random2, taskEncodeId, request.getRequestURI());
		}
		return check(taskEncodeId, request, response);
	}

	@RequestMapping(value = "/wechat/check/{id:\\w+}")//加密后的任务ID
	public ModelAndView check(@PathVariable("id")String taskEncodeId,HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(logger.isInfoEnabled()){
			logger.info("enter check,  encodeId={}, url={}", taskEncodeId, request.getRequestURI());
		}
		ModelAndView mv = new ModelAndView();
		if(logger.isInfoEnabled()){
			logger.info("Weixin check auth, params:"+request.getQueryString());
		}
		//转发任务的ID 
		Integer taskId = IdEncoder.decode(taskEncodeId);
		if(taskId == null){
			logger.info("task_id is not found");
			return getErrorView("参数错误");
		}
		//获得文章ID
		String a_id = ServletRequestUtils.getStringParameter(request,"a_id","");
		Integer aid = IdEncoder.decode(a_id);
		if(aid == null){
			logger.info("文章Id is not found");
			return getErrorView("参数错误！");
		}
		//用的是user的用户标识符码
		int userId = ServletRequestUtils.getIntParameter(request,"u_id",0);
		if(userId <= 0){
			logger.info("not found user");
		}
		String w_id = ServletRequestUtils.getStringParameter(request,"w_id","");
		Integer wx_id = IdEncoder.decode(w_id);
		if(wx_id == null){
			logger.error("解密出的wx_id为null");
			return getErrorView("参数错误！");
		}
		Account account = accountService.getAccount(wx_id);
		if(account == null){
			logger.info("weixin_id:{}",wx_id);
			return getErrorView("参数错误！");
		}
		int accountid = account.getId();
		String url = null;

		StringBuilder sb = new StringBuilder(300);
		String host = account.getHost();
		sb.append(host);
		if(!host.endsWith("/")){
			sb.append("/");
		}
		sb.append("wechat/auth?acc=").append(accountid);
		sb.append("&userinfo=").append(userId);
		sb.append("&taskid=").append(taskEncodeId);
		sb.append("&a_id=").append(a_id);
		url = sb.toString();

		String redirectUrl = URLEncoder.encode(url, "utf-8");
		String appId = account.getWxappid();
		String appSecret = account.getWxappsecret();
		String scope = "snsapi_base";
		String jumpUrls = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+
				"&redirect_uri="+redirectUrl+"&response_type=code&scope="+scope+"&state="+ accountid+ "#wechat_redirect";
		if(logger.isInfoEnabled()){
			logger.info("jump to weixin auth url: "  + jumpUrls);
		}
		mv.setViewName("redirect:" +jumpUrls);
		return mv;
	}

	@RequestMapping(value = "/wechat/auth")
	public ModelAndView auth(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView mv = new ModelAndView();
		//String url = ServletRequestUtils.getStringParameter(request, "url", "");
		int accountid = ServletRequestUtils.getIntParameter(request, "acc", 0);
		String access_code = ServletRequestUtils.getStringParameter(request, "code");
		//这里使用的User标识码 不是user_id
		int needUserInfo = ServletRequestUtils.getIntParameter(request, "userinfo", 0);
		String taskid = ServletRequestUtils.getStringParameter(request, "taskid");
		String articleIdStr = ServletRequestUtils.getStringParameter(request, "a_id");

		String ip = RequestUtil.getIpAddr(request);
		if(logger.isInfoEnabled()){
			logger.info("Weixin callback auth, params:{}, ip:{}", request.getQueryString(), ip);
		}
		Integer articleId = IdEncoder.decode(articleIdStr);
		if(articleId == null || articleId <= 0){
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

		String url = article.getUrl();

		//如果是微信分享任务
		if (article.getType() == TransArticleTask.TYPE_WEIXIN){
			String host = account.getHost();
			if(!host.endsWith("/")){
				host = host + "/";
			}
			url = host + "open/article/" + taskid;
		}

		mv.setViewName("redirect:" + url.trim());
		if(StringUtils.isEmpty(access_code)){
			if(logger.isWarnEnabled()){
				logger.warn("access code is null, user not author!");
			}
			return mv;
		}



		String appId = account.getWxappid();
		String appSecret = account.getWxappsecret();

		String accessUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appId
				+"&secret="+appSecret+"&code="+access_code+"&grant_type=authorization_code";

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
			String openId = retJson.optString("openid");
			String accessToken = retJson.optString("access_token");
			String refreshToken = retJson.optString("refresh_token");

			String unionid=retJson.optString("unionid");
			//needUserInfo,taskid
			Integer article_task_id = IdEncoder.decode(taskid);
			if(StringUtils.isEmpty(unionid)){
				logger.warn("appId={}, get unionid error! set union id to openid:{}", appId, openId);
//				return mv;

				unionid = openId; //使用openId代替union id， 同一篇文章分享出去的公众号是一致的
			}
			

			if(article_task_id != null && articleId != null){
				userViewArticleRecordService.record(needUserInfo,article_task_id,articleId, unionid,  accountid, openId, ip);
			}else{
				if (logger.isErrorEnabled()) {
					logger.error("似乎存在盗链：view_unionid:{},openId:{}",unionid,openId );
				}
			}
			//CookieUtil.setNewCookie(Constants.openId_cookie_name+accountid, openId, response);
		}catch(Exception e){
			if(logger.isErrorEnabled())
				logger.error("parse callback content error", e);
			return mv;
		}
		if(logger.isInfoEnabled())
			logger.info("redirect to:" + url);
		return mv;
	}


	/**
	 * 用户复制链接后到微信打开的页面，成功后，种下cookie，并重定向到用户浏览文章的页面
	 * 该页面根据cookie判断用户是不是分享用户
	 *
	 * @param taskId
	 * @param request
	 * @return
	 */
	@RequestMapping("/sha{rd:\\d+}/{utid:\\w+}")
	public ModelAndView showSharePage(@PathVariable("utid") String utStr, @PathVariable("rd") String rd,
									  HttpServletRequest request, HttpServletResponse response){
		if (StringUtils.isBlank(utStr)){
			return getErrorView("参数错误");
		}

		//判断UA
		UserAgentUtil.UserAgent userAgent = UserAgentUtil.getUserAgent(request);
		if (!(userAgent.isIPad() || userAgent.isIPhone() || userAgent.isAndroid())){
			return getErrorView("参数错误");
		}

		if (!userAgent.isWeixin()){
			return getErrorView("参数错误");
		}

		String host = request.getHeader("Host");
		long userTaskId = UserArticleTaskService.decryptUserTaskId(host, utStr);
		if (userTaskId > 0){
			UserArticleTask userTask = userArticleTaskService.getUserArticleTaskById(userTaskId);

			logger.info("show share page for user:{}", userTask);

			if (userTask != null){
				String viewArticleCookie = "share_" + TransArticle.getHiddenId(userTask.getUser_id() + userTask.getArticle_id());
				CookieUtil.setNewCookie(viewArticleCookie, rd, response);

				String tarLink = String.format("/tar%d/%s", 10000 + new Random().nextInt(90000), UserArticleTaskService.encyptViewTaskId(host, userTaskId));
				return new ModelAndView("redirect:" + tarLink);
			}

		}

		return getErrorView("参数错误");

	}

	/**
	 * 用户自己或者分享后查看文章的链接，根据Cookie判断是否要重定向到最终的分享链接
	 * @param utStr
	 * @param request
	 * @return
	 */
	@RequestMapping("/tar{rd:\\d+}/{utid:\\w+}")
	public ModelAndView showTargetPage(@PathVariable("utid") String utStr,
									   @PathVariable("rd") String rd,
									   HttpServletRequest request,
									   HttpServletResponse response){
		if (StringUtils.isBlank(utStr)){
			return getErrorView("参数错误");
		}

		//设置阅读者cookie
		String unionId = CookieUtil.getCookieValue("wxuid", request);
		if (StringUtil.isBlank(unionId)){
			unionId = UUIDUtil.getCommonUUID();
		}
		CookieUtil.setNewCookie("wxuid", unionId, 365 * 24 * 3600, null, response);


		String host = request.getHeader("Host");
		long userTaskId = UserArticleTaskService.decryptViewTaskId(host, utStr);
		if (userTaskId > 0){
			UserArticleTask userTask = userArticleTaskService.getUserArticleTaskById(userTaskId);

			TransArticleTask articleTask = transArticleTaskService.getTransArticleTask(userTask.getArticle_task_id());

			if (userTask != null){
				TransArticle article = transArticleTaskService.getTransArticle(userTask.getArticle_id());

				String viewArticleCookie = "share_" +  TransArticle.getHiddenId(userTask.getUser_id() + userTask.getArticle_id());
				String shareCookie = CookieUtil.getCookieValue(viewArticleCookie, request);


				//判断UA
				UserAgentUtil.UserAgent userAgent = UserAgentUtil.getUserAgent(request);
				if (!(userAgent.isIPad() || userAgent.isIPhone() || userAgent.isAndroid())){
					return new ModelAndView("redirect:" + article.getUrl());
				}

				if (!userAgent.isWeixin()){
					return new ModelAndView("redirect:" + article.getUrl());
				}


				//没有cookie，说明是其他用户
				if (StringUtil.isBlank(shareCookie)){

					//首先检查session
					if (request.getSession().getAttribute(viewArticleCookie) == null){
						request.getSession().setAttribute(viewArticleCookie, 1);

						//记录阅读人
						String ip = RequestUtil.getIpAddr(request);
						userViewArticleRecordService.record(userTask,unionId, NumberUtil.safeParseInt(rd), unionId, ip);
					}

//					ModelAndView mv = new ModelAndView("/common/redirect.html.ftl");
//					mv.addObject("title", articleTask.getName());
//					mv.addObject("url", article.getUrl());
//
//					return mv;
					return new ModelAndView("redirect:" + article.getUrl());

				}else{ //说明在微信中进行分享
					logger.info("user start share article:{}", userTask);

					ModelAndView mv = new ModelAndView("/open/share_article.html.ftl");
					mv.addObject("articleTask", articleTask);
					mv.addObject("article", article);

					return mv;
				}
			}
		}

		return getErrorView("参数错误");
	}


}
