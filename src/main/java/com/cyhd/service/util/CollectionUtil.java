package com.cyhd.service.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class CollectionUtil {

	
	public static <T> List<List<T>> split(List<T> list, int max){
		List<List<T>> all = new ArrayList<List<T>>();
		if(list == null)
			return all;
		if(list.size() <= max){
			all.add(list);
		}else{
			int start = 0;
			int size = max;
			while(start < list.size()){
				all.add(CollectionUtil.rangeCopy(list, start, size));
				start += size;
			}
		}
		return all;
	}
	
	/**
	 * 将多个id转换为形如：  1,2,12,14。逗号分隔
	 * @param <T>
	 * @param list
	 * @return
	 */
	public static <T> String fromListToString(List<T> list){
		String result = "";
		if(list != null && list.size() > 0){
			for(T i : list){
				result += i;
				if(i != list.get(list.size() - 1)){
					result += ",";
				}
			}
		}
		return result;
	}
	
	/**
	 * 将  1,2,12,14 转换为list
	 * @param str
	 * @return
	 */
	public static List<Integer> fromStringToList(String str){
		List<Integer> list = new ArrayList<Integer>();
		if( str != null && !str.equals("")){
			String[] sArray = str.split(",");
			for(String s : sArray){
				if(!StringUtils.isEmpty(s)){
					int i = safeParseInt(s);
					if(i != default_value)
						list.add(i);
				}
			}
		}
		return list;
	}
	
	/**
	 * 将多个id转换为形如：  1,2,12,14。逗号分隔
	 * @param list
	 * @return
	 */
	public  static String fromLongListToString(List<Long> list){
		String result = "";
		if(list != null && list.size() > 0){
			for(int i = 0;i <list.size(); i++){
				result += list.get(i);
				if(i != list.size() - 1){
					result += ",";
				}
			}
		}
		return result;
	}
	
	/**
	 * 将  1,2,12,14 转换为list
	 * @param str
	 * @return
	 */
	public  static List<Long> fromStringToLongList(String str){
		List<Long> list = new ArrayList<Long>();
		if( str != null && !str.equals("")){
			String[] sArray = str.split(",");
			for(String s : sArray){
				if(!StringUtils.isEmpty(s)){
					long i = safeParseLong(s);
					if(i != default_value)
						list.add(i);
				}
			}
		}
		return list;
	}
	
	public  static List<String> fromStringToStringList(String str){
		List<String> list = new ArrayList<String>();
		if( str != null && !str.equals("")){
			String[] sArray = str.split(",");
			for(String s : sArray){
				if(!StringUtils.isEmpty(s))
					list.add(s);
			}
		}
		return list;
	}
	
	public  static String fromStringListToString(List<String> list){
		String result = "";
		if(list.size() > 0){
			for(String i : list){
				result += i;
				if(i != list.get(list.size() - 1)){
					result += ",";
				}
			}
		}
		return result;
	}
	
	private static final int default_value = -219939611;
	private static int safeParseInt(String s){
		try{
			return Integer.parseInt(s);
		}catch(Exception e){
			return default_value;
		}
	}
	
	private static long safeParseLong(String s){
		try{
			return Long.parseLong(s);
		}catch(Exception e){
			return default_value;
		}
	}
	
	public static <T> List<T> rangeCopy(List<T> src, int offset, int length){
		List<T> newList = new ArrayList<T>(length);
		if(src == null || src.size() < offset+1){
			return newList;
		}
		for(int i = offset; i < src.size() && i < offset+length; i++){
			newList.add(src.get(i));
		}
		return newList;
	}
	
	public static Long[] collectionToArray(Collection<String> collection){
		if(collection == null || collection.isEmpty()){
			return null;
		}
		Long[] arry = new Long[collection.size()];
		int index = 0;
		for(String value:collection){
			arry[index++] = Long.valueOf(value);
		}
		return arry;
	}
	
	public static void main(String[] args){
		List<Long> ls = new ArrayList<Long>();
		ls.add(1L);
		ls.add(2L);
		
		int start = 0;
		int size = 3;
		while(start < ls.size()){
			System.out.println(rangeCopy(ls, start, size));
			start += size;
		}
	}
	public static <T> String join(Collection<T> colles,String deli){
		StringBuilder sb = new StringBuilder(320);
		deli = (deli == null)?"":deli;
		for(T t:colles){
			sb.append(t).append(deli);
		}
		//删除最后一个分隔符
		if (sb.length() > 0&&StringUtils.isNotBlank(deli)){
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}
}
