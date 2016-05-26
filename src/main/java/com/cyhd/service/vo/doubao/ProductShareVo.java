package com.cyhd.service.vo.doubao;
 
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.doubao.ProductActivity;
import com.cyhd.service.dao.po.doubao.ProductShare;
import com.cyhd.service.dao.po.doubao.OrderProduct;

public class ProductShareVo {
	
	private ProductShare productShare;
	private ProductActivity productActivity;
	private User user;
	private OrderProduct OrderProduct;
	
	public ProductShare getProductShare() {
		return productShare;
	}
	public void setProductShare(ProductShare productShare) {
		this.productShare = productShare;
	}
	public ProductActivity getProductActivity() {
		return productActivity;
	}
	public void setProductActivity(ProductActivity productActivity) {
		this.productActivity = productActivity;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public OrderProduct getOrderProduct() {
		return OrderProduct;
	}
	public void setOrderProduct(OrderProduct orderProduct) {
		OrderProduct = orderProduct;
	} 
	 
	 
}
