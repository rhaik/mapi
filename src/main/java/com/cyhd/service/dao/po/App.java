package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

import com.cyhd.service.util.IdEncoder;

public class App implements Serializable {

	/**
	 */
	private static final long serialVersionUID = -7382206326857844791L;
	
	private int id;
	private String name;
	private String process_name;  //进程名 
	private String icon;
	private String url;
	private int status;		// 状态(0:未审核1:已审核2:审核未通过,3:删除
	private int creater;
	private Date createtime;
	private int auditor;
	private Date audit_time;
	private String description;
	private String agreement;
	private String bundle_id;
	private String download_size;
	private String appstore_id;

	//创建应用的时候选择厂商，创建应用的时候选择是否是厂商回调任务
	private int vendor_id; //厂商id

	//是否在推广中，是的话，会将其url scheme返回给客户端
	private int is_promotion;
	
	private int pay_way;
	private int pay_money; //付费应用支付金额
	
	private String version;
	
	/**1---免费，*/
	public static final int PAY_WAY_FREE = 1;
	/**2---付费*/
	public static final int PAY_WAY_PAY = 2;
	
	public boolean isPayWay(){
		return this.pay_way == PAY_WAY_PAY;
	}
	
	public String getAppstore_id() {
		return appstore_id;
	}
	public void setAppstore_id(String appstore_id) {
		this.appstore_id = appstore_id;
	}
	public String getDownload_size() {
		return download_size;
	}
	public void setDownload_size(String download_size) {
		this.download_size = download_size;
	}
	public String getProcess_name() {
		return process_name;
	}
	public void setProcess_name(String process_name) {
		this.process_name = process_name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCreater() {
		return creater;
	}
	public void setCreater(int creater) {
		this.creater = creater;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public int getAuditor() {
		return auditor;
	}
	public void setAuditor(int auditor) {
		this.auditor = auditor;
	}
	public Date getAudit_time() {
		return audit_time;
	}
	public void setAudit_time(Date audit_time) {
		this.audit_time = audit_time;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAgreement() {
		return agreement;
	}
	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}

	public int getVendor_id() {
		return vendor_id;
	}

	public void setVendor_id(int vendor_id) {
		this.vendor_id = vendor_id;
	}

	public int getIs_promotion() {
		return is_promotion;
	}

	public void setIs_promotion(int is_promotion) {
		this.is_promotion = is_promotion;
	}

	public boolean isPromoting(){
		return is_promotion == 1;
	}

	public String getBundle_id() {
		return bundle_id;
	}
	public void setBundle_id(String bundle_id) {
		this.bundle_id = bundle_id;
	}

	public int getAdid(){
		return id * 11 + 997;
	}
	
	@Override
	public String toString() {
		return "App [id=" + id + ", name=" + name + ", icon=" + icon + ", url=" + url + ", status=" + status + ", creater=" + creater + ", createtime="
				+ createtime + ", auditor=" + auditor + ", audit_time=" + audit_time + ", description=" + description + ", agreement=" + agreement + "]";
	}
	public String getEncodedAppId(){
		return IdEncoder.encode(id);
	}
	public int getPay_way() {
		return pay_way;
	}
	public void setPay_way(int pay_way) {
		this.pay_way = pay_way;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getPay_money() {
		return pay_money;
	}

	public void setPay_money(int pay_money) {
		this.pay_money = pay_money;
	}
}
