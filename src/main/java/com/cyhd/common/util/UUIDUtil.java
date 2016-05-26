package com.cyhd.common.util;

import java.util.UUID;

/**
 * uuid generate util
 *
 */
public class UUIDUtil {

	public static String getCommonUUID() {
		
		String uuid = UUID.randomUUID().toString() ;
		return uuid.replaceAll("-", "") ;
	}
}
