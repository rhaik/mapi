package com.cyhd.service.vo;

public class AppUpdate {
	
	private String version;
	private String url;
	private String content;
	private String origUrl;

	
	public AppUpdate(){
		
	}

	public AppUpdate(String version, String url) {
		this.version = version;
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOrigUrl() {
		return origUrl;
	}

	public void setOrigUrl(String origUrl) {
		this.origUrl = origUrl;
	}

	@Override
	public String toString() {
		return "AppUpdate [version=" + version + ", url=" + url + ", content=" + content + "]";
	}
	
}
