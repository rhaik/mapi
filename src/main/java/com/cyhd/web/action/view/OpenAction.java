package com.cyhd.web.action.view;

import com.cyhd.service.dao.po.Account;
import com.cyhd.service.dao.po.AppUpdateIos;
import com.cyhd.service.dao.po.ArticleComercial;
import com.cyhd.service.dao.po.ArticleWeixinAccount;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.IdEncoder;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.service.util.WeixinShareService;
import com.cyhd.service.vo.UserArticleTaskVo;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.exception.CommonException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by hy on 9/30/15.
 */
@Controller
@RequestMapping("/open")
public class OpenAction extends BaseAction{

    @Resource
    private UserArticleTaskService userArticleTaskService;

    @Resource
    private TransArticleTaskService transArticleTaskService;

    @Resource
    private WeixinArticleService weixinArticleService;

    @Resource
    private AccountService accountService;

    @Resource
    private WeixinShareService weixinShareService;

    @Resource
    private AppUpdateService appUpdateService;


    private final String prefix="/open/";
    /**
     * 从微信里浏览文章
     * @return
     */
    @RequestMapping("/article/{tid}")
    public ModelAndView viewArticle(@PathVariable("tid") String taskIdEncoded, HttpServletRequest request) throws  Exception{
        ModelAndView modelAndView = new ModelAndView();

        Integer taskId = IdEncoder.decode(taskIdEncoded);
        if(taskId == null){
            logger.info("task_id is not found");
            return getErrorView("参数错误");
        }

        UserArticleTaskVo taskVo = userArticleTaskService.getUserArticleTaskVo(0, taskId);

        if (taskVo.getTransArticleTask() != null && taskVo.getTransArticle() != null){
            ArticleComercial commercial = weixinArticleService.getCommercial(taskVo.getTransArticleTask().getComercial_id());
            ArticleWeixinAccount weixinAccount = weixinArticleService.getWeixinAccount(taskVo.getTransArticleTask().getWx_account_id());

            modelAndView.setViewName(prefix + "article_detail.html.ftl");
            modelAndView.addObject("vo", taskVo);
            modelAndView.addObject("commercial", commercial);
            modelAndView.addObject("weixinAccount", weixinAccount);
            modelAndView.addObject("wxAccountId", IdEncoder.encode(weixinAccount.getId()));


            Account wxAccount = accountService.getAccountByHost(request.getHeader("Host"));
            String host = wxAccount.getHost();
            if(!host.endsWith("/")){
                host = host + "/";
            }

            modelAndView.addObject("title", taskVo.getTransArticle().getName());
            modelAndView.addObject("shareUrl",  host + "open/article/" + taskIdEncoded);

            Map<String, String> shareMap = weixinShareService.sign(request) ;
            modelAndView.addObject("sharemap", shareMap) ;

        }else{
            return getErrorView("参数错误");
        }


        return modelAndView;
    }


    /**
     * 查看微信公众号信息
     * @return
     */
    @RequestMapping("/account/{wid}")
    public ModelAndView accountDetail(@PathVariable("wid") String wxIdEncoded, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        Integer wid = IdEncoder.decode(wxIdEncoded);
        if (wid == null) {
            return getErrorView("参数错误");
        }

        ArticleWeixinAccount weixinAccount = weixinArticleService.getWeixinAccount(wid);

        modelAndView.addObject("title", weixinAccount.getName());
        modelAndView.addObject("wxAccount", weixinAccount);

        modelAndView.setViewName(prefix + "qrcode.html.ftl");

        return modelAndView;
    }

    @RequestMapping("/report")
    public ModelAndView report(){
        ModelAndView mv = new ModelAndView(prefix + "report.html.ftl");
        mv.addObject("title", "举报");
        return mv;
    }

    @RequestMapping("/reportContent")
    public ModelAndView reportContent(){
        ModelAndView mv = new ModelAndView(prefix + "reportContent.html.ftl");
        mv.addObject("title", "举报");
        return mv;
    }

    @RequestMapping("/doReport")
    public String doReport(){
        return "";
    }


    /**
     * ios app下载路径
     * @param request
     * @return
     * @throws CommonException 
     */
    
    @RequestMapping("/ios/manifest-{id:[A-Za-z0-9]+}.plist")
    public ModelAndView iosDownload(@PathVariable("id")String unionIdMd5, HttpServletRequest request) throws CommonException{
        String agentStr = request.getHeader("user-agent");
        UserAgentUtil.UserAgent userAgent = UserAgentUtil.getUserAgent(request);

        String ip = RequestUtil.getIpAddr(request);
        logger.info("download ios manifest: user agent:{}, remote ip:{}", agentStr, ip);

        if (userAgent.isItunesstored() && (userAgent.isIPhone() || userAgent.isIPad()) ){
            AppUpdateIos ios = appUpdateService.getAppUpdateIos(unionIdMd5);
            if(ios == null || ios.getDownload_url() == null) {
            	return getErrorView("暂无更新");
            }

            logger.info("download ios app:{}, md5:{}, ip:{}", ios, unionIdMd5, ip);

            boolean isIOS8 = agentStr.indexOf("iOS/8") > 0;

            ModelAndView mv = new ModelAndView(prefix + "manifest.xml.ftl");
            appUpdateService.updateAppDownloadNumber(ios.getId());
            mv.addObject("update", ios);
            mv.addObject("isIOS8", isIOS8);

            return mv;
        }

        return getErrorView("请在iPhone Safari浏览器中打开此链接");
    }

    @RequestMapping("/redirect")
    public String redirect(HttpServletRequest request){
        String link = request.getParameter("link");

        try {
            String url = URLDecoder.decode(link, "utf-8");

            logger.info("redirect to:{}, ip:{}", url, RequestUtil.getIpAddr(request));
            return "redirect:" + url;

        } catch (UnsupportedEncodingException e) {
            logger.error("redirect request error:{}, ip:{}",  request.getQueryString(),  RequestUtil.getIpAddr(request));
        }
        return "404";
    }
}
