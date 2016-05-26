package com.cyhd.web.action.web;

import com.cyhd.common.util.AesCryptUtil;
import com.cyhd.common.util.Helper;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.*;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.IdEncoder;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.service.util.UserAgentUtil.UserAgent;
import com.cyhd.service.util.VersionUtil;
import com.cyhd.service.vo.UserArticleTaskVo;
import com.cyhd.service.vo.UserIncomeLogVo;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.ErrorCode;
import org.apache.commons.lang.StringUtils;
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
import java.util.Map;

@Controller
@RequestMapping("/web/article/")
public class ArticleTaskAction extends BaseAction {
	
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
	
	@RequestMapping("tasks.html")
	public ModelAndView tasksList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		ClientInfo clientInfo = getClientInfo(request);
		int currentVerCode = VersionUtil.getVersionCode(clientInfo.getAppVer());
		int lowestVerCode = VersionUtil.getVersionCode("1.1.0");
		boolean version = currentVerCode >= lowestVerCode;
		//没达到最低版本所以不让看
		if(version == false){
			mv.addObject("tasks", "true");
			mv.setViewName("common/nodata.html.ftl");
			mv.addObject("title", "转发任务");
			return mv;
		}
		
		User user = getUser(request);
		
		//unionid就没有必要要啦 毕竟他是唯一的之后对应一个唯一的id
		List<UserArticleTaskVo> tasks = userArticleTaskService.getTasks(user.getId(), TransArticleTask.TYPE_DEFAULT);
		
