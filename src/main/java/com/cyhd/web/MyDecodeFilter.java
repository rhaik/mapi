package com.cyhd.web;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.cyhd.common.util.StringUtil;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.cyhd.common.util.Helper;
import com.cyhd.web.common.ClientAuth;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.common.util.AESCoder;
import com.cyhd.web.common.util.ClientAuthUtil;
import com.cyhd.web.common.util.ClientInfoUtil;
import com.cyhd.web.exception.CommonException;

public class MyDecodeFilter implements Filter {

	private static final Logger REQUESTLOG = LoggerFactory.getLogger("apirequest");
	
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
     
		String uri = request.getRequestURI();
		uri = URLDecoder.decode(uri, "utf-8");

		ClientAuth auth = null;

		//如果client info加密过，则对client info做解密
		String ci = request.getHeader("clientInfo");
		if (StringUtil.isNotBlank(ci) && ci.charAt(0) != '{' ){ //加密过
			try {
				auth = ClientAuthUtil.getClientAuth(request);
				String newClientInfo = AESCoder.decrypt(ci, "nci" + auth.getSign().substring(0, 13));
				request.setAttribute("decyptedClientInfo", newClientInfo);

				REQUESTLOG.info("decoded client info:{}", newClientInfo);
			} catch (Exception e) {
				REQUESTLOG.error("client info decode error, ci:{}, error:{}", ci, e);
			}
		}

        if(request.getMethod().equalsIgnoreCase("post") && uri.startsWith("/api/")){
			ClientInfo clientInfo = null;
			try {
				auth = ClientAuthUtil.getClientAuth(request);
				clientInfo = ClientInfoUtil.getClientInfo(request);
			} catch (CommonException e) {
				REQUESTLOG.error("get client auth or client info error, error:{}", e);
				throw new ServletException("参数错误");
			}

			String contents = request.getParameter("_data");
			REQUESTLOG.info("enter mydecode filter: _data={}", contents);
    		if(!StringUtils.isEmpty(contents)){
    			MyHttpServletRequestWrapper myhttp = new MyHttpServletRequestWrapper(request);
    			try{
    				contents = URLDecoder.decode(contents, "utf-8"); 
    				String cKey = null;
    				if(clientInfo.isIos()){
    					cKey = auth.getCode().substring(0,16);
    				}else{
    					cKey = "mzdq"+auth.getSign().substring(auth.getSign().length()-12);
    				}
    				String s = AESCoder.decrypt(contents, cKey);
    				if(s != null){
    					//s = URLDecoder.decode(s, "utf-8");
    					Map mp = Helper.getEncodedUrlParams(s);
    					if(mp != null)
    						myhttp.setMyParams(mp);
    				}
    				myhttp.setBody(s);
    				REQUESTLOG.info("enter mydecode filter, code={}, decoded={}", auth.getCode(), s);       
    			}catch(Exception e){
    				REQUESTLOG.error("decode error!",e);
    			}
    			chain.doFilter(myhttp, res);
    		}else{
    			chain.doFilter(req, res);
    		}
        }else{
        	chain.doFilter(req, res);
        }
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}
	
	public static void main(String[] args) throws Exception{
		String s1 = "fFVXaMlJY2E7oxm3A8aAqURS29U8hKLy1SJTTSutqgcrXVgTs%2BvAAffgVgdGAnHz%2BW3%2F%2FTx8oLc"+
"jr8sKg%2F01KP4EUGFjMhUQbvNy6X5sSiIs%2BqVPVjyFfLuQINwVA%2Bo9SbEnv8PYgnxgy%2Fj113lgPQn5CM%2BJoi2CN58aoDIcEfSrVWIGHkkxdoeNpeYXJdbV660twL5lr3AYyz1r4zmwL7v9kR6C46G%2BTz7vD"
+"TDx6%2Bj0kNCIJUk18gc4a8rPcoUjH5vdai9BwnngSUGZDZPDiG1DfEkW4N5YKT1Viuh8BnrtE%2FH9BIvV7s%2Bjk6Sx%2F0wat1gkYG4Q6AGD2QGUc3Qj6wvvwQ1ZO5lSNt0KSqByFh06kBXEaITlwUwRrfpNEWzR60v"
+"zKh0x%2Ba7SBKZjh7wMyWExuTPBcYdgLiBKAPxaP41Y1auXa946mRh%2FhajZ3YxThA232QgSJ3DsxOrFmGQP%2BmCMuqHu5WiswTHnd9SOXRmQ3XPxMcEZggkicVkmG0QhOpGcqZtVYqR32cZk9Ng9Fxtasdr%2BbPRR4"
+"Xzzpn2eUxxXLP5UInTzoMakTc6LNEQFactIqaX8ZwCHIl5vUS69D7qtx0eKx3BiN%2FFXgJZ7%2FMOPVS%2FxeAIjC23SiiORyuClMr7tHn38EH6R%2FXZsb0qz87vrg52hqyvuB%2Bh5y0kIHUU%3D";
		
		String contents = URLDecoder.decode(s1, "utf-8"); 
		System.err.println(contents);
		String cKey = "06e8eb528be1af04";
		String s = AESCoder.decrypt(contents, cKey);
		System.out.println(s);
		if(s != null){
			s = URLDecoder.decode(s, "utf-8");
		}
		System.err.println(s);
		JSONObject json = JSONObject.fromObject(s);
		System.err.println(json.toString());
	}

}
