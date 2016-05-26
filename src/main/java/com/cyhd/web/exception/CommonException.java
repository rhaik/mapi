package com.cyhd.web.exception;

/**
 * 自定义接口异常类，主要包含错误码
 */
public class CommonException extends Exception {
	
	private static final long serialVersionUID = -7095716137939454692L;
	
	protected final int errorCode;
	
	public CommonException(Exception e){
		this(ErrorCode.ERROR_CODE_UNKNOWN);
	}
	
	public CommonException(int errorCode){
		super(ErrorCode.getErrorMsg(errorCode));
		this.errorCode = errorCode;
	}

	public CommonException(int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public CommonException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	@Override
	public String toString() {
		return "StudyException [errorCode=" + errorCode + ", message="+ this.getMessage() + "]";
	}
	
}
