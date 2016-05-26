package com.cyhd.service.push.android.umeng;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.cyhd.service.push.PushResult;

public class UmengPusherUM extends AndroidNotification{

//	private String appkey = null;
//	private String appMasterSecret = null;
	private String timestamp = null;
	
	public UmengPusherUM(){
		appMasterSecret = Configuation.appMasterSecret;
		timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
	}
	
	public PushResult push(String device_tokens,String title,String content,JSONObject params) throws Exception{
		AndroidUnicast unicast = new AndroidUnicast();
		unicast.setAppMasterSecret(appMasterSecret);
		unicast.setPredefinedKeyValue("appkey", Configuation.appkey);
		unicast.setPredefinedKeyValue("timestamp", this.timestamp);
		// TODO Set your device token
		unicast.setPredefinedKeyValue("device_tokens",device_tokens);
		unicast.setPredefinedKeyValue("ticker", title);
		if(StringUtils.isNotBlank(title)){
			unicast.setPredefinedKeyValue("title",  title);
		}
		unicast.setPredefinedKeyValue("text",   content);
		unicast.setPredefinedKeyValue("after_open", "go_custom");
		unicast.setPredefinedKeyValue("display_type", "notification");
		// TODO Set 'production_mode' to 'false' if it's a test device. 
		// For how to register a test device, please see the developer doc.
		unicast.setPredefinedKeyValue("production_mode", "true");
		// Set customized fields
		unicast.setExtraField("extra_data",params.toString());
		unicast.setPredefinedKeyValue("custom", params.toString());
		return unicast.send();
	}
}
