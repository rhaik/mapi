package com.cyhd.web.action.web;



import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.service.dao.po.UserDrawLog;
import com.cyhd.service.dao.po.UserDrawLog.UserDrawLogType;
import com.cyhd.service.impl.HongbaoActivityService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserDraw;
import com.cyhd.service.impl.UserDrawService;
import com.cyhd.service.impl.UserFriendService;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.service.util.UserAgentUtil.UserAgent;
import com.cyhd.service.util.WeixinShareService;
import com.cyhd.service.vo.InviteUserVo;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;

@Controller
@RequestMapping("/web/activity/")
public class ActivityAction extends BaseAction {

	@Resource
	private UserDrawService userDrawService;

	@Resource
	HongbaoActivityService hongbaoService;
	
	@Resource
	UserFriendService userFriendService;
	
	@Resource
	WeixinShareService weixinShareService;
	
	private final String prefix = "/web/activity/";


//	public ModelAndView index_old(HttpServletRequest request,HttpServletResponse response) throws CommonException{
//		ClientInfo clientinfo = getClientInfo(request);
//		User user = getUser(request);
//		ModelAndView mv = new ModelAndView();
//		
//		if((GlobalConfig.ACTIVITY_ID > 0) && clientinfo.isIos()){
//			UserDraw userDraw = userDrawService.getUserDraw(user.getId(), GlobalConfig.ACTIVITY_ID);
//			List<UserDrawLog> drawLogList = userDrawService.getRewardedLog(GlobalConfig.ACTIVITY_ID, 0, 50);
//			List<UserDrawLogVo> logVos = userDrawService.getUserDrawLogVos(drawLogList);
//			mv.addObject("logs", logVos);
//			mv.addObject("userDraw", userDraw);
//			mv.setViewName(prefix+"activity.html.ftl");
//		}else{
//			mv.setViewName("common/nodata.html.ftl");
//		}
//		
//		mv.addObject("title", "双旦活动");
//		return mv;
//	}


//	@RequestMapping("/index2.html")
//	public ModelAndView index_spring(HttpServletRequest request,HttpServletResponse response) throws CommonException{
//		ClientInfo clientinfo = getClientInfo(request);
//		User user = getUser(request);
//		ModelAndView mv = new ModelAndView();
//
//		if((GlobalConfig.ACTIVITY_ID > 0) && clientinfo.isIos()){
//			mv.addObject("startDate", GlobalConfig.ACTIVITY_START);
//			mv.addObject("endDate", GlobalConfig.ACTIVITY_END);
//			mv.addObject("totalNum", HongbaoActivityService.SYSTEM_HONGBAO_NUM);
//			mv.addObject("maxNum", HongbaoActivityService.MAX_DRAW_TIMES);
//			mv.addObject("user", user);
//			mv.setViewName(prefix+"activity.html.ftl");
//		}else{
//			mv.setViewName("common/nodata.html.ftl");
//		}
//
//		mv.addObject("title", "摇一摇红包");
//		return mv;
//	}
	
	@RequestMapping("/index.html")
	public ModelAndView index(HttpServletRequest request,HttpServletResponse response) throws CommonException{
		ClientInfo clientinfo = getClientInfo(request);
		User user = getUser(request);
		ModelAndView mv = new ModelAndView();

		if((GlobalConfig.ACTIVITY_ID > 0) && clientinfo.isIos()){
			int inside = DateUtil.isInsideTwoTime( GenerateDateUtil.getCurrentDate(),GlobalConfig.ACTIVITY_START, GlobalConfig.ACTIVITY_END);
			if(inside == 2){
				UserDraw userDraw = userDrawService.getUserDraw(user.getId(), GlobalConfig.ACTIVITY_ID);
				mv.addObject("userDraw", userDraw);
			}
			
			mv.addObject("todayMax", userDrawService.getTodyMaxDrawLog());
			
			mv.setViewName(prefix+"activity.html.ftl");
		}else{
			mv.setViewName("common/nodata.html.ftl");
		}
		mv.addObject("startDate", GlobalConfig.ACTIVITY_START);
		mv.addObject("endDate", GlobalConfig.ACTIVITY_END);
		mv.addObject("user",user);
		mv.addObject("title", "摇一摇轻松赢iPhone");
		return mv;
	}
	
