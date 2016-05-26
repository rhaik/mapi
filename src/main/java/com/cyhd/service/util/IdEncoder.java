package com.cyhd.service.util;

import com.cyhd.common.util.MagicKey;

public class IdEncoder {

	private static final int magic_key = 37760271;
	
	static MagicKey mk = new MagicKey(magic_key);
	
	public static String encode(int id){
		return mk.encode(id);
	}
	
	public static Integer decode(String code){
		MagicKey mt = MagicKey.decode(code, magic_key);
		return mt == null ? null: mt.getHideValue();
	}
	
	public static void main(String args[])throws Exception {
		String key = IdEncoder.encode(75);
		System.out.println(key);
		//String key = "54140c30ad04a020000000000000fc180000000000000000f4045814a010a020";
		System.out.println(key.length());
		
		System.out.println(IdEncoder.decode("d7ece1cc8eab41238140420282436ddd438000c14143c3c1f90545c423afbaf6"));
	}
	
}
