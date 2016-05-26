package com.cyhd.service.util;

import com.cyhd.common.util.KeywordMatcher;
import com.cyhd.common.util.KeywordMatcher.Match;

public class DirtyWordUtil{
	
	private static KeywordMatcher matcher = new KeywordMatcher(true);
    static {
        matcher.addKeyword("核心", 1);
        matcher.addKeyword("核 心", 1);
        matcher.addKeyword("he xin", 1);
        matcher.addKeyword("hexin", 1);
        matcher.addKeyword("51", 2);
        matcher.addKeyword("滴答", 2);
        matcher.addKeyword("嘀嗒", 2);
        matcher.addKeyword("VV", 2);
        matcher.addKeyword("爱拼车", 2);
        matcher.addKeyword("Uber", 2);
        matcher.addKeyword("优步", 2);
        matcher.addKeyword("微微", 2);
    }
	
	public static String getDirty(String content){
		Match match = matcher.nextMatch(content, 0);
		if(match != null){
			return content.substring(match.beginIndex, match.endIndex);
		}
		return null;
	}
	
	public static String filterDirty(String input){
		Match match = null;
		// Fast path
		if ((match = matcher.nextMatch(input, 0)) == null)
			return input;
				
		// Do my job
		StringBuilder buf = new StringBuilder();
		int position = 0;
		while ((match = matcher.nextMatch(input, position)) != null) {
			if (match.beginIndex > position) 
				buf.append(input.substring(position, match.beginIndex));
			position = match.endIndex;
		}
		if (position < input.length()) 
			buf.append(input.substring(position));
		return buf.toString();
	}
	
	private static KeywordMatcher matcher_im = new KeywordMatcher(true);
	static {
		matcher_im.addKeyword("北京站");
		matcher_im.addKeyword("北京北站");
		matcher_im.addKeyword("北京南站");
		matcher_im.addKeyword("北京西站");
		matcher_im.addKeyword("火车站");
		matcher_im.addKeyword("机场");
		matcher_im.addKeyword("汽车站");
		matcher_im.addKeyword("杭州东站");
		matcher_im.addKeyword("杭州城站");
		matcher_im.addKeyword("杭州站");
	}
	
	public static String getImWarn(String content){
		if(content == null)
			return null;
		Match match = matcher_im.nextMatch(content, 0);
		if(match != null){
			return content.substring(match.beginIndex, match.endIndex);
		}
		return null;
	}
	
	
	public static void main(String[] args){
		//System.err.println(service.getDirty("啊啊核心阿道夫静安寺快递费"));
		
		System.err.println(DirtyWordUtil.filterDirty("爱拼车正常么51房价可打算，嘀嗒，加法"));
		System.err.println(DirtyWordUtil.filterDirty(null));
	}
	
}
