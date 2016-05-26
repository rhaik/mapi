package com.cyhd.common.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
	
	/**
	 * copy a range of list to a new list
	 * @param src
	 * @param offset start from 0
	 * @param length the size of new list
	 * @return
	 */
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
	
	public <T> void printList(List<T> list){
		System.out.println("---begin---");  
	    for(T t : list){  
	        System.out.println(t);  
	    }  
	    System.out.println("---end---"); 
	}
}
