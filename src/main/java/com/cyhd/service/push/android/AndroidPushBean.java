package com.cyhd.service.push.android;

import net.sf.json.JSONObject;

public class AndroidPushBean {

	private String alertBody;
	private String title;
	private String deviceToken;
	
	private JSONObject params;
    
    private String clientType = "android";
   
    private int alertType;
    
    private boolean Newsstand = false;
    
    private String appver ;

	public String getAlertBody() {
		return alertBody;
	}
	public void setAlertBody(String alertBody) {
		this.alertBody = alertBody;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public JSONObject getParams() {
		return params;
	}
	public void setParams(JSONObject params) {
		this.params = params;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public int getAlertType() {
		return alertType;
	}
	public void setAlertType(int alertType) {
		this.alertType = alertType;
	}
	public boolean isNewsstand() {
		return Newsstand;
	}
	public void setNewsstand(boolean newsstand) {
		Newsstand = newsstand;
	}
	public String getAppver() {
		return appver;
	}
	public void setAppver(String appver) {
		this.appver = appver;
	}
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(200);
		sb.append( "AndroidPushBean [alertBody=" ).append( alertBody).append(", title=" ).append( title
				).append(", deviceToken=" ).append(deviceToken + ", params=" ).append( params
				).append( ", clientType=").append( clientType ).append( ", alertType=" ).append( alertType
				).append(", Newsstand=").append( Newsstand).append(", appver=" ).append( appver + "]");;
		return sb.toString();
	}
	
}
/*
{
    "notification" : {
        "android" : {
             "alert" : "hello, JPush!", 
             "title" : "JPush test", 
             "builder_id" : 3, 
             "extras" : {
                  "news_id" : 134, 
                  "my_key" : "a value"
             }
        }
    }
}

*/