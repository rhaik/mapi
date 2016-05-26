package com.cyhd.service.dao.po.doubao;

import java.io.Serializable; 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 
 




import net.sf.json.JSONArray;
 

public class ProductShare implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private Long order_sn;
	private int product_id;
	private int order_product_id;
	private int product_activity_id;
	private int user_id;
	private String title;
	private String images;
	private Date createtime;
	private int status;
	
	public static final int STATUS_WAIT_AUDIT = 0; //等待审核
	public static final int STATUS_AUDIT_PASS = 1; //审核通过
	public static final int STATUS_AUDIT_FAIL = 2; //不通过
	
	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Long getOrder_sn() {
		return order_sn;
	}


	public void setOrder_sn(Long order_sn) {
		this.order_sn = order_sn;
	}
	
	public int getProduct_id() {
		return product_id;
	}


	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public int getOrder_product_id() {
		return order_product_id;
	}


	public void setOrder_product_id(int order_product_id) {
		this.order_product_id = order_product_id;
	}


	public int getProduct_activity_id() {
		return product_activity_id;
	}


	public void setProduct_activity_id(int product_activity_id) {
		this.product_activity_id = product_activity_id;
	}


	public int getUser_id() {
		return user_id;
	}


	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getImages() {
		return images;
	}


	public void setImages(String images) {
		this.images = images;
	}


	public Date getCreatetime() {
		return createtime;
	}


	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	public List<String> getShareImages() {
		if(this.images==null || this.images.isEmpty()) return new ArrayList<String>();
		JSONArray jsonObject = JSONArray.fromObject( this.images ); 
	    List<String> list = new ArrayList<>(JSONArray.toCollection(jsonObject));
		return list;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public static void main(String[] args)  {
		List<String> list = new ArrayList<String>();
		list.add("http://money.lieqicun.cn/upload/2015/07/jpg%20%281%29.jpeg");
		list.add("http://money.lieqicun.cn/upload/2015/07/jpg%20%283%29.jpeg");
		list.add("http://money.lieqicun.cn/upload/2015/07/jpg%20%285%29.jpeg");
		JSONArray json = JSONArray.fromObject(list);
		System.out.println(json);
		JSONArray jsonObject = JSONArray.fromObject( json ); 
	    List arrayList = (List)JSONArray.toCollection(jsonObject);
		 
		System.out.println(arrayList);
	}
}
