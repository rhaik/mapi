/*
 * Copyright (c) 2012-2022 mayi.com
 * All rights reserved.
 * 
 */
package com.cyhd.common.util;

import java.io.UnsupportedEncodingException;

/**
 * MD5加密工具类 
 * @version 1.0
 */
public class MD5Util {

	public static String getMD5(byte[] source){
		String s = null;
        try {
           java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");                
           md.update(source);
           byte[] byteDigest = md.digest();
          s = String.valueOf(toHexChar(byteDigest));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return s;
	}
      /**
       * 对字符串进行md5转换
       * @param strSource
       * @return
       */
      public static String getMD5(String strSource){
          byte[] source = null;
          try {
              source = strSource.getBytes("utf-8");
          } catch (UnsupportedEncodingException e1) {
              source = strSource.getBytes();
          }
          return getMD5(source);
      }
      
      /**
       * 二行制转字符串
       * @param b
       * @return
       */
      public static String toHexChar(byte[] b){
          String hs = "";
          String stmp = "";
          for (int n = 0; n < b.length; n++) {
              stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
              if (stmp.length() == 1){
                  hs = hs + "0" + stmp;
              }else{
                  hs = hs + stmp;
              }
          }
          return  hs;
     }
      
     public static void main(String[] args) {
    	 //e22adbe8c9451a416f68884de3be0feb
    	 //fdd2934eec37e7239c47f10255f3cf36
         System.out.println(MD5Util.getMD5("ac_token"));
     }
      
      
}

