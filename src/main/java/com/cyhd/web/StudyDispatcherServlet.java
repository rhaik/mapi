package com.cyhd.web;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.service.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import com.cyhd.service.monitor.ServiceMonitor;
import com.cyhd.service.util.AppContext;
import com.cyhd.web.common.ClientAuth;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.common.util.ApiUtil;
import com.cyhd.web.exception.CommonException;

public class StudyDispatcherServlet extends DispatcherServlet {
	
	private static final long serialVersionUID = 129284928390283903L;
	
	private static final Logger REQUESTLOG = LoggerFactory.getLogger("apirequest");
	
	private ServiceMonitor monitor;
	private static final String businessName = "api_request";

	@Override
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long startTime = System.currentTimeMillis();
		String url = request.getRequestURI();
		String pathUrl = url.replace("/api/", "");
		ServiceMonitor m = getMonitorService();
		try {
			//response.setHeader("api", pathUrl);
			super.doDispatch(request, response);
			if(m != null){
				m.reportSucc(businessName, pathUrl, 1);
			}
		} catch (Exception e) {
			if(REQUESTLOG.isErrorEnabled()){
				if(e instanceof CommonException && ((CommonException)e).getErrorCode() == 6 )
					REQUESTLOG.error("request error." + getDetailErrorMsg(request,e), e);
				else 
					REQUESTLOG.error("request error." + getDetailErrorMsg(request,e), e);
			}
			if(m != null){
				m.reportErr(businessName, pathUrl, 1);
			}

			//api或者ajax请求，则返回json格式数据
			if (url.startsWith("/api/") || RequestUtil.isAjax(request)) {
				String result= ApiUtil.getErrorResponse(request, e);
				response.setContentType("text/json; charset=utf-8");
				response.getWriter().write(result);
				response.getWriter().flush();
			}else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		}finally{
			long endTime = System.currentTimeMillis();
	        int consumeTime = (int)(endTime - startTime);   //消耗的时间  
	        if(m != null)
	        	m.reportTotalTime(businessName, pathUrl, consumeTime);
	        if(consumeTime > 200) {//此处认为处理时间超过300毫秒的请求为慢请求  
	        	REQUESTLOG.error(String.format("%s consume %d millis", url, consumeTime)); 
	        	if(m != null)
	        		m.reportTimeout(businessName, pathUrl, 1);
	        }else{
	        	//REQUESTLOG.info(String.format("%s consume %d millis", url, consumeTime)); 
	        }
		}
	}
	
	private ServiceMonitor getMonitorService(){
		if(monitor == null){
			monitor = (ServiceMonitor)AppContext.getBean(ServiceMonitor.class);
		}
		return monitor;
	}
	
	protected String getDetailErrorMsg(HttpServletRequest request, Exception ex) {
		StringBuffer sb = new StringBuffer();
		String url = request.getRequestURI();
		if (url != null) {
			sb.append(" url=").append(url);
		}
		String queryStr = request.getQueryString();
		if (queryStr != null) {
			sb.append("?").append(queryStr);
		}
		if(ex instanceof CommonException) {
			CommonException ttyc = (CommonException) ex ;
			sb.append(",errorCode=" + ttyc.getErrorCode() + ",errorMessage=" + ttyc.getMessage()) ;
		}
		if(request.getMethod().equals("POST"))
			sb.append("," + requestParam(request)) ;
		try {
			ClientInfo info = (ClientInfo) request.getAttribute("clientInfo");
			sb.append(", clientinfo=" + info);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ClientAuth auth = (ClientAuth) request.getAttribute("clientAuth");
		sb.append(", clientauth=" + auth);
		
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static String requestParam(HttpServletRequest request) {
		
		String result = "{" ;
		Enumeration<String> enumeration = request.getParameterNames() ;
		boolean flag1 = false ;
		while (enumeration.hasMoreElements()) {
			String k =  enumeration.nextElement();
			String []v =  request.getParameterValues(k) ;
			
			result += k + ":[" ;
			boolean flag2 = false ;
			for(String e : v) {
				result += e + "," ;
				flag2 = true ;
			}
			if(flag2)
				result = result.substring(0, result.length()-1) ;
			result += "]," ; 
			flag1 = true ;
		}
		if(flag1)
			result = result.substring(0, result.length()-1) ;
		result += "}" ;
		return result ;
	}
}
