package com.cyhd.service.push.android.umeng;

import com.cyhd.service.util.GlobalConfig;

public class Configuation {

	public static String appkey = "55d44cb867e58e80ec000bc8";
	public static String appMasterSecret="k2lg6wscvz8ixbx6hlhbpjecoocfe3xb";
	
	static{
		if(GlobalConfig.isDeploy == false){
			appkey="55d44d4e67e58e7fc8000d4d";
			appMasterSecret = "bwj5k17qlggtpv9debqbzeng5dliosmc";
		}
	}
	
}