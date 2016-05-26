package com.cyhd.web.action.api;

import java.util.Date;
import java.util.concurrent.locks.Lock;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.common.util.*;
import com.cyhd.service.util.GlobalConfig;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.richtext.HtmlUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserEnchashment;
import com.cyhd.service.dao.po.UserEnchashmentAccount;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.impl.AntiCheatService;
import com.cyhd.service.impl.UserEnchashmentService;
import com.cyhd.service.impl.UserIncomeService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.service.util.UserLock;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/api/v1")
public class EnchashmentAction extends BaseAction {

	@Resource
	UserService userService;
	
	@Resource
	UserEnchashmentService userEnchashmentService;

	@Resource
	private AntiCheatService antiCheatService;
	
	private static final String prefix = "/api/v1/";
	
	
	/**
	 * 提现
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@Resource
	UserIncomeService userIncomeService;
	
	@RequestMapping(value = {"/enchashment/accounts","/ap/pa"}, method = RequestMethod.POST)
	public ModelAndView accounts(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "enchashment/accounts.json.ftl");
		
		User user = getUser(request);
		
		UserEnchashmentAccount ue = userEnchashmentService.getUserEnchashmentAccount(user.getId());
		
		mv.addObject("ue", ue);
		
		fillStatus(mv);
		return mv;
	}
	
	@RequestMapping(value = {"/enchashment/save"},method=RequestMethod.POST)
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "common/save.json.ftl");
		
		User u = getUser(request);
		
		Lock lock = UserLock.getUserLock(u.getId());
		lock.tryLock();
		try{

			int type = ServletRequestUtils.getIntParameter(request, "type", UserEnchashment.ACCOUNT_TYPE_WX);
			int amount = ServletRequestUtils.getIntParameter(request, "amount", 0);

			type = type == UserEnchashment.ACCOUNT_TYPE_WX ? UserEnchashment.ACCOUNT_TYPE_WX : UserEnchashment.ACCOUNT_TYPE_ALIPAY;

			if (amount <= 0){
				throw new CommonException(ErrorCode.ERROR_CODE_WALLET_PARAMETER_WRONG, "提现金额错误");
			}

			//检查提现金额是否是设定的金额
			UserEnchashmentService.EnchashmentStage enchashmentStage = UserEnchashmentService.EnchashmentStage.getStageByAmount(amount);
			if (enchashmentStage == null){ //未找到对应的提现阶段
				throw new CommonException(ErrorCode.ERROR_CODE_WALLET_PARAMETER_WRONG, "提现金额错误");
			}

			//提现类型支不支持该金额
			if (!enchashmentStage.isValidOfType(type)){
				throw new CommonException(ErrorCode.ERROR_CODE_WALLET_PARAMETER_WRONG, "提现金额错误");
			}

			UserIncome income = userIncomeService.getUserIncome(u.getId());

			int balance = income.getBalance();
			if(balance < 1000) {
				throw new CommonException(ErrorCode.ERROR_CODE_WALLET_NOT_ENOUTH,"账户余额不足！");
			}

			//余额不足
			if (amount > balance){
				throw new CommonException(ErrorCode.ERROR_CODE_WALLET_PARAMETER_WRONG, "提现金额错误");
			}

			//当前正在提现，保证一次只会有一笔在提现中
			if (income.getEncashing() > 0){
				throw new CommonException(ErrorCode.ERROR_CODE_WALLET_PARAMETER_WRONG, "您有未处理完成的提现");
			}

			//检查今天是不是已经提现成功过
			UserEnchashment lastEnchashment = userEnchashmentService.getUserLatestEnchashment(u.getId());
			if (lastEnchashment != null && lastEnchashment.getStatus() == UserEnchashment.STATUS_SUCCESS
					&& DateUtil.isSameDay(GenerateDateUtil.getCurrentDate(), lastEnchashment.getMention_time())){
				throw new CommonException(ErrorCode.ERROR_CODE_SAME_DAY, "您今天已经提过现");
			}

			UserEnchashmentAccount userAccount = userEnchashmentService.getUserEnchashmentAccount(u.getId());

			//已被封禁
			if (userAccount.isMasked()){
				throw new CommonException(ErrorCode.ERROR_CODE_USER_MASKED, "您的账号异常，暂时无法提现，请联系客服处理");
			}
			
			String accountName = "",account="";
			if(type == UserEnchashment.ACCOUNT_TYPE_WX) {
				if(userAccount.getWx_bank_name().isEmpty()) {
					throw new CommonException(ErrorCode.ERROR_CODE_LACA_WX_PARAMETER, "缺少微信提现账号");
				}

				if (!userEnchashmentService.isInWeixinEnchashTime()){
					throw new CommonException(ErrorCode.ERROR_CODE_LACA_WX_PARAMETER, "不在微信提现的时间范围内");
				}

				if (!userEnchashmentService.isUnderWeixinLimit()){
					throw new CommonException(ErrorCode.ERROR_CODE_LACA_WX_PARAMETER, "今日微信提现总额已达上限，请使用支付宝提现");
				}

				String openId = userService.getUserOpenID(GlobalConfig.weixin_pay_appid, u.getId());
				if (StringUtil.isBlank(openId)){
					throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "您未关注“秒赚大钱”公众号，请关注后再提现到微信钱包");
				}
				//微信提现，account为openID
				accountName = userAccount.getWx_bank_name();
				account = openId;

			} else if(type == UserEnchashment.ACCOUNT_TYPE_ALIPAY) {
				if(userAccount.getAlipay_account().isEmpty()||userAccount.getAlipay_name().isEmpty() ) {
					throw new CommonException(ErrorCode.ERROR_CODE_LACA_ALIPAY_PARAMETER, "缺少支付宝提现账号");
				}
				account = userAccount.getAlipay_account();
				accountName = userAccount.getAlipay_name();
				//
				if("jjww9009@126.com".equals(account)){
					logger.error("jjww9009@126.com支付宝账号出现:账号:{},user:{},ip:{}",account,u,RequestUtil.getIpAddr(request));
					throw new CommonException(ErrorCode.ERROR_CODE_LACA_ALIPAY_PARAMETER, "亲,财务回家结婚,一个月后才能回来!!!");
				}else if(Constants.blacklist.contains(account)){
					throw new CommonException(ErrorCode.ERROR_CODE_LACA_ALIPAY_PARAMETER, "绑定的支付宝账号过多");
				}else{
					int count = userEnchashmentService.countAccountByAlipay_account(account);
					if(count > 2){
						throw new CommonException(ErrorCode.ERROR_CODE_LACA_ALIPAY_PARAMETER, "绑定的支付宝账号过多");
					}
				}
				
				UserEnchashment enchashment = this.userEnchashmentService.getUserEnchashmentTopByAccount(account);
				if(enchashment != null){
					if(DateUtil.isSameDay(GenerateDateUtil.getCurrentDate(), enchashment.getMention_time())){
						throw new CommonException(ErrorCode.ERROR_CODE_SAME_DAY, "今天该支付宝已提现");
					}
				}
			}
			
			UserEnchashment userEnchashment = new UserEnchashment();
			userEnchashment.setUser_id(u.getId());
			userEnchashment.setAmount(amount);
			userEnchashment.setAccount(account);
			userEnchashment.setAccount_name(accountName);
			userEnchashment.setMention_time(new Date());
			userEnchashment.setStatus(UserEnchashment.STATUS_INIT);
			userEnchashment.setType(type);
			userEnchashment.setIp(RequestUtil.getIpAddr(request));


			if (userAccount.isAutoPassed()){
				userEnchashment.setStatus(UserEnchashment.STATUS_AUDIT_SUCCESS);

				logger.info("auto pass user enchashment, user:{}, account:{}, amount:{}", u.getId(), userAccount.getAlipay_account(), userEnchashment.getAmount());
			}else if (antiCheatService.isAutoPassEnchashment(request, u, userEnchashment)){
				//审核通过修改状态
				userEnchashment.setStatus(UserEnchashment.STATUS_AUDIT_SUCCESS);

				//设置自动审核标志位
				userEnchashmentService.setAutoCheckPassed(userAccount.getUser_id());

				logger.info("auto pass user enchashment and set account passed, user:{}, account:{}, amount:{}", u.getId(), userAccount.getAlipay_account(), userEnchashment.getAmount());
			}

			if (userIncomeService.addUserEncashingAmount(u.getId(), amount) && userEnchashmentService.save(userEnchashment) ) {
				if (userEnchashment.getStatus() == UserEnchashment.STATUS_INIT){
				 	//未能自动审核通过，评估账号
					antiCheatService.evaluateUserEnchash(request, u, userAccount, userEnchashment);
				}
				fillStatus(mv);
			} else {
				fillErrorStatus(mv, ErrorCode.ERROR_CODE_UNKNOWN);
			}
		}finally{
			lock.unlock();
		}
		return mv;
	}
	/**
	 * 账号设置
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = {"/enchashment/setting","/ao/oa"},method=RequestMethod.POST)
	public ModelAndView saveAccount(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "common/save.json.ftl");
		int type = ServletRequestUtils.getIntParameter(request, "type", 1);
		
		User u = getUser(request);
		UserEnchashmentAccount account = userEnchashmentService.getUserEnchashmentAccount(u.getId());
		if(account == null){
			account = new UserEnchashmentAccount();
			account.setUser_id(u.getId());
			account.setCreatetime(new Date());
		}
		account.setUpdatetime(new Date());
		if(type == UserEnchashment.ACCOUNT_TYPE_WX) {	//微信
			String wx_bank_name = HtmlUtil.toPlainText(ServletRequestUtils.getStringParameter(request, "wx_bank_name"));
			if (StringUtil.isBlank(wx_bank_name)){
				throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "输入参数不合法！");
			}
			if(Validator.isContainDigitalLetter(wx_bank_name)) {
				throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "请输入您微信钱包绑定的银行卡的姓名！");
			}

			String openId = userService.getUserOpenID(GlobalConfig.weixin_pay_appid, u.getId());
			if (StringUtil.isBlank(openId)){
				throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "您未关注“秒赚大钱”公众号，请关注后再设置微信提现账号");
			}

			account.setWx_bank_name(wx_bank_name);
		} else {	//支付宝
			String alipay_name = HtmlUtil.toPlainText(ServletRequestUtils.getStringParameter(request, "alipay_name"));
			String alipay_account = HtmlUtil.toPlainText(ServletRequestUtils.getStringParameter(request, "alipay_account"));
			if (StringUtils.isEmpty(alipay_name)
					|| StringUtils.isEmpty(alipay_account)) {
				throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "输入参数不合法！");
			}

			if(Validator.isContainDigitalLetter(alipay_name)) {
				throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "请输入您的支付宝实名认证姓名！");
			}

			if(alipay_account.indexOf("@") >=0) {
				if(!Validator.isEmail(alipay_account)) {
					throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "输入参数不合法！");
				}
			} else {
				if(!Validator.isMobile(alipay_account)) {
					throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "输入参数不合法！");
				}
				//判断手机号是不是别的用户绑定过的手机号
				User anotherUser = userService.getUserByMobile(alipay_account);
				if (anotherUser != null && anotherUser.getId() != u.getId()){ //不是当前用户绑定的手机号
					logger.warn("用户的提现账号为其他用户绑定的手机号，user:{}, mobile:{}, another user:{}", u.getId(), alipay_account, anotherUser.getId());
					throw new CommonException(ErrorCode.ERROR_CODE_PARAMETER, "该支付宝账号已被别人绑定");
				}
			}

			account.setAlipay_account(alipay_account);
			account.setAlipay_name(alipay_name);
			//检查不通过
			boolean checkFailed = false;
			if ("jjww9009@126.com".equals(account)) {
				logger.error("绑定支付宝达五次以上的支付宝账号出现:账号:{},user:{},ip:{}",alipay_account,u,RequestUtil.getIpAddr(request));
				//TODO 先让你绑定成功，看看你有多少用户
			}else if(Constants.blacklist.contains(alipay_account)) {
				logger.error("绑定支付宝达五次以上的支付宝账号出现:账号:{},user:{},ip:{}", alipay_account, u, RequestUtil.getIpAddr(request));
				fillErrorStatus(mv, ErrorCode.ERROR_CODE_USER_TYPE);
				checkFailed = true;
			}else if(userEnchashmentService.isBlacklist(alipay_account, alipay_name)){ //检查要绑定的支付宝账号是不是已被封禁
				logger.error("设置支付宝账号时,绑定一个已封掉的支付宝账号,账号:{},user:{},ip:{}", alipay_account, u, RequestUtil.getIpAddr(request));
				//封用户
				userService.setMaskedUser(u.getId(), true, "绑定一个已封禁的支付宝账号");
				fillErrorStatus(mv, ErrorCode.ERROR_CODE_UNKNOWN);
				checkFailed = true;
			}else{
				UserEnchashmentAccount accountTmp = userEnchashmentService.getAccountByAlipay_account(alipay_account);
				//不是同一个用户
				if(accountTmp != null && accountTmp.getUser_id() != u.getId()){
					logger.error("设置支付宝账号时,已存在的支付宝账号,账号:{},user:{},ip:{}",alipay_account,u,RequestUtil.getIpAddr(request));
					fillErrorStatus(mv, ErrorCode.ERROR_CODE_UNKNOWN, "该支付宝账号已被别人绑定，请重新输入");
					checkFailed = true;
				}
			}

			if(checkFailed){
				return mv;
			}
		}
		
		
		if (userEnchashmentService.addOrUpdateEnchashmentAccount(account)){
			antiCheatService.evaluateEnchashAccount(request, u, account);
			fillStatus(mv);
		} else {
			fillErrorStatus(mv, ErrorCode.ERROR_CODE_UNKNOWN);
		}
		return mv;
	}
	/**
	 * 提现列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/enchashment/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "enchashment/list.json.ftl");
		User u = getUser(request);
		
		int page = ServletRequestUtils.getIntParameter(request, "page", 0);
		int start = page * defaultPageSize;
		
		mv.addObject("userEnchashments", userEnchashmentService.getUserEnchashmentList(u.getId(), start, defaultPageSize));
		return mv; 
	}
}
