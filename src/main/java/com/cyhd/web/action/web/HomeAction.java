package com.cyhd.web.action.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.impl.UserService;
import com.cyhd.web.common.BaseAction;

@Controller
@RequestMapping("/web")
public class HomeAction extends BaseAction {

	@Resource
	UserService userService;

	private static final String prefix = "/web/";

	@RequestMapping(value = "/income.html")
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "home.html.ftl");
		
		mv.addObject("title", "发现发现");
		return mv;
	}

}
