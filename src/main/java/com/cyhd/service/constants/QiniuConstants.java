package com.cyhd.service.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 七牛常量
 * @author sten
 *
 */
public class QiniuConstants {

//	/**
//	 * 七牛帐号：961@qq.com的  ak、sk信息
//	 */
//	public final static String accessKey = "sxgwFe1_PvSOjfHxJxhmV5E_KifvGWSgtac0YDlP" ;
//	public final static String secretKey = "T1bIgUIt5DpxHFwbb8y5lnoMjq34ul8bexjoQlmF" ;
	
	//七牛的账号 fengbao@erbicun.cn 
	public final static String accessKey = "aZxVp0PxsPDpkEAinwZOm2nNrsnfimlj9DbeFlBc" ;
	public final static String secretKey = "IhB2GGEPturtbhWhNbUd5tuKGntneYR8mMjFDlgG" ;
	
	/**
	 * 头像信息空间
	 */
	public final static String media_bucket = "zdq-media" ;
	
	/**
	 * 日志信息空间
	 */
	//public final static String log_bucket = "ttyc-log" ;
	
	/**
	 * 私有空间列表
	 */
	public final static List<String> priviceBuckets = new ArrayList<String>() ;
//	static{
//		priviceBuckets.add(im_bucket) ;
//	}
	
	/**
	 * 空间的domain后缀
	 */
//	public final static String domain_suffix  = "7xiptf.com1.z0.glb.clouddn.com" ;
	public final static String domain_suffix  = "cdn.erbicun.cn" ;
}
