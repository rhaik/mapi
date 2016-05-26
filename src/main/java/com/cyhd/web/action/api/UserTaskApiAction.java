package com.cyhd.web.action.api;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.*;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.IdEncoder;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.service.util.VersionUtil;
import com.cyhd.service.vo.UserTaskVo;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1")
public class UserTaskApiAction extends BaseAction {

	@Resource
	UserTaskService userTaskService;
	
	@Resource
	private AppTaskService appTaskService;
	 
	
	@Resource
	UserMessageService userMessageService;

	@Resource
	private ChannelService channelService;
	
	private static final String prefix = "/api/v1/task/";

	@Resource
	private UserInstalledAppService userInstalledAppService;


	@Resource
	private AppVendorService appVendorService;

	@Resource
	private AntiCheatService antiCheatService;

	@Resource
	private AppChannelQuickTaskService appChannelQuickTaskService;
	
	//允许做限时任务的最小版本
	private static final int MIN_TASK_VERSION = VersionUtil.getVersionCode("1.5.0");


	/**
	 * 用户接受某个任务：
	 * 1、检查任务有效
	 * 2、检查任务有空余名额
	 * 3、检查用户是否已经接收过
	 * 4、添加用户任务
	 * 5、更改任务属性（剩余名额，已接单人数等）
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"/ah/ha"}, method = RequestMethod.POST)
	public ModelAndView receive(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ClientInfo clientInfo = getClientInfo(request);
		User u = getUser(request);
		if(StringUtils.isBlank(clientInfo.getIdfa())){
			logger.error("User receive task parameter error,idfa is blank;did:{},user_id:{}",clientInfo.getIdfa(),u.getId());
			throw new CommonException(ErrorCode.ERROR_CODE_USER_MASKED, "请在手机“设置”->“隐私”->“广告”中关闭“限制广告跟踪”！");
		}
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "receive.json.ftl");
		
		String v = clientInfo.getAppVer();

		if(v == null || v.startsWith("0.")){
			logger.error("需要更新！！userid={}, version={}", u.getId(), v);
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "当前版本低，请在“我”->“设置”->“检测新版本”，更新最新版本！");
		}

		if( (v.startsWith("0.") || v.startsWith("1.0.")) && clientInfo.isIos9()){
			logger.error("User receive task parameter error,  user is ios9 {}", clientInfo.getOs());
			throw new CommonException(ErrorCode.ERROR_CODE_USER_MASKED, "该任务暂时不支持ios9，请等待版本更新！");
		}
		if(GlobalConfig.isDeploy){
			//版本太低，不允许做限时任务
			if (VersionUtil.getVersionCode(clientInfo.getAppVer()) < MIN_TASK_VERSION){
				logger.error("User app version is low", clientInfo);
				throw new CommonException(ErrorCode.ERROR_CODE_USER_MASKED, "当前版本低，请先更新钥匙版本");
			}

			// 限制接任务的ip频率，不能连续接
			if(antiCheatService.isContinuousTaskIp(clientInfo.getIpAddress())){
				logger.error("接任务时连续操作的ip:ip:{},user:{}",clientInfo.getIpAddress(),u);
				throw new CommonException(ErrorCode.ERROR_CODE_UNKNOWN, "您的操作太频繁，请稍后重试！");
			}

			if (antiCheatService.isFrequentTaskIp(clientInfo.getIpAddress())){
				logger.error("接任务时操作太频繁的ip:ip:{},user:{}",clientInfo.getIpAddress(),u);
				throw new CommonException(ErrorCode.ERROR_CODE_UNKNOWN, "您的操作太频繁，请稍后重试！");
			}
	
			// 限制每个ip每天接任务的数量
			if (antiCheatService.isTooManyTaskFromIp(clientInfo.getIpAddress())){
				logger.error("接任务时ip当天接过的任务过多:ip:{},user:{}",clientInfo.getIpAddress(),u);
				throw new CommonException(ErrorCode.ERROR_CODE_UNKNOWN, "你的操作太频繁，请稍后重试");
			}
		}
		String taskEncodeId = ServletRequestUtils.getStringParameter(request, "task");
		if(StringUtils.isEmpty(taskEncodeId))  {
			logger.error("User receive task parameter error, taskid error!!");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER);
		}
		int taskId = IdEncoder.decode(taskEncodeId);
		if(taskId <= 0){
			logger.error("User receive task parameter error, taskid error!!");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER);
		}
		
		if(u.isBlack()){
			logger.error("User {} masked, can't receive task !!", u.getId());
			throw new CommonException(ErrorCode.ERROR_CODE_USER_MASKED, "账号异常，禁止接任务！");
		}
		
		AppTask appTask = appTaskService.getAppTask(taskId);
		if(appTask == null){
			logger.error("User receive task parameter error, task error!!");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER);
		}
		
		if(!appTask.isValid() || !appTask.isHasLeftTasks()){
			logger.error("任务无效！userid={}, taskid={}", u.getId(),taskId);
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "当前任务无效，请稍后重试！");
		}
		
		//是正式环境才去判断 否则不
		if(GlobalConfig.isDeploy){
			//是否有进行中的任务
			List<UserTask> doingTasks = userTaskService.getUserDoingTasks(u.getId());
			if (doingTasks != null && doingTasks.size() > 0){
				logger.error("User receive task parameter error,  use has doing tasks:{}", doingTasks.size());
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您有未完成的限时任务，完成后才能接新任务");
				return mv;
			}
		}
		
		//先获取历史任务信息，有可能是放弃后再次接任务
		UserTask ut = userTaskService.getUserTaskByAppId(u.getId(), appTask.getApp_id());
		if(ut != null){
			if (!ut.isExpired() && !ut.isAborted()){ //任务进行中，并且不是已放弃的任务
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您已接过该任务！");
				return mv;
			}

			//如果已经激活过，也不让接
			if (ut.getActive() == 1){
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您已接过该任务！");
				return mv;
			}

			if (!ut.getIdfa().equals(clientInfo.getIdfa())){ //放弃任务时的idfa和当前不一致，不让重新接任务
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您已接过该任务！");
				return mv;
			}
		}

		UserTask ut2 = userTaskService.getUserTaskByDid(appTask.getApp_id(), u.getDid());
		//如果任务存在，并且是进行中或者已完成的状态
		if(ut2 != null){
			if (!ut2.isExpired() && !ut2.isAborted()){
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您已接过该任务！");
				return mv;
			}
			if (ut != null && ut.getId() != ut2.getId()){ //ut和ut2不同，不让接任务
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您已接过该任务！");
				return mv;
			}
		}

		if(userTaskService.isRevicedByIdfa(clientInfo.getIdfa(), appTask.getApp_id())) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您已接过该任务！");
			return mv;
		}
		//快速任务 就不判断电池
		if(!appTask.isQuicktask()){
			//判断电池id是不是接过此任务
			if(userTaskService.existTodayUserTaskByBatteryIdAndAppId(clientInfo.getShortBid(), appTask.getApp_id())){
				logger.error("今天已经接过任务的电池,battery_id:{},user:{},idfa:{},appId:{}",clientInfo.getBid(),u.getId(),clientInfo.getIdfa(),appTask.getId());
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您已接过该任务！");
				return mv;
			}
			
			if(userTaskService.existBatteryTaskByAppId(clientInfo.getShortBid(), appTask.getApp_id())){
				logger.error("已经接过5次以上任务的电池,battery_id:{},user:{},idfa:{},appId:{}",clientInfo.getBid(),u.getId(),clientInfo.getIdfa(),appTask.getId());
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您已接过该任务！");
				return mv;
			}
		}
		
		if(appTask.isQuicktask()){
			//快速任务
			Map<String, String> extraParams = new HashMap<String, String>();
			if(appChannelQuickTaskService.click(clientInfo, u,appTask, extraParams) == false){
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "接任务失败，请稍后再试");
				return mv;
			}
		}else if(appTask.getIschannel() > 0) { 	//有渠道信息 
			//检查当前第三方任务是允许接收
			boolean canRevice = channelService.isAllowReceiveTask(u, appTask, clientInfo);
			if(!canRevice) {
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "接任务失败，请稍后再试");
				return mv;
			}
		}else if (appTask.isVendorTask()){ //厂商回调任务，先检查厂商点击
			//向应用厂商发起点击请求
			if (!appVendorService.onClick(u, appTask, clientInfo)){
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "检测您已安装过哟，试试别的吧");
				return mv;
			}
		} else {
			//无点击的任务，检查redis预先导入的排重数据
			boolean isReceivedIDFA = userInstalledAppService.isPreFilteredByIDFA(appTask.getApp_id(), clientInfo.getIdfa());
			if (isReceivedIDFA){ //该IDFA已经接过任务
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您已试用过该App");
				return mv;
			}
		}

		UserTask newUt = null;
		if (ut != null) {
			newUt = userTaskService.restartTask(ut, appTask, clientInfo);
			logger.info("user restart exist task:{}", newUt);
		}else {
			newUt = userTaskService.addTask(u.getId(), appTask, clientInfo);
		}

		if(newUt != null){
			antiCheatService.cacheTaskUserIp(clientInfo.getIpAddress());
			appTaskService.onUserReceiveTask(taskId);
			App app = appTaskService.getApp(appTask.getApp_id());
			userMessageService.addUserAppMessage(u, app, appTask, newUt, UserAppMessage.STATUS_APP_START);
			mv.addObject("expireTime", newUt.getDefaultExpireMinuteTime());
			fillStatus(mv);
			//添加电池id和appid记录入redis
			userTaskService.addTodayBatteryTaskToRedis(newUt.getBattery_id(), app.getId());
		}else {
			fillErrorStatus(mv, ErrorCode.ERROR_CODE_UNKNOWN, "接任务失败，请稍后再试");
		}

		return mv;
	}
	
	@RequestMapping(value = {"/task/doings","/az/za"}, method = RequestMethod.POST)
	public ModelAndView doing(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "tasks.json.ftl");
		
		User u = getUser(request);
		fillStatus(mv);
		if(u.isBlack()){
			logger.error("User {} masked, can't receive task !!", u.getId());
			return mv;
		}
		List<UserTaskVo> tasks = userTaskService.getUserDoingTaskVos(u.getId());
		mv.addObject("tasks", tasks);
		
		return mv;
	}
	
	/**
	 * 客户端打开了第三方应用，上报数据，记录用户打开应用的状态
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"/ba/ab"}, method = RequestMethod.POST)
	public ModelAndView setAppOpened(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "result.json.ftl");
		
		User u = getUser(request);
		fillStatus(mv);
		if(u.isBlack()){
			logger.error("User {} masked, can't set App opened for task !!", u.getId());
			return mv;
		}

		int userId = u.getId();
		
		//客户端传递protocol参数，获取对应的用户任务
		String protocol = ServletRequestUtils.getStringParameter(request, "protocol");
		if(StringUtils.isNotBlank(protocol)){
			//检查是否带有"://"
			if(!protocol.endsWith("://")){
				protocol += "://";
			}
			
			App app = appTaskService.getAppByProtocol(protocol);
			if(app != null){
				List<UserTask> tasks = userTaskService.getUserDoingAppTasks(userId, app.getId()); 
				
				for(UserTask ut : tasks){
					//如果当前的用户任务是有效的，则设置打开过该应用
					if(ut.isValid()){
						userTaskService.setOpened(userId, ut.getId());
						logger.info("user={} has opened task={}, protocol={}", userId, ut.getId(), protocol);
					}
				}
			}
		}

		return mv;
	}


	/**
	 * 获取IOS的scheme 列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/bc/cb", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAppSchemeList(){

		List<String> schemeList = new ArrayList<String>();
//		List<App> appList = appTaskService.getAllApps();
//		for (App app: appList){
//			if (app.isPromoting()){
//				schemeList.add(app.getAgreement());
//			}
//		}

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("code", 0);
		resultMap.put("message", "OK");
		resultMap.put("data", schemeList);

		return JSONObject.fromObject(resultMap).toString();
	}

	/**
	 * 应用试用日志列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/task/logs", method = RequestMethod.GET)
	public ModelAndView List(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "usertasklist.json.ftl");
		//mv.setViewName(prefix + "tasklist.htl.ftl");

		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		int start = pageIndex * defaultPageSize;
		User u = getUser(request);
		mv.addObject("usertasks", userTaskService.getUserTasks(u.getId(), start, defaultPageSize));
		return mv; 
	}

	/**
	 * 上报已安装的应用
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"/task/repost_installed", "/bg/gb"})
	public ModelAndView reportInstalledApps(HttpServletRequest request) throws Exception{
		String[] appidArry = request.getParameterValues("appid[]");
		String[] agreementArry = request.getParameterValues("agreement[]");

		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		if(appidArry!= null && appidArry.length > 0 && agreementArry != null){
			for(int i = 0 ; i < appidArry.length && i < agreementArry.length; i++){
				Integer app_id = IdEncoder.decode(appidArry[i]);
				if(app_id != null){
					userInstalledAppService.insert(u.getId(), app_id, clientInfo.getDid(), agreementArry[i]);
				}
			}
		}

		ModelAndView mv = new ModelAndView(prefix + "result.json.ftl");
		fillStatus(mv);
		return mv;
	}


	/**
	 * 检查用户任务完成状态
	 * @return
	 */
	@RequestMapping(value = {"/task/check/{tid:\\w+}", "/task/info/{tid:\\w+}"})
	public ModelAndView checkTaskStatus(HttpServletRequest request, @PathVariable("tid") String tid) throws CommonException {
		User u = getUser(request);

		Integer id = IdEncoder.decode(tid);
		if(id == null){
			throw ErrorCode.getParameterErrorException("参数错误!");
		}

		AppTask  task = appTaskService.getAppTask(id);
		if(task == null){
			throw ErrorCode.getParameterErrorException("参数错误!");
		}

		//check if get app info
		boolean showInfo = false;
		App app = null;
		String appInfo = null;

		long expireTime = Constants.TASK_EXPIRE_TIME;
		if(task.isQuicktask()){
			expireTime = Constants.QUICK_TASK_EXPIRE_TIME;
		}
		int remainTime = (int)(expireTime / Constants.minutes_millis);

		String uri = request.getRequestURI();
		if (uri.contains("/info/")){
			showInfo = true;
			app = appTaskService.getApp(task.getApp_id());
			appInfo = appTaskService.getEncryptedAppInfo(app, u.getUser_identity());
		}

		int status = -2; //小于等于0，表示已完成或者过期的状态，大于0表示进行中的状态
		String desc = "您未接该任务";
		UserTask userTask = userTaskService.getUserTaskByAppId(u.getId(), task.getApp_id());
		if (userTask == null || userTask.isAborted()){
			status = -2;
			desc = "您未接该任务";
		}else if(userTask.isExpired()){
			remainTime = 0;
			status = -1;
			desc = "很遗憾，您未能按时完成任务，任务已超时";
		}else{
			remainTime = userTask.getExpireMinuteTime();
			if(userTask.getReward() > 0) {
				status = 0;
				desc = "恭喜，任务已完成";
			} else  if (userTask.isCompleted()){
				if (userTask.isTimeout()){
					status = -1;
					desc = "很遗憾，任务未审核通过";
				}else {
					status = 2;
					desc = "任务审核中，如果您未按照第三步完成任务，可继续打开试玩";
				}
			} else {
				status = 1;
				desc = "任务未完成，请继续试用";
			}
		}

		ModelAndView mv = new ModelAndView(prefix + "status.json.ftl");
		fillStatus(mv);
		mv.addObject("showInfo", showInfo);
		mv.addObject("app", app);
		mv.addObject("appTask", task);
		mv.addObject("userTask", userTask);
		mv.addObject("appInfo", appInfo);
		mv.addObject("remainTime", remainTime);
		mv.addObject("status", status);
		mv.addObject("message", desc);
		return mv;
	}


