package com.cyhd.service.push.ios;

import net.sf.json.JSONObject;

/**
 */
public class IosPushBean {

    private String token;
    
    private JSONObject params;
    
    private String clientType = "iphone";

    private String alertBody;
   
    private int badge;

    private int alertType;
    
    private boolean Newsstand = false;
    
    private String appver ;
    private String bundleId;
    
    public IosPushBean(){}
    
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
    
    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

    public void setParams(String key, String value) {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
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

	@Override
	public String toString() {
		return "IosPushBean [token=" + token + ", params=" + params + ", clientType=" + clientType + ", alertBody=" + alertBody + ", badge=" + badge
				+ ", alertType=" + alertType + "]";
	}

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}
    
    
}
