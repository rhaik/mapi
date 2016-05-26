package com.cyhd.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class Validator {

	public static boolean isDigital(String str) { 
		Pattern p = null;
		Matcher m = null;
		boolean b = false; 
		p = Pattern.compile("^[0-9]+$");
		m = p.matcher(str);
		b = m.matches(); 
		return b;
	}
	
	public static boolean isMobile(String str) { 
		Pattern p = null;
		Matcher m = null;
		boolean b = false; 
		p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches(); 
		return b;
	}
	/**
     * 验证邮箱地址是否正确
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[_\\-|\\.]?)+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}

		return flag;
    }
    /**
     * 验证是否是数字和字母
     * 
     * @param str
     * @return
     */
    public static boolean isDigitalLetter(String str){
		boolean flag = false;
		try {
			Pattern regex = Pattern.compile("^[a-zA-Z@._0-9]+$");
			Matcher matcher = regex.matcher(str);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}

		return flag;
    }
	
    public static boolean isContainDigitalLetter(String str){
    	for(int i = 0; i < str.length(); i ++){
    		char c = str.charAt(i);
    		if('0'<=c && c <= '9'){
    			return true;
    		}
    		if('A' <=c && c <= 'z'){
    			return true;
    		}
    		if(c == '_' || c == '@'){
    			return true;
    		}
    	}
    	return false;
    }

	public static boolean isIPAddress(String ipaddr) {
		boolean flag = false;
		Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher m = pattern.matcher(ipaddr);
		flag = m.matches();
		return flag;
	}
	/**本机环回地址*/
	public static final long ipNumeric127 = getIPNumericByV4(getRealIpNumericsByV4("127.0.0.1"));
	/**A类私网地址起止*/
	public static final long minIpNumericByClassA = getIPNumericByV4(getRealIpNumericsByV4("10.0.0.0")); 
	public static final long maxIpNumericByClassA = getIPNumericByV4(getRealIpNumericsByV4("10.255.255.255"));
	/**B类私网地址起止*/
	public static final long minIpNumericByClassB = getIPNumericByV4(getRealIpNumericsByV4("172.16.0.0")); 
	public static final long maxIpNumericByClassB = getIPNumericByV4(getRealIpNumericsByV4("172.31.255.255"));
	/**C类私网地址起止*/
	public static final long minIpNumericByClassC = getIPNumericByV4(getRealIpNumericsByV4("192.168.0.0")); 
	public static final long maxIpNumericByClassC = getIPNumericByV4(getRealIpNumericsByV4("192.168.255.255"));
	/**组播地址起止*/
	public static final long minIpNumericByClassMulticast = getIPNumericByV4(getRealIpNumericsByV4("224.0.0.0")); 
	public static final long maxIpNumericByClassMulticast = getIPNumericByV4(getRealIpNumericsByV4("239.255.255.255"));
	
	/**
	 * 是不是公网传输的ip <br/>
	 * 1: 合法的ip<br/>
	 * 2: 非私网ip<p>
	 * A class: 10.0.0.0 ~ 10.255.255.255<br/>
       B class: 172.16.0.0 ~ 172.31.255.255 <br/>
       C class: 192.168.0.0 ~ 192.168.255.255
      <p/>
       3: 组播地址
       224.0.0.0--239.255.255.255<br/>
       4: 广播地址 
       主机地址全为1的
	 * @param ip
	 * @return  false是公网ip 
	 */
	public static boolean isRealIpByV4(String ip){
		int[] ipValues = getRealIpNumericsByV4(ip);
		if(ipValues == null){
			return false;
		}
		//首位全为0不可用
		if(ipValues[0] == 0 ){
			return false;
		}
		
		long ipValue = getIPNumericByV4(ipValues);
		//本机环回地址 或ip有问题
		if(ipValue == ipNumeric127 || ipValue < 0){
			return false;
		}
		//组播地址
		if(ipValue > minIpNumericByClassMulticast && ipValue < maxIpNumericByClassMulticast){
			return false;
		}
		//C类私网地址
		else if(ipValue > minIpNumericByClassC && ipValue < maxIpNumericByClassC){
			return false;
		}
		//B类私网地址
		else if(ipValue > minIpNumericByClassB && ipValue < maxIpNumericByClassB){
			return false;
		}
		//A类私网地址
		else if(ipValue > minIpNumericByClassA && ipValue < maxIpNumericByClassA){
			return false;
		}
		return true;
	}
	public static long getIPNumericByV4(int[] ipNumerics){
		int len = ipNumerics.length;
		long ipNumSum = 0;
		long ipValue = 0;
		for(int i = 0; i < len; i++){
			ipValue = ipNumerics[i];
			if(i == len - 1){
				ipNumSum += ipValue;
			}else{
				ipNumSum += ipValue * (2 << ((len-1-i) * 8));
			}
		}
		return ipNumSum;
	}
	/***
	 * 将ip的每个字段转化成数值
	 * @param ip
	 * @return
	 */
	public static int[] getRealIpNumericsByV4(String ip){
		if(StringUtils.isBlank(ip)){
			return null;
		}
		String[] ipValues = ip.split("\\.");
		if(ipValues.length != 4){
			return null;
		}
		int[] ipNumerics = new int[4];
		int len = ipValues.length;
		try {
			for(int i = 0; i < len; i++){
				ipNumerics[i] = Integer.parseInt(ipValues[i]);
				//每个字段的值只能在 0~255 之间 
				if(ipNumerics[i] < 0 || ipNumerics[i] > 255){
					return null;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return ipNumerics;
	}
	
	public static void testIp() {
		String ip = "";
		for(int j= 0; j< 5000;j++){
			StringBuilder sb = new StringBuilder();
			for(int i = 0 ; i < 4; i++){
				sb.append((int)(Math.random()*254)+1).append(".");
			}
			sb.setLength(sb.length() - 1);
			ip = sb.toString();
			if(isRealIpByV4(ip) == false){
				System.out.println(ip+" -> ");
			}	
		}
	}
    public static void main(String[] args){
    	String s = "zengyating520@163.com";
    	System.out.println(Validator.isEmail(s));
    	testIp();
    }
}
