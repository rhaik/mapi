package com.cyhd.web.action.api;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.AesCryptUtil;
import com.cyhd.common.util.Helper;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.TransArticleTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.TransArticleTaskService;
import com.cyhd.service.impl.UserArticleTaskService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.IdEncoder;
import com.cyhd.service.vo.UserArticleTaskVo;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;

@Controller
@RequestMapping("/api/v1/")
public class ArticleTaskApiAction extends BaseAction{

	@Resource
	private UserService userService;
	
	@Resource
	private TransArticleTaskService transArticleTaskService;
	
	@Resource
	private UserArticleTaskService userArticleTaskService;
	
	
	private final String prefix="/api/v1/article/";
	
	
	@RequestMapping(value={"article/logs"})
	public ModelAndView getArticleMessageLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix+"articles_log.json.ftl");
		User u = getUser(request);
		int page = ServletRequestUtils.getIntParameter(request, "page", 0);
		int start = page* defaultPageSize;
		int size = ServletRequestUtils.getIntParameter(request, "size", defaultPageSize);
		List<UserArticleTaskVo> userArticlelist = userArticleTaskService.getUserArtricleLog(u.getId(),start,size);
		mv.addObject("userArticlelist", userArticlelist);
		return mv;
		
	}
	
	
}
