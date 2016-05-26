package com.cyhd.service.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

public class EmojUtil {
	
	private final static String emoj_prefix = "http://7xkdr1.com1.z0.glb.clouddn.com/";
	
	static HashSet<String> emojis = new HashSet<String>();
	
	static {
		try{
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("emoticons.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line = br.readLine()) != null){
				emojis.add(line.trim());
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String string2Unicode(String string) {
	    StringBuffer unicode = new StringBuffer();
	    for (int i = 0; i < string.length(); i++) {
	        // 取出每一个字符
	        char c = string.charAt(i);
	        // 转换为unicode
	        unicode.append("\\u" + Integer.toHexString(c));
	    }
	    return unicode.toString();
	}
	
	public static String toCommonString(String str){
		int len = str.length();
		int codeLength = str.codePointCount(0, len);
		if(len == codeLength){
			return str;
		}
		String result = "";
		for(int i = 0; i < len; ){
			int codePoint = str.codePointAt(i);
			//System.err.println(codePoint);
			int charCount = Character.charCount(codePoint) ;
			//System.err.println("char count == " + charCount + ", code index=" + i  + ", charindex=" + j);
			if (charCount == 1) {
				result += Character.toString(str.charAt(i));
				//System.err.println("result=" + result);
				i++;
			} else {
				i+= charCount;
				String code = Integer.toHexString(codePoint);
				if(emojis.contains(code)){
					String image = "<span style='background-image:url("+ emoj_prefix+ code +".png"
							+ ")' class='emojicon-m'></span>";
					result += image;
				}
				//System.err.println("result=" + result);
			}
		}
		return result;
	}
	public static String removeEmoj(String str){
		int len = str.length();
		int codeLength = str.codePointCount(0, len);
		if(len == codeLength){
			return str;
		}
		String result = "";
		for(int i = 0; i < len; ){
			int codePoint = str.codePointAt(i);
			//System.err.println(codePoint);
			int charCount = Character.charCount(codePoint) ;
			//System.err.println("char count == " + charCount + ", code index=" + i  + ", charindex=" + j);
			if (charCount == 1) {
				result += Character.toString(str.charAt(i));
				//System.err.println("result=" + result);
				i++;
			} else {
				i+= charCount;
//				String code = Integer.toHexString(codePoint);
//				if(emojis.contains(code)){
//					String image = "<span style='background-image:url("+ emoj_prefix+ code +".png"
//							+ ")' class='emojicon-m'></span>";
//					result += image;
//				}
				//System.err.println("result=" + result);
			}
		}
		return result;
	}
	
}
