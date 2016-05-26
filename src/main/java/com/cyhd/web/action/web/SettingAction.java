package com.cyhd.web.action.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.common.util.ClientInfoUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.web.common.BaseAction;

@Controller
@RequestMapping("/web/setting")
public class SettingAction extends BaseAction {

	private static final String prefix = "web/setting/";
	/**
	 * 关于我们，支持不验证用户访问
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/aboutus.html", method = RequestMethod.GET)
	public ModelAndView aboutus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ClientInfo clientInfo = null;

		//如果在应用内，则获取clientInfo
		UserAgentUtil.UserAgent userAgent = UserAgentUtil.getUserAgent(request);
		if (userAgent.isInAppView()){
			clientInfo = ClientInfoUtil.getClientInfo(request);
		}

		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "aboutus.html.ftl");
		if (clientInfo != null) {
			mv.addObject("os", clientInfo.isIos() ? "iPhone版" : "Android版");
			mv.addObject("appVer", clientInfo.getAppVer());
		}else{
			mv.addObject("os", "");
			mv.addObject("appVer", "1.0.0");
		}
		mv.addObject("test", !GlobalConfig.isDeploy);
		mv.addObject("title", "关于我们");
		return mv; 
	}
	
	@RequestMapping(value = "/about_version.html", method = RequestMethod.GET)
	public ModelAndView aboutVersion(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String v = request.getParameter("v");
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "about_version.html.ftl");
		
		mv.addObject("title", "版本介绍");
		return mv; 
	}
	/**
	 * 联系客服
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/contactcustomer.html", method = RequestMethod.GET)
	public ModelAndView contactcustomer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "contactcustomer.html.ftl");
		
		mv.addObject("title", "联系客服");
		return mv; 
	}
}
