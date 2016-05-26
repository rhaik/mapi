package com.cyhd.service.util;

import java.util.Random;

import com.cyhd.service.constants.Constants;

public class CommonUtils {

	public final static char[] CODESE_QUENCE = { '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'G', 'K', 'L', 'M', 'N', 'P',
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	public static String getRandomCode(int length) {
		StringBuffer codeBuffer = new StringBuffer();
		Random random = new Random();
		for (int i = 1; i <= length; i++) {
			codeBuffer.append(CODESE_QUENCE[random.nextInt(CODESE_QUENCE.length)]);
		}
		String res = codeBuffer.toString();
		return res;
	}
	
	public static boolean isTestMobile(String mobile){
		return mobile.startsWith(Constants.test_mobile_prefix);
	}
}
