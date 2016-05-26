package com.cyhd.web.common.util;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import com.cyhd.web.exception.CommonException;

public class ApiUtil {

	public static String getErrorResponse(HttpServletRequest request, Exception e){
		
		CommonException te =null;
		if(!(e instanceof CommonException)){
			 te = new CommonException(e);
		}else{
			te = (CommonException)e;
		}
		JSONObject errorJson = new JSONObject();
		errorJson.put("requestUrl", request.getRequestURL().toString());
		errorJson.put("code", te.getErrorCode());
		errorJson.put("message", te.getMessage());
		return errorJson.toString(4);
	}
	
}