	@RequestMapping(value = {"/task/abort/{tid:\\w+}"})
	public ModelAndView abortUserTask(HttpServletRequest request, @PathVariable("tid") String tid) throws CommonException {
		User u = getUser(request);

		ModelAndView mv = new ModelAndView(prefix + "result.json.ftl");
		Integer id = IdEncoder.decode(tid);
		if(id == null){
			fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "参数错误");
		}

		AppTask  task = appTaskService.getAppTask(id);
		if(task == null){
			fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "参数错误");
		}
		UserTask userTask = userTaskService.getUserTaskByAppId(u.getId(), task.getApp_id());
		if (userTask == null || !userTask.isValid()){
			fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "任务状态错误");
		}else if (userTask.getActive() == 1) {
			fillErrorStatus(mv, ErrorCode.ERROR_CODE_UNKNOWN, "您已开始试玩，不能放弃该任务");
		}else{
			boolean flag = userTaskService.abortTask(userTask.getId(), u.getId());
			logger.info("user abort task, user:{}, userTask:{}, flag:{}", u.getId(), userTask, flag);
			if (flag){
				appTaskService.onUserAbortTask(task.getId());
				fillStatus(mv);
				userTaskService.removeTodayBatteryTaskToRedis(userTask.getBattery_id(), userTask.getApp_id());
				antiCheatService.removeTaskIPCache(RequestUtil.getIpAddr(request));
			}else {
				fillErrorStatus(mv, ErrorCode.ERROR_CODE_UNKNOWN, "放弃任务失败");
			}
		}
		return mv;
	}
}
