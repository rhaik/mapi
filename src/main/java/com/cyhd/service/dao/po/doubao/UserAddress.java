package com.cyhd.service.dao.po.doubao;

import java.io.Serializable;
import java.util.Date;

public class UserAddress implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private int user_id;
	private String name;
	private String mobile;
	private String address;
	private Date createtime; 
	private int deleted;
	private int preferred;

	//不保存到数据库的字段，存数据库时，这些值用空格拼接成address
	private String province, city, area, detailAddress;


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;

		setProvinceInfo(address);
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public int getDeleted() {
		return deleted;
	}

	public boolean isDeleted(){
		return deleted == 1;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public boolean isDefault(){
		return preferred == 1;
	}

	public int getPreferred() {
		return preferred;
	}

	public void setPreferred(int preferred) {
		this.preferred = preferred;
	}


	/**
	 * 传入一个详细地址，获取其省份等信息
	 * @param address
	 */
	public void setProvinceInfo(String address){
		if (address != null && address.length() > 0){
			String[] items = address.split("[ ]+", 4);

			setDetailAddress(items[items.length -1]);
			if (items.length > 1){
				setProvince(items[0]);

				if (items.length > 2){
					setCity(items[1]);

					if (items.length > 3){
						setArea(items[2]);
					}
				}
			}
		}
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getDetailAddress() {
		return detailAddress;
	}

	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}
}
