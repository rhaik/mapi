package com.cyhd.service.dao.po;

import java.util.Date;

public class UserEnchashmentAccount {

	private int user_id;
	private int wx_bank;
	private String wx_bank_name;
	private String alipay_name;
	private String alipay_account;
	private Date createtime;
	private Date updatetime;
	private int masked;
	
	private int auto_pass;

	private int score;
	
	/**被封的账号标识*/
	public static final int IS_MASKED = 1;
	
	/**自动审核通过的标识**/
	public static final int IS_AUTO_PASSED = 1;
	
	public boolean isAutoPassed(){
		return auto_pass == IS_AUTO_PASSED;
	}
	public boolean isMasked(){
		return this.masked == 1;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getWx_bank() {
		return wx_bank;
	}
	public void setWx_bank(int wx_bank) {
		this.wx_bank = wx_bank;
	}
	public String getWx_bank_name() {
		return wx_bank_name;
	}
	public void setWx_bank_name(String wx_bank_name) {
		this.wx_bank_name = wx_bank_name;
	}
	public String getAlipay_name() {
		return alipay_name;
	}
	public void setAlipay_name(String alipay_name) {
		this.alipay_name = alipay_name;
	}
	public String getAlipay_account() {
		return alipay_account;
	}
	public void setAlipay_account(String alipay_account) {
		this.alipay_account = alipay_account;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public int getMasked() {
		return masked;
	}
	public void setMasked(int masked) {
		this.masked = masked;
	}

	public int getAuto_pass() {
		return auto_pass;
	}

	public void setAuto_pass(int auto_pass) {
		this.auto_pass = auto_pass;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
