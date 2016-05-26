package com.cyhd.service.util;

import com.cyhd.common.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加密解密<br/>
 * 公钥为X509EncodedKeySpec格式的字符串<br/>
 * 私钥是PKCS8EncodedKeySpec格式的字符串
 * @author hy
 *
 */
public class RSAUtil {
	private static final Logger logger = LoggerFactory.getLogger(RSAUtil.class);
	public static final String ALGORITHM = "RSA";

	
	/**
	 * 使用rsa进行加密
	 * @param encodedKey 自动识别是公钥还是私钥
	 * @param data 要加密的数据
	 * @return
	 */
	public static byte[] encrypt(String encodedKey, byte[] data){
		return doRSA(encodedKey, data, Cipher.ENCRYPT_MODE);
	}
	
	/**
	 * 使用RSA算法进行解密
	 * @param encodedKey 自动识别是公钥还是私钥
	 * @param data
	 * @return
	 */
	public static byte[] decrypt(String encodedKey, byte[] data){
		return doRSA(encodedKey, data, Cipher.DECRYPT_MODE);
	}
	
	
	/**
	 * 执行加密或解密操作
	 * @param encodedKey
	 * @param data
	 * @param mode
	 * @return
	 */
	private static byte[] doRSA(String encodedKey, byte[] data, int mode){
		byte[] encrypted = null;
		try {
			byte[] keyBytes = Base64.decode(encodedKey);
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
			
			//先尝试获取公钥，如果获取不到，则再获取私钥
			//RSA加密明文最大长度(密钥字节-11)字节，解密要求密文最大长度为密钥字节，所以在加密和解密的过程中需要分块进行。
			Key key = null;
			try{
				key = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
			}catch(InvalidKeySpecException exp){
				key = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
			}

			if(key != null){
				final Cipher cipher = Cipher.getInstance(ALGORITHM);
			    cipher.init(mode, key);

                //根据key的长度决定加密解密的块大小
                int bitLength = ((RSAKey)key).getModulus().bitLength();
                int byteLength = bitLength / 8;

				int blockSize = byteLength;

				//加密的时候，块大小需要减去11字节
				if ( mode == Cipher.ENCRYPT_MODE){
					blockSize = byteLength - 11;
				}

				encrypted = doUsingBlock(cipher, data, blockSize);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return encrypted;
	}

	/**
	 * 分块进行加密或者解密操作<br/>
     * RSA区分块大小，参考：http://stackoverflow.com/questions/5583379/what-is-the-limit-to-the-amount-of-data-that-can-be-encrypted-with-rsa
	 * @param cipher
	 * @param data
	 * @return
	 */
	private static byte[] doUsingBlock(Cipher cipher, byte[] data, int blockSize) throws BadPaddingException, IllegalBlockSizeException, IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (int offset = 0; offset < data.length; offset += blockSize){
			int size = blockSize;

			//可能是最后一个块
			if (offset + blockSize > data.length){
				size = data.length - offset;
			}

			byte[] outBuf = cipher.doFinal(data, offset, size);
			bos.write(outBuf);
		}
		return bos.toByteArray();
	}

	
}
