package com.cyhd.web.action.web;

import com.cyhd.service.dao.po.AppUpdateIos;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.AppUpdateService;
import com.cyhd.service.vo.AppUpdate;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.common.util.ClientInfoUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hy on 3/24/16.
 */
@Controller
@RequestMapping("/web/online")
public class AppOnlineAction extends BaseAction{

    private final static String prefix = "/web/online/";

    @Resource
    AppUpdateService appUpdateService;

    @RequestMapping(value = "/enter.html", method = RequestMethod.GET)
    public ModelAndView enter(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = (User) request.getAttribute("userInfo");
        ClientInfo clientInfo = ClientInfoUtil.getClientInfo(request);

        boolean isAudited = appUpdateService.isAppAudited(clientInfo.getAppnm());
        if (user != null){
            return redirectForApp(request, "/ios/index.html");
        }

        return redirectForApp(request, "/web/online/login.html");
    }

    @RequestMapping(value = "/login.html", method = RequestMethod.GET)
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = (User) request.getAttribute("userInfo");
        ClientInfo clientInfo = ClientInfoUtil.getClientInfo(request);

        if (user != null){
            return redirectForApp(request, "/web/online/enter.html");
        }

        ModelAndView mv = new ModelAndView(prefix + "login.html.ftl");
        return mv;
    }

}
