package com.cyhd.service.impl.doubao;


import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.doubao.OrderMapper;
import com.cyhd.service.dao.db.mapper.doubao.UserAddressMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.doubao.*;
import com.cyhd.service.impl.BaseService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.util.IpAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@Service
public class OrderService extends BaseService {
	@Resource
	private OrderMapper orderMapper; 
	
	@Resource
	private ProductActivityService productActivityService;

	@Resource
	private ProductActivityCalculateService activityCalculateService;

	@Resource
	private UserService userService; 
	
	@Resource
	private ProductService productService; 
	
	@Resource
	private OrderProductService orderProductService; 
	
	@Resource
	UserDuobaoCoinService duobaoCoinService;
	
	@Resource
	UserAddressMapper userAddressMapper;
	
	
	
	//缓存订单信息
	private CacheLRULiveAccessDaoImpl<Order> cacheOrder = new CacheLRULiveAccessDaoImpl<Order>(Constants.hour_millis * 1, 1024);
	
	//缓存历史订单记录信息
	private CacheLRULiveAccessDaoImpl<List<OrderHistory>> cacheOrderHistory = new CacheLRULiveAccessDaoImpl<List<OrderHistory>>(Constants.hour_millis * 1, 1024);
	
	private static Logger log = LoggerFactory.getLogger("order");
	/**
	 * 根据Id获取订单信息
	 * @return
	 */
	public Order getByOrderId(int orderId) {
		String key = "ORDER_"+orderId;
		Order o = cacheOrder.get(key);
		if(o == null) {
			o = orderMapper.getByOrderId(orderId);
			cacheOrder.set(key, o);
		}
		return o; 
	}
	/**
	 * 根据订单号获取订单信息
	 * @return
	 */
	public Order getByOrderSn(long orderSn) {
		String key = "ORDER_"+orderSn;
		Order o = cacheOrder.get(key);
		if(o == null) {
			o = orderMapper.getByOrderSn(orderSn);
			cacheOrder.set(key, o);
		}
		return o;
	}


