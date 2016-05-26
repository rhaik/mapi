package com.cyhd.service.vo.doubao;
 
import java.io.Serializable;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.doubao.Order;
import com.cyhd.service.dao.po.doubao.OrderProduct;
import com.cyhd.service.dao.po.doubao.Product;
import com.cyhd.service.dao.po.doubao.ProductActivity;
import com.cyhd.service.dao.po.doubao.ProductActivityRule;

public class ProductActivityVo  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Product product;
	private ProductActivity productActivity;
	private User user;  //中奖用户
	private OrderProduct orderProduct; //中奖订单

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public ProductActivity getProductActivity() {
		return productActivity;
	}
	public void setProductActivity(ProductActivity productActivity) {
		this.productActivity = productActivity;
	}
	public OrderProduct getOrderProduct() {
		return orderProduct;
	}
	public void setOrderProduct(OrderProduct orderProduct) {
		this.orderProduct = orderProduct;
	}
	
}
