package com.cyhd.service.util;

import com.cyhd.common.util.Base64;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * 请求签名的工具类
 * @author hy
 *
 */
public class RequestSignUtil {

	private final static Logger logger = LoggerFactory.getLogger(RequestSignUtil.class);
	/**
	 * 根据请求数据生成排序的请求字符串
	 * @param params
	 * @return
	 */
	public static String getSortedRequestString(Map<String, String> params){
		StringBuilder sb = new StringBuilder();
		TreeMap<String, String> treeMap = new TreeMap<String,String>(params);
		for(Entry<String, String> entry : treeMap.entrySet()){
			sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
		}
		
		//删除末尾的&
		if(sb.length() > 0){
			sb.setLength(sb.length() - 1);
		}
		
		return sb.toString();
	}
	
	/**
	 * 对请求字符串做RSA加密
	 * @param requestStr
	 * @param key
	 * @return
	 */
	public static String signRequestUsingRSA(String requestStr, String key){
		String result = Base64.encode(RSAUtil.encrypt(key, requestStr.getBytes()));

		logger.info("signed request:{}, key:{}, sign:{}", requestStr, key, result);

		return result;
	}
	
	/**
	 * 对请求数据做RSA加密
	 * @param params
	 * @param key
	 * @return
	 */
	public static String signRequestUsingRSA(Map<String, String> params, String key){
        String reqStr = getSortedRequestString(params);
		return signRequestUsingRSA(reqStr, key);
	}

	public static String getSortedRequestString(Map<String, String> params,
			String separ) {
		StringBuilder sb = new StringBuilder();
		TreeMap<String, String> treeMap = new TreeMap<String,String>(params);
		for(Entry<String, String> entry : treeMap.entrySet()){
			sb.append(entry.getKey()).append('=').append(entry.getValue()).append(separ==null?"":separ);
		}
		//删除末尾的
		if(sb.length() > 0 && StringUtils.isNotBlank(separ)){
			sb.deleteCharAt(sb.lastIndexOf(separ));
		}
		return sb.toString();
	}
	/***
	 * 
	 * @param params
	 * @param separ 
	 * @param connectKeyValue key 和value之间的链接符
	 * @return
	 */
	public static String getSortedRequestString(Map<String, String> params,
			String separ,String connectKeyValue) {
		if(connectKeyValue==null){
			connectKeyValue = "";
		}
		StringBuilder sb = new StringBuilder();
		TreeMap<String, String> treeMap = new TreeMap<String,String>(params);
		for(Entry<String, String> entry : treeMap.entrySet()){
			sb.append(entry.getKey()).append(connectKeyValue).append(entry.getValue()).append(separ==null?"":separ);
		}
		//删除末尾的
		if(sb.length() > 0 && StringUtils.isNotBlank(separ)){
			sb.deleteCharAt(sb.lastIndexOf(separ));
		}
		return sb.toString();
	} 
}
