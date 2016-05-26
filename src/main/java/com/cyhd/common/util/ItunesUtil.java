package com.cyhd.common.util;

import org.apache.commons.lang.StringUtils;

public class ItunesUtil {

	public static String parseItunesURL(String itunes) {
		String appID = null;
	        if (StringUtils.isNumeric(itunes)){
	            appID = itunes;
	        }else if ((itunes.startsWith("http://") || itunes.startsWith("https://"))){
	            int pos = -1, offset = 0;
	            if (itunes.contains("/id")){ //https://itunes.apple.com/cn/app/id858355695
	                pos = itunes.lastIndexOf("/id");
	                offset = 3;
	            }else if (itunes.contains("id=")){ //https://itunes.apple.com/WebObjects/MZStore.woa/wa/viewSoftware?id=858355695
	                pos = itunes.lastIndexOf("id=");
	                offset = 3;
	            }

	            if (pos > 0){
	                StringBuilder sb = new StringBuilder();
	                for (int i = pos + offset; i < itunes.length(); ++ i){
	                    char c = itunes.charAt(i);
	                    if (Character.isDigit(c)) {
	                        sb.append(c);
	                    }else {
	                        break;
	                    }
	                }
	                appID = sb.toString();
	            }
	        }
	        return appID;
	}

}
