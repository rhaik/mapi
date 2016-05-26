package com.cyhd.web.action.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserTaskReportService;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/api/v1")
public class ReportAction extends BaseAction {

	private static final String prefix = "/api/v1/report/";
	
	@Resource
	private UserTaskReportService userTaskReportService;
	
	
	/**
	 * 检测任务上报
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = {"/report/tasks","/ag/ga"}, method = RequestMethod.POST)
	public ModelAndView upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "result.json.ftl");
		
		String content = request.getParameter("content");
		if(StringUtils.isEmpty(content)){
			logger.error("report task error!!");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER);
		}
		
		User u = getUser(request);
		
		JSONObject json = JSONObject.fromObject(content);
		int appDate = (int)json.getDouble("date");
		String network = json.getString("net");
		String battery_level = json.getString("battery_level");
		String mobile_network = json.getString("mobile_net");
		String screen_brightness = json.getString("screen_brightness");
		
		JSONArray appArray = json.getJSONArray("running_apps");
		
		if(appArray == null || appArray.size() == 0){
			fillStatus(mv);
			return mv;
		}

		//String did = this.getClientInfo(request).getDid();
		ClientInfo clientInfo = getClientInfo(request);

		userTaskReportService.report(u, appDate, network, battery_level, mobile_network, screen_brightness, appArray,clientInfo);
		
		fillStatus(mv);
		return mv;
	}
	
}

