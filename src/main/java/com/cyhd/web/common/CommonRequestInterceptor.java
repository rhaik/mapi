package com.cyhd.web.common;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.MagicKey;
import com.cyhd.common.util.NumberUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.util.UserAgentUtil.UserAgent;
import com.cyhd.web.common.util.ApiUtil;
import com.cyhd.web.common.util.ClientAuthUtil;
import com.cyhd.web.common.util.ClientInfoUtil;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;

/**
 * 公共的请求拦截器
 * 
 */
public class CommonRequestInterceptor extends HandlerInterceptorAdapter {

	private static String callBackSuffix = ".3w";

	private static final Logger REQUESTLOG = LoggerFactory.getLogger("apirequest");
	
	private static final String AUTH_CODE = "AUTH_CODE";
	private static final String USER_TICKET = "USER_TICKET";

	//safari中用户信息的cookie
	private static final String UCD_COOKIE = "UCD";

	//微信跳转到safari得入口页面
	private static final String SAFARI_INDEX_URI = "/ios/enter.html";
	
	@Resource
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		String url = request.getRequestURI();
		if (handler == null){
			return true;
		}

		//是否在debug
		boolean isDebug = false;
		if (GlobalConfig.isDeploy) {
			isDebug = "mm_debug".equals(request.getParameter("debug"));
		}else{
			isDebug = "money".equals(request.getParameter("debug"));
		}
		
		//先判断request中是否有用户，有则说明是forward请求，无需再次验证
		if (request.getAttribute("userInfo") != null) {
			return true;
		}


		String ip = RequestUtil.getIpAddr(request);
		String host = request.getHeader("host");
		if(!host.equalsIgnoreCase("api.miaozhuandaqian.com")){
			if (REQUESTLOG.isWarnEnabled())
				REQUESTLOG.warn("HOST ERROR! http Request:{}, params={},ci={}, ca={},ip={} host={}", 
						url, RequestUtil.getRequestParams(request),request.getHeader("clientInfo"), request.getHeader("clientAuth"),ip, host);
		}
		
		//debug模式
		if((url.contains("/web/") || url.contains("/weixin/") || url.contains("/doubao/") || ip.equalsIgnoreCase("127.0.0.1")) && isDebug){
			if (REQUESTLOG.isInfoEnabled())
				REQUESTLOG.info("Api Request:{}, params={}, debug mode", url, RequestUtil.getRequestParams(request));
			ClientInfo info = ClientInfoUtil.getDefaultClientInfo();
			if (info != null)
				request.setAttribute("clientInfo", info);
			String userId = request.getParameter("uid");
			int uid = 0;
			if(userId != null && userId.length() > 0){
				uid = Integer.parseInt(userId);
			}
			if(uid == 0)
				uid = 1;
			User u = userService.getUserById(uid);
			request.setAttribute("userInfo", u);
			return true;
		}

		//不做校验的请求，都以open开头，目前用在微信文章部分
		if (url.startsWith("/open/")){
			REQUESTLOG.info("new open request:{}", RequestUtil.getRequestParams(request));
			return true;
		}

		//微信公众号来源的拦截
		if (url.startsWith("/weixin/")) {
			boolean flag =  validateWechatRequest(request, response);
			if(!flag){
				try{
					response.sendRedirect("/www/downloads/share/wxgongzhonghao");
				}catch(Exception e){
					REQUESTLOG.error("", e.getMessage());
				}
			}
			return flag;
		}

		//Safari网页版来源的拦截
		if (url.startsWith("/ios/")){
			boolean isValid = validateSafariRequest(request, response);
			if (!isValid){
				UserAgent userAgent = UserAgentUtil.getUserAgent(request);
				if (userAgent.isInAppView()) {
					isValid = validateAppRequest(request, response);
				}
			}

			if (!isValid){
				//判断是不是分享链接的入口，是的话去往分享页
				if (url.startsWith("/ios/index.html") && StringUtil.isNotBlank(request.getParameter("u"))){
					request.setAttribute("fromYaoshiHome", true);
					request.getRequestDispatcher("/www/downloads/share/" + request.getParameter("u")).forward(request,response);
				}else {
					try {
						response.sendRedirect("/www/downloads/safari");
					} catch (Exception e) {
						REQUESTLOG.error("", e.getMessage());
					}
				}
			}
			return isValid;
		}


