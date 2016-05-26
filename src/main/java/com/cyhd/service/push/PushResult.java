package com.cyhd.service.push;


/**
 * 推送返回结果
 *
 * @version 1.0
 */
public class PushResult {
	
	
    private boolean isSuccess; // 是否成功
    private int errcode ;// 失败code
    private String errMessage ; // 失败时，失败原因
    private String clientType ;// android  or  ios
    private long pushRecordId ; // push record id
    
    public PushResult(){
    	
    }
    
    public PushResult(boolean success){
		this.isSuccess = success;
	}
    
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getErrMessage() {
		return errMessage;
	}
	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}
	
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	public long getPushRecordId() {
		return pushRecordId;
	}
	public void setPushRecordId(long pushRecordId) {
		this.pushRecordId = pushRecordId;
	}
	@Override
	public String toString() {
		return "PushResult [isSuccess=" + isSuccess + ", errcode=" + errcode + ", errMessage=" + errMessage + ", clientType=" + clientType + ", pushRecordId="
				+ pushRecordId + "]";
	}
}
