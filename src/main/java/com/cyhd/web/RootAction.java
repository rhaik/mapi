package com.cyhd.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.impl.DeviceService;
import com.cyhd.service.push.PushService;
import com.cyhd.web.common.BaseAction;

import groovy.lang.GroovyShell;

@Controller
public class RootAction extends BaseAction {

	@Resource
	DeviceService deviceService;
	
	@Resource
	PushService pushService;
	
	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/")
	@ResponseBody
	public String hello(HttpServletRequest request, HttpServletResponse response) {
		return "hello study!";
	}

	public final static GroovyShell groovy = new GroovyShell();
	private static final String defaultImport = "import com.cyhd.service.constants.*;\n" + "import com.cyhd.service.impl.*;\n" + "import com.cyhd.service.jobs.*;\n"
			+ "import com.cyhd.service.dao.*;\n" + "import com.cyhd.service.push.*;\n" + "import com.cyhd.service.util.*;\n\n";

	
	private static final String[] roots = {"free"};
	@RequestMapping(value = "/ms", method = RequestMethod.GET)
	public String groovyGet(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception{
		
		return groovy(request, response, modelMap);
	}

	@RequestMapping(value = "/ms", method = RequestMethod.POST)
	public String groovy(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception{
		response.setContentType("text/html; charset=utf-8");
		String msid = request.getParameter("msid");
		logger.info("msid=" + msid);
		if(!isRoot(msid)){
			logger.error("msid is not root!");
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return "404";
		}
		modelMap.put("msid", msid);
		String groovyInput = (String) request.getParameter("groovyInput");
		
		Runtime r = Runtime.getRuntime();
		long m = 1024 * 1024, f = r.freeMemory(), t = r.totalMemory();
		modelMap.put("freeMemory", f / m);
		modelMap.put("totalMemory", t / m);
		if (groovyInput != null && groovyInput.length() > 0) {
			String groovyOutput = "";
			try {
				if (groovy != null) {
					groovyOutput = groovy.evaluate(defaultImport + groovyInput).toString();
				} else {
					groovyOutput = "Groovy is null!";
				}
			} catch (Throwable e) {
				groovyOutput = e.getMessage();
			} finally {
				modelMap.put("groovyOutput", groovyOutput);
				modelMap.put("groovyInput", groovyInput);
			}
		}
		logger.info("admin:" + msid +",sumbit:" + groovyInput);
		return "manager.html.ftl";
	}
	
	@RequestMapping(value = "/ms/push")
	public String push(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception{
		String host = request.getHeader("host");
		if(!host.equalsIgnoreCase("slave.api.erbicun.cn")){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return "404";
		}
		response.setContentType("text/html; charset=utf-8");
		String msid = request.getParameter("msid");
		logger.info("msid=" + msid);
		if(!isRoot(msid)){
			logger.error("msid is not root!");
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return "404";
		}
		modelMap.put("msid", msid);
		int clientType = ServletRequestUtils.getIntParameter(request, "clientType", 2);
		int appId = ServletRequestUtils.getIntParameter(request, "appId", 0);
		int start = ServletRequestUtils.getIntParameter(request, "start", 0);
		int size = ServletRequestUtils.getIntParameter(request, "size", 10);
		
		String content = ServletRequestUtils.getStringParameter(request, "content", "");
		
		modelMap.put("clientType", clientType);
		modelMap.put("appId", appId);
		modelMap.put("start", start);
		modelMap.put("size", size);
		modelMap.put("content", content);
		
		if(clientType > 2 || clientType < 0 || StringUtils.isEmpty(content) || size <= 0){
			modelMap.put("error", "参数不合法");
			return "manager2.html.ftl";
		}
		
		List<Long> userIds = new ArrayList<Long>();
		if(appId <= 0){
			logger.info("ms push getDeviceUsers({}, {}, {})", clientType, start, size);
			userIds = deviceService.getDeviceUsers(clientType, start, size);
		}else if(clientType == Constants.platform_ios){
			logger.info("ms push getIosNotAcceptUsers({}, {}, {})", appId, start, size);
			userIds = deviceService.getIosNotAcceptUsers(appId, start, size);
		}
		
		modelMap.put("pushSize", userIds.size());
		for(long userId : userIds){
			pushService.notifyUserSystemPrompt(userId, content, clientType);
		}
		
		logger.info("admin:" + msid +",sumbit:" + request.getQueryString());
		return "manager2.html.ftl";
	}

	private boolean isRoot(String msid) {
		if(msid == null)
			return false;
		for(String root : roots){
			if(root.equalsIgnoreCase(msid)){
				return true;
			}
		}
		return false;
	}

}
