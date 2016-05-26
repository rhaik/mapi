package com.cyhd.web.action.api;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.richtext.HtmlUtil;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserIdea;
import com.cyhd.service.impl.UserIdeaService;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/api/v1")
public class UserIdeaAction extends BaseAction {

	@Resource
	UserIdeaService userIdeaService;

	private static final String prefix = "/api/v1/common/";

	/**
	 * 保存意见反溃
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = {"/idea/report","/an/na"}, method = RequestMethod.POST)
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "save.json.ftl");
		
		User u = getUser(request);
		
		String content = HtmlUtil.toPlainText(ServletRequestUtils.getStringParameter(request, "content"));
		String version = this.getClientInfo(request).getAppVer();
		String equipment = this.getClientInfo(request).getModel();
		
		if(StringUtils.isEmpty(content)) {
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER);
		}
		
		UserIdea userIdea = new UserIdea();
		userIdea.setUser_id(u.getId());
		userIdea.setContent(content);
		userIdea.setVersion(version);
		userIdea.setEquipment(equipment);
		userIdea.setCreate_time(new Date());
		
		if (userIdeaService.save(userIdea)) {
			fillStatus(mv);
		} else {
			fillErrorStatus(mv, ErrorCode.ERROR_CODE_UNKNOWN);
		}
		return mv;
	}
}