	/**
	 * 夺宝
	 * @param activityId 夺宝活动id
	 * @param number 参与的次数
	 * @param source 来源
	 * @param userId 用户id
	 * @param ip 用户的ip
	 */
	@Transactional (timeout = 5)
	public void createOrder(int activityId, int number, int source, int userId, String ip)  {
		Date now = GenerateDateUtil.getCurrentDate();

		//先获取活动
		ProductActivity activity = productActivityService.getProductActivityById(activityId);
		//支付金额=夺宝次数*一次夺宝的单价
		int payAmount = number * activity.getPrice();

		Order o = new Order();
		o.setOrder_sn(createOrderSn());
		o.setOrder_type(1);
		o.setCreatetime(now);
		o.setPay_type(1);
		o.setPay_amount(payAmount);
		o.setPaytime(now);
		o.setPay_status(1);
		o.setSource(source);
		o.setStatus(1);
		o.setTotal_amount(payAmount);
		o.setUser_id(userId);
		if(orderMapper.create(o) > 0) {
			if(!duobaoCoinService.useCoinByDuobao(userId, payAmount, 0, activity.getProduct_name())){
				log.error("user{} create order{} pay amount:{}", userId, o.getOrder_sn(), payAmount);
				throw new RuntimeException("您的夺宝币不足");
			} 

			OrderLog orderLog = new OrderLog();
			orderLog.setOrder_sn(o.getOrder_sn());
			orderLog.setOperator(userId);
			orderLog.setOperation_content("提交夺宝订单");
			orderLog.setOperator_time(now);
			orderLog.setStatus(o.getStatus());
			orderLog.setType(1);
			orderMapper.createOrderLog(orderLog);

			//创建订单商品，相当于支付完成后发货
			if(productActivityService.updateBuyNumber(activityId, number)) {
				OrderProduct op = new OrderProduct();
				op.setOrder_sn(o.getOrder_sn());
				op.setCreatetime(now);

				//ip地址解析
				String ip_area = "未知";
				if(ip == null || ip.isEmpty()) {
					ip_area = "中国";
				} else {
					ip_area = IpAddressUtil.getAddress(ip);
				}

				op.setIp(ip);
				op.setIp_area(ip_area);
				op.setNumber(number);
				op.setPrice(activity.getPrice());
				op.setProduct_activity_id(activityId);
				op.setProduct_id(activity.getProduct_id());
				op.setUser_id(userId);
				op.setStatus(1);
				if(!orderProductService.createOrderProduct(op)) {
					log.error("user{} create order{} OrderProduct fail", userId, o.getOrder_sn());
					throw new RuntimeException("创建订单商品失败");
				}
				//创建订单成功，删除缓存
				productActivityService.deleteProductActivityCache(activityId, activity.getProduct_id());

				//检查是否达到购买人数
				if ( (activity.getBuy_number() + number) == activity.getNumber() ){
					//重新从数据库里获取一遍
					activityCalculateService.updateAnnounced(activity.getId(), activity.getProduct_id());
				}
			} else { //购买失败，直接生成下一期
				productActivityService.deleteProductActivityCache(activityId, activity.getProduct_id());

				activityCalculateService.buildNextPeriod(activity);
				log.error("user:{} order:{} product number:{} than sell number", userId, o.getOrder_sn(), number);
				throw new RuntimeException("很抱歉，您来晚了，本次活动已结束");
			}
		} else {
			log.error("user{} create order fail", userId);
			throw new RuntimeException("创建订单失败");
		}
	}
	/**
	 * 生成订单号
	 * @return
	 */
	public long createOrderSn() {
		long orderSn = System.currentTimeMillis();
		while(orderMapper.getByOrderSn(orderSn) != null) {
			orderSn = System.currentTimeMillis();
		}
		return orderSn;
	}
	/**
	 * 获取最近订单列表
	 * @param row
	 * @return
	 */
	public List<Order> getLatestOrder(int row) {
		return orderMapper.getLatestOrder(row);
	}
	/**
	 * 创建历史订单信息
	 * @param o
	 * @return
	 */
	public boolean createOrderHistory(List<OrderHistory> o) {
		return orderMapper.createOrderHistory(o) > 0;
	}
	/**
	 * 获取历史订单记录
	 * @param productActivityId
	 * @return
	 */
	public List<OrderHistory> getOrderHistoryList(int productActivityId, int limit) {
		String key = "ORDER_HISTORY_"+productActivityId;
		List<OrderHistory> oh = cacheOrderHistory.get(key);
		if(oh == null) {
			oh =  orderMapper.getOrderHistoryList(productActivityId, limit);
			cacheOrderHistory.set(key,oh);
		}
		return oh;
	}
	/**
	 * 创建用户收货人地址
	 * @return
	 */
	public boolean saveUserAddress(UserAddress address) {
		if(address.getId() > 0) {
			return userAddressMapper.updateAddress(address) > 0;
		}
		return userAddressMapper.saveUserAddress(address) > 0;
	}
	
	/**
	 * 获取用户地址列表
	 * @return
	 */
	public List<UserAddress> getUserAddressList(int userId) {
		return userAddressMapper.getList(userId);
	}
	
	/**
	 * 获取用户默认地址列表
	 * @return
	 */
	public UserAddress getUserDefaultAddress(int userId) {
		return userAddressMapper.getDefaultAddressByUserId(userId);
	}
	
	/**
	 * 获取用户默认地址列表
	 * @return
	 */
	public boolean updateCancelDefault(int userId) {
		return userAddressMapper.updateCancelDefault(userId) > 0;
	}
	
	/**
	 * 删除用户地址
	 * @return
	 */
	public boolean deleteUserAddress(int userId, int id) {
		return userAddressMapper.deleteUserAddress(userId, id) > 0;
	}
	/**
	 * 根据ID获取用户收货
	 * @return
	 */
	public UserAddress getUserAddressById(int id) {
		return userAddressMapper.getById(id);
	}
	
}
