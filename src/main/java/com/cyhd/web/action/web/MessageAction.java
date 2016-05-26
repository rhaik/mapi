package com.cyhd.web.action.web;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.impl.UserMessageService;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/web/message")
public class MessageAction extends BaseAction {

	@Resource
	UserMessageService userMessageService;

	private static final String prefix = "web/message/";

	/**
	 * 显示系统详情
	 * 
	 * @param id
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "system/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView detail(@PathVariable("id")Integer id, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "system.html.ftl");
		
		//Integer id = IdEncoder.decode(encodedId);
		if(id <= 0){
			throw ErrorCode.getParameterErrorException("参数错误!");
		}
		
		mv.addObject("message", userMessageService.getSysMessagePageById(id));
		mv.addObject("title", "系统通知");
		return mv;
	}
}
