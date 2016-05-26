package com.cyhd.web.common.util;

import java.net.URLDecoder;
import java.security.Key;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyhd.common.util.Base64;


/*******************************************************************************
 * AES加解密算法
 */

public class AESCoder {
	
	private static final Logger logger = LoggerFactory.getLogger(AESCoder.class);

	private static final String KEY_ALGORITHM = "AES";
    
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    
    //private static final String cKey = "a1b2c3d4e5f67890";
    
    // 加密
    public static String encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
        	logger.error("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
        	logger.error("Key长度不是16位");
            return null;
        }
        
        byte[] raw = sKey.getBytes();
        Key skeySpec = toKey(raw);
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

        return Base64.encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }
    
    private static Key toKey(byte[] key){  
        //生成密钥  
        return new SecretKeySpec(key, KEY_ALGORITHM);  
    }  

    // 解密
    public static String decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
            	logger.error("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
            	logger.error("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes();
            Key skeySpec = toKey(raw);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64.decode(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception e) {
            	e.printStackTrace();
            	logger.error(e.toString(), e);
                return null;
            }
        } catch (Exception ex) {
        	logger.error("decode error!", ex);
           return null;
        }
    }

 // 加密
    private static byte[] encryptByYouMi(String content, String key) throws Exception {
	    if (key == null) {
	    	return null;
	    }
	    // 判断 Key 是否为 16 位
	    if (key.length() != 16) {
	    	return null;
	    }
	    //random iv
	    Random random = new Random();
	    byte[] buff = new byte[16];
	    random.nextBytes(buff);
	    byte[] raw = key.getBytes();
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
	    IvParameterSpec iv = new IvParameterSpec(buff);// 使用 CBC 模式,需要一个向量 iv,可增加加 密算法的强度
	    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
	    byte[] encrypted = cipher.doFinal(content.getBytes());
	    int totalLength = iv.getIV().length + encrypted.length;
	    byte[] combine = new byte[totalLength];
	    System.arraycopy(iv.getIV(), 0, combine, 0, iv.getIV().length); System.arraycopy(encrypted, 0, combine, iv.getIV().length, encrypted.length);
	    return combine;
    }
    /***
     * 有米的加密方式<br/>
     * AES/CBC/PKCS5Padding
     * @param data
     * @param salt
     * @return
     * @throws Exception
     */
    public static String encrryptAndEncodeByYouMi(String data,String salt) throws Exception{
    	return Base64.encode(encryptByYouMi(data, salt));
    }
    
    public static void main(String[] args) throws Exception {
        /*
         * 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定
         * 此处使用AES-128-CBC加密模式，key需要为16位。
         */
    	String cKey = "973d52e15e10ad85";
         //需要加密的字串
        String cSrc = "giaadd=1111111r22dddqqqqq";
        System.out.println(cSrc);
        // 加密
        long lStart = System.currentTimeMillis();
//        String enString = AESCoder.encrypt(cSrc, cKey);
//        System.out.println("加密后的字串是：" + enString);

        long lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("加密耗时：" + lUseTime + "毫秒");
        // 解密
        String enString="mKk9cr8QYLHaAEj9k/cKhJMgtEmL7SZIqRdhSH/q2esdsP9DNR5EnEyu5uHq4oWrWx1LQZCYGOYqiYl1+V6m/ijU7g5Si6zzWYviu7iYfMMtqDcqq3/M3tBhgr+SQ9IOMftGgkUX49U99tDaYwPfhl1D03pHbQNnxs2RKLzT/a52GV9S17hOkUzK5zKQf1QKWUQHrw1yEnkNJKS3r1/qZSv2JWAcPKpL2gtGF7usCpkhDIDWfz4JqwL2txLXYSCl1V3j6pM/UCfsH8CbuiKIiP7lY6N8IRDGiJSLMaaEEuufXEZcn+ANGuAIdMhmXoD3HWfzRBd+OqMlh30FiTh1KIr6d1qCBGjtOYFbBE+ts2T25/ZvcMTQjRV2Hb4OSDtrpEf4lKootk3HE+hBDLevHwhW4xQK/1BKos8iX0I4Qa4U+cpv5Lx0Yi2XqEpKO9zp7/NvoiU8Y8rjoaUMTOAI6jAKnWjgpTiqz5gFtRRsHaYzfqmVCMrwsNO5wx57CEzaHf4i5CecV0E2lDv3N1ajXSFn6lxl8zA1yierfnX3AvXC+tpKPGPR0dEiRrHLpueL7LwtD4mouDTHFjn5SKDg+vHA8xzaVLrtHcb6E9IBfr/j0iwVv2Asd8sLdMgqGWz/MPHfiyEexRfvZM7oDVFjBGgYkxjl4Rz71iusSilobLI80pN5ezTar1zx4XYr5Oy/";
        String DeString = AESCoder.decrypt(enString, cKey);
        System.out.println("解密后的字串是：" + URLDecoder.decode(DeString, "utf-8"));

        String info = "NES_hello://&com.ganji.haoche";
        System.out.println("解密后的info："  + AESCoder.encrypt(info, "mzdqweb201539712544".substring(0, 16)));
    }

}