		if(tasks != null && tasks.size() > 0) {
			mv.addObject("hint_text", taskUpdateTimeHintService.getArticleTaskUpdateTimehint());
			mv.setViewName(prefix + "tasks.html.ftl");
			mv.addObject("tasks", tasks);
		} else {
			mv.addObject("tasks", "true");
			mv.setViewName("common/nodata.html.ftl");
		}
		mv.addObject("title", "转发任务");
		return mv;
	}
	
	@RequestMapping(value="/{id:\\w+}")
	public ModelAndView detail(@PathVariable("id")String encodeId,HttpServletRequest request, HttpServletResponse response) throws Exception {
		Integer id = IdEncoder.decode(encodeId);
		if(id == null){
			throw ErrorCode.getParameterErrorException("参数错误!");
		}

		ModelAndView mv = new ModelAndView();
		mv.addObject("title", "任务详情");

		//检查是否在应用中打开
		UserAgentUtil.UserAgent agent = UserAgentUtil.getUserAgent(request);
		if (agent.isInAppView()) {
			ClientInfo clientInfo = getClientInfo(request);
			int currentVerCode = VersionUtil.getVersionCode(clientInfo.getAppVer());
			int lowestVerCode = VersionUtil.getVersionCode("1.1.0");
			boolean version = currentVerCode >= lowestVerCode;

			if (version == false) {
				mv.addObject("tasks", "true");
				mv.setViewName("common/nodata.html.ftl");
				return mv;
			}
		}

		User u = getUser(request);

		//用户的转发任务VO
		UserArticleTaskVo vo = userArticleTaskService.getUserArticleTaskVo(u.getId(), id);
		mv.addObject("vo", vo);
		mv.addObject("encodeUserId", IdEncoder.encode(u.getUser_identity()));

		mv.setViewName(prefix + "detail.html.ftl");
		if (vo.getTransArticleTask().getType() == TransArticleTask.TYPE_WEIXIN){
			mv.setViewName(prefix + "weixin_detail.html.ftl");
		}

		//任务已过期就不要拼加密数据啦
		if((vo.getUserArticleTask() != null && vo.getTransArticleTask().isExpired()) == false){

			//获取分享的微信公众号信息，微信内和测试环境的分享根据域名来获取
			Account wxAccount = null;
			if (GlobalConfig.isDeploy) {
				//wxAccount = accountService.getRandomAccount();
				wxAccount = accountService.getAccountByArticleId(vo.getTransArticleTask().getArticle_id());
			} else {
				wxAccount = accountService.getAccountByHost(request.getHeader("Host"));
			}

			if (vo.getTransArticleTask().getType() == TransArticleTask.TYPE_WEIXIN){
				ArticleWeixinAccount weixinAccount = weixinArticleService.getWeixinAccount(vo.getTransArticleTask().getWx_account_id());
				mv.addObject("weixinName", weixinAccount == null? wxAccount.getName() : weixinAccount.getName());
				mv.addObject("wxAccountId", IdEncoder.encode(weixinAccount == null? 0: weixinAccount.getId()));
			}

			String shareUrl = UserArticleTaskService.getShareUrl(u, wxAccount, vo);
			mv.addObject("shareUrl",shareUrl );
			logger.info("shareUrl:{}",shareUrl);

			String shareData = UserArticleTaskService.getShareData(u, wxAccount, vo);
			logger.info("加密后的_data:{}",shareData);
			mv.addObject("_data", shareData);

			//无效字段 不用处理 做迷惑用
			mv.addObject("se_id", u.getInvite_code());
		}

		return mv;
	}


	@RequestMapping("/success/{id:\\w+}")
	public ModelAndView articleSuccess(HttpServletRequest request, @PathVariable("id")String encodeId) throws Exception{
		Integer id = IdEncoder.decode(encodeId);
		if(id == null){
			throw ErrorCode.getParameterErrorException("参数错误!");
		}

		TransArticleTask task = transArticleTaskService.getTransArticleTask(id);
		if (task == null){
			throw ErrorCode.getParameterErrorException("参数错误!");
		}

		ModelAndView mv = new ModelAndView(prefix + "success.html.ftl");
		mv.addObject("title", "分享成功");
		mv.addObject("task", task);

		return mv;
	}

	@RequestMapping(value={"article/list.html"})
	public ModelAndView getArticleMessageLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		int page = ServletRequestUtils.getIntParameter(request, "page", 1);
		int start = (page-1) * defaultPageSize;
		int size = ServletRequestUtils.getIntParameter(request, "size", defaultPageSize);
		
		int total = userArticleTaskService.getFinshTotal(u.getId());
		
		if(total > 0){
			List<UserArticleTaskVo> userArticlelist = userArticleTaskService.getUserArtricleLog(u.getId(),start,size);
			mv.addObject("userArticlelist", userArticlelist);
			mv.setViewName(prefix+"articles_log.html.ftl");
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage", totalPage);
		}else{
			mv.addObject("tasks", "true");
			mv.setViewName("common/nodata.html.ftl");
		}
		mv.addObject("title", "转发任务");
		return mv;
	}

	@RequestMapping(value={"article/revice"},method=RequestMethod.POST)
	public ModelAndView revice(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/api/v1/article/share_success.json.ftl");
		//服务端传过去的加密
		String data = ServletRequestUtils.getStringParameter(request, "d_id","");
		String _data = AesCryptUtil.decrypt(data, Constants.ARTICLE_AES_PASSWORD);
		logger.info("处理分享成功开始");
		if(StringUtils.isBlank(data)
				||StringUtils.isBlank(_data)){
			logger.error("转发任务请求似乎有点问题,加解密数据为空,QueryString:{}",request.getQueryString());
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "缺少关键参数");
			return mv;
		}
		//账号有没有异常
		if(u.isBlack()){
			logger.error("User {} masked, can't receive task !!", u.getId());
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "缺少关键参数");
			return mv;
		}
		
		/***
		 * 加密端代码原始数据
		sb.append("u_id=").append(IdEncoder.encode(u.getId())).append("&");
		sb.append("u_iden=").append(u.getUser_identity()).append("&");
		sb.append("t_id=").append(task.getEncodedId()).append("&");
		sb.append("a_id=").append(IdEncoder.encode(task.getArticle_id())).append("&");
		sb.append("w_id=").append(IdEncoder.encode(wx_id ));
		 */
		
		Map mp = Helper.getEncodedUrlParams(_data);
		logger.info(""+mp);
		Integer user_id = IdEncoder.decode((String) mp.get("u_id"));
		Integer user_identity =  Integer.parseInt((String)mp.get("u_iden"));
		Integer task_id = IdEncoder.decode((String) mp.get("t_id"));
		Integer article_id = IdEncoder.decode((String) mp.get("a_id"));
		Integer wx_account_id = IdEncoder.decode((String)mp.get("w_id"));
		if(checkInteger(user_id) == false 
				||checkInteger(user_identity) == false  
				||checkInteger(task_id) == false
				||checkInteger(article_id) == false){
			logger.error("解密出错,user_id:{},user_identity:{},task_id:{},article_id:{}",user_id,user_identity,task_id,article_id);
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "缺少关键参数");
			return mv;
		}
		
		//判断获取的和传入的是不是同一用户
		if(u.getId() != user_id || u.getUser_identity() != user_identity){
			logger.error("获取的不是同一个用户:获取的用户ID:{}|user_identity:{},传入的用户ID:{}|user_identity:{}",u.getId(),u.getUser_identity(),user_id,user_identity);
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "账号异常,禁止接任务");
			return mv;
		}
		
		TransArticleTask transArticle = transArticleTaskService.getTransArticleTask(task_id);
		//判断得到的任务中的文章id和传入的是不是匹配
		if(transArticle == null || transArticle.getArticle_id() != article_id){
			logger.error("得到的任务和所转发的文章不匹配,任务ID，文章ID:{}",task_id,article_id);
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "当前任务无效,请稍后重试");
			return mv;
		}
		
		
		UserArticleTask ut = userArticleTaskService.getArticleTask(user_id, task_id);
		//判断任务还有没有剩余
		if(transArticle.isValid() == false || (transArticle.isHasLeftTasks() == false && ut == null)){
			logger.error("任务无效！userid={}, taskid={}", task_id, u.getId());
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "当前任务无效,请稍后重试");
			return mv;
		}
		//其实分享成功在缓存中放入一个TTL等于3min的还要好些 
		UserArticleTask usertask = userArticleTaskService.getUserLastTask(user_id);
		if(usertask != null){
			Date startTime = usertask.getStarttime();
			if(startTime == null){
				startTime = usertask.getSharetime();
			}
			if(new Date().getTime() - startTime.getTime() - Constants.minutes_millis < 0){
				mv.addObject("ret_code", -1);
				mv.addObject("ret_message", "连续转发文章对阅读数不利哟~一分钟后再尝试吧");
				return mv;
			}
		}
		mv.addObject("ret_code", 0);
		return mv;
		
	}
	
	@RequestMapping(value={"article/share_success"},method=RequestMethod.POST)
	public ModelAndView shareSuccess(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/api/v1/article/share_success.json.ftl");
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		//服务端传过去的加密
		//String secret = ServletRequestUtils.getStringParameter(request, "se_id", "");
		String data = ServletRequestUtils.getStringParameter(request, "d_id", "");
		String _data = AesCryptUtil.decrypt(data, Constants.ARTICLE_AES_PASSWORD);
		logger.info("处理分享成功开始");
		if(StringUtils.isBlank(data)
				||StringUtils.isBlank(_data)){
			logger.error("转发任务请求似乎有点问题,加解密数据为空,QueryString:{}",request.getQueryString());
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "缺少关键参数");
			return mv;
		}
		//账号有没有异常
		if (u.isBlack()) {
			logger.error("User {} masked, can't receive task !!", u.getId());
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "缺少关键参数");
			return mv;
		}
		
		/***
		 * 加密端代码原始数据
		sb.append("u_id=").append(IdEncoder.encode(u.getId())).append("&");
		sb.append("u_iden=").append(u.getUser_identity()).append("&");
		sb.append("t_id=").append(task.getEncodedId()).append("&");
		sb.append("a_id=").append(IdEncoder.encode(task.getArticle_id())).append("&");
		sb.append("w_id=").append(IdEncoder.encode(wx_id ));
		 */
		
		Map mp = Helper.getEncodedUrlParams(_data);
		logger.info(""+mp);
		Integer user_id = IdEncoder.decode((String) mp.get("u_id"));
		Integer user_identity =  Integer.parseInt((String)mp.get("u_iden"));
		Integer task_id = IdEncoder.decode((String) mp.get("t_id"));
		Integer article_id = IdEncoder.decode((String) mp.get("a_id"));
		Integer wx_account_id = IdEncoder.decode((String)mp.get("w_id"));
		if(checkInteger(user_id) == false 
				||checkInteger(user_identity) == false  
				||checkInteger(task_id) == false
				||checkInteger(article_id) == false){
			logger.error("解密出错,user_id:{},user_identity:{},task_id:{},article_id:{}",user_id,user_identity,task_id,article_id);
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "缺少关键参数");
			return mv;
		}
		
		//判断获取的和传入的是不是同一用户
		if(u.getId() != user_id || u.getUser_identity() != user_identity){
			logger.error("获取的不是同一个用户:获取的用户ID:{}|user_identity:{},传入的用户ID:{}|user_identity:{}",u.getId(),u.getUser_identity(),user_id,user_identity);
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "账号异常,禁止接任务");
			return mv;
		}
		
		TransArticleTask transArticle = transArticleTaskService.getTransArticleTask(task_id);
		//判断得到的任务中的文章id和传入的是不是匹配，以及任务是否还有剩余
		if(transArticle == null || transArticle.getArticle_id() != article_id){
			logger.error("得到的任务和所转发的文章不匹配,任务ID，文章ID:{}",task_id,article_id);
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "当前任务无效,请稍后重试");
			return mv;
		}
		
		//判断是否有效 如果文章id大于10 判断是都有剩余
		if(transArticle.isValid() == false || (article_id > 10 && transArticle.isHasLeftTasks() == false)){
			logger.error("任务无效,任务ID，文章ID:{}",task_id,article_id);
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "当前任务无效,请稍后重试");
			return mv;
		}
		
		UserArticleTask ut = userArticleTaskService.getTaskByArticleId(user_id, article_id);
		//判断是否接过任务
		if( ut != null ){
			logger.error("任务无效！userid={}, taskid={}",  u.getId(),task_id);
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "您已接过该任务");
			return mv;
		}

		//入库
		logger.info("插入数据article_id:{},user_id:{},task_id:{}",article_id, user_id,task_id);
		long id = userArticleTaskService.addTask(article_id, user_id,task_id,clientInfo.getIdfa(),clientInfo.getDid(),clientInfo.isIos()?Constants.platform_ios:Constants.platform_android,wx_account_id, clientInfo.getIpAddress());
		if(id >= 0){
			logger.info("操作成功:user_id:{},task_id:{}",user_id,task_id);
			mv.addObject("ret_message", "接任务成功");
			if(id > 0){
				String host = transArticleTaskService.getRandomShareHost(transArticle.getArticle_id());
				StringBuilder shareSB = new StringBuilder(200);
				//sha{rd:\\d+}/{utid:\\w+}
				shareSB.append(host);
				shareSB.append("/sha").append((int)(Math.random()*100000)+9909);
				shareSB.append("/").append(UserArticleTaskService.encyptUserTaskId(host, id));
				mv.addObject("shareUrl",shareSB.toString());
			}
		}else{
			logger.info("修改数据库失败:user_id:{},task_id:{}",user_id,task_id);
			mv.addObject("ret_message", "小伙伴们手太快啦,导致没能抢到任务");
		}
		return mv;
	}
	
	private boolean checkInteger(Integer _data){
		if(_data == null || _data <= 0){
			return false;
		}
		return true;
	}
	
	@RequestMapping(value={"zhuangfa.html"})
	public ModelAndView newUserBindMobileAndforward(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ClientInfo clientInfo = getClientInfo(request);
		ModelAndView mv = new ModelAndView();
		if(clientInfo.isIos() == false){
			return mv;
		}
		UserAgent ua = UserAgentUtil.getUserAgent(request);
		User user =getUser(request);
		//
		if(ua.isWeixin()){
			mv.addObject("sign", AesCryptUtil.encryptWithCaseInsensitive(user.getOpenid(), Constants.ARTICLE_AES_PASSWORD));
			mv.addObject("se_id", user.getInvite_code());
			mv.addObject("isWexin", true);
			List<UserIncomeLogVo> list = userIncomeService.getUserIncomeLogs(20);
			mv.addObject("friendLogList", list);
			//return mv;
		}else if(ua.isInAppView()){
			TransArticleTask task = transArticleTaskService.getSystemTask();
			if(task != null){
				Account wxAccount = null;
				if (GlobalConfig.isDeploy) {
//					wxAccount = accountService.getRandomAccount();
					wxAccount = accountService.getAccountByArticleId(task.getArticle_id());
				} else {
					String host = "https://www.mapi.lieqicun.cn";//request.getHeader("Host");
					wxAccount = accountService.getAccountByHost(host);
				}
				UserArticleTaskVo vo =new UserArticleTaskVo();
				vo.setTransArticleTask(task);
				vo.setTransArticle(this.transArticleTaskService.getTransArticle(task.getArticle_id()));
				vo.setReceived(user.isTranArticleComplete());
//				//获取分享的微信公众号信息，微信内和测试环境的分享根据域名来获取
				String shareUrl = userArticleTaskService.getShareUrlByNewUserTask(user, wxAccount, vo);
				mv.addObject("shareUrl",shareUrl );
				String shareData = UserArticleTaskService.getShareData(user, wxAccount, vo);
				mv.addObject("_data", shareData);
				mv.addObject("logo", GlobalConfig.logo);
			}
		}
		
		mv.addObject("user", user);
		mv.setViewName(prefix+"zhuanfa.html.ftl");
		return mv;
	}
	
