package com.cyhd.service.vo.doubao;

import java.util.List;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.doubao.Order;

public class OrderVo {
	
	private Order order;
	private List<OrderProductVo> orderProductVo;
	private User user;
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
	public List<OrderProductVo> getOrderProductVo() {
		return orderProductVo;
	}
	public void setOrderProductVo(List<OrderProductVo> orderProductVo) {
		this.orderProductVo = orderProductVo;
	}
 
}
