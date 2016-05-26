package com.cyhd.web.action.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.service.dao.po.UserEnchashment;
import com.cyhd.service.impl.SmsService;
import com.cyhd.service.impl.UserDrawService;
import com.cyhd.service.impl.UserEnchashmentService;
import com.cyhd.service.impl.UserIntegalIncomeService;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserFriendService;
import com.cyhd.service.impl.UserService;
import com.cyhd.web.common.BaseAction;

@Controller
@RequestMapping("/www/inner")
public class InnerAction extends BaseAction {

	@Resource
	UserService userService;
	
	@Resource
	private UserFriendService userFriendService;

	@Resource
	UserEnchashmentService userEnchashmentService;

	@Resource
	UserIntegalIncomeService integalIncomeService;

	@Resource
	private UserDrawService userDrawService;
	
	@Resource
	private SmsService smsService;
	
	@RequestMapping(value = {"/cache"})
	@ResponseBody
	public String cache(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String type = ServletRequestUtils.getRequiredStringParameter(request, "type");
		String value= ServletRequestUtils.getRequiredStringParameter(request, "value");

		logger.info("Inner Action, type={}, value={}, ip={}", type, value, RequestUtil.getIpAddr(request));
		if(type.equalsIgnoreCase("user")){
			try{
				int id = Integer.parseInt(value);
				userService.clearUserCache(id);
			}catch(Exception e){
				logger.error(this.getClass().getSimpleName() + " error",e);
			}
		}
		return "0";
		
	}
	
	@RequestMapping(value = {"/invite"}, method = RequestMethod.POST)
	@ResponseBody
	public String invite(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String type = ServletRequestUtils.getRequiredStringParameter(request, "type");
		if (!"Invite".equals(type)){
			return "-1";
		}

		int invitorId = ServletRequestUtils.getIntParameter(request, "inid", 0);
		int userId = ServletRequestUtils.getIntParameter(request, "uid", 0);
		
		logger.info("Inner Action, invite, invitor={}, userid={}, ip={}", invitorId, userId, RequestUtil.getIpAddr(request));
		try{
			if(invitorId == userId)
				return "1";
			int realInvitor = userFriendService.getInvitor(userId);
			if(realInvitor > 0){
				return "1";
			}
			User invitor = userService.getUserByIdentifyId(invitorId);
			User u = userService.getUserByIdentifyId(userId);
			
			userFriendService.onAddUserFriend(invitor, u, null);
		}catch(Exception e){
			logger.error(this.getClass().getSimpleName() + " error",e);
		}
		
		return "0";
		
	}

	/**
	 * 提现完成后，给用户返回金币
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"/enchashSuccess"}, method = RequestMethod.POST)
	@ResponseBody
	public String onEnchashSuccess(HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		String type = ServletRequestUtils.getRequiredStringParameter(request, "type");
		if (!"Enchash".equals(type)){
			return "-1";
		}

		int code = -1;
		int eid = ServletRequestUtils.getIntParameter(request, "enchId", 0);

		logger.info("inner action enchashSuccess, enchId:{}, ip:{}", eid, RequestUtil.getIpAddr(request));

		if (eid > 0){
			UserEnchashment userEnchashment = userEnchashmentService.getById(eid);

			if (userEnchashment != null
					&& userEnchashment.isRewarded() == false){
				UserEnchashmentService.EnchashmentStage enchashmentStage = UserEnchashmentService.EnchashmentStage.getStageByAmount(userEnchashment.getAmount());
				User user = userService.getUserById(userEnchashment.getUser_id());
				if (enchashmentStage != null && user != null){
					int rewardAmount = enchashmentStage.getCoins();

					//不管是否有奖励的金币，都设置为已奖励状态，必须金币数大于0才返还
					if(userEnchashmentService.setRewarded(userEnchashment.getId()) && rewardAmount > 0) {
						integalIncomeService.addRewardedIntegral(userEnchashment.getUser_id(), rewardAmount, "提现返还金币", user.getDevicetype(), String.valueOf(userEnchashment.getId()));
						
						code = 0;
						logger.info("onEnchashSuccess, reward user:{}, coins:{}, enchashment:{}", userEnchashment.getUser_id(), rewardAmount, userEnchashment.getId());
					}
					//增加抽奖机会
					//userDrawService.addUserDraw(user.getId(), "您的徒弟"+user.getName()+"提现成功");
				}else {
					logger.warn("onEnchashSuccess, enchashmentStage is null or user is null, enchId:{}", eid);
				}
			}else {
				logger.warn("onEnchashSuccess, user userEnchashment is null or status error: {}", userEnchashment);
			}
		}
		return "" + code;
	}

	/*****
	 * 发货的时候给发短信
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = {"/send_goods"}, method = RequestMethod.POST)
	@ResponseBody
	public String sendGoodsWithSMS(HttpServletRequest request, HttpServletResponse response){
		String type=request.getParameter("type");
		
		String ip = RequestUtil.getIpAddr(request);
		String query = RequestUtil.getQueryString(request);
		StringBuilder sb = new StringBuilder(640);
		sb.append("发货发短信接口调用,ip:").append(ip);
		sb.append(",query:").append(query);
		
		int code = -1;
		boolean next = true;
		if("Delivery".equals(type) == false){
			sb.append(",type parameters is error");
			next = false;
		}
		if(next){
			//得到参数
			String mobile = request.getParameter("mobile");
			String content = request.getParameter("content");
			
			if(StringUtil.isBlank(mobile) 
					|| StringUtil.isBlank(content)
					){
				sb.append(",the core parameters is empty");
				code = -2;
				next = false;
			}
			
			if(next){
				if(smsService.sendSendGoodsPrompt(mobile, content)){
					sb.append("send prompt is success");
					code = 0;
				}else{
					sb.append("send prompt is fail !");
				}
			}
		}
		logger.info(sb.toString());
		return String.valueOf(code);
	}

	
}
