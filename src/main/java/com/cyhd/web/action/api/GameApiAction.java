package com.cyhd.web.action.api;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.Game;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.GameService;
import com.cyhd.service.util.IdEncoder;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;

@Controller
@RequestMapping("/api/v1/")
public class GameApiAction extends BaseAction{

	private final static Logger logger = LoggerFactory.getLogger("game");
	
	@Resource
	private GameService gameService;
	
	private String prefix = "/api/v1/game/";
	
//	@RequestMapping("game/get_games_category")
//	public ModelAndView getGameByCategory(HttpServletRequest request,HttpServletResponse response)throws Exception{
//		ModelAndView mv = new ModelAndView();
//		String category  = request.getParameter("ct");
//		ClientInfo clientInfo = getClientInfo(request);
//		if(StringUtils.isBlank(category)){
//			
//		}else{
//			List<Game> data = gameService.getGamesByCategory(clientInfo.isIos()?Constants.platform_ios:Constants.platform_android, category);
//			mv.addObject("games", data);
//			mv.setViewName(prefix+"gameList.json.ftl");
//		}
//		return mv;
//	}
//	
//	@RequestMapping("game/list")
//	public ModelAndView getGameByClientType(HttpServletRequest request,HttpServletResponse response)throws Exception{
//		ModelAndView mv = new ModelAndView();
//		ClientInfo clientInfo = getClientInfo(request);
//		List<Game> data = gameService.getPayGame(clientInfo.getPlatform());
//		if(data != null && data.size() > 0){
//			mv.addObject("games", data);
//		}else{
//			mv.addObject("ret_code", -1);
//			mv.addObject("ret_message", "not data");
//		}
//		mv.setViewName(prefix+"gameList.json.ftl");
//		return mv;
//	}
//	
//	@RequestMapping("game/detail/{id:\\w+}")
//	public ModelAndView getGameDetail(@PathVariable("id")String encodeId, HttpServletRequest request,HttpServletResponse response)throws Exception{
//		ModelAndView mv = new ModelAndView();
//		ClientInfo clientInfo = getClientInfo(request);
//		User user = getUser(request);
//		String query = RequestUtil.getQueryString(request);
//		StringBuilder sb = new StringBuilder(500);
//		sb.append("userid:").append(user.getId());
//		sb.append("uri:").append(request.getRequestURI());
//		sb.append(",query:").append(query);
//		Integer id = IdEncoder.decode(encodeId);
//		if(id == null){
//			sb.append("id为null");
//			logger.error(sb.toString());
//		}else{
//			Game game = gameService.getGameById(id.intValue());
//			int code = -1;
//			if(game == null){
//				sb.append(",请求获得的游戏为Null");
//				logger.error(sb.toString());
//			}else if(game.getPlatform() != clientInfo.getPlatform()&& game.getPlatform() != 3){
//				sb.append("请求的游戏平台类型不一致：{}");	
//				logger.error(sb.toString());
//				mv.addObject("ret_code", -1);
//			}else{
//				sb.append("请求通过");
//				logger.info(sb.toString());
//				mv.addObject("game", game);
//				code = 0;
//			}
//			mv.addObject("ret_code", code);
//		}
//		mv.setViewName(prefix+"game.json.ftl");
//		return mv;
//	}
}
