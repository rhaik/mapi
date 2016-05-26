package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户轮播图信息(user_banner)
 */
public class UserBanner implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;//主键，自动递增

    private int ctype;//类型
    
    private int cindex;//轮播图序号

    private String image;//图片url

    private String url;//跳转url

    private String content;//文本

    private int estate;//是否有效  1 有效  2 无效

    private Date createtime;//

    private Date updatetime;//

	private int category; //banner所属类型，1：系统，2：一元夺宝

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCtype() {
		return ctype;
	}

	public void setCtype(int ctype) {
		this.ctype = ctype;
	}

	public int getCindex() {
		return cindex;
	}

	public void setCindex(int cindex) {
		this.cindex = cindex;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public int getEstate() {
		return estate;
	}

	public void setEstate(int estate) {
		this.estate = estate;
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

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "UserBanner [id=" + id + ", ctype=" + ctype + ", cindex=" + cindex + ", image=" + image + ", url=" + url + ", content=" + content + ", estate="
				+ estate + ", createtime=" + createtime + ", updatetime=" + updatetime + "]";
	}
   
}
