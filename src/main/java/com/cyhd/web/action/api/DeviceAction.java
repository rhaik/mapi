package com.cyhd.web.action.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.impl.DeviceService;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/api/v1")
public class DeviceAction extends BaseAction {

	@Resource
	DeviceService deviceService;

	private static final String prefix = "/api/v1/device/";

	/**
	 * 登录上报
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = {"/device/token","/ac/ca"}, method = RequestMethod.POST)
	public ModelAndView upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "token.json.ftl");
		boolean result = false;
		// contents
		String act = request.getParameter("act");
		String token = request.getParameter("token");
		if (StringUtils.isEmpty(act)) {
			throw ErrorCode.getParameterErrorException("参数错误，缺少act!");
		}
		ClientInfo client = getClientInfo(request);
		int userId = 0;
		try{
			userId = getUser(request).getId();
		}catch(Exception e){
			
		}
		logger.info("upload device token, act={}, token={}, ixintoken={}, bat_token={},userId={}", act, token, null,null,userId);
		//int deviceType = client.isIos() ? 1 : 0;
		if (act.equalsIgnoreCase("upload") && userId == 0) {
			result = deviceService.saveDeviceNoUser(token, client.getClientType(), client.getModel(), client.getAppVer(), null, null,client.getAppnm());
		} else {
			result = deviceService.saveDevice(userId, act, token, client.getClientType(), client.getModel(), client.getAppVer(), null, null,client.getAppnm());
		}

		fillStatus(mv);
		return mv;
	}
	
}

