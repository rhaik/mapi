package com.cyhd.service.dao.po;

import java.util.Date;

/**
 * 短信验证码Domain
 * @author jack
 *
 * 2014年4月2日
 */
public class MobileCode {
	
    public final static int VALID_MAX = 3;//最多校验次数
    
    public final static long VALID_AGE_MM = 10*60*1000L;//生命周期 10分钟
    
    public final static int VALID_AGE_SS = 5*60;//生命周期 60秒 1分钟
    
    public final static char[] CODESE_QUENCE={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    
    public final static int CODE_COUNT=4;//随机字符个数
    
    public final static int CODE_VALID_NUM=4;//校验次数
	
	private long id;
	
	private String mobile;
	
	private String code;
	
	private Date createtime;
	
	private int validnum;

	private int estate;
	
	public MobileCode(){}
	
	public MobileCode( String mobile, String code, 
			Date createtime, int estate,int validnum) {
		super();
		
		this.mobile = mobile;
		this.code = code;
		this.createtime = createtime;
		this.estate = estate;
		this.validnum = validnum;
	}

	public int getEstate() {
		return estate;
	}

	public void setEstate(int estate) {
		this.estate = estate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getValidnum() {
		return validnum;
	}

	public void setValidnum(int validnum) {
		this.validnum = validnum;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
}

