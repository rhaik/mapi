package com.cyhd.web.action.api;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.MoneyUtils;
import com.cyhd.common.util.ObjectUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.*;
import com.cyhd.service.impl.UserMessageService;
import com.cyhd.service.util.IdEncoder;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1")
public class UserMessageApiAction extends BaseAction {

	@Resource
	UserMessageService userMessageService;
	

	private static final String prefix = "/api/v1/message/";

	/**
	 * 应用试用消息列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = {"/message/app","/aj/ja"})
	public ModelAndView AppList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "app.json.ftl");
		User u = getUser(request);
		long lastId = ServletRequestUtils.getLongParameter(request, "last_id", 0);
		
		int total = userMessageService.getAppMessageCount(u.getId());
		List<UserAppMessage> ls = new ArrayList<UserAppMessage>(); 
		if(total > 0) {
			ls = userMessageService.getAppMessageList(u.getId(), lastId, defaultPageSize);
		}
		
		mv.addObject("messages", ls);
		mv.addObject("total", total);
		boolean hasMore = (ls != null && ls.size() == defaultPageSize);
		mv.addObject("ismore", String.valueOf(hasMore));
		return mv; 
	}

	/**
	 * Safari版本的试用消息列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"/message/app-msg-safari"}, produces="text/json; charset=UTF-8")
	@ResponseBody
	public String safariAppList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User u = getUser(request);
		long lastId = ServletRequestUtils.getLongParameter(request, "last_id", 0);

		int total = userMessageService.getAppMessageCount(u.getId());
		List<UserAppMessage> ls = new ArrayList<UserAppMessage>();
		if(total > 0) {
			ls = userMessageService.getAppMessageList(u.getId(), lastId, defaultPageSize);
		}

		JsonObject jo_p = new JsonObject();
		jo_p.addProperty("has_more", ls.size() == defaultPageSize);
		jo_p.addProperty("total", total);
		jo_p.addProperty("code", 0);
		jo_p.addProperty("message", "OK");

		JsonArray array = new JsonArray();

		for (UserAppMessage message : ls){
			JsonObject object = new JsonObject();
			object.addProperty("id", message.getSort_time());
			object.addProperty("time", DateUtil.getBeforeDateTimeShow(message.getCreate_time()));
			object.addProperty("app_name", message.getApp_name());
			object.addProperty("status", message.getStatus());
			object.addProperty("amount", MoneyUtils.fen2yuanS(message.getAmount()));
			array.add(object);
		}
		jo_p.add("data", array);
		return jo_p.toString();
	}


	/**
	 * 好友消息列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
//	@RequestMapping(value = "/friend", method = RequestMethod.POST)
//	public ModelAndView FriendList(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ModelAndView mv = new ModelAndView();
//		mv.setViewName(prefix + "/friend.json.ftl");
//		User u = getUser(request);
//		
//		long lastId = ServletRequestUtils.getLongParameter(request, "last_id", 0);
//		int total = userMessageService.getFriendMessageCount(u.getId());
//		List<UserFriendMessage> ls = new ArrayList<UserFriendMessage>(); 
//		if(total > 0) {
//			ls = userMessageService.getFriendMessageList(u.getId(), lastId, defaultPageSize);
//		}
//		mv.addObject("messages", ls);
//		mv.addObject("total", total);
//		boolean hasMore = (ls != null && ls.size() == defaultPageSize);
//		mv.addObject("ismore", String.valueOf(hasMore));
//		return mv; 
//	} 
	@RequestMapping(value = {"/message/friend","/ak/ka"},produces="text/json; charset=UTF-8")
	@ResponseBody
	public String FriendList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//ModelAndView mv = new ModelAndView();
		//mv.setViewName(prefix + "/friend.json.ftl");
		User u = getUser(request);
		
		long lastId = ServletRequestUtils.getLongParameter(request, "last_id", 0);
		int total = userMessageService.getFriendMessageCount(u.getId());
		List<UserFriendMessage> ls = new ArrayList<UserFriendMessage>();
		if(total > 0) {
			ls = userMessageService.getFriendMessageList(u.getId(), lastId, defaultPageSize);
		}
		
		boolean hasMore = (ls != null && ls.size() == defaultPageSize);
		
		JsonObject jo_p = new JsonObject();
		jo_p.addProperty("has_more", hasMore);
		jo_p.addProperty("total", total);
		jo_p.addProperty("code", 0);
		jo_p.addProperty("message", "OK");
		
		JsonArray array = new JsonArray();
		
		if(ls != null && ls.size() > 0){
			for(UserFriendMessage message : ls){
				JsonObject object = new JsonObject();
				if (request.getAttribute("fromSafari") != null){
					object.addProperty("id", message.getSort_time());
					object.addProperty("time", DateUtil.getBeforeDateTimeShow(message.getCreate_time()));
					object.addProperty("title", "好友“" + message.getFriend_name() + "”完成限时任务");
					object.addProperty("content", UserMessageService.getFriendMessageContent(message));
				}else {
					object.addProperty("source", message.getSource());
					object.addProperty("db_id", message.getId());
					object.addProperty("id", message.getSort_time());
					object.addProperty("app_name", message.getApp_name());
					object.addProperty("friend_avater", message.getFriendHeadImg());
					object.addProperty("friend_name", message.getFriend_name());
					object.addProperty("friend_amount", MoneyUtils.fen2yuanS(message.getFriend_amount()));
					object.addProperty("create_time", DateUtil.format(message.getCreate_time(),"yyyy-MM-dd HH:mm"));
					object.addProperty("amount", MoneyUtils.fen2yuanS(message.getAmount()));
					object.addProperty("level", message.getFriend_level());
					object.addProperty("timestamp", message.getTimestamp());
				}

				array.add(object);
			}
		}
		jo_p.add("data", array);
		return jo_p.toString();
	}
	/**
	  * {
	<#include "/common/status.json.ftl">,
	"total" : ${total},
	"has_more" : ${ismore},
	"data" : [
		<#if messages?? && (messages?size > 0)>
				<#list messages as m>
				{
					"id" : ${m.sort_time},
					"db_id" : ${m.id},
					"title" : "${util.jsonQuote(m.title)}",
					"type" : ${m.type},
					"create_time" : "${m.create_time?string('yyyy-MM-dd HH:mm')}",
					"timestamp" : "${m.timestamp}",
					"thumb" : "${m.thumb!''}",
					"description" : "${util.jsonQuote(m.description!'')}",
					"content" : "${util.jsonQuote(m.content!'')}",
					"url":"${util.jsonQuote(m.target_url!'')}"
				}<#if m_index != messages?size -1>,</#if>
				</#list>
			</#if>
	]
}
	 */
	@RequestMapping(value = {"/message/system","/al/la"}, produces="text/json; charset=UTF-8")
	@ResponseBody
	public String sysList2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		int clientType = clientInfo.isIos()?UserSystemMessage.PUSH_CLIENT_TYPE_IOS:UserSystemMessage.PUSH_CLIENT_TYPE_ANDROID;
		