		//如果是一元夺宝的请求，则先使用Safari网页版的形式进行验证，通不过再用App验证方式验证一次
		if (url.startsWith("/doubao")){
			boolean isValid = validateSafariRequest(request, response);
			if (!isValid){
				UserAgent userAgent = UserAgentUtil.getUserAgent(request);
				if (userAgent.isInAppView()) {
					isValid = validateAppRequest(request, response);
				}
			}
			request.setAttribute("hideRefresh", true);

			return isValid;
		}
	
		// 如果不是支付宝、微信支付的回调，则进行验证
		if (!url.endsWith(callBackSuffix) && !url.endsWith("/v1/upload/image")) {
			if (isDebug) {
				if (REQUESTLOG.isWarnEnabled())
					REQUESTLOG.warn("Api Request:" + url + ", params=" + RequestUtil.getRequestParams(request) + "，debug mode！");
				String userId = request.getParameter("uid");
				int uid = NumberUtil.safeParseInt(userId);
				uid = uid == 0 ? 1 : uid;
				User u = userService.getUserById(uid);
				request.setAttribute("userInfo", u);
			}else {
				validateAppRequest(request, response);
			}
		} else {
			if (REQUESTLOG.isInfoEnabled()) {
				REQUESTLOG.info("Api Request:{}, params={}", url, RequestUtil.getRequestParams(request));
			}
		}

		//判断用户是否被封禁，封禁后禁止登录
		User user = (User)request.getAttribute("userInfo");
		if (user != null && user.isBlack()){
			REQUESTLOG.warn("user is black:{}", user);
			throw new CommonException(ErrorCode.ERROR_CODE_USER_NOT_LOGIN);
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		String url = request.getRequestURI();
		
		if (handler == null)
			return;
		if (modelAndView == null) {
			return;
		}
		
		if (url.startsWith("/weixin/")) {
			//清除AUTH_CODE cookie
			CookieUtil.setNewCookie(AUTH_CODE, null, 0, RequestUtil.getAuthCodeDomain(request), response);
			CookieUtil.deleteCookie(AUTH_CODE, request, response);

			//设置或者清除ticket
			User user = (User)request.getAttribute("userInfo");
			if(user != null){
				CookieUtil.setHttpOnlyCookie(USER_TICKET, LoginIDEncoder.encode(user.getId()), GlobalConfig.isDeploy, response);
			}else{
				CookieUtil.deleteCookie(USER_TICKET, request, response);
			}


			try{
				Map<String, String> shareMap = weixinShareService.sign(request) ;
				modelAndView.addObject("sharemap", shareMap) ;

				REQUESTLOG.debug("weixin sign: url={}, sharemap={}", url, shareMap);
			}catch(Exception e){
				REQUESTLOG.error("sharemap error", e);
			}
		}else if (url.startsWith(SAFARI_INDEX_URI)){ //safari版本请求地址
			UserAgent userAgent = UserAgentUtil.getUserAgent(request);
			if (userAgent.isWeixin() || userAgent.isSafari()) {
				//设置或者清除ticket
				User user = (User) request.getAttribute("userInfo");
				if (user != null) {
					//半年有效期
					CookieUtil.setNewCookie(UCD_COOKIE, IdEncoder.encode(user.getId()), 120 * 24 * 3600, null, response);
				} else {
					CookieUtil.deleteCookie(UCD_COOKIE, request, response);
				}
			}else {
				CookieUtil.deleteCookie(UCD_COOKIE, request, response);
			}
		}

		/**
		 * 在测试环境中， 输出调用结果的数据，做调试用
		 */
		if (!GlobalConfig.isDeploy) {
			REQUESTLOG.info("Api Request:" + url + ", params=" + RequestUtil.getRequestParams(request) + "; resData:" + modelAndView.getModel());
		}
	}

