package com.cyhd.common.util;

import net.sf.json.JSONObject;

public class JsonUtils {

	public static String toCommonString(String str){
		if(str == null)
			return "";
		int len = str.length();
		int codeLength = str.codePointCount(0, len);
		if(len == codeLength){
			return str;
		}
		//System.out.println("len="+len + ", codelen=" + codeLength);
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
			}
		}
		return result;
	}
	public static String filterMb4(String str){
		if(str == null)
			return null;
		int len = str.length();
		int codeLength = str.codePointCount(0, len);
		if(len == codeLength){
			return str;
		}
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < len; ){
			int codePoint = str.codePointAt(i);
			int charCount = Character.charCount(codePoint) ;
			if (charCount == 1) {
				sb.append(str.charAt(i));
				i++;
			}else{
				i+= charCount;
			}
		}
		return sb.toString();
	}
	
	 public static String jsonQuote(String string) {
	        if (string == null || string.length() == 0) {
	            return "\"\"";
	        }
	        char b;
	        char c = 0;
	        int i;
	        int len = string.length();
	        StringBuilder sb = new StringBuilder(len + 4);
	        String t;

	        sb.append('"');
	        for (i = 0; i < len; i += 1) {
	            b = c;
	            c = string.charAt(i);
	            switch (c) {
	            case '\\':
	            case '"':
	                sb.append('\\');
	                sb.append(c);
	                break;
	            case '/':
	                if (b == '<') {
	                    sb.append('\\');
	                }
	                sb.append(c);
	                break;
	            case '\b':
	                sb.append("\\b");
	                break;
	            case '\t':
	                sb.append("\\t");
	                break;
	            case '\n':
	                sb.append("\\n");
	                break;
	            case '\f':
	                sb.append("\\f");
	                break;
	            case '\r':
	                sb.append("\\r");
	                break;
	            default:
	                if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
	                        || (c >= '\u2000' && c < '\u2100')) {
	                    t = "000" + Integer.toHexString(c);
	                    sb.append("\\u").append(t.substring(t.length() - 4));
	                } else if(c <= 255 ){
	                	sb.append(c);
	                } else if(!isChinese(c) ){
	                	
	                }else {
	                    sb.append(c);
	                }
	            }
	        }
	        sb.append('"');
	        return sb.toString();
	    }

		/**
		 * 判断是否为中文字符
		 * @param c
		 * @return
		 */
		public static boolean isChinese(char c) {
	        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
	        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
	                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
	                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
	                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
	                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
	                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
	            return true;
	        }
	        return false;
	    }
		public static String handleBegin(String jsonData){
			if(StringUtil.isBlank(jsonData)){
				return jsonData;
			}
			jsonData = jsonData.trim();
			int i = jsonData.indexOf("{");
			if (i > 0){
				jsonData = jsonData.substring(i);
			}
			return jsonData;
		}
	 
	 
}