		long lastId = ServletRequestUtils.getLongParameter(request, "last_id", 0);
		
		int total = userMessageService.getSysMessageCount(u.getId(), u.getCreatetime(),clientType);
		List<UserSystemMessage> ls = new ArrayList<UserSystemMessage>();
		if(total > 0) {
			ls = userMessageService.getSysMessageList(u.getId(), u.getCreatetime(), lastId, defaultPageSize,clientType);
		} 
		boolean hasMore = (ls != null && ls.size() == defaultPageSize);
		
		JsonObject jo_p = new JsonObject();
		jo_p.addProperty("has_more", hasMore);
		jo_p.addProperty("total", total);
		jo_p.addProperty("code", 0);
		jo_p.addProperty("message", "OK");
		
		JsonArray array = new JsonArray();
		if(ls != null && ls.size() > 0){
			for(UserSystemMessage message : ls){
				JsonObject object = new JsonObject();

				if (request.getAttribute("fromSafari") != null) {
					object.addProperty("id", message.getSort_time());
					object.addProperty("time", DateUtil.getBeforeDateTimeShow(message.getCreate_time()));
					object.addProperty("title", message.getTitle());
					object.addProperty("content", message.getContent());
				}else {
					object.addProperty("id", message.getSort_time());
					object.addProperty("db_id", message.getId());
					object.addProperty("title", ObjectUtil.getStringDefaultValue(message.getTitle(), ""));
					object.addProperty("type", message.getType());
					object.addProperty("create_time", DateUtil.format(message.getCreate_time(), "yyyy-MM-dd HH:mm"));
					object.addProperty("timestamp", message.getTimestamp());
					object.addProperty("thumb", ObjectUtil.getStringDefaultValue(message.getThumb(), ""));
					object.addProperty("description", ObjectUtil.getStringDefaultValue(message.getDescription(), ""));
					object.addProperty("content", ObjectUtil.getStringDefaultValue(message.getContent(), ""));
					object.addProperty("url", ObjectUtil.getStringDefaultValue(message.getTarget_url(), ""));
				}
				array.add(object);
			}
		}
		jo_p.add("data", array);
		return jo_p.toString();
	}
	/**
	 * 系统消息列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
//	@RequestMapping(value = {"/message/system","/al/la"}, method = RequestMethod.POST)
//	public ModelAndView SysList(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ModelAndView mv = new ModelAndView();
//		mv.setViewName(prefix + "/system.json.ftl");
//		User u = getUser(request);
//		ClientInfo clientInfo = getClientInfo(request);
//		int clientType = clientInfo.isIos()?UserSystemMessage.PUSH_CLIENT_TYPE_IOS:UserSystemMessage.PUSH_CLIENT_TYPE_ANDROID;
//		
//		long lastId = ServletRequestUtils.getLongParameter(request, "last_id", 0);
//		
//		int total = userMessageService.getSysMessageCount(u.getId(), u.getCreatetime(),clientType);
//		List<UserSystemMessage> ls = new ArrayList<UserSystemMessage>();
//		if(total > 0) {
//			ls = userMessageService.getSysMessageList(u.getId(), u.getCreatetime(), lastId, defaultPageSize,clientType);
//		} 
//		mv.addObject("messages", ls);
//		mv.addObject("total", total);
//		boolean hasMore = (ls != null && ls.size() == defaultPageSize);
//		mv.addObject("ismore", String.valueOf(hasMore));
//		return mv; 
//	}
	
	@RequestMapping(value={"/message/mess_total","/aq/qa"},produces="text/json; charset=UTF-8")
	@ResponseBody
	public String getMessageTotal(HttpServletRequest request,HttpServletResponse response) throws CommonException{
//		ModelAndView mv = new ModelAndView();
//		mv.setViewName(prefix+"mess_total.json.ftl");
		
		JsonObject json = new JsonObject();
		
		User user = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		long app_lastId = ServletRequestUtils.getLongParameter(request, "app_last_id", 0);
		Map<String, Object> appMessageMap =  userMessageService.getAppMessageNotReadCount(user.getId(),app_lastId);
		int clientType=clientInfo.isIos()?UserSystemMessage.PUSH_CLIENT_TYPE_IOS:UserSystemMessage.PUSH_CLIENT_TYPE_ANDROID;
		if(clientType == Constants.platform_ios){
		json.addProperty("app_total", appMessageMap.get("app_total").toString());
		UserAppMessage message = (UserAppMessage) appMessageMap.get("appMessage");
		JsonObject app = new JsonObject();
			if(message != null){
				app.addProperty("id", message.getId());
				app.addProperty("app_name", ObjectUtil.getStringDefaultValue(message.getApp_name(),""));
				app.addProperty("user_id", message.getUser_id());
				app.addProperty("amount", MoneyUtils.fen2yuanS(message.getAmount()));
				app.addProperty("user_task_id", message.getUser_task_id());//_stamp
				app.addProperty("create_time", DateUtil.format(message.getCreate_time(),"yyyy-MM-dd HH:mm"));
				app.addProperty("finish_time", DateUtil.format(message.getFinish_time(),"yyyy-MM-dd HH:mm"));
				app.addProperty("timestamp", message.getCreate_time().getTime());
				app.addProperty("task_description", ObjectUtil.getStringDefaultValue(message.getTask_description(),""));
				app.addProperty("trial_time", message.getTrial_time());
				app.addProperty("keyword", ObjectUtil.getStringDefaultValue(message.getKeyword(),""));
				app.addProperty("type", message.getStatus());
				app.addProperty("app_icon", ObjectUtil.getStringDefaultValue(message.getApp_icon(),""));
				app.addProperty("agreement", ObjectUtil.getStringDefaultValue(message.getAgreement(),""));
			}
			json.add("app_message", app);
		}
		
		
		long sys_lastId = ServletRequestUtils.getLongParameter(request, "sys_last_id", 0);
		Map<String,Object> sysMessageMap = userMessageService.getSysMessageNotReadCount(user.getId(), user.getCreatetime(), sys_lastId,clientType);
		json.addProperty("sys_total", sysMessageMap.get("sys_total").toString());
		UserSystemMessage systemMessage = (UserSystemMessage) sysMessageMap.get("sysMessage");
		JsonObject sys = new JsonObject();
		if(systemMessage != null){
			sys.addProperty("id", systemMessage.getId());
			sys.addProperty("title", ObjectUtil.getStringDefaultValue(systemMessage.getTitle(),""));
			sys.addProperty("type", systemMessage.getType());
			sys.addProperty("create_time", DateUtil.format(systemMessage.getCreate_time(),"yyyy-MM-dd HH:mm"));
			sys.addProperty("timestamp", systemMessage.getCreate_time().getTime());
			sys.addProperty("thumb", ObjectUtil.getStringDefaultValue(systemMessage.getThumb(),""));
			sys.addProperty("description", ObjectUtil.getStringDefaultValue(systemMessage.getDescription(),""));
			sys.addProperty("content", ObjectUtil.getStringDefaultValue(systemMessage.getContent(),""));
			sys.addProperty("url", ObjectUtil.getStringDefaultValue(systemMessage.getTarget_url(),""));
		}
		json.add("sys_message", sys);
		
		if(clientType == Constants.platform_ios){
			long friend_lastId = ServletRequestUtils.getLongParameter(request, "friend_last_id", 0);
			Map<String,Object> friendMessageMap = userMessageService.getFriendMessageNotReadCount(user.getId(), friend_lastId);
			json.addProperty("friend_total", friendMessageMap.get("friend_total").toString());
			UserFriendMessage friendMessage = (UserFriendMessage) friendMessageMap.get("friendMessage");
			JsonObject firend = new JsonObject();
			if(friendMessage != null){
				firend.addProperty("source", friendMessage.getSource());
				firend.addProperty("id", friendMessage.getId());
				firend.addProperty("app_name", ObjectUtil.getStringDefaultValue(friendMessage.getApp_name(),""));
				firend.addProperty("friend_avater", ObjectUtil.getStringDefaultValue(friendMessage.getFriend_avater(),""));
				firend.addProperty("friend_name", ObjectUtil.getStringDefaultValue(friendMessage.getFriend_name(),""));
				firend.addProperty("create_time", DateUtil.format(friendMessage.getCreate_time(),"yyyy-MM-dd HH:mm"));
				firend.addProperty("timestamp", friendMessage.getCreate_time().getTime());
				if (friendMessage.getSource() >= 3) {
					firend.addProperty("friend_amount", friendMessage.getFriend_amount());
					firend.addProperty("amount", friendMessage.getAmount());
				}else{
					firend.addProperty("friend_amount", MoneyUtils.fen2yuanS(friendMessage.getFriend_amount()));
					firend.addProperty("amount", MoneyUtils.fen2yuanS(friendMessage.getAmount()));
				}
				firend.addProperty("level", friendMessage.getFriend_level());
			}
			json.add("friend_message", firend);
		}
		//UserArticleMessage
		long article_lastId = ServletRequestUtils.getLongParameter(request, "article_last_id", 0);
		Map<String,Object> articleMessageMap = userMessageService.getArticleMessageNotReadCount(user.getId(), article_lastId,clientType);
		json.addProperty("article_total", articleMessageMap.get("total").toString());
		UserArticleMessage articleMessage = (UserArticleMessage) articleMessageMap.get("articleMessage");
		JsonObject article = new JsonObject();
		if(articleMessage != null){
			article.addProperty("task_name", ObjectUtil.getStringDefaultValue(articleMessage.getTask_name(),""));
			article.addProperty("task_id", IdEncoder.encode(articleMessage.getTask_id()));
			article.addProperty("task_description", articleMessage.getTask_description());
			article.addProperty("create_time", DateUtil.format(articleMessage.getCreate_time(),"yyyy-MM-dd HH:mm"));
			article.addProperty("view_num", articleMessage.getView_num());
			article.addProperty("amount", MoneyUtils.fen2yuanS(articleMessage.getAmount()));
			article.addProperty("expired_time", DateUtil.format(articleMessage.getExpired_time(),"yyyy-MM-dd HH:mm"));
			article.addProperty("timestamp", articleMessage.getTimestamp());
			article.addProperty("type", articleMessage.getType());
			article.addProperty("extra_info", ObjectUtil.getStringDefaultValue(articleMessage.getExtra_info()));
		}
		json.add("article_message", article);
		/**
		 * "code":${ret_code!0},                   
	"message":"${util.jsonQuote(ret_message!"OK")}"
		 */
		json.addProperty("code", 0);
		json.addProperty("message", "OK");
		return json.toString();

	}
	
	/**
	 * 转发任务的消息列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 * 
	 */
	@RequestMapping(value = {"/bb/bb"})
	public ModelAndView articleMessageList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "article.json.ftl");
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		long lastId = ServletRequestUtils.getLongParameter(request, "last_id", 0);
		int client_type = clientInfo.isIos()?Constants.platform_ios:Constants.platform_android;
		int total = userMessageService.getArticleMessageCount(u.getId(),client_type);
		List<UserArticleMessage> ls = new ArrayList<UserArticleMessage>(); 
		if(total > 0) {
			ls = userMessageService.getArticleMessageList(u.getId(), lastId, defaultPageSize,client_type);
		}
		
		mv.addObject("idEncoder", new IdEncoder());
		mv.addObject("messages", ls);
		mv.addObject("total", total);
		boolean hasMore = (ls != null && ls.size() == defaultPageSize);
		mv.addObject("ismore", String.valueOf(hasMore));
		return mv; 
	}



	/**
	 * Safari版本的转发任务的消息列表
	 *
	 * @param request
	 * @param response
	 * @return ModelAndView
	 *
	 */
	@ResponseBody
	@RequestMapping(value = {"/message/article-msg-safari"}, produces="text/json; charset=UTF-8")
	public String safariArticleMessageList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		long lastId = ServletRequestUtils.getLongParameter(request, "last_id", 0);
		int client_type = clientInfo.isIos()?Constants.platform_ios:Constants.platform_android;
		int total = userMessageService.getArticleMessageCount(u.getId(),client_type);
		List<UserArticleMessage> ls = new ArrayList<UserArticleMessage>();
		if(total > 0) {
			ls = userMessageService.getArticleMessageList(u.getId(), lastId, defaultPageSize,client_type);
		}
		JsonObject jo_p = new JsonObject();
		jo_p.addProperty("has_more", ls.size() == defaultPageSize);
		jo_p.addProperty("total", total);
		jo_p.addProperty("code", 0);
		jo_p.addProperty("message", "OK");

		JsonArray array = new JsonArray();
		for (UserArticleMessage message : ls){
			JsonObject object = new JsonObject();
			object.addProperty("id", message.getSort_time());
			object.addProperty("time", DateUtil.getBeforeDateTimeShow(new Date(message.getTimestamp())));
			object.addProperty("task_name", message.getTask_name());
			object.addProperty("amount",  MoneyUtils.fen2yuanS(message.getAmount()));
			object.addProperty("type", message.getType());
			array.add(object);
		}
		jo_p.add("data", array);

		return jo_p.toString();
	}
}
