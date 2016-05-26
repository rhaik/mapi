package com.cyhd.service.push.android;

import com.cyhd.service.util.GlobalConfig;

public class Configuration {
	public static  String appkey="cb08c2b079b64451edc226a2";
	public static  String masterSecret="d1e3ef988cc9659c84432b63";
	
	static{
		if(GlobalConfig.isDeploy){
			appkey = "da7d15254887ccc6d62879e4";
			masterSecret="c424f0b251a8278b004d4bea";
		}
	}
}
