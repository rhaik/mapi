package com.cyhd.service.push;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.cyhd.service.constants.Constants;

/**
 */
public class PushBean{
    
    private JSONObject params;
    
    private boolean pushToTeacher;
    
	private List<String> tokens = new ArrayList<String>();
    
    private long expireTime = PushConstants.DEFAULT_PUSH_EXPIRE_TIME;
    
    private int clientType = Constants.platform_android;   //platform_android, platform_ios
    
    protected String title;

    protected String alertBody;
   
    protected int badge = 1;

    protected int alertType;
    
    public PushBean(){}
    
    public boolean isPushToTeacher() {
		return pushToTeacher;
	}

	public void setPushToTeacher(boolean pushToTeacher) {
		this.pushToTeacher = pushToTeacher;
	}
    
    public String getTransmissionContent(){
    	return params == null ? "{}" : params.toString();
    }
    
    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

    public void addParam(String key, String value) {
        if (this.params == null) {
            this.params = new JSONObject();
        }
        this.params.put(key, value);
    }

    public String getParam(String key) {
        if (this.params != null) {
            return this.params.getString(key);
        }
        return null;
    }

    /**
     * @return the tokens
     */
    public List<String> getTokens() {
        return tokens;
    }

    /**
     * @param tokens
     */
    public void setTokens(List<String> tokens) {
        if (tokens != null) {
            this.tokens = tokens;
        }
    }

    public String getSingleToken() {
        if (tokens.isEmpty()) {
            return null;
        }
        return tokens.get(0);
    }
    
    public void addToken(String token) {
        tokens.add(token);
    }

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}
	
	public int getClientType() {
		return clientType;
	}

	public void setClientType(int clientType) {
		this.clientType = clientType;
	}

	public void setIos(){
		this.clientType = Constants.platform_ios;
	}

	public boolean isIos(){
		return clientType == Constants.platform_ios;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAlertBody() {
		return alertBody;
	}

	public void setAlertBody(String alertBody) {
		this.alertBody = alertBody;
	}

	public int getBadge() {
		return badge;
	}

	public void setBadge(int badge) {
		this.badge = badge;
	}

	public int getAlertType() {
		return alertType;
	}

	public void setAlertType(int alertType) {
		this.alertType = alertType;
	}
	
	@Override
	public String toString() {
		return "PushBean [params=" + params + ", pushToTeacher=" + pushToTeacher + ", tokens=" + tokens + ", expireTime=" + expireTime + ", clientType="
				+ clientType + ", title=" + title + ", alertBody=" + alertBody + ", badge=" + badge + ", alertType=" + alertType + "]";
	}

}
