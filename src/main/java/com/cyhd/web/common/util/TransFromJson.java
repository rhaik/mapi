package com.cyhd.web.common.util;

import java.util.List;

import com.cyhd.common.util.JsonUtils;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class TransFromJson implements TemplateMethodModel {

	@Override
	public Object exec(List arg0) throws TemplateModelException {
		String str = (String)arg0.get(0);
		
        return JsonUtils.jsonQuote(str) ; 
	}
}
