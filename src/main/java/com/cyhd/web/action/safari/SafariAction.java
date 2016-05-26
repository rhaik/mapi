package com.cyhd.web.action.safari;

import com.cyhd.common.util.NumberUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserHomePageMenu;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.CookieUtil;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.GrayStrategyUtil;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.service.util.UserAgentUtil.UserAgent;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.exception.CommonException;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Safari（钥匙版）的相关页面
 * Created by hy on 10/26/15.
 */
@Controller
@RequestMapping("/ios")
public class SafariAction extends BaseAction {

    private String prefix = "safari/";

    @Resource
    UserIncomeService userIncomeService;

    @Resource
    private UserFriendService userFriendService;

    @Resource
    private UserHomePageMenuService userHomePageMenuService;

    @Resource
    private BeginnerService beginnerService;

    @Resource
    private UserTaskService userTaskService;

    @Resource
    private RepairService repairService;

    /**
     * 进入safari版本的入口页面
     * @param request
     * @return
     */
    @RequestMapping("/enter.html")
    public ModelAndView enter(HttpServletRequest request, HttpServletResponse response) throws Exception{
        UserAgent userAgent = UserAgentUtil.getUserAgent(request);
        if (userAgent.isWeixin()){ //仍然在微信里面，提示用户点击用safari打开
            return new ModelAndView(prefix + "weixin.html.ftl");

            //微信直接进入任务列表
            //return new ModelAndView("redirect:/ios/tasks.html");
        }
        
        User u = getUser(request);
        //未给用户发过新用户奖励，则处理收徒和发奖的逻辑
        try {
            if (!u.isRewardNewUserComplete()) {
                boolean isInvited = false;

                //处理收徒的逻辑，理论上只会进行一次
                String invite_code = CookieUtil.getCookieValue(Constants.INVITE_COOKIE_KEY, request);
                if (StringUtils.isNotEmpty(invite_code)) {
                    logger.info("safari enter, user login and has invitor,user={}, code={}", u.getId(), invite_code);

                    //处理用户邀请的逻辑
                    User invitor = null;
                    if (invite_code.length() == 32) {
                        invitor = userService.getUserByInviteCode(invite_code);
                    }else if (invite_code.length() == 8){
                        invitor = userService.getUserByIdentifyId(NumberUtil.safeParseInt(invite_code));
                    }

                    if (invitor != null && invitor.getId() != u.getId()) {
                        isInvited = true;
                        if(invitor.getId() < u.getId()){
	                        userFriendService.onAddUserFriend(invitor, u, u.getIdfa());
                        }else{
                        	logger.warn("safari enter, error invitor id when login, identify_id={}",invitor.getId(), u.getId());
                        }
                    } else {
                        logger.error("safari enter, error invitor id when login, identify_id={}", invite_code);
                    }
                }

                //给新用户发奖 必须是有师傅的
                if (userService.isNewUser(u) ) {
                	if(isInvited){
                		//保险期间，先给用户创建收入记录，再发奖
                		userIncomeService.createNewUserIncome(u.getId());
                		userService.executeRewardNewUser(u, Constants.platform_ios);
                	}
                    CookieUtil.setNewCookie("is_nubie", isInvited? "200" : "150" ,response);
                }
               
            }
        }catch (Exception exp){
            logger.error("enter safari error:{}", exp);
        }

        return new ModelAndView("redirect:/ios/index.html?rnd=" + Math.random());
    }

