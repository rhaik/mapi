package com.cyhd.web.action.wx;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.web.common.ClientAuth;
import com.cyhd.web.common.util.ClientAuthUtil;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.AesCryptUtil;
import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MoneyUtils;
import com.cyhd.common.util.Validator;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserFriendService;
import com.cyhd.service.impl.UserIncomeService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.service.util.UserAgentUtil.UserAgent;
import com.cyhd.web.common.BaseAction;

@Controller
@RequestMapping(value = {"/weixin/api", "/ios/wxapi"})
public class WeiXinAPIAction extends BaseAction{

	@Resource
	private UserIncomeService userIncomeService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserFriendService userFriendService;
	
	/**
	 * 邀请收入列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/inviteincome", method = RequestMethod.GET)
	public ModelAndView inviteincome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/api/v1/user/inviteincome.json.ftl");
		
		int friend_level = ServletRequestUtils.getIntParameter(request, "friend_level", 1);
		friend_level = friend_level == 1 ? 1 : 2;
		User u = getUser(request);
		//u.setId(85);
		int userId = u.getId();
		
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		int start = pageIndex * defaultPageSize;
		mv.addObject("userIncome", userIncomeService.getUserFriendInviteIncome(userId, friend_level, start, defaultPageSize));
		
		mv.addObject("title", "邀请收入记录");
		return mv; 
	}

	@RequestMapping(value = "/inviteincomelogs")
	public ModelAndView inviteIncomeDetail(HttpServletRequest request){
		return new ModelAndView("forward:/api/v1/user/inviteincome/detail");
	}

	@RequestMapping(value = "/task/logs")
	public ModelAndView getTaskLogs(HttpServletRequest request){
		return new ModelAndView("forward:/api/v1/task/logs");
	}

	@RequestMapping(value="get_mobile_code",method=RequestMethod.POST)
	public ModelAndView getMobileCode(HttpServletRequest request, HttpServletResponse response) throws Exception{
		return new ModelAndView("forward:/api/v1/user/get_mobile_code");
	}
	
	@RequestMapping(value="bind_mobile",method=RequestMethod.POST)
	public ModelAndView bindMobile(HttpServletRequest request, HttpServletResponse response) throws Exception{
		return new ModelAndView("forward:/api/v1/user/bind_mobile");
	}
	
	@RequestMapping(value="get_qrCode.ajax",method=RequestMethod.POST)
	@ResponseBody
	public String getQRCode(HttpServletRequest request, HttpServletResponse response) throws Exception{
		int code = ServletRequestUtils.getIntParameter(request, "uid", 0);
		
		if(code < 10000000 || code > 100000000){
			return "";
		}
		//判断有没有用户登录 没有就有异常
		User u2 = getUser(request);
		
		if(u2.getUser_identity() != code){
			return "";
		}
		
		String qr =  HttpUtil.get(Constants.CREATE_QR_CODE_URl+code, null);
		userService.clearUserCache(u2.getId());
		return qr;
	}
	
	@RequestMapping("/new_user_bind_forward")
	@ResponseBody
	public String newUserBindUserByForward(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		User user = getUser(request);
		
		JSONObject json = new JSONObject();
		String mobile = request.getParameter("mobile");
		String sign = request.getParameter("d");
		boolean flag = true;
		int code=0;
		String message = "";
		
		//TODO check code and Mobile
		if(StringUtils.isBlank(sign)
				|| user.getOpenid().equals(AesCryptUtil.decryptWithCaseInsensitive(sign, Constants.ARTICLE_AES_PASSWORD)) == false 
				||Validator.isMobile(mobile)==false){
			code = -1;
			message = "不合法的手机号";
			flag = false;
		}
		if(flag){
			//判断是不是微信  不是window
			UserAgent ua = UserAgentUtil.getUserAgent(request);
			if(ua.isWeixin()==false||ua.isWindows()){
				code = -1;
				message = "来源不对";
				flag = false;
			}
		}
		if(flag){
			if(user.isRewardNewUserComplete()){
				if(user.getMobile()==null){
					userService.bindMobile(user.getId(), mobile);
				}
				code= 0;
				message= "您已经获得过现金奖励！";
				flag = false;
			}
		}
		if(flag){
			if(userService.isNewUser(user) == false){
				code = -1;
				message="已经是是我们的老客户啦";
				flag = false;
			}
		}
		
		if(flag){
			if(user.getMobile() != null){
				code = -4;
				message = "您已经绑定过手机号";
				flag= false;
			}
		}
		if(flag){
			User tmp = userService.getUserByMobile(mobile);
			if(tmp != null){
				code = -2;
				message = "该手机号已绑定";
			}else{
				if(userService.bindMobile(user.getId(), mobile)){
					//暂时不在这里给新用户发奖
					//int amount = userService.executeRewardNewUser(user, user.getDevicetype());
					int amount = 0;
					json.put("amount", MoneyUtils.fen2yuanS(amount));
				}else{
					code = -2;
					message = "绑定电话失败";
				}
			}
		}
		json.put("code", code);
		json.put("message", message);
		return json.toString();
	}

	/**
	 * 转发任务记录
	 * @return
	 */
	@RequestMapping("/article/logs")
	public String getArticleTaskLog(){
		return "forward:/api/v1/article/logs";
	}


	/**
	 * 用户的邀请记录
	 * @return
	 */
	@RequestMapping("/user/invites")
	public String getInvitesLog() {
		return "forward:/api/v1/user/invites";
	}


	/**
	 * 用户的提现列表
	 * @return
	 */
	@RequestMapping("/enchashment/list")
	public String getEnchashList() {
		return "forward:/api/v1/enchashment/list";
	}

	/**
	 * 用户的金币、积分记录日志
	 * @return
	 */
	@RequestMapping("/user/thridpartlog")
	public String getThirdpartylog() {
		return "forward:/api/v1/user/thridpartlog";
	}

	/**
	 * 用户的金币、积分兑换日志
	 * @return
	 */
	@RequestMapping("/user/exchangelog")
	public String getExchangeLog() {
		return "forward:/api/v1/user/exchangelog";
	}

	/**
	 * 兑换积分
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/integral/exchange"},method=RequestMethod.POST)
	public String exchange(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ClientAuth clientAuth = ClientAuthUtil.getDefaultClientAuth();
		clientAuth.setSign("" + System.currentTimeMillis());
		request.setAttribute("clientAuth", clientAuth);
		return "forward:/api/v1/integral/exchange";
	}
}
