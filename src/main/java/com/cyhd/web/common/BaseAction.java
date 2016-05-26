package com.cyhd.web.common;

import java.io.File;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.cyhd.common.util.Helper;
import com.cyhd.service.util.RedirectCodeEncoder;
import com.cyhd.service.util.UserAgentUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.DateUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserService;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;

public abstract class BaseAction {

	@Resource
	protected UserService userService;
	
	public static final int defaultPageSize = 10;
	protected static Logger logger = LoggerFactory.getLogger("apirequest");

	protected void fillStatus(ModelAndView mv){
		mv.addObject("ret_code", 0);
		mv.addObject("ret_message", "ok");
	}
	
	protected void fillErrorStatus(ModelAndView mv, int code){
		mv.addObject("ret_code", code);
		mv.addObject("ret_message", ErrorCode.getErrorMsg(code));
	}
	
	protected void fillErrorStatus(ModelAndView mv, int code, String message){
		mv.addObject("ret_code", code);
		mv.addObject("ret_message", message);
	}
	
	public ModelAndView getErrorView(String message){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("error.ftl");
		mv.addObject("errormsg", message);
		return mv;
	}
	public ModelAndView getErrorView(int code, String message){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("error.ftl");
		mv.addObject("errormsg", message);
		return mv;
	}
	
	protected ClientInfo getClientInfo(HttpServletRequest request) throws CommonException {
		ClientInfo clientInfo = (ClientInfo) request.getAttribute("clientInfo");
		if (clientInfo == null) {
			logger.error("missing required parameter clientInfo!");
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTINFO);
		}
		return clientInfo;
	}
	
	protected ClientAuth getClientAuth(HttpServletRequest request) throws CommonException {
		ClientAuth auth = (ClientAuth) request.getAttribute("clientAuth");
		if (auth == null) {
			logger.error("missing required parameter auth!");
			throw new CommonException(ErrorCode.ERROR_CODE_CLIENTAUTH);
		}
		return auth ;
	}
	
	protected User getUser(HttpServletRequest request) throws CommonException {
		User user = (User) request.getAttribute("userInfo");
		if (user != null) {
			return user;
		}else {
			logger.error("can not get user by this ticket");
			throw new CommonException(ErrorCode.ERROR_CODE_USER_NOT_LOGIN);
		}
	}
	
	protected boolean hasUser(HttpServletRequest request) throws CommonException {
		User user = (User) request.getAttribute("userInfo");
		if (user != null) {
			return true;
		}else {
			return false ;
		}
	}
	
	public static boolean creatDir(String aDir) {
		File aFile = new File(aDir);
		if (!aFile.exists()) {
			return aFile.mkdirs();
		}
		return true;
	}
	
	public int getPlatform(HttpServletRequest request) throws CommonException {
		
		ClientInfo clientInfo = getClientInfo(request) ;
		if(StringUtils.isEmpty(clientInfo.getClientType())) {
			return Constants.platform_unkonwn ;
		}
		if(clientInfo.getClientType().equalsIgnoreCase("android")) {
			return Constants.platform_android ;
		} else if(clientInfo.getClientType().equalsIgnoreCase("ios")) {
			return Constants.platform_ios ;
		} else {
			return Constants.platform_unkonwn ;
		}
	}
	
	public String getClientTypeName(HttpServletRequest request) throws CommonException {
		
		ClientInfo info = this.getClientInfo(request);
		if(info == null) {
			return "unknown" ;
		}
		String clientType = "" ;
		if(info.getClientType().equals("ios")) {
			clientType = info.getOs() ;
		} else {
			clientType = info.getClientType() + info.getOs() + "(" + info.getModel() + ")" ;
		}
		if(clientType.length() > 180) {
			clientType = clientType.substring(0, 180) ;
		}
		return clientType ;
	}
	
	public String getAppver(HttpServletRequest request) throws CommonException {
		
		ClientInfo info = this.getClientInfo(request);
		if(info == null) {
			return "unknown" ;
		}
		return info.getAppVer() ;
	}
	
	/**
	 * 获得方法的唯一性调用，用于记录日志
	 * @return
	 */
	public String getUniqueCall() {
		return DateUtil.format(new Date(), "HHmmssS ") ;
	}


	/**
	 * json返回，默认code=0， message=OK
	 * @param results
	 * @return
	 */
	public String toJSONResult(Map<String, Object> results){
		return toJSONResult(0, "OK", results);
	}

	/**
	 * json返回
	 * @param code
	 * @param results
	 * @return
	 */
	public String toJSONResult(int code, Map<String, Object> results){
		return toJSONResult(code, ErrorCode.getErrorMsg(code), results);
	}

	/**
	 * 根据code和msg返回json对象
	 * @param code
	 * @param msg
	 * @return
	 */
	public String toJSONResult(int code, String msg){
		return toJSONResult(code, msg, null);
	}

	/**
	 * json数据输出
	 * @param code
	 * @param message
	 * @param results
	 * @return
	 */
	public String toJSONResult(int code, String message, Map<String, Object> results){
		JsonObject jsonObject = null;

		//如果results不为空，则先转换成JsonObject
		if (results != null && results.size() > 0) {
			Gson gson = new Gson();
			JsonElement jsonElement = gson.toJsonTree(results);
			jsonObject = (JsonObject) jsonElement;
		}

		//为空的话，再创建json对象
		if (jsonObject == null){
			jsonObject = new JsonObject();
		}
		jsonObject.addProperty("code", code);
		jsonObject.addProperty("message", (message == null ? ErrorCode.getErrorMsg(code) : message));

		return jsonObject.toString();
	}

	/**
	 * 特殊的重定向操作，支持安卓重定向<br/>
	 * 因为安卓app里是用okhttp发起get请求，如果遇到重定向，不会再次根据url重新生成clientAuth等信息，这里根据当前的clientAuth信息，生成一个特殊的编码
	 * @param request
	 * @param url
	 * @return
	 */
	public ModelAndView redirectForApp(HttpServletRequest request, String url){
		UserAgentUtil.UserAgent userAgent = UserAgentUtil.getUserAgent(request);
		if (userAgent.isInAppView()) {
			String rnd = "";
			try {
				ClientAuth clientAuth = getClientAuth(request);
				int authValue = clientAuth.getSignValue();
				if (authValue > 0){
					rnd = RedirectCodeEncoder.encode(authValue);
				}
			} catch (CommonException e) {

			}

			return new ModelAndView("redirect:" + Helper.appendUrlParam(url, "_rnd_", rnd));
		}else {
			return new ModelAndView("redirect:" + url);
		}
	}
}
