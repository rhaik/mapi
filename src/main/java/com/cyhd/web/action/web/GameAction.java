package com.cyhd.web.action.web;


import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.Game;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.GameService;
import com.cyhd.service.util.IdEncoder;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.service.util.VersionUtil;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.function.Predicate;

/**
 * 安卓游戏频道Action
 * Created by hy on 11/18/15.
 */
@Controller
@RequestMapping("/web/game")
public class GameAction extends BaseAction {

    private final static Logger logger = LoggerFactory.getLogger("game");
    
    @Resource
    private GameService gameService;
    
    private String prefix="/web/game/";
    
    @RequestMapping("/go")
    public String gotoGame(HttpServletRequest request) throws Exception{
        User user = getUser(request);
        String url = request.getParameter("gm");

        try {
            String gameUrl = URLDecoder.decode(url, "utf-8");

            logger.info("user:{}, goto game:{}, ip:{}", user.getUser_identity(), gameUrl, RequestUtil.getIpAddr(request));
            return "redirect:" + gameUrl;

        } catch (UnsupportedEncodingException e) {
            logger.error("user:{}, request error:{}", user.getUser_identity(), request.getQueryString());
        }
        return "404";
    }
   
    @RequestMapping("/list.html")
    public ModelAndView gameList(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mv = new ModelAndView();
    	ClientInfo clientInfo = getClientInfo(request);
    	User user = getUser(request);
    	
    	StringBuilder query = new StringBuilder(500);
    	query.append("user:").append(user.getId());
    	query.append(",ip:").append(RequestUtil.getIpAddr(request));
    	query.append(",query:");
    	query.append(RequestUtil.getQueryString(request));
    	//增加过滤 非H5的安卓游戏判断客户端 游戏现在只有安卓有
    	List<Game> games = gameService.getGames(clientInfo.isIos()?Constants.platform_ios:Constants.platform_android,new Predicate<Game>() {
			@Override
			public boolean test(Game t) {
        		if(t.getPlatform() == Constants.platform_android){
        			return VersionUtil.isRequiredTargetVsersion(clientInfo.getAppVer(), "1.6.0");
        		}
        		return true;
			}
    	});

    	//开始只有安卓有
    	if(clientInfo.isIos()){
    		logger.error(query.toString());
    	}
		mv.addObject("title", "游戏频道");
    	mv.addObject("isIos", clientInfo.isIos());
    	mv.addObject("games", games);
    	mv.setViewName(prefix+"list.html.ftl");
    	return mv;
    }
    
    @RequestMapping("/detail/{id:\\w+}")
    public ModelAndView detail(@PathVariable("id")String encodeId,HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ClientInfo clientInfo = getClientInfo(request);
    	User user = getUser(request);
    	ModelAndView mv = new ModelAndView();
    	StringBuilder query = new StringBuilder(500);
    	query.append("user:").append(user.getId());
    	query.append(",ip:").append(RequestUtil.getIpAddr(request));
    	query.append(",query:");
    	query.append(RequestUtil.getQueryString(request));
    	
    	//添加版本检测
//    	if(!VersionUtil.isRequiredTargetVsersion(clientInfo.getAppVer(), "1.6.0")){
//    		query.append(",没有达到目标版本要求:1.6.0");
//    		if (logger.isWarnEnabled()) {
//				logger.warn(query.toString());
//			}
//    		return mv;
//    	}
    	
    	Integer id = IdEncoder.decode(encodeId);
    	//404
    	String viewName = "";
    	String title = "游戏详情";
    	if(id == null){
    		query.append("按id没有获取数据");
    		logger.error(query.toString());
    	}else{
    		Game game = gameService.getGameById(id.intValue());
			if(game == null){
				query.append(",请求获得的游戏为Null");
				logger.error(query.toString());
			}else if(game.getPlatform() != clientInfo.getPlatform()&& game.getPlatform() != 3){
				query.append(",请求的游戏平台类型不一致：{}");	
				logger.error(query.toString());
			}else{
				if(game.getPlatform() != 3){
					if(VersionUtil.isRequiredTargetVsersion(clientInfo.getAppVer(), "1.6.0")){
						title = game.getName();
						query.append(",请求通过");
						logger.info(query.toString());
						mv.addObject("game", game);
						viewName = prefix+"detail.html.ftl";
					}
				}
			}
    	}
    	mv.addObject("title", title);
    	mv.setViewName(viewName);
    	return mv;
    }
}
