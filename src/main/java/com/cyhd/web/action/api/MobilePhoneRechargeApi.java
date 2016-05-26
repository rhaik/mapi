package com.cyhd.web.action.api;


import java.util.Date;
import java.util.concurrent.locks.Lock;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.Validator;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.po.Recharge;
import com.cyhd.service.dao.po.RechargeDenomination;
import com.cyhd.service.dao.po.Ref;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.impl.RechargeService;
import com.cyhd.service.impl.UserIncomeService;
import com.cyhd.service.util.CacheUtil;
import com.cyhd.service.util.Recharfe99douInterfaceUtil;
import com.cyhd.service.util.UserLock;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/api/v1/recharge/")
public class MobilePhoneRechargeApi extends BaseAction {
	@Resource
	RechargeService rechargeService;
	
	@Resource(name = CacheUtil.MEMCACHED_RESOURCE)
	private CacheDao memcachedCacheDao;
	
	private static final String prefix = "/api/v1/recharge/";
	 
	 
	/**
	 * 手机归属地查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/queryarea")
	public ModelAndView queryarea(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "queryarea.json.ftl"); 
		
		String mobile = request.getParameter("mobile");
		String areachannel="" ;
		int channel = 1;
		if(Validator.isMobile(mobile)) {
			Recharfe99douInterfaceUtil dou = new Recharfe99douInterfaceUtil();
			Ref<String> msg = new Ref<String>();
			String rs = dou.MobileQuery(mobile, msg);
			if(rs!="") {
				String[] temp = rs.split(";");
				String channelName = temp[0].split(":")[1];
				String area = temp[1].split(":")[1];
				if(channelName.indexOf("移动") >=0) {
					channel = 1;
				} else if(channelName.indexOf("联通")>=0) {
					channel = 2;
				} else if(channelName.indexOf("电信")>=0) {
					channel = 3;
				}
				areachannel = area+channelName;
			}  
		}  
		mv.addObject("recharge", rechargeService.getRechargeDenominationList(channel));
		mv.addObject("areachannel", areachannel.isEmpty() ? "" : "("+areachannel+")");
		fillStatus(mv);
		return mv;
	}
	@Resource
	UserIncomeService userIncomeService;
	/**
	 * 提交充值订单
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/save", method= RequestMethod.POST)
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "save.json.ftl"); 
		String code = request.getParameter("code");
		
		User u = getUser(request);
		String serviceCode = (String) memcachedCacheDao.get(CacheUtil.getCaptchasKey(u.getId()));
		if(!code.equalsIgnoreCase(serviceCode)) {
			logger.error("User validate input code {} memcache {} error!", code, serviceCode);
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "验证码不正确!");
		}
		
		ClientInfo clientInfo = getClientInfo(request);
		String mobile = request.getParameter("mobile");
		if(!Validator.isMobile(mobile)) {
			logger.error("User recharge mobile {} error!", mobile);
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "手机号不正确!");
		}
		int type = ServletRequestUtils.getIntParameter(request, "type");
		if(type == 0) {
			logger.error("User recharge type {} error!", type);
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "请选择充值面额!");
		}
		RechargeDenomination rd = rechargeService.getRechargeDenomination(type);
		if(rd == null) {
			logger.error("User recharge type {} not exist!", type);
			throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "充值的面额不存在");
		}
		Lock lock = UserLock.getUserLock(u.getId());
		lock.tryLock();
		try{
			UserIncome userIncome = userIncomeService.getUserIncome(u.getId());
			if(userIncome.getBalance() < rd.getPay_amount()) {
				logger.error("Lack of user balance {}, recharge amount {}!", userIncome.getBalance(), rd.getPay_amount());
				throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "您的余额不足");
			}
			String mobile_area = ServletRequestUtils.getStringParameter(request, "mobile_area");
			
			long order_sn = rechargeService.createOrderSn();
			Recharge re = new Recharge();
			re.setChannel(rd.getChannel());
			re.setCreatetime(new Date());
			re.setMobilephone(mobile);
			re.setOrder_sn(order_sn);
			re.setPay_amount(rd.getPay_amount());
			re.setQuantity(1);
			re.setRecharge_denomination_id(rd.getId());
			re.setStatus(1);
			re.setUser_id(u.getId());
			re.setValue(rd.getValue());
			re.setMobile_area(mobile_area);
			
			if (rechargeService.recharge(re)) {
				 //调用第三方充值接口
				 Recharfe99douInterfaceUtil dou = new Recharfe99douInterfaceUtil();
				 Ref<String> msg = new Ref<String>();
				 int rs = dou.Huafei(re.getOrder_sn()+"", re.getMobilephone(), re.getChannelName(), re.getQuantity(), re.getValue()+"", clientInfo.getIpAddress(), 5, msg);
				 int status = 1;
				 if(rs == 0) {	//成功
					 status = Recharge.ORDER_STATUS_RECHARGE;
					 mv.addObject("ret_code", 0);
					 mv.addObject("ret_message", "提交充值订单成功");
				 } else { //充值失败 
					 status = Recharge.ORDER_STATUS_FAIL;
					 //退回余额
					 userIncomeService.returnRechargeUserBalance(re.getUser_id(), re.getPay_amount());
					 mv.addObject("ret_code", 1);
					 mv.addObject("ret_message", msg.toString());
				 }
				 rechargeService.updateStatusById(re.getId(), status, msg.toString());
			} else {
				mv.addObject("ret_code", 1);
				mv.addObject("ret_message", "提交充值订单失败，请稍后再试!");
			}
		}finally{
			lock.unlock();
		}
		return mv;
	}
}
