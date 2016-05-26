package com.cyhd.util;

import com.cyhd.common.util.Base64;
import com.cyhd.service.util.RSAUtil;

public class TestRSA {

	static String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ/0wEfCoLSGrJofngEofOIloQ2jhpBETAbtrgpNZw8hgYNLIccaWalcMVOICB3ZbCMT5X8CGXVzpT+mGbr7D0kCAwEAAQ==";
	static String privateKey = "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAn/TAR8KgtIasmh+eASh84iWhDaOGkERMBu2uCk1nDyGBg0shxxpZqVwxU4gIHdlsIxPlfwIZdXOlP6YZuvsPSQIDAQABAkAKf/h3PXFrKEQAQf8POGcqOSofSRK2OaV79vIrvvT66y+SGDgvuy+fFg+vXpYm/CZEPqx9clXWmcIX56vTM90ZAiEAytHGGkXtv835jdcuSlkFCDOVZPnmaPovuYNYa+6v618CIQDJ5ceKqVHFo1kwyit5H4AsNpva2EAIBToe0BLm33kuVwIgMRawse826fN6cSAhrhD5rNB/Wh856zKSln35yrXMklUCIE9fk6wy1uARUQ46XQ74UGaRbYKDA+FjVK8qdFPpxXVvAiBR0DU1giGWiqmNfpDDN9eth8aO+IT+AJkpxS1wxTIhWQ==";
	
	
	public static void main(String[] args){
		
		String data = "http://stackoverflow.com/questions/5583379/what-is-the-limit-to-the-amount-of-data-that-can-be-encrypted-with-rsa";
		
		System.out.println("------test public encryption---");
		
		byte[] encrypted = RSAUtil.encrypt(publicKey, data.getBytes());
		System.out.println("encrypted with public key：" + encrypted.length + ", encrypted:" + Base64.encode(encrypted));
		
		
		byte[] decrypted = RSAUtil.decrypt(privateKey, encrypted);
		System.out.println("decrypted with private key：" + new String(decrypted));

		
		System.out.println("------test private encryption---");
		
		encrypted = RSAUtil.encrypt(privateKey, data.getBytes());
		System.out.println("encrypted with private key:" + encrypted.length + ", encrypted:" + Base64.encode(encrypted));
		
		decrypted = RSAUtil.decrypt(publicKey, encrypted);
		System.out.println("decrypted with public key：" + new String(decrypted));
	}
}
