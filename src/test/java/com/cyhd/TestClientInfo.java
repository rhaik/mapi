package com.cyhd;

import com.cyhd.web.common.util.AESCoder;

public class TestClientInfo {

	public static void main(String[] args) throws Exception {
		
		//test();
	}

	private static void test() throws Exception {
		String ci = "Z6MAc68+lsitk2908WijIUnCAmSB8o6OXL8e+1h4948DaETpONhDir021KUt8w7vVTJakoi/Mh3xCBWqdtH2dJD6fRUWAXCsgz0Y3HGsT5VwKN6cxuufo9/mpgoHzylnkx5iv40CaAcH2ci+IW5zR1Iq6E9P1KoRxwmBU0YZ/hz1vJE61Kk4vJ8X2FYNsBb9cpu0tcmwBCO5Q5RsjRKZTTUEsjQszBoCZSZwvFF/cqhgjHSqzMRBCzGPoECRlDqPEkxpr4XUQc+dIGSyU3kpqdxPixfblS1CdGT9hRBcnhG+lyDK+IexwqzTDHlb+3TM6pTtYi10Zlw5E06zUCA3zoiHo1lD2t8sCTQbir09hON7TNigGbJeiCtWt0nCBRQf";
		String sign = "78a7b9884fbb09b97260746ec9aaa8ee";
		String newClientInfo = AESCoder.decrypt(ci, "nci" + sign.substring(0, 13));
		System.out.println(newClientInfo);
	}
	
}
