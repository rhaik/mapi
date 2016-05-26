package com.cyhd.web.action.web;


import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserTask;
import com.cyhd.service.impl.AppTaskService;
import com.cyhd.service.impl.ChannelService;
import com.cyhd.service.impl.UserFriendService;
import com.cyhd.service.impl.UserInstalledAppService;
import com.cyhd.service.impl.UserTaskService;
import com.cyhd.service.util.IdEncoder;
import com.cyhd.service.util.VersionUtil;
import com.cyhd.service.vo.UserTaskVo;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.common.util.AESCoder;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/web/task")
public class TaskAction extends BaseAction {

	@Resource
	AppTaskService appTaskService;
	
	@Resource
	UserTaskService userTaskService;
	
	@Resource
	private ChannelService channelService;
	
	@Resource
	private UserInstalledAppService userInstalledAppService;

	@Resource
	private UserFriendService userFriendService;
	
	private static final String prefix = "web/task/";
	/**
	 * 应用试用详情
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id:\\w+}", method = RequestMethod.GET)
    public ModelAndView detail(@PathVariable("id")String encodedId, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();

		User u = getUser(request);
		
		ClientInfo info = super.getClientInfo(request);
		if(info.getAppVer() == null || info.getAppVer().startsWith("0.")){
			super.fillErrorStatus(mv, ErrorCode.ERROR_CODE_UNKNOWN, "版本过低，请在我->设置中检测新版本并升级！");
			return mv;
		}
		
		Integer id = IdEncoder.decode(encodedId);
		if(id == null){
			throw ErrorCode.getParameterErrorException("参数错误!");
		}
		
		AppTask  task = appTaskService.getAppTask(id);
		if(task == null){
			throw ErrorCode.getParameterErrorException("参数错误!");
		}
		
		if(task.getApp_id() == 96){
			if(request.getAttribute("fromSafari") != null){
				mv.setViewName("redirect:/ios/share.html");
			}else {
				mv.setViewName("redirect:/static/html/apprentice.html");
			}
			return mv;
		}
		
		//ios 版本号，比如9.0
		double iosVersion = VersionUtil.getIOSVersion(info.getOs());
	
		UserTaskVo vo = new UserTaskVo();

		if (userInstalledAppService.isPreFilteredByIDFA(task.getApp_id(), info.getIdfa())){
			vo.setInstalledApp(true);
		}else {
			Set<Integer> installedApp = userInstalledAppService.getListByUserId(u.getId());
			if (installedApp.contains(task.getApp_id())) {
				vo.setInstalledApp(true);
			}
		}


		vo.setApp(appTaskService.getApp(task.getApp_id()));
		vo.setAppTask(task);

		//只有当用户的任务是当前task的任务，或者任务未过期时，才认为是当前的任务
		UserTask ut = userTaskService.getUserTaskByAppId(u.getId(), task.getApp_id());
		if (ut != null && (ut.getTask_id() == task.getId() || !ut.isExpired())) {
			vo.setUserTask(ut);
		}

		//如果是厂商回调或者渠道任务，只能接一次
		if (ut != null && (task.isVendorTask() || task.getIschannel() > 0)){
			vo.setUserTask(ut);
		}
		int inviteNums = userFriendService.countUserFriends(u.getId());
		float shareRate = userFriendService.getExtraShareRateByAppTask(u.getId(),inviteNums);
		//mv.addObject("hint_text", taskUpdateTimeHintService.getAppTaskUpdateTimehint());
		String hint_text = "您收徒为%d人，任务单价奖励为%3.2f倍";
		mv.addObject("hint_text",String.format(hint_text, inviteNums,shareRate));
		mv.addObject("shareRate",shareRate);
		
		String viewName = prefix + "detail_new.html.ftl";
		if (request.getAttribute("fromSafari") != null){
			viewName = "/safari/detail.html.ftl";
			mv.addObject("info", appTaskService.getEncryptedAppInfo(vo.getApp(), u.getUser_identity()));
		}

		mv.setViewName(viewName);
		mv.addObject("user", u);
		mv.addObject("task", vo);
		mv.addObject("iosVersion", iosVersion);
		
		mv.addObject("title", task.getKeywords());
		return mv; 
	} 
}