	@RequestMapping("/g_m_log.hz")
	@ResponseBody
	public void addMaxDrawLog(HttpServletRequest request,HttpServletResponse response){
		String sign = "Jslscnu8LSiLoAnkjmSNul";
		int userId = ServletRequestUtils.getIntParameter(request, "uid", 0);
		String type = request.getParameter("action");
		String token = request.getParameter("bid");
		int amount = ServletRequestUtils.getIntParameter(request, "amount", 4);
		String drawType = ServletRequestUtils.getStringParameter(request, "type","夺宝币");
		int isMaxDraw = ServletRequestUtils.getIntParameter(request, "max", 0);
		//action=add_log&uid=1562&bid=Jslscnu8LSiLoAnkjmSNul&amount=10&type=夺宝币
		//添加log type为类型是"金币","夺宝比","iPhone 6s" amount为数量
		//web/activity/g_m_log.hz?action=add_log&uid=30&bid=Jslscnu8LSiLoAnkjmSNul&amount=1&type=iPhone 6s
		response.setStatus(404);
		
		if("add_log".equals(type) == false 
				|| sign.equals(token) == false){
			return ;
		}
		
		UserDrawLog log = new UserDrawLog();
		log.setActivity_id(GlobalConfig.ACTIVITY_ID);
		log.setDraw_amount(-1);
		log.setCreatetime(GenerateDateUtil.getCurrentDate());
		log.setDraw_amount(amount);
		String reason = amount+" "+drawType;
		log.setReason(reason);
		log.setType(UserDrawLogType.DECREMENT.getType());
		log.setUser_id(userId);
		log.setDraw_times(1);
		log.setFriend_id(0);
		if(userDrawService.addMaxDrawLog(log,isMaxDraw>=1)){
			logger.info("中奖啦:{}",log);
		}
		
	}
	private static Date inviteStartDate = DateUtil.parseDate( "2016/05/10","yyyy/MM/dd");
	
//	@RequestMapping(value={"/invite.html"})
//	public ModelAndView inviteIndex(HttpServletRequest request,HttpServletResponse response) throws CommonException{
//		ModelAndView mv = new ModelAndView();
//		if(hasUser(request)){
//			User user = getUser(request);
//			int effectiveNum = userFriendService.getUserTodayEffectiveInvite(user.getId());
//			int rank = userFriendService.getUserTodayInviteRank(user.getId(), effectiveNum);
//			InviteUserVo vo = new InviteUserVo();
//			vo.setRank(rank);
//			vo.setInviteNum(effectiveNum);
//			vo.setUser(user);
//			mv.addObject("userVo", vo);
//		}
//		String day = DateUtil.getYesterdayStr();
//		Calendar calendar = Calendar.getInstance();
//		boolean isShowRankList = true;
//		if(calendar.get(Calendar.HOUR_OF_DAY ) == 0){
//			isShowRankList  = userFriendService.isShowInviteRankListData(day);
//			if(!isShowRankList){
//				day= "后台正在统计数据中.......";
//			}
//		}
//		if(isShowRankList){
//			mv.addObject("rankList",userFriendService.getDayRankByInvite(30,day));
//		}
//		mv.addObject("rankDay",day);
//		UserAgent ua = UserAgentUtil.getUserAgent(request);
//		mv.addObject("title", "收徒排行榜");
//		mv.setViewName("/web/activity/inviteRanking.html.ftl");
//		mv.addObject("isWeixin", ua.isWeixin());
//		mv.addObject("isInAppView", ua.isInAppView());
//		return mv;
//	}
//
//	@RequestMapping("/effectiveList.html")
//	public ModelAndView effectiveInviteList(HttpServletRequest request,HttpServletResponse repResponse) throws CommonException{
//		ModelAndView mv = new ModelAndView();
//		User user = getUser(request);
//		Map<String, List<InviteUserVo>> datas = userFriendService.getUserAllInvite(user.getId());
//		if(datas != null && !datas.isEmpty()){
//			mv.addObject("datas", datas);
//			mv.setViewName("/web/discovery/effectiveInviteList.html.ftl");
//			mv.addObject("title", "有效徒弟列表");
//		}else{
//			mv.addObject("title","有效收徒");
//			mv.addObject("description", "收徒做任务");
//			mv.setViewName("/common/nodata.html.ftl");
//		}
//		return mv;
//	}
}
