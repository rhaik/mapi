package com.cyhd.service.dao.db.mapper.doubao;


import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.doubao.Order;
import com.cyhd.service.dao.po.doubao.OrderHistory;
import com.cyhd.service.dao.po.doubao.OrderLog;
 
@Repository
public interface OrderMapper {
	
	@Select("select * from money_order where order_sn=#{0}")
	public Order getByOrderSn(long order_sn);
	
	@Select("select * from money_order where id=#{0}")
	public Order getByOrderId(int orderId);
	
	@Select("select * from money_order where user_id=#{0} limit #{1},#{2}")
	public List<Order> getOrderListByUserId(int userId);
	
	@Insert("INSERT INTO `money_order` (`order_sn`,`order_type`, `pay_type`, `createtime`, `paytime`,`total_amount`, `pay_amount`, `user_id`, `status`, `pay_status`,  `source`) VALUES "
			+ "(#{order_sn},#{order_type}, #{pay_type}, #{createtime}, #{paytime}, #{total_amount}, #{pay_amount}, #{user_id},#{status},#{pay_status},#{source})")
	public int create(Order o);
	
	@Insert("INSERT INTO `money_order_log` (`order_sn`, `operator`, `operation_content`, `operator_time`, `status`, `type`) VALUES "
			+ "(#{order_sn}, #{operator}, #{operation_content}, #{operator_time}, #{status},#{type})")
	public int createOrderLog(OrderLog log);

	/**
	 * 获取最近的多少条订单，用来计算夺宝编号
	 * @param row
	 * @return
	 */
	@Select("select * from money_order order by id desc limit #{0}")
	public List<Order> getLatestOrder(int row);
 
	public int createOrderHistory(List<OrderHistory> list);

	/**
	 * 使用第二个参数，防止并发导致多次插入了订单历史数据
	 * @param productActivityId
	 * @param limit
	 * @return
	 */
	@Select("select * from money_order_history where product_activity_id=#{0} limit #{1}")
	public List<OrderHistory> getOrderHistoryList(int productActivityId, int limit);
}