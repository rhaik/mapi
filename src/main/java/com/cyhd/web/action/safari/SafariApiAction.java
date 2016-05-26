package com.cyhd.web.action.safari;

import com.cyhd.common.util.NumberUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.po.*;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.CookieUtil;
import com.cyhd.service.util.RedisUtil;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.exception.CommonException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Safari版本使用到的Ajax请求
 * Created by hy on 11/16/15.
 */
@RequestMapping("/ios/api")
@Controller
public class SafariApiAction extends BaseAction {

	@Resource
    private AppTaskService appTaskService;

    @Resource
    private UserTaskService userTaskService;

    @Resource
    private TransArticleTaskService transArticleTaskService;

    @Resource
    private UserCheckInService checkInService;

    @Resource
    private UserMessageService userMessageService;


    @Resource(name= RedisUtil.NAME_ALIYUAN)
    private IJedisDao userMessageCacheDao;

    @Resource
    private UserFriendService userFriendService;
    
    @Resource
    private UserIncomeService userIncomeService;
    
    //首页消息缓存时间(s)
    private static final int USER_MESSAGE_TTL = 60;


    /**
     * 用户签到
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/doCheckin", method = RequestMethod.POST)
    public ModelAndView doCheckIn(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("forward:/api/v1/user/doCheckin");
    }

    /**
     * 获取用户的签到记录
     */
    @RequestMapping(value = "/checkinlog", method = RequestMethod.GET)
    public ModelAndView getCheckInLog(HttpServletRequest request, HttpServletResponse response){
        return new ModelAndView("forward:/api/v1/user/checkinlog");
    }

