package com.cyhd.web.action.doubao;


import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserIncomeService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.impl.doubao.ProductActivityService;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.service.util.UserAgentUtil.UserAgent;
import com.cyhd.service.util.WeixinShareService;
import com.cyhd.service.vo.doubao.ProductActivityVo;
import com.cyhd.web.common.BaseAction;

@Controller
@RequestMapping("/www/doubao")
public class ShareProductAction extends BaseAction {

	@Resource
	UserService userService;
	
	@Resource
	UserIncomeService userIncomeService;
	
	@Resource
	private WeixinShareService weixinShareService;
	
	@Resource
	ProductActivityService productActivityService;
	
	private static final String prefix = "/doubao/web/index/";
	/**
	 * 分享页
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/share/{id:[A-Za-z0-9_]+}/{productActivityId:[0-9]+}")
	public ModelAndView share(@PathVariable("id")String unionIdMd5, @PathVariable("productActivityId")int productActivityId,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "share.html.ftl");
		UserAgent ua = UserAgentUtil.getUserAgent(request);
		String destUrl = GlobalConfig.base_url + "www/doubao/share/"+unionIdMd5+"/"+productActivityId;
		if(request.getQueryString() != null) {
			destUrl += "?" + request.getQueryString() ;
		}
		if(unionIdMd5.length() == 32){
			User u = userService.getUserByInviteCode(unionIdMd5);
			mv.addObject("user", u);
		}
		ProductActivityVo pa = productActivityService.getVoByActivityId(productActivityId);
		if(pa == null) {
			pa = productActivityService.getLastProductActivity();
		}
		try{
			Map<String, String> shareMap = weixinShareService.sign(destUrl) ;
			mv.addObject("sharemap", shareMap) ;
		}catch(Exception e){
			logger.error("weixin share error!",e);
		}
		boolean isSafari = ua != null && ua.isSafari();
		mv.addObject("isSafari", isSafari);
		mv.addObject("isWeixin", ua.isWeixin() + "");
		mv.addObject("baseUrl", GlobalConfig.base_url);
		
		mv.addObject("unionIdMd5", unionIdMd5);
		mv.addObject("pa", pa);
		mv.addObject("title", "下载秒赚大钱");
		return mv;
	}
}
