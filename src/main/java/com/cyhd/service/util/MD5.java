package com.cyhd.service.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * MD5的算法在RFC1321 中定义 在RFC 1321中，给出了Test suite用来检验你的实现是否正确： MD5 (\"\") =
 * d41d8cd98f00b204e9800998ecf8427e MD5 (\"a\") = 0cc175b9c0f1b6a831c399e269772661
 * MD5 (\"abc\") = 900150983cd24fb0d6963f7d28e17f72 MD5 (\"message digest\") =
 * f96b697d7cb7938d525a2f31aaf161d0 MD5 (\"abcdefghijklmnopqrstuvwxyz\") =
 * c3fcd3d76192e4007dfb496cca67e13b
 * 
 * @author haogj
 * 
 *         传入参数：一个字节数组 传出参数：字节数组的 MD5 结果字符串
 */
public class MD5 {
  private static final Log logger = LogFactory.getLog(MD5.class);
  private static final String DEFAULT_CHARACTER_ENCODING = "GBK";

  /**
   * 
   * getMD5(这里用一句话描述这个方法的作用)
   * 
   * @param source
   * @return
   * @return String
   */
  public static String getMD5(byte[] source) {
    String s = null;
    char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
        'f' };
    try {
      java.security.MessageDigest md = java.security.MessageDigest
          .getInstance("MD5");
      md.update(source);
      byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
      // 用字节表示就是 16 个字节
      char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
      // 所以表示成 16 进制需要 32 个字符
      int k = 0; // 表示转换结果中对应的字符位置
      for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
        // 转换成 16 进制字符的转换
        byte byte0 = tmp[i]; // 取第 i 个字节
        str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
        // >>> 为逻辑右移，将符号位一起右移
        str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
      }
      s = new String(str); // 换后的结果转换为字符串

    } catch (Exception e) {
      e.printStackTrace();
    }
    return s;
  }

  public static String getMD5(String source) {
    if (StringUtils.isBlank(source)) {
      return null;
    }

    byte[] bytes = null;
    try {
      bytes = source.getBytes(DEFAULT_CHARACTER_ENCODING);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    if (bytes == null) {
      return null;
    }
    String result = getMD5(bytes);
    return result;
  }
  
  

  /**
   * 意外险接口报文验签
   * 
   * @param reqDoc
   *          String 报文
   * @param signCode 意外险验签码
   * @param sign
   *          String 验签码
   * @return boolean
   */
  public static boolean verifyAccident(String reqDoc, String signCode, String sign) {
    if (StringUtils.isBlank(reqDoc) || StringUtils.isBlank(sign)) {
      return false;
    }
    try {
      String s = MD5.getMD5((signCode +reqDoc).getBytes(DEFAULT_CHARACTER_ENCODING));
      return sign.equals(s);
    } catch (UnsupportedEncodingException e) {
      logger.error("MD5::getMD5::加密失败=>得到[" + sign + "]");
      return false;
    }
  }
  
  public static void main(String[] args){
	  System.out.println(MD5.getMD5("15901063802"));
  }
}