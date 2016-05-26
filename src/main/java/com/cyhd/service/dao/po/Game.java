package com.cyhd.service.dao.po;

import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;

import com.cyhd.service.util.IdEncoder;

public class Game implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8182481021261698847L;
	private int id;
	private String name;
	private String title;
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
	private int player_num;
	private String version;
	private int order_num;
	private int platform;
	private int customer_id;
	private int pay;
	
	private String category;
	private String images;
	/**是付费的游戏么？*/
	public boolean isPayGame(){
		return pay > 0;
	}
	
	public String getEncodedId(){
		return IdEncoder.encode(id);
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
	public String getProcess_name() {
		return process_name;
	}
	public void setProcess_name(String process_name) {
		this.process_name = process_name;
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
	public String getBundle_id() {
		return bundle_id;
	}
	public void setBundle_id(String bundle_id) {
		this.bundle_id = bundle_id;
	}
	public String getDownload_size() {
		return download_size;
	}
	public void setDownload_size(String download_size) {
		this.download_size = download_size;
	}
	public String getAppstore_id() {
		return appstore_id;
	}
	public void setAppstore_id(String appstore_id) {
		this.appstore_id = appstore_id;
	}
	public int getPlayer_num() {
		return player_num;
	}
	public void setPlayer_num(int player_num) {
		this.player_num = player_num;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getOrder_num() {
		return order_num;
	}
	public void setOrder_num(int order_num) {
		this.order_num = order_num;
	}
	public int getPlatform() {
		return platform;
	}
	public void setPlatform(int platform) {
		this.platform = platform;
	}
	public int getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(int customer_id) {
		this.customer_id = customer_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getPay() {
		return pay;
	}
	public void setPay(int pay) {
		this.pay = pay;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		//需要去掉转义
		if (images != null) {
			images = StringEscapeUtils.unescapeJava(images);
		}
		this.images = images;
	}
	
}
