package com.cyhd.web.action.wx;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.cyhd.service.impl.*;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.service.vo.UserArticleTaskVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.dao.po.UserIncomeLog;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping(value = {"/weixin/my", "/ios/my"})
public class WeiXinMyAction extends BaseAction{

	@Resource
	private UserService userService;
	
	@Resource
	private	AppTaskService appTaskService;
	
	@Resource
	private UserTaskService userTaskService;
	
	@Resource
	private UserIncomeService userIncomeService;
	
	@Resource
	private UserFriendService userFriendServer;
	
	@Resource
	private UserEnchashmentService userEnchashmentService;
	
	@Resource
	private MobileCodeService mobileCodeService;

	@Resource
	private UserArticleTaskService userArticleTaskService;
	
	private static final String prefix = "/weixin/my/";
	
	@RequestMapping(value = "/apps.html", method = RequestMethod.GET)
	public ModelAndView appsIncome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//return new ModelAndView("forward:/web/my/apps.html");
		ModelAndView mv = new ModelAndView();

		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		int start = pageIndex * defaultPageSize;
		User u = getUser(request);
		
		int total = userTaskService.getUserTaskTotal(u.getId());
		if(total > 0) {
			mv.setViewName(prefix + "apps.html.ftl");
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage",totalPage);
			mv.addObject("usertasks", userTaskService.getUserTasks(u.getId(), start, defaultPageSize));
		} else {
			mv.addObject("description", "限时任务");
			mv.setViewName("common/nodata.html.ftl");
		}
		mv.addObject("title", "限时任务记录");
		return mv; 
	}
	
	/**
	 * 其它收入列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/otherincome.html", method = RequestMethod.GET)
	public ModelAndView otherincome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//return new ModelAndView("forward:/web/my/otherincome.html");
		
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		
		List<UserIncomeLog> otherIncome = userIncomeService.getUserOtherIncome(u.getId());
		if(otherIncome !=null && otherIncome.size() > 0) {
			mv.addObject("otherIncome", otherIncome); 
			mv.setViewName(prefix + "otherincome.html.ftl");
		} else {
			mv.addObject("info", "其它收入记录");
			mv.addObject("description", "新手教程");
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", "其它收入");
		return mv; 
	}
	
	/**
	 * 邀请收入列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/inviteincome.html", method = RequestMethod.GET)
	public ModelAndView inviteincome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//return new ModelAndView("forward:/web/my/inviteincome.html");
		ModelAndView mv = new ModelAndView();

		int start = ServletRequestUtils.getIntParameter(request, "start", 0);
		int friend_level = ServletRequestUtils.getIntParameter(request, "friend_level", 1);
		friend_level = friend_level == 1 ? 1 : 2;
		User u = getUser(request);
		int userId = u.getId();
		
		int total = userIncomeService.countUserFriendInviteIncome(userId, 1);
		UserIncome income = userIncomeService.getUserIncome(userId);
		if(income.getShare_total() > 0 || total > 0) {
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage",totalPage);
			mv.addObject("income",income);
			mv.addObject("userIncome", userIncomeService.getUserFriendInviteIncome(userId, friend_level, start, defaultPageSize));
			mv.setViewName(prefix + "inviteincome.html.ftl");
		} else {
			mv.addObject("info", "学徒奖励记录");
			mv.addObject("description", "邀请好友");
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", "学徒奖励");
		return mv; 
	}
	
	
	@RequestMapping(value = "/invites.html", method = RequestMethod.GET)
	public ModelAndView invites(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//return new ModelAndView("forward:/web/my/invites.html");
		ModelAndView mv = new ModelAndView();

		int start = ServletRequestUtils.getIntParameter(request, "start", 0);
		User u = getUser(request);
		int total = userFriendServer.countUserFriends(u.getId());
		
		int uid  = userFriendServer.getInvitor(u.getId());
		if(uid > 0 || total > 0) {
			if(uid > 0) {
				mv.addObject("superiors", userService.getUserById(uid));
				mv.addObject("createTime", u.getCreatetime());
			}
			if(total > 0) {
				int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
				mv.addObject("totalPage",totalPage);
				mv.addObject("userFriends", userFriendServer.getUserFriends(u.getId(), start, defaultPageSize));
			}
			mv.setViewName(prefix + "invites.html.ftl");
		} else {
			mv.addObject("description", "收徒");
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", "收徒记录");
		return mv; 
	}
	/**
	 * 提现列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "enchashments.html", method = RequestMethod.GET)
	public ModelAndView enchashments(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//return new ModelAndView("forward:/web/my/enchashments.html");
		
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		int total = userEnchashmentService.getUserEnchashmentCount(u.getId());
		if(total > 0) {
			mv.setViewName(prefix + "enchashments.html.ftl");
			int start = ServletRequestUtils.getIntParameter(request, "start", 0);
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage",totalPage);
			mv.addObject("userEnchashments", userEnchashmentService.getUserEnchashmentList(u.getId(), start, defaultPageSize));
		} else {
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", "提现记录");
		return mv; 
	}
	
	/**
	 * 显示提现详情
	 * 
	 * @param id
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/enchashments/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView detail(@PathVariable("id")int id, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "enchashmentdetail.html.ftl");
		
		mv.addObject("userEnchashment", userEnchashmentService.getById(id));
		mv.addObject("title", "提现详情");
		return mv;
	}
	
	@RequestMapping(value="to_bind_mobile.html",method=RequestMethod.GET)
	public ModelAndView toBindMobile(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "bind_mobile.html.ftl");
		User u = getUser(request);
		UserAgentUtil.UserAgent userAgent = UserAgentUtil.getUserAgent(request);

		mv.addObject("ua", userAgent);
		mv.addObject("mobile", u.getMobile());
		mv.addObject("title", "绑定手机号");
		return mv;
	}
	/**
	 * 邀请收入明细
	 * 
	 * @param request
	 * @return
	 * @throws CommonException 
	 */
	@RequestMapping(value = "/inviteincome/{id:\\d+}", method = RequestMethod.GET)
	 public ModelAndView inviteIncomeDetail(@PathVariable("id")int from_user, HttpServletRequest request) throws CommonException {
		ModelAndView mv = new ModelAndView();

		int start = ServletRequestUtils.getIntParameter(request, "start", 0);
		if(from_user <= 0) {
			logger.error("from_user parameter error!!");
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "缺少来源用户！");
		}
		User u = getUser(request);
		//u.setId(85);
		
		int total = userIncomeService.countUserFriendInviteIncomeDetail(u.getId(), from_user);
		if(total > 0) {
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage",totalPage);
			mv.addObject("userIncomeDetail", userIncomeService.getUserFriendInviteIncomeDetail(u.getId(), from_user, start, defaultPageSize));
			mv.addObject("from_user",from_user);
			mv.setViewName(prefix + "inviteincomedetail.html.ftl");
		} else {
			mv.addObject("description", "邀请好友");
			mv.setViewName("common/nodata.html.ftl");
		}
		
		mv.addObject("title", "学徒奖励明细");
		return mv; 
	}


	/**
	 * 转发收入页面
	 *
	 * @param request
	 * @return
	 * @throws CommonException
	 */
	@RequestMapping(value = "/articlelist.html", method = RequestMethod.GET)
	public ModelAndView articlieList(HttpServletRequest request) throws CommonException {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		int page = ServletRequestUtils.getIntParameter(request, "page", 1);
		int start = (page-1) * defaultPageSize;
		int size = ServletRequestUtils.getIntParameter(request, "size", defaultPageSize);

		int total = userArticleTaskService.getFinshTotal(u.getId());

		if(total > 0){
			List<UserArticleTaskVo> userArticlelist = userArticleTaskService.getUserArtricleLog(u.getId(),start,size);
			mv.addObject("userArticlelist", userArticlelist);
			mv.setViewName(prefix+"articles_log.html.ftl");
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage", totalPage);
		}else{
			mv.addObject("tasks", "true");
			mv.setViewName("common/nodata.html.ftl");
		}
		mv.addObject("title", "转发任务记录");
		return mv;

	}

	@RequestMapping(value = "/exchangelog.html", method = RequestMethod.GET)
	public String exchangelog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "forward:/web/my/exchangelog.html";
	}

	@RequestMapping(value = "/thridpartlog.html", method = RequestMethod.GET)
	public String thridpartlog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "forward:/web/my/thridpartlog.html";
	}

	@RequestMapping(value = "goldcoin.html", method = RequestMethod.GET)
	public String myGoldCoin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "forward:/web/my/goldcoin.html";
	}

	@RequestMapping(value = "giftscore.html", method = RequestMethod.GET)
	public String myGiftscore(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "forward:/web/my/giftscore.html";
	}
}