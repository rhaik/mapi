package com.cyhd.web.action.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserExchangeIntegralLog;
import com.cyhd.service.dao.po.UserIntegalIncome;
import com.cyhd.service.impl.UserExchangeIntegralLogService;
import com.cyhd.service.impl.UserIntegalIncomeService;
import com.cyhd.service.impl.UserThridPartIntegralService;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientAuth;
import com.cyhd.web.common.ClientInfo;

@Controller
@RequestMapping("/api/v1")
public class UserIntegalAction extends BaseAction{

	@Resource
	private UserExchangeIntegralLogService userExchangeIntegralLogService;
	
	@Resource
	private UserThridPartIntegralService userThridPartIntegralService;

	@Resource
	private UserIntegalIncomeService userIntegalIncomeService;
	private final String prefix = "/api/v1/integral/";
	
//	@RequestMapping(value="/detail",method=RequestMethod.POST)
//	public ModelAndView deail(HttpServletRequest request,HttpServletResponse response) throws Exception{
//		ModelAndView mv = new ModelAndView();
//		mv.setViewName(prefix+"detail.json.ftl");
//		
//		int source = ServletRequestUtils.getIntParameter(request, "source",1);
//		User u = getUser(request);
//	    
//		UserIntegalIncome incomes = userIntegalIncomeService.getIntegalIncomeBySource(u.getId(),source);
//	    if(incomes != null ){
//	    	userThridPartIntegralService.
//			
//		}
//		
//		
//		return mv;
//	}
	
	
	@RequestMapping(value={"/integral/exchange","/au/ua"},method=RequestMethod.POST)
	public ModelAndView exchange(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix+"exchange.json.ftl");
		
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		
		//就不用在去制造token啦 
		ClientAuth auth = getClientAuth(request);
		String token = auth.getSign();
		
		int exchangeNum = ServletRequestUtils.getIntParameter(request, "exchange_num", 0);
		int source = ServletRequestUtils.getIntParameter(request, "source",1);
		
		int clientType=clientInfo.isIos()?Constants.platform_ios:Constants.platform_android;
		//不是万普就统一为积分
		if(source != Constants.INTEGAL_SOURCE_WANPU){
			source = Constants.INTEGAL_SOURCE_YOUMI;
		}
		
		UserExchangeIntegralLog log = userExchangeIntegralLogService.exchange(u.getId(), exchangeNum, token, source, clientInfo.getDid(),clientType);
		
		mv.addObject("ret_code", log.getCode());
		mv.addObject("ret_message", log.getRemark());
		
		mv.addObject("log", log);
		
		return mv;
	}
	
	@RequestMapping(value={"/integral/exchange_youmi","/ax/xa"},method=RequestMethod.POST)
	public ModelAndView exchangeYouMi(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix+"exchange.json.ftl");
		
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		
		//就不用在去制造token啦 
		ClientAuth auth = getClientAuth(request);
		String token = auth.getSign();
		
		int exchangeNum = ServletRequestUtils.getIntParameter(request, "exchange_num", 0);
		int source = Constants.INTEGAL_SOURCE_YOUMI;
		exchangeNum = this.userThridPartIntegralService.convert(exchangeNum);
		
		int clientType=clientInfo.isIos()?Constants.platform_ios:Constants.platform_android;
		//不是万普就统一为积分
		if(source != Constants.INTEGAL_SOURCE_WANPU){
			source = Constants.INTEGAL_SOURCE_YOUMI;
		}
		UserExchangeIntegralLog log = userExchangeIntegralLogService.exchange(u.getId(), exchangeNum, token, source, clientInfo.getDid(),clientType);
		
		mv.addObject("ret_code", log.getCode());
		mv.addObject("ret_message", log.getRemark());
		
		mv.addObject("log", log);
		
		return mv;
	}
	
	@RequestMapping(value={"/integral/get_integer_income","/ay/ya"})
	public ModelAndView getIntegerIncome(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix+"integer_details.json.ftl");
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		int clientType = clientInfo.isIos()?Constants.platform_ios:Constants.platform_android;
		int source = ServletRequestUtils.getIntParameter(request, "source", 0);
		if(source < 0||source > 6){
		}else{
			//不是万普就统一为积分
			if(source != Constants.INTEGAL_SOURCE_WANPU){
				source = Constants.INTEGAL_SOURCE_YOUMI;
			}
			UserIntegalIncome income = userIntegalIncomeService.getIntegalIncomeBySource(u.getId(), source, clientType);
			if(income != null){
				mv.addObject("balance", income.getBalance());
				mv.addObject("exchange", income.getExchange());
				mv.addObject("income", income.getIncome());
				mv.addObject("estate", income.getEstate());
			}
		}
		return mv;
	}
}