    /**
     * safari版本首页
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/index.html")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception{

        User u = getUser(request);
        UserAgent userAgent = UserAgentUtil.getUserAgent(request);

        if (!userAgent.isInAppView()) {
            //判断是不是从秒赚入口进来的，是的话发奖励，必须安装过入口才能完成新手任务
            String source = request.getParameter("s");
            if ("clip".equals(source)) {
                String ysCookie = CookieUtil.getCookieValue("ys_clip", request);

                if (!u.isYaoshiClipComplete() && StringUtil.isNotBlank(ysCookie)) {
                    beginnerService.addYaoshiClipReward(u);
                }
                logger.info("user open yaoshi using clip, user:{}, yaoshi compelete:{}, ysCookie:{}", u.getId(), u.isYaoshiClipComplete(), ysCookie);
            }
        }

        int todayIncome = userIncomeService.countUserTodyIncome(u.getId());
        int  todayInvite = userFriendService.countTodyInviteFriendByuserId(u.getId());

        int income = 0, balance = 0;
        UserIncome userIncome = userIncomeService.getUserIncome(u.getId());
        if (userIncome != null){
            income = userIncome.getIncome();
            balance = userIncome.getBalance();
        }else {
            //可能还没有收入数据
            userIncomeService.createNewUserIncome(u.getId());
        }

        List<UserHomePageMenu> homeMenuList = userHomePageMenuService.getSafariHomePageMenus(GlobalConfig.safari_version);

        ModelAndView mv = new ModelAndView(prefix + "index_new.html.ftl");

        mv.addObject("todayIncome", todayIncome);
        mv.addObject("todayInvite", todayInvite);
        mv.addObject("user", u);
        mv.addObject("income", income);
        mv.addObject("balance", balance);
        mv.addObject("homeMenuList", homeMenuList);
        mv.addObject("hideBack", Boolean.TRUE);
        mv.addObject("userAgent", userAgent);
        mv.addObject("isGrayUser", GrayStrategyUtil.isGrayUser(u.getId()));

        boolean isNewbie = userService.isNewUser(u);
        String nubie = CookieUtil.getCookieValue("is_nubie", request);
        if (isNewbie || StringUtil.isNotBlank(nubie)) {
            int nubieAmount = NumberUtil.safeParseInt(nubie);
            nubieAmount = nubieAmount > 0 ? nubieAmount : 150;

            mv.addObject("showNubie", true);
            mv.addObject("nubieAmount", nubieAmount);
            mv.addObject("hasInvite", nubieAmount==200);
            CookieUtil.deleteCookie("is_nubie", request, response);
        }
        mv.addObject("showReward", userFriendService.userHasEffectiveReward(u.getId()));

        return mv;
    }

    /**
     * 限时任务列表页
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/tasks.html")
      public ModelAndView tasks(HttpServletRequest request, HttpServletResponse response){

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.

        return new ModelAndView("forward:/web/discovery/tasks.html");
    }


    /**
     * 限时任务详情页
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/task/{\\w+}")
    public ModelAndView taskDetail(HttpServletRequest request, HttpServletResponse response){

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.

        return new ModelAndView("forward:" + request.getRequestURI().replace("/ios/task/", "/web/task/"));
    }


    /**
     * 新手任务页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/beginner.html")
    public ModelAndView beginnerTasks(HttpServletRequest request, HttpServletResponse response){
        return new ModelAndView("forward:/web/discovery/beginner.html");
    }

    /**
     * 分享收徒页面
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/share.html")
    public ModelAndView share(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("forward:/web/discovery/share.html");
    }

    /**
     * 排行榜页面
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/ranks.html")
    public ModelAndView ranks(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("forward:/web/discovery/ranks.html");
    }

    /**
     * 用户签到页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/checkin.html", method = RequestMethod.GET)
    public ModelAndView myCheckinPage(HttpServletRequest request, HttpServletResponse response){
        return new ModelAndView("forward:/web/my/checkin.html");
    }

    /**
     * 签到记录页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/checkinlog.html", method = RequestMethod.GET)
    public ModelAndView myCheckinLog(HttpServletRequest request, HttpServletResponse response){
        return new ModelAndView("forward:/web/my/checkinlog.html");
    }


    /**
     * 我的消息页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/message.html", method = RequestMethod.GET)
    public ModelAndView myMessageList(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new ModelAndView(prefix + "message.html.ftl");
        mv.addObject("title", "我的消息");
        return mv;
    }

    /**
     * 安装钥匙的入口
     * @return
     */
    @RequestMapping(value = "/clip", method = RequestMethod.GET)
    public ModelAndView installYaoshiClip(HttpServletResponse response){
        CookieUtil.setNewCookie("ys_clip", "" + System.currentTimeMillis(), response);
        return new ModelAndView("redirect:/static/ios/yaoshi" + (GlobalConfig.isDeploy? "" : "_test") + ".mobileconfig");
    }


    /**
     * 我的收入页面
     * @param request
     * @return
     */
    @RequestMapping(value = "/income.html",  method = RequestMethod.GET)
    public ModelAndView myIncome(HttpServletRequest request){
        return  new ModelAndView("forward:/web/my/income.html");
    }
    /**
     * 我的收入页面
     * @param request
     * @return
     */
    @RequestMapping(value = "/activity.html",  method = RequestMethod.GET)
    public ModelAndView activity(HttpServletRequest request){
        return  new ModelAndView("forward:/web/activity/index.html");
    } 
    
//    @RequestMapping("/activity/invite.html")
//	public ModelAndView shareInvite(HttpServletRequest request,HttpServletResponse repResponse) throws CommonException{
//		return new ModelAndView("forward:/web/activity/invite.html");
//	}
//    
//    @RequestMapping("/activity/effectiveList.html")
//	public ModelAndView effectiveInviteList(HttpServletRequest request,HttpServletResponse repResponse) throws CommonException{
//		return new ModelAndView("forward:/web/activity/effectiveList.html");
//	}

    /**
     * 用户个人中心
     * @param request
     * @param repResponse
     * @return
     * @throws CommonException
     */
    @RequestMapping("/user/home.html")
    public ModelAndView userHome(HttpServletRequest request,HttpServletResponse repResponse) throws CommonException{
        return new ModelAndView("forward:/web/my/home.html");
    }
    
    @RequestMapping("/user/show_order_image.html")
    public ModelAndView showOrderImage(HttpServletRequest request,HttpServletResponse repResponse) throws CommonException{
        return new ModelAndView("forward:/web/my/show_order_image.html");
    }

    @RequestMapping("/user/invites.html")
    public ModelAndView invitesFriends(HttpServletRequest request,HttpServletResponse repResponse) throws CommonException{
        return new ModelAndView("forward:/web/my/invites.html");
    } 
}
