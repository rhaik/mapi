package com.cyhd.common.util;

import java.net.URLDecoder;
import java.util.*;

public class Helper {
	
	public static int inetAddr(String ip)
	{
		if((ip==null) || (ip.length()==0)) return 0;
		String[] ips = ip.split("\\.");
		if(ips.length != 4) return 0;
		
		return Integer.parseInt(ips[0]) << 24
			  | Integer.parseInt(ips[1]) << 16
			  | Integer.parseInt(ips[2]) << 8
			  | Integer.parseInt(ips[3]);
	}	

	public static String inetIp(int address)
	{
		return String.format("%s.%s.%s.%s", 
				((address>>24)&0xff),
				((address>>16)&0xff),
				((address>>8)&0xff),
				(address&0xff)
				);
	}
	
	public static String format(String src, int size)
	{		
		if(src == null) return null;
		if(size <  0) return src;
		if(size == 0) return null;
		if(src.length()*2 <= size) return src;
		
		int c = 0;
		int i;
		for(i=0; i<src.length(); i++)
		{
			if(src.charAt(i)>=128) c+=2;
			else c+=1;
			
			if(c >= size) break;
			
		}

		if(i == src.length()) return src;

		return src.substring(0, i);
	}
	
	public static String truncate(String src, int size)
	{
		if(src == null) return null;
		if(size <  0) return src;
		if(size == 0) return null;		
		if(src.length()*2 <= size) return src;
		
		double c = 0;
		int i;
		char ch;
		for(i=0; i<src.length(); i++)
		{
			ch = src.charAt(i);
			if(ch>=128) c+=2;
			else if(('A'<=ch) && (ch<='Z')) c+=1.5;
			else c+=1;
			
			if(c >= size) break;
			
		}

		if(i == src.length()) return src;

		return src.substring(0, i) + "...";
	}
	
	public static String join(String[] input, char separator)
    {
        if (input == null || input.length == 0)
            return null;

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < input.length; i++)
        {
            if (i > 0)
                buffer.append(separator);

            buffer.append(input[i]);
        }