	/**
	 * 验证来自应用请求
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean validateAppRequest(HttpServletRequest request,
									   HttpServletResponse response) throws  Exception{
		String host = request.getHeader("host");
		String url = request.getRequestURI();

		try {
			ClientInfo info = ClientInfoUtil.getClientInfo(request);
			if (info != null) {
				request.setAttribute("clientInfo", info);
			}

			ClientAuth auth = ClientAuthUtil.getClientAuth(request);
			if (REQUESTLOG.isInfoEnabled())
				REQUESTLOG.info("Api Request:{}, params={}, maps={}, clientInfo={}, clientAuth={}, host={}", url, RequestUtil.getRequestParams(request),request.getParameterMap(), info, auth,host);

			if (auth != null) {
				// 测试环境
				if (!GlobalConfig.isDeploy) {
//							if (REQUESTLOG.isInfoEnabled()) {
//								REQUESTLOG.info(url + " start check client auth=" + auth + ", clientinfo=" + info);
//							}
					ClientAuthUtil.checkAuth(request, auth, info);
//							if (REQUESTLOG.isInfoEnabled()) {
//								REQUESTLOG.info(url + " end check client auth=" + auth + ", clientinfo=" + info);
//							}
				} else {
					ClientAuthUtil.checkAuth(request, auth, info);
					int devicetype = VersionUtil.getDeviceType(info.getClientType());
					if(devicetype == Constants.platform_android){
//								if (REQUESTLOG.isInfoEnabled()) {
//									REQUESTLOG.info(url + " android request: host={}", host);
//								}
						if(!host.equalsIgnoreCase("api.miaozhuandaqian.com")){
							if (REQUESTLOG.isErrorEnabled()) {
								REQUESTLOG.error(url + " android request: host={}", host);
							}
							throw new CommonException(ErrorCode.ERROR_CODE_HOST);
						}
					}

				}
				String ticket = auth.getTicket();
				if (!StringUtils.isEmpty(ticket)) {
					User user = userService.getUserByTicket(ticket);
					if (user != null) {
						request.setAttribute("userInfo", user);
						if(GlobalConfig.isDeploy){
							if(!info.getDid().equals(user.getDid())){
								REQUESTLOG.error(url + " user did not equals client info did!");
								throw new CommonException(ErrorCode.ERROR_CODE_CLIENTAUTH);
							}

							if (info.isIos()){
								//检测用户的IDFA是否匹配
								if (StringUtil.isNotBlank(info.getIdfa()) &&  StringUtil.isNotBlank(user.getIdfa()) && !info.getIdfa().equals(user.getIdfa())){
									REQUESTLOG.error(url + " user IDFA not equals client info IDFA!");
									throw new CommonException(ErrorCode.ERROR_CODE_CLIENTAUTH);
								}
							}
						}

						//用户被封禁
						if (user.isBlack()) {
							throw new CommonException(ErrorCode.ERROR_CODE_USER_NOT_LOGIN);
						}
					}else {
						REQUESTLOG.error("Get user by ticket return null, ticket=" + ticket);
					}
				}
				request.setAttribute("clientAuth", auth);
				return true;
			}
		} catch (Exception e) {
			REQUESTLOG.error("intercepter error", e);
			String s = ApiUtil.getErrorResponse(request, e);
			response.setContentType("text/json; charset=utf-8");
			response.getWriter().write(s);
			response.getWriter().flush();
		}
		return false;
	}

	@Resource
	private WeixinShareService weixinShareService;
	/**
	 * 验证微信的请求
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean validateWechatRequest(HttpServletRequest request,
			HttpServletResponse response) {
		UserAgent agent = UserAgentUtil.getUserAgent(request);
		String ip = RequestUtil.getIpAddr(request);
//		
		if(agent == null ){
			REQUESTLOG.warn("agent is null, ip={}", ip);
			return false;
		}
		
		if(!(agent.isAndroid() || agent.isIPhone() || agent.isIPad())){
			REQUESTLOG.info("agent is not phone, ip={}", ip);
			return false;
		}
		
		
		if ( !agent.isWeixin()) {
			try {
				response.sendRedirect("/www/downloads/app/wxgongzhonghao");
			} catch (IOException e) {
				//e.printStackTrace();
			}
			//REQUESTLOG.info("comeform is not weixin with phone, ip={}", ip);
			return false;
		}

		String inviteCode = CookieUtil.getCookieValue(AUTH_CODE, request);
		String ticket = CookieUtil.getCookieValue(USER_TICKET, request);
		if(inviteCode == null){
			inviteCode = request.getParameter("code");
		}
		User user = null;
		
		//用户未登录，优先用cookie值进行登录，否则用cookie中ticket进行登陆
		if (StringUtils.isNotBlank(inviteCode)) {
			//REQUESTLOG.warn("new user, auth code：{}",inviteCode);
			user = userService.getUserByInviteCode(inviteCode);
		}else if(StringUtils.isNotBlank(ticket)){ //old user
			//REQUESTLOG.warn("old user, ticket={}", ticket);
			Integer uid = LoginIDEncoder.decode(ticket);
			if(uid != null && uid.intValue() > 0){
				user = userService.getUserById(uid.intValue());
			}
		}
		
		if (user != null || "/weixin/user/aboutus.html".equals(request.getRequestURI())) {

			int newDeviceType = agent.isAndroid() ? Constants.platform_android : Constants.platform_ios;
			if(user != null && user.getDevicetype() != newDeviceType){
				//REQUESTLOG.warn("device type not equal, old={}, new={}", oldDeviceType, newDeviceType);
				userService.updateDeviceType(user.getId(), newDeviceType);
			}else{
				//REQUESTLOG.info("device type equal, old=new={}", oldDeviceType);
			}
			
			request.setAttribute("userInfo", user);

			//设置默认的clientInfo和clientAuth
			request.setAttribute("clientInfo",  ClientInfoUtil.getDefaultClientInfo());
			request.setAttribute("clientAuth", ClientAuthUtil.getDefaultClientAuth());

			//REQUESTLOG.warn("wechat auth success, user={}", user.getId());
		}else{
			REQUESTLOG.warn("wechat auth failed, no login user");
		}

		request.setAttribute("fromWeixin", true);
		//检查用户是否被封禁
		return (user != null && !user.isBlack());
	}


	/**
	 * 验证Safari网页版的请求
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean validateSafariRequest(HttpServletRequest request, HttpServletResponse response) {
		String ip = RequestUtil.getIpAddr(request);
		UserAgent agent = UserAgentUtil.getUserAgent(request);

		if(agent == null ){
			REQUESTLOG.warn("agent is null, ip={}", ip);
			return false;
		}

		if(!(agent.isIPhone() || agent.isIPad())){
			REQUESTLOG.info("agent is not phone, ip={}", ip);
			return false;
		}


		User user = null;

		//入口地址，检查请求参数中的code值
		if (SAFARI_INDEX_URI.equals(request.getRequestURI())){

			//入口地址，运行safari和微信访问
			if (!(agent.isSafari() || agent.isWeixin()) ){
				REQUESTLOG.info("agent is not safari or weixin, ip={}", ip);
				return false;
			}

			String ticket = request.getParameter("tk");
			String code = request.getParameter("code");

			//从钥匙过来，则带上tk参数
			if (StringUtil.isNotBlank(ticket)){
				user = userService.getUserByTicket(ticket);
			}else if (StringUtil.isNotBlank(code)){ //如果用tk没有取到用户，检查是否带有code参数
				Integer uidInteger = LoginIDEncoder.decode(code);
				if (uidInteger != null && uidInteger > 0) {
					user = userService.getUserById(uidInteger);
				}
			}

			REQUESTLOG.info("safari user login: tk={}, code={}, user={}, ip={}", ticket, code, user, ip);
		}else { //其他页面，检查cookie

			//只允许safari访问
			if (!(agent.isSafari() || agent.isWeixin())){
				REQUESTLOG.info("agent is not safari, ip={}", ip);
				return false;
			}

			String code = CookieUtil.getCookieValue(UCD_COOKIE, request);

			//校验cookie，不检查id的有效期
			Integer uid = IdEncoder.decode(code);
			if (uid != null && uid > 0){
				user = userService.getUserById(uid);
			}
		}

		if (user != null){
			request.setAttribute("userInfo", user);

			//设置默认的clientInfo和clientAuth
			request.setAttribute("clientInfo",  ClientInfoUtil.getSafariClientInfo(request));
			request.setAttribute("clientAuth", ClientAuthUtil.getDefaultClientAuth());
		}

		request.setAttribute("fromSafari", true);
		request.setAttribute("fromWeixin", agent.isWeixin());

		request.setAttribute("yaoshiScheme", GlobalConfig.yaoshi_scheme);
		request.setAttribute("websocketAddress", GlobalConfig.websocket_address);

		//检查用户是否被封禁
		return (user != null && !user.isBlack());
	}
}
