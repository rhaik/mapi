package com.cyhd.common.util;


public class ObjectUtil {


	public static final String getStringDefaultValue(String src,String def){
		if(src==null){
			return def;
		}
		return src;
	}
	
	public static final String getStringDefaultValue(String src){
		return getStringDefaultValue(src,"");
	}
}
