package com.cyhd.service.vo.doubao;
 
import java.util.List;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.doubao.Order;
import com.cyhd.service.dao.po.doubao.OrderProduct;
import com.cyhd.service.dao.po.doubao.OrderProductLottery;
import com.cyhd.service.dao.po.doubao.Product;
import com.cyhd.service.dao.po.doubao.ProductActivity;

public class OrderProductVo {
	
	private Product product;
	private OrderProduct orderProduct;
	private ProductActivity productActivity;
	private List<OrderProductLottery> orderProductLottery;
	private User user;
	private Order order;
	private boolean displayDate = false;
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
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
	public void setDisplayDate(boolean displayDate) {
		this.displayDate = displayDate;
	}
	public boolean getDisplayDate() {
		return this.displayDate;
	}
	public List<OrderProductLottery> getOrderProductLottery() {
		return orderProductLottery;
	}
	public void setOrderProductLottery(List<OrderProductLottery> orderProductLottery) {
		this.orderProductLottery = orderProductLottery;
	}
}
