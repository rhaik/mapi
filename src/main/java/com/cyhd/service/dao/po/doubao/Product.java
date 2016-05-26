package com.cyhd.service.dao.po.doubao;

import com.cyhd.common.util.richtext.HtmlUtil;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private String name;
	private long code;
	private String thumb;
	private String content;
	private int market_price;
	private int sell_stock;
	private Date createtime;
	private Date updatetime;
	private int is_enabled;
	private int status;
	private String abbreviation;

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getShortName(){
		String shortName = "";
		if (abbreviation != null && abbreviation.length() > 0){
			shortName = abbreviation;
		}else if (name != null){
			if (name.length() > 0){
				shortName = name.substring(0, 7) + "...";
			}else {
				shortName = name;
			}
		}
		return shortName;
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


	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public String getThumb() {
		return thumb;
	}


	public void setThumb(String thumb) {
		this.thumb = thumb;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public int getMarket_price() {
		return market_price;
	}


	public void setMarket_price(int market_price) {
		this.market_price = market_price;
	}


	public int getSell_stock() {
		return sell_stock;
	}


	public void setSell_stock(int sell_stock) {
		this.sell_stock = sell_stock;
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


	public int getIs_enabled() {
		return is_enabled;
	}


	public void setIs_enabled(int is_enabled) {
		this.is_enabled = is_enabled;
	}


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}

	public String getRawContent(){
		return HtmlUtil.escapeFromHtml(content);
	}


	@Override
	public String toString() {
		return "Product{" +
				"id=" + id +
				", name='" + name + '\'' +
				", code=" + code +
				", market_price=" + market_price +
				", sell_stock=" + sell_stock +
				", createtime=" + createtime +
				", status=" + status +
				'}';
	}
}