    /**
     * 检查当前任务的执行状态
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/task/check/{\\w+}", "/task/info/{\\w+}"}, method = RequestMethod.GET)
    public ModelAndView checkTask(HttpServletRequest request, HttpServletResponse response){
        return new ModelAndView("forward:/api/v1/" + request.getRequestURI().replace("/ios/api/", ""));
    }

    /**
     * 放弃任务
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/task/abort/{\\w+}"}, method = RequestMethod.POST)
    public ModelAndView abortTask(HttpServletRequest request, HttpServletResponse response){
        return new ModelAndView("forward:/api/v1/" + request.getRequestURI().replace("/ios/api/", ""));
    }


    @RequestMapping(value = "/homeinfo", method = RequestMethod.GET, produces="text/json; charset=UTF-8")
    @ResponseBody
    public String getHomeInfo(HttpServletRequest request) throws CommonException {
        Map<String, Object> resultMap = new HashMap<>();

        User user = getUser(request);

        List<UserTask> userTasks = userTaskService.getUserDoingTasks(user.getId());
        //没有进行中的限时任务，则检查是否有新任务
        if (userTasks == null || userTasks.size() == 0){
            int lastAppTime = NumberUtil.safeParseInt(CookieUtil.getCookieValue("last_app_time", request));
            AppTask appTask = appTaskService.getLastAppTask();

            //检查是否有新的限时任务
            if (lastAppTime > 0 && appTask != null && appTask.getStart_time().getTime() > (lastAppTime * 1000L)) {
                resultMap.put("newApp", true);
            }
        }else {
            //有当前进行的限时任务，则返回限时任务的数量
            resultMap.put("doingNum", userTasks.size());
        }


        //检查是否有新的转发任务
//        TransArticleTask lastArticleTask = transArticleTaskService.getLastArticleTaskTask();
//        int lastArticleTime = NumberUtil.safeParseInt(CookieUtil.getCookieValue("last_article_time", request));
//        if (lastArticleTime > 0 && lastArticleTask != null && lastArticleTask.getStart_time().getTime() > (lastArticleTime * 1000)){
//            resultMap.put("newArticle", true);
//        }

        //检查用户是否签到过
        boolean hasCheckinToday = checkInService.isCheckinToday(user.getId());
        resultMap.put("hasCheckin", hasCheckinToday);


        return JSONObject.fromObject(resultMap).toString();
    }


    /**
     * 消息列表页，重定向到UserMessageApiAction中进行处理
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/message/list")
    public ModelAndView getUserMessage(HttpServletRequest request) throws Exception{
        int type = ServletRequestUtils.getIntParameter(request, "type");

        switch (type){
            case 1: //应用消息
                return new ModelAndView("forward:/api/v1/message/app-msg-safari");
            case 2: //好友消息
                return new ModelAndView("forward:/api/v1/ak/ka");
            case 3: //系统消息
                return new ModelAndView("forward:/api/v1/al/la");
            case 4: //转发消息
                return new ModelAndView("forward:/api/v1/message/article-msg-safari");
        }
        return new ModelAndView("404");
    }


    /**
     * safari版本首页定时获取用户的额消息，有1分钟的缓存
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/message/latest", produces="text/json; charset=UTF-8")
    @ResponseBody
    public String getLatestMessage(HttpServletRequest request) throws Exception{
        long appLastId = NumberUtil.safeParseLong(CookieUtil.getCookieValue("msg1_last_id", request));
        long friendLastId = NumberUtil.safeParseLong(CookieUtil.getCookieValue("msg2_last_id", request));
        long sysLastId = NumberUtil.safeParseLong(CookieUtil.getCookieValue("msg3_last_id", request));
        long articleLastId = NumberUtil.safeParseLong(CookieUtil.getCookieValue("msg4_last_id", request));

        User user = getUser(request);
        String key = RedisUtil.buildHomeMessageKey(user.getId());

        //首先从缓存中获取
        String userMessage = userMessageCacheDao.get(key);
        if (userMessage == null) {
            JSONArray array = new JSONArray();
            //对于应用试用，好友分成和转发消息，必须传入了最新的id才返回最近的消息
            if (appLastId > 0) {
                UserAppMessage appMessage = userMessageService.getUserLastAppMessage(user.getId());
                if (appMessage != null && appMessage.getSort_time() > appLastId) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", 1);
                    jsonObject.put("id", appMessage.getSort_time());
                    jsonObject.put("content", UserMessageService.getAppMessageContent(appMessage));
                    array.add(jsonObject);
                }
            }

            if (friendLastId > 0) {
                UserFriendMessage friendMessage = userMessageService.getUserLastFirendMessage(user.getId());
                if (friendMessage != null && friendMessage.getSort_time() > friendLastId) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", 2);
                    jsonObject.put("id", friendMessage.getSort_time());
                    jsonObject.put("content", UserMessageService.getFriendMessageContent(friendMessage));
                    array.add(jsonObject);
                }
            }

            if (articleLastId > 0) {
                UserArticleMessage articleMessage = userMessageService.getUserLastArticleMessage(user.getId());
                if (articleMessage != null && articleMessage.getSort_time() > articleLastId) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", 4);
                    jsonObject.put("id", articleMessage.getSort_time());
                    jsonObject.put("content", UserMessageService.getArticleMessageContent(articleMessage));
                    array.add(jsonObject);
                }
            }

            //一直会有系统消息
            UserSystemMessage systemMessage = userMessageService.getUserLastSystemMessage(user.getId(), user.getCreatetime(), Constants.platform_ios);
            if (systemMessage != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 3);
                jsonObject.put("isNew", systemMessage.getSort_time() > sysLastId);
                jsonObject.put("id", systemMessage.getSort_time());
                jsonObject.put("content", systemMessage.getContent());
                array.add(jsonObject);
            }

            JSONObject result = new JSONObject();
            result.put("data", array);
            result.put("code", 0);
            result.put("status", "ok");

            userMessage = result.toString();
            userMessageCacheDao.set(key, userMessage, USER_MESSAGE_TTL);
        }
        return userMessage;
    }

    @RequestMapping("/activity/draw")
    public ModelAndView activityDraw(HttpServletRequest request){
    	return new ModelAndView("forward:/api/v1/activity/draw");
    }

    @RequestMapping(value={"/invite/code"},produces={"text/json; charset=UTF-8"})
    public ModelAndView invite(HttpServletRequest request,HttpServletResponse response) throws CommonException{
    	return new ModelAndView("forward:/api/v1/bh/hb");
    }
    
    @RequestMapping(value={"/active/effective/reward"},method=RequestMethod.POST,produces={"text/json; charset=UTF-8"})
    @ResponseBody
    public String userEffectiveInviteReward(HttpServletRequest request,HttpServletResponse response)throws CommonException{
    	String rtvFormat = "{\"code\":%d,\"message\":\"%s\"}";
    	int code = -1; 
    	String message = "ERROR";
    	User user = getUser(request);
    	if(userFriendService.removeUserEffectiveInviteCacheKey(user.getId())){
    		code = 0;
    		message = "OK";
    	}
    	return String.format(rtvFormat, code,message);
    }
    
    @RequestMapping(value={"/invite/re_input_code"},produces={"text/json; charset=UTF-8"})
    public ModelAndView reInvite(HttpServletRequest request,HttpServletResponse response) throws CommonException{
    	return new ModelAndView("forward:/api/v1/invite_code/re_input_invitor");
    }
}
