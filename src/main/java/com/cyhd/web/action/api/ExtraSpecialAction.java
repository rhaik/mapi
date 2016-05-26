package com.cyhd.web.action.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserSystemMessage;
import com.cyhd.service.impl.UserIntegalIncomeService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientAuth;
import com.cyhd.web.common.ClientInfo;

@Controller
@RequestMapping("/api/v1/")
public class ExtraSpecialAction extends BaseAction{

	@Resource
	private UserIntegalIncomeService userIntegalIncomeService;
	
	@Resource
	private UserService userService;
	
	protected static Logger logger = LoggerFactory.getLogger("extraSpecial");
	
	@RequestMapping(value={"/hy/bfjd/extra"},produces={"text/json; charset=UTF-8"})
	@ResponseBody
	public String reissueIntegtal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ClientInfo clientInfo = getClientInfo(request);
		
		User operator = getUser(request);
		StringBuilder sb = new StringBuilder(200);
		String ip = RequestUtil.getIpAddr(request);
		sb.append("ip:").append(ip);
		sb.append(",query:").append(request.getQueryString());
		
		if(operator == null || operator.getId() != 1 ){
			sb.append(":操作者id不对");
			logger.error(sb.toString());
			return "operator is error";
		}
		if(clientInfo.getIdfa() == null || !clientInfo.getIdfa().equals(operator.getIdfa())){
			sb.append(":操作者id不对");
			logger.error(sb.toString());
			return "operator is error";
		}
		
		int userIdenfy = ServletRequestUtils.getIntParameter(request, "userId", 0);
		String reason = request.getParameter("reason");
		int amount = ServletRequestUtils.getIntParameter(request, "amount", 0);
		
		if(userIdenfy < 10000000
				||StringUtils.isBlank(reason)
				||amount < 0
				||amount > 10000){
			sb.append(",参数不对");
			logger.error(sb.toString());
			return "parameters is error";
		}
		User user = userService.getUserByIdentifyId(userIdenfy);
		if(user == null){
			sb.append(",没有找到目标用户");
			logger.error(sb.toString());
			return "not found user";
		}
		if(userIntegalIncomeService.reissueIntegal(user, amount, reason,UserSystemMessage.PUSH_CLIENT_TYPE_ALL)){
			sb.append(",操作成功");
			logger.error(sb.toString());
			return "OK";
		}else{
			sb.append(",操作失败但没有出现异常");
			logger.error(sb.toString());
			return "operator fail,but not appear ecxeption";
		}
	}
}
