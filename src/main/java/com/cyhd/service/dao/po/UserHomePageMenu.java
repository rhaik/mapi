package com.cyhd.service.dao.po;

import java.util.Date;

public class UserHomePageMenu {

	private int id;
	//logo url
	private String logo;
	//显示的标题
	private String title;
	//点击后跳转的页面
	private String link;
	//显示的顺序号
	private int  cindex;
	//创建时间
	private Date createtime;
	//类型 按照用户类型显示 TODO 会不会有地域分组？
	private int ctype;
	//是否有效
	private int estate;
	//更新时间
	private Date updatetime;
	
	/**客户端展示*/
	private int client_show;
	/**显示的版本 如果是null就不用处理 否则表示为客户端需要的最低版本要求*/
	private String version;
	
	public static final int ANDROID=1;
	public static final int IOS=2;
	/**安卓和ios都显示*/
	public static final int ALL=3;

	//safari 版本
	public static final int SAFARI = 4;
	
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public int getCindex() {
		return cindex;
	}
	public void setCindex(int cindex) {
		this.cindex = cindex;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public int getCtype() {
		return ctype;
	}
	public void setCtype(int ctype) {
		this.ctype = ctype;
	}
	public int getEstate() {
		return estate;
	}
	public void setEstate(int estate) {
		this.estate = estate;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(150);
		sb.append("UserHomePageIcon [logo=").append(logo).append(",title=" ).append(title
				).append( ",link=" ).append( link ).append( ",cindex=" ).append( cindex ).append( ",createtime="
				).append( createtime ).append( ",ctype=" ).append( ctype ).append( ",estate=" ).append( estate
				).append( ",updatetime=" ).append( updatetime ).append( "]");
		return sb.toString();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getClient_show() {
		return client_show;
	}
	public void setClient_show(int client_show) {
		this.client_show = client_show;
	}
	
	public boolean isAndroidShow(){
		return this.client_show == ALL || this.client_show==ANDROID;
	}
	
	public boolean isIosShow(){
		return this.client_show == ALL || this.client_show==IOS;
	}

	public boolean isSafariShow(){
		return this.client_show == SAFARI;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
