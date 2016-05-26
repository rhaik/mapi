package com.cyhd.service.util;

import com.cyhd.service.constants.Constants;


public class VersionUtil {
	
	public static int getDeviceType(String clientType){
		if(clientType == null){
			return 0;
		}
		if(clientType.toLowerCase().contains("android")){
			return Constants.platform_android;
		}
		return Constants.platform_ios;
	}
	
	// 获得客户端版本值
	public static int getVersionCode(String v) {
		try {
			String[] s = v.split("\\.");
			int length = s.length;
			if (length > 4) {
				return 0;
			}
			int total = 0;
			int base = 100 * 100 * 100;
			for (int i = 0; i < length; i++) {
				total += Integer.parseInt(s[i].trim()) * base;
				base = base / 100;
			}
			return total;
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 获取iOS客户端版本号
	 * @param os clientInfo中的os
	 * @return
	 */
	public static double getIOSVersion(String os){
		boolean isHasDot = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = os.length(); i < len; ++ i){
			char c = os.charAt(i);
			if (Character.isDigit(c)){
				sb.append(c);
			}else if (c == '.' && !isHasDot){ //只添加一个点，后面的点直接忽略
				sb.append(c);
				isHasDot = true;
			}

		}

		double version = 0;
		if (sb.length() > 0) {
			try {
				version = Double.parseDouble(sb.toString());
			} catch (Exception exp) {

			}
		}
		return version;
	}
	/***
	 * 达到目标版本号的要求么
	 * @param currentVersion
	 * @param targetVsesion
	 * @return true 是
	 */
	public static boolean isRequiredTargetVsersion(String currentVersion,String targetVsesion){
		return VersionUtil.getVersionCode(currentVersion) >= VersionUtil.getVersionCode(targetVsesion);
	}
}
