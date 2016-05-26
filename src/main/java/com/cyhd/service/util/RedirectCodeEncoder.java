package com.cyhd.service.util;

import com.cyhd.common.util.MagicKey;
import com.cyhd.service.constants.Constants;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 重定向时，生成密钥的Encoder
 */
public class RedirectCodeEncoder {

	private static final int magic_key = 285371618;

	/**
	 * 1分钟有效期，过期后无效
	 */
	static MagicKey mk = new MagicKey(Constants.minutes_millis * 1, magic_key);
	
	public static String encode(int id){
		return mk.encode(id);
	}


	/**
	 *
	 * 1分钟有效期，过期后无效，返回null
	 *
	 * @param code
	 * @return
	 */
	public static Integer decode(String code){
		MagicKey mt = MagicKey.decode(code, magic_key);

		//必须未过期才有效
		if (mt != null && new Date().before(mt.getExpiration())){
			return mt.getHideValue();
		}
		return null;
	}


	/**
	 * 解密成MagicKey，不管是否过期
	 * @param code
	 * @return
	 */
	public static MagicKey decodeMagicKey(String code){
		return MagicKey.decode(code, magic_key);
	}
	
	public static void main(String args[])throws Exception {
		String key = RedirectCodeEncoder.encode(75);
		System.out.println(key);
		//String key = "54140c30ad04a020000000000000fc180000000000000000f4045814a010a020";
		System.out.println(key.length());

		TimeUnit.SECONDS.sleep(3);
		//System.out.println(IdEncoder.decode(key));
		MagicKey mk = MagicKey.decode(key, magic_key);
		System.out.println("expire time:" + mk.getExpiration());
		System.out.println("number:" + mk.getHideValue());

		System.out.println(RedirectCodeEncoder.decode(key));
	}
	
}