        return buffer.toString();
    }
	
	public static String join(List<Integer> input, char separator) {
		
		if (input == null || input.size() == 0)
			return null;
		
		String[] words = new String[input.size()];
		for (int i = 0; i < words.length; i++) {
			words[i] = String.valueOf(input.get(i));
		}
		
		return join(words, separator);
	}
	
	public static String trimAll(String src)
	{
		if(src == null || src.length() == 0) return src;
		return src.replaceAll("[ \t\r\n]+", "");
	}
	
	public static String encode(String src, CharSequence meta) { return encode(src, meta, '&');	 }
	public static String encode(String src, CharSequence meta, char enc)
	{
		if(src == null || meta == null || src.length() == 0) return src;
		
		Set<Character> metaSet = new HashSet<Character>();
		for (int i=0; i<meta.length(); i++) metaSet.add(meta.charAt(i));
		metaSet.add(enc);
		
		StringBuffer buf = new StringBuffer();
		for (char c : src.toCharArray()) {
			if(metaSet.contains(c)) buf.append(enc).append(c);
			else buf.append(c);
		}
		return buf.toString();
	}
	
	public static String decode(String src) { return decode(src, '&'); }
	public static String decode(String src, char dec)
	{
		if(src == null || src.length() == 0) return src;
		
		StringBuffer buf = new StringBuffer();
		boolean encounter = false;
		for (char c : src.toCharArray()) {
			if (encounter) {
				buf.append(c);
				encounter = false;
			} else if (c == dec) {
				encounter = true;
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}
	
	public static boolean isToday(Date day)
	{
		if(day == null) return false;
		Date toDay = new Date();
		return (toDay.getYear()==day.getYear()) && (toDay.getMonth() == day.getMonth()) && (toDay.getDate()==day.getDate());
	}
	public static int timeBetween(Date beginTime, Date endTime)
	{
		long now = System.currentTimeMillis();
		int in = -1;

		if((beginTime == null) || now >= beginTime.getTime())
		{
			if((endTime == null) || now <= endTime.getTime()) in = 0;
			else in = 1;
		}
		
		return in;
	}
	
	public static String appendUrlParam(String url, String name, int value)
	{
		if(name==null || name.length()==0 || value<=0) return url;
		return new StringBuffer(url).append(url.contains("?") ? '&' : '?').append(name).append('=').append(value).toString();
	}
	public static String appendUrlParam(String url, String name, String value)
	{
		if(name==null || name.length()==0 || value==null || value.length()==0) return url;
		return new StringBuffer(url).append(url.contains("?") ? '&' : '?').append(name).append('=').append(value).toString();
	}
	public static String appendUrlParam(String url, String name, Object value)
	{
		if(value==null) return url;
		return appendUrlParam(url, name, value.toString());
	}
	public static String appendUrlParams(String url, Object[] names, Object[] values)
	{
		if(names==null || names.length==0 || values==null || values.length==0 || names.length!=values.length) return url;
		StringBuffer buf = new StringBuffer(url);
		char sp = url.contains("?") ? '&' : '?';
		for(int i=0; i<names.length; i++)
		{
			Object name = names[i];
			Object value = values[i];
			if(name==null || name.toString().length()==0 || value==null || value.toString().length()==0) continue;
			buf.append(sp).append(name.toString()).append('=').append(value.toString());
			sp = '&';
		}
		return buf.toString();
	}
	public static String appendUrlParams(String url, Map parameterMap, String[] nameFilter)
	{
		if ((null == parameterMap) || 0 == parameterMap.size()) return url;
		StringBuffer buf = new StringBuffer(url);
		char sp = url.contains("?") ? '&' : '?';
		for (Object o: parameterMap.keySet())
		{			
			String name = o.toString();
			if (null==name || 0==name.length()) continue;
			boolean needFilter = false;
			for (int i = 0; i < nameFilter.length; i++)
			{
				if (name.equalsIgnoreCase(nameFilter[i]))
					needFilter = true;
			}
			if (needFilter)	continue;
			String[] values = ((String[])parameterMap.get(o));
			if (null==values || 0==values.length) continue;
			for (int i=0; i < values.length; i++)
				buf.append(sp).append(name).append('=').append(values[i]);
			sp = '&';
		}		
		return buf.toString();
	}
	
	public static int[] shuffle(int length) {
		  int[] card = new int[length];     
		  int[] result = new int[length];  
		  for (int i = 0; i < card.length; i++)
			  card[i] = i;
		  Random rand = new Random(System.currentTimeMillis());
		  int rndIndex = 0;
		  int remain = card.length;
		  for (int i = 0; i < card.length; i++) {
			  rndIndex = Math.abs(rand.nextInt() % remain);
			  result[i] = card[rndIndex];
			  card[rndIndex] = card[remain - 1];
			  remain--;
		  }
		  return result;
	}
	
	public static int[] shuffle(int length,int count) {
		  int[] card = new int[length];      //�洢δϴ����
		  int[] result = new int[length];     //�洢ϴ�����
		  for (int i = 0; i < card.length; i++)
			  card[i] = i;
		  Random rand = new Random(System.currentTimeMillis());
		  int rndIndex = 0;
		  int remain = card.length;
		  if(card.length <= count) 
			  count=card.length;
		  for (int i = 0; i < count; i++) {
			  rndIndex = Math.abs(rand.nextInt() % remain);
			  result[i] = card[rndIndex];
			  card[rndIndex] = card[remain - 1];
			  remain--;
		  }
		  return result;
	}
	
	private final static int BYTEMASK=0xff;
	public static int bulidState8(int... ints){
		int ret = 0;
		if(null==ints) return ret;
		
		int len = Math.min(ints.length, 4);
		for (int i = 0; i < len; i++) {
			ret |= (ints[i]<<(8*i));
		}
		return ret;
	}
	public static List<Integer> parseState8(int states){
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 4; i++) {
			int state = states & BYTEMASK;
			states >>= 8;
			if(0==state)
				continue;
			list.add(state);
		}
		return list;
	}
	
	/**
	 * 将url参数转换成map
	 * @param param aa=11&bb=22&cc=33
	 * @return
	 */
	public static Map getUrlParams(String param) {
		Map<String, String> map = new HashMap<String, String>();
		if(param == null || param.length() == 0)
			return map;
		String[] params = param.split("&");
		for (int i = 0; i < params.length; i++) {
			String[] p = params[i].split("=");
			if (p.length == 2) {
				map.put(p[0], p[1]);
			}
		}
		return map;
	}
	
	public static Map getEncodedUrlParams(String param) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		if(param == null || param.length() == 0)
			return map;
		String[] params = param.split("&");
		for (int i = 0; i < params.length; i++) {
			String[] p = params[i].split("=");
			if (p.length == 2) {
				map.put(p[0], URLDecoder.decode(p[1], "utf-8"));
			}
		}
		return map;
	}

	/**
	 * 将map转换成url
	 * @param map
	 * @return
	 */
	public static String getUrlParamsByMap(Map<String, Object> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append("&");
		}
		String s = sb.toString();
		if (s.endsWith("&")) {
			s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
		}
		return s;
	}

}