//	@RequestMapping("auth")
//	public ModelAndView zhuanfaAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ModelAndView mv = new ModelAndView();
//		int accountid = ServletRequestUtils.getIntParameter(request, "acc", 0);
//		//这里使用的User标识码 不是user_id
//		String taskid = ServletRequestUtils.getStringParameter(request, "taskid");
//		String userInfoId = ServletRequestUtils.getStringParameter(request, "u_id");
//		String access_code = ServletRequestUtils.getStringParameter(request, "code");
//		
//		taskid = AesCryptUtil.decrypt(taskid, Constants.ARTICLE_AES_PASSWORD);
//		userInfoId	= AesCryptUtil.decrypt(userInfoId, Constants.ARTICLE_AES_PASSWORD);
//		
//		if(StringUtils.isBlank(taskid) 
//				|| StringUtils.isBlank(userInfoId)
//				||accountid <= 0
//				||StringUtils.isBlank(access_code)){
//			return getErrorView("参数错误！");
//		}
//		
//		int uid = Integer.parseInt(userInfoId);
//		
//		if(logger.isInfoEnabled()){
//			logger.info("Weixin callback auth, params:"+request.getQueryString());
//		}
//		
//		int articleId = 0;
//		try {
//			articleId = Integer.parseInt(taskid);
//		} catch (Exception e) {
//		}
//		
//		if( articleId <= 0){
//			return getErrorView("参数错误！");
//		}
//		TransArticle article = transArticleTaskService.getTransArticle(articleId);
//		if(article == null){
//			return getErrorView("参数错误！");
//		}
//
//		Account account = accountService.getAccount(accountid);
//		if(account == null){
//			return getErrorView("参数错误！");
//		}
//
//		String url = article.getUrl();
//
//		mv.setViewName("redirect:" + url.trim());
//		if(StringUtils.isEmpty(access_code)){
//			if(logger.isWarnEnabled()){
//				logger.warn("access code is null, user not author!");
//			}
//			return getErrorView("认证失败");
//		}
//
//		String appId = account.getWxappid();
//		String appSecret = account.getWxappsecret();
//		
//		StringBuilder sb = new StringBuilder(500);
//		sb.append("https://api.weixin.qq.com/sns/oauth2/access_token");
//		sb.append("?appid=").append(appId);
//		sb.append("&secret=").append(appSecret);
//		sb.append("&code=").append(access_code);
//		sb.append("&grant_type=").append("authorization_code");
//		String accessUrl =sb.toString();
//		//清空 下一个用
//		sb.delete(0, sb.length());
//		String content = HttpUtil.get(accessUrl, null, "utf-8");
//		if(logger.isInfoEnabled()){
//			logger.info("CallbackController.auth get access token from wx, url={}, returns: {} ", accessUrl, content);
//		}
//		try{
//			JSONObject retJson =JSONObject.fromObject(content);
//			if(retJson.has("errcode")){
//				if(logger.isErrorEnabled()){
//					logger.error("appId={}, response error:{}",appId, content);
//				}
//				return mv;
//			}
///***
// {
// "access_token":"ACCESS_TOKEN",
// "expires_in":7200,
// "refresh_token":"REFRESH_TOKEN",
// "openid":"OPENID",
// "scope":"SCOPE",
// "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
// }
// */
//			String openId = retJson.getString("openid");
//			String accessToken = retJson.getString("access_token");
//			String refreshToken = retJson.getString("refresh_token");
//
//			String unionid=retJson.getString("unionid");
//			//needUserInfo,taskid
//			Integer article_task_id = IdEncoder.decode(taskid);
//			if(StringUtils.isEmpty(unionid)){
//				logger.error("appId={}, get unionid error!",appId);
//				return mv;
//			}
//			//获取用户信息
//			//?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
//			sb.append("https://api.weixin.qq.com/sns/userinfo");
//			sb.append("access_token=").append(accessToken);
//			sb.append("&openid=").append(openId);
//			sb.append("&lang=").append("zh_CN");
//			
//			
//			content = HttpUtil.get(accessUrl, null, "utf-8");
//			retJson =JSONObject.fromObject(content);
//			if(retJson.has("errcode")){
//				if(logger.isErrorEnabled()){
//					logger.error("appId={}, response error:{}",appId, content);
//				}
//				//怎么办 不让访问
//				return getErrorView("认证失败");
//			}
//			
//			User u = userService.getUserByUnionId(unionid);
//			User invitor= null;
//			
//			if(u == null){
//				openId = retJson.getString("openid");
//				String avatar = retJson.getString("headimgurl");
//				String name = retJson.getString("nickname");
//				int sex = 1;
//				if(retJson.containsKey("sex"))
//					sex = retJson.getInt("sex");
//				
//				String country = "CN";
//				String province = "";
//				String city = "";
//				 unionid = "";
//				if(retJson.containsKey("country"))
//					country = retJson.getString("country");
//				if(retJson.containsKey("province"))
//					province = retJson.getString("province");
//				
//				if(retJson.containsKey("city"))
//					city = retJson.getString("city");
//				
//				if(retJson.containsKey("unionid"))
//					unionid = retJson.getString("unionid");
//				
//				//创建一个新的用户
//				u = new User();
//				u.setOpenid(openId);
//				u.setTicket(userService.generateTicket(openId));
//				u.setSex(sex);
//				u.setUnionid(unionid);
//				u.setInvite_code(MD5Util.getMD5(unionid));
//				u.setCountry(country);
//				u.setCity(city);
//				u.setProvince(province);
//				u.setAvatar(avatar);
//				u.setName(name);
//				
//				if(userService.insertOrUpdate(u)){
//					//增加徒弟
//					u = userService.getUserByUnionId(unionid);
//					invitor = userService.getUserByIdentifyId(uid);
//					userFriendService.onAddUserFriend(invitor, u, null);
//				}
//				
//			}else{
//				CookieUtil.setNewCookie(USER_TICKET, IdEncoder.encode(u.getId()), response);
//				int invite = userFriendService.getInvitor(u.getId());
//				invitor = userService.getUserById(invite);
//			}
//			
//			if(invitor != null){
//				List<UserIncomeLogVo> logs =  userIncomeService.getUserFriendInviteIncomeDetail(u.getId(), invitor.getId(), 0, 1);
//				if(logs!= null && logs.isEmpty() == false){
//					mv.addObject("friendLog", logs.get(0));
//				}
//			}
//			CookieUtil.setNewCookie(AUTH_CODE, u.getInvite_code(), response);
//			
//		}catch(Exception e){
//			if(logger.isErrorEnabled())
//				logger.error("parse callback content error", e);
//			return getErrorView("认证失败");
//		}
//		if(logger.isInfoEnabled())
//			logger.info("redirect to:" + url);
//		return mv;
//	}
//	
//	public ModelAndView newUserBindMobileAndforwardCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		
//		//invite wxId
//		ModelAndView mv = new ModelAndView();
//		String wxIdStr = ServletRequestUtils.getStringParameter(request,"wxId","");
//		String inviteStr = ServletRequestUtils.getStringParameter(request,"invite","");
//		
//		Integer wxid = IdEncoder.decode(wxIdStr);
//		inviteStr = AesCryptUtil.decrypt(inviteStr, Constants.ARTICLE_AES_PASSWORD);
//		
//		if(wxid == null||StringUtils.isBlank(inviteStr)){
//			return getErrorView("来源不对");
//		}
//		Account wxAccount = accountService.getAccount(wxid);
//		
//		mv.addObject("isWeiXin", true);
//		StringBuilder sb = new StringBuilder(300);
//		String host = wxAccount.getHost();
//		sb.append(host);
//		if(!host.endsWith("/")){
//			sb.append("/");
//		}
//		sb.append("web/article/auth?acc=").append(wxIdStr);
//		sb.append("&u_id=").append(inviteStr);
//		sb.append("&taskid=").append(AesCryptUtil.encrypt("1", Constants.ARTICLE_AES_PASSWORD));
//		String url = sb.toString();
//		//清空sb
//		sb.delete(0, sb.length());
//		
//		String redirectUrl = URLEncoder.encode(url, "utf-8");
//		String appId = wxAccount.getWxappid();
//		String appSecret = wxAccount.getWxappsecret();
//		String scope = "snsapi_userinfo";
//		
//		sb.append("https://open.weixin.qq.com/connect/oauth2/authorize");
//		sb.append("?appid=").append(appId);
//		sb.append("&redirect_uri=").append(redirectUrl);
//		sb.append("&response_type=").append("code");
//		sb.append("&scope=").append(scope);
//		sb.append("&state=").append(wxAccount.getId());
//		sb.append("#wechat_redirect");
//		
//		String jumpUrls = sb.toString();
//		if(logger.isInfoEnabled()){
//			logger.info("jump to weixin auth url: "  + jumpUrls);
//		}
//		mv.setViewName("redirect:" +jumpUrls);
//		
//		return new ModelAndView("forward:/web/article/zhuangfa.html");
//	}
}
