package com.cyhd.web.action.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserDraw;
import com.cyhd.service.impl.UserDrawService;
import com.cyhd.service.impl.UserDrawService.DrawNum;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;

@Controller
@RequestMapping("/api/v1/")
public class ActivityApiAction extends BaseAction{

	
	@Resource
	private UserDrawService userDrawService;
	
	private final String prefix = "/api/v1/activity/";

	@RequestMapping(value={"activity/draw"},produces="text/json; charset=UTF-8",method=RequestMethod.POST)
	public @ResponseBody ModelAndView userDraw(HttpServletRequest request,HttpServletResponse response) throws CommonException{
		User user = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		StringBuilder sb = new StringBuilder();
		sb.append("双旦活动抽奖;ip:").append(RequestUtil.getIpAddr(request));
		sb.append(",userId:").append(user.getId());
		sb.append(",query:").append(RequestUtil.getQueryString(request));
		
		ModelAndView mv = new ModelAndView();
		UserDraw userDraw = userDrawService.getUserDraw(user.getId(), GlobalConfig.ACTIVITY_ID);
		if(userDraw == null || userDraw.getBalance_times() <= 0 || clientInfo.isIos() == false){
			sb.append(",用户似乎存在盗链行为!!!");
			logger.warn(sb.toString());
			mv.addObject("ret_code", -1);
			mv.addObject("ret_message", "亲，你没有未拆开的红包");
			mv.addObject("reflush", true);
		}else{
			int inside = DateUtil.isInsideTwoTime( GenerateDateUtil.getCurrentDate(),GlobalConfig.ACTIVITY_START, GlobalConfig.ACTIVITY_END);
			if(inside == 2){
				DrawNum drawnum = userDrawService.getRandomAmount(user, clientInfo);
				if(drawnum != null && drawnum.getCount() > 0){
					mv.addObject("drawType", drawnum.getType().getName());
					mv.addObject("amount", drawnum.getCount());
				}
				userDraw = userDrawService.getUserDraw(user.getId(), GlobalConfig.ACTIVITY_ID);
			}
			
		}
		mv.addObject("userDraw", userDraw);
		mv.setViewName(prefix+"draw_yaoyiyao.json.ftl");
		return mv;
	}
}
