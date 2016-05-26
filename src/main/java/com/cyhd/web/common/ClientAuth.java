package com.cyhd.web.common;

public class ClientAuth {
	
	private String ticket;
	private String sign;
	private int ts;
	private String code;
	private String rd ;
	
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public int getTs() {
		return ts;
	}
	public void setTs(int ts) {
		this.ts = ts;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getRd() {
		return rd;
	}
	public void setRd(String rd) {
		this.rd = rd;
	}

	/**
	 * 获取sign前6位的值，用于app端重定向校验用
	 * @return
	 */
	public int getSignValue(){
		if (sign != null && sign.length() > 5) {
			try {
				return Integer.parseInt(sign.substring(0, 6), 16);
			} catch (Exception e) {

			}
		}
		return 0;
	}
	@Override
	public String toString() {
		return "ClientAuth [ticket=" + ticket + ", sign=" + sign + ", ts=" + ts
				+ ", code=" + code + ", rd=" + rd + "]";
	}
	
	
}
