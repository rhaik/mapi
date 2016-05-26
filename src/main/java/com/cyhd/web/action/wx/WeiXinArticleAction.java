package com.cyhd.web.action.wx;

import com.cyhd.service.dao.po.*;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.IdEncoder;
import com.cyhd.service.vo.UserArticleTaskVo;
import com.cyhd.web.common.BaseAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by hy on 9/29/15.
 */
@Controller
@RequestMapping("/weixin/article")
public class WeiXinArticleAction extends BaseAction {

    @Resource
    private UserArticleTaskService userArticleTaskService;

    @Resource
    private TransArticleTaskService transArticleTaskService;

    @Resource
    private WeixinArticleService weixinArticleService;

    @Resource
    private AccountService accountService;



    private final String prefix="/weixin/article/";


    /**
     * 微信里的转发文章列表页面
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/tasks.html")
    public ModelAndView tasksList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = new ModelAndView();

        User user = getUser(request);

        //unionid就没有必要要啦 毕竟他是唯一的之后对应一个唯一的id
        List<UserArticleTaskVo> tasks = userArticleTaskService.getTasks(user.getId(), TransArticleTask.TYPE_WEIXIN);

        if(tasks != null && tasks.size() > 0) {
            mv.setViewName(prefix + "tasks.html.ftl");
            mv.addObject("tasks", tasks);
        } else {
            mv.addObject("tasks", "true");
            mv.setViewName("common/nodata.html.ftl");
        }
        mv.addObject("title", "转发任务");
        return mv;
    }


    @RequestMapping(value="/detail")
    public String taskDetail(HttpServletRequest request) throws Exception {
        return "forward:/web/article/" + request.getParameter("aid");
    }


    @RequestMapping("/share_success")
    public String shareSuccess(){
        return "forward:/web/article/article/share_success";
    }

}
