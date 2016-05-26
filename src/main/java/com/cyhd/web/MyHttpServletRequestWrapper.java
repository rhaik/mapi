package com.cyhd.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

@SuppressWarnings("rawtypes")
public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper implements HttpServletRequest {

	private Map myParams = new HashMap();
	private String body = "";

	public MyHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public String getParameter(String name) {
		String s = super.getParameter(name);
		if(s == null){
			return (String)myParams.get(name);
		}
		return s;
	}

	/**
	 * The default behavior of this method is to return getParameterMap() on the
	 * wrapped request object.
	 */
	@SuppressWarnings("unchecked")
	public Map getParameterMap() {
		Map s = super.getParameterMap();
		if(s != null){
			myParams.putAll(s);
		}
		return myParams;
	}

	/**
	 * 根据参数名称获取参数值数组<br/>
	 * 目前对请求参数加密只支持单个参数值
	 * @param name
	 * @return
	 */
	public String[] getParameterValues(String name){
		String[] values = super.getParameterValues(name);
		if (values == null){
			String v = getParameter(name);

			if (v != null) {
				values = new String[]{v};
			}
		}
		return values;
	}

	public Map getMyParams() {
		return myParams;
	}

	public void setMyParams(Map myParams) {
		this.myParams = myParams;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
