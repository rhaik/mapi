package com.cyhd.service.dao.db.mapper.doubao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.doubao.OrderProduct;
import com.cyhd.service.dao.po.doubao.OrderProductLottery;


@Repository
public interface OrderProductMapper {
	
	@Select("select * from money_order_product where id=#{0}")
	public OrderProduct getOrderProductById(int id);
	
	@Select("select * from money_order_product where order_sn=#{0}")
	public List<OrderProduct> getOrderProductByOrderSn(long orderSn);
	
	@Select("select * from money_order_product where product_activity_id=#{0} ORDER BY id DESC limit #{1},#{2}")
	public List<OrderProduct> getOrderProductListByActivityId(int productActivityId, int start, int size);
	
	@Select("select * from money_order_product where product_activity_id=#{0}")
	public List<OrderProduct> getOrderProductByActivityId(int productActivityId);
	 
	@Select("select count(*) from money_order_product where product_activity_id=#{0}")
	public int countProductBuyHistory(int productActivityId);

	
	@Update("Update money_order_product set share=1 where id=#{0} AND user_id=#{1} AND share=0")
	public int updateShare(int id, int userId);
	
	@Insert("INSERT INTO `money_order_product` (`order_sn`, `user_id`, `product_id`, `product_activity_id`, `number`, `price`, `createtime`, `ip`, `ip_area`,`status`) VALUES "
			+ "(#{order_sn}, #{user_id}, #{product_id}, #{product_activity_id}, #{number},#{price},#{createtime},#{ip},#{ip_area},#{status})")
	public int createOrderProduct(OrderProduct op);


	//保存用户的抽奖号码
	//	insert into money_order_product_lottery(order_sn, product_activity_id, order_product_id , number)
	//	select 1442912646866, 11, 1, a.num + 1 from ( select IFNULL( (select max(number) from money_order_product_lottery where product_activity_id = 11), 10000000) as num ) as a

	public int createOrderProductLottery(List<OrderProductLottery> list);
	
	@Select("select * from money_order_product_lottery where order_product_id=#{0}")
	public List<OrderProductLottery> getLotteryByOrderProductId(int orderProductId);


	//根据用户和夺宝活动获取用户参加活动的所有夺宝号
	@Select("select * from money_order_product_lottery where user_id=#{0} and product_activity_id=#{1}")
	public List<OrderProductLottery> getAllLotteryByUserAndProductActivityId(int user_id, int product_activity_id);

	//根据用户和夺宝活动分页获取用户参加活动的夺宝号
	@Select("select * from money_order_product_lottery where user_id=#{0} and product_activity_id=#{1} limit #{2}, #{3}")
	public List<OrderProductLottery> getLotteryByUserAndProductActivityId(int user_id, int product_activity_id, int start, int limit);

	//获取当前活动已经试用过的夺宝号
	@Select("select * from money_order_product_lottery where product_activity_id=#{0}")
	public List<OrderProductLottery> getLotteryByProductActivityId(int productActivityId);
	
	@Select("select * from money_order_product_lottery where product_activity_id=#{0} AND number=#{1} limit 1")
	public OrderProductLottery getProductActivityLotteryByNumber(int productActivityId, int number);
	
	@Update("Update money_order_product set lottery=1 where id=#{0}")
	public int updateLotteryById(int id);

	//设置为已揭晓
	@Update("Update money_order_product set status=2 where product_activity_id=#{0} and status=1")
	public int updateAnnouncedByActivityId(int productActivityId);

	//设置为已过期
	@Update("Update money_order_product set status=3 where product_activity_id=#{0} and status=1")
	public int updateExpireByProductActivityId(int productActivityId);
	
	@Update("Update money_order_product set refund=1,refund_time=now() where id=#{0} and refund=0")
	public int updateRefundById(int id);
	
	@Select("SELECT IFNULL(SUM(number),0) FROM money_order_product where user_id=#{0} AND product_activity_id=#{1}")
	public int countUserBuy(int userId,int productActivityId);
	
	@Update("Update money_order_product set shipping_status=2,consignee=#{2},consignee_mobile=#{3},address=#{4},consignee_time=now() where id=#{1} AND user_id=#{0}")
	public int saveConsignee(int userId, int id, String consignee, String consignee_mobile,String address);
	
	@Update("Update money_order_product set shipping_status=4,sign_time=now() where id=#{1} AND user_id=#{0}")
	public int confirmGoods(int userId, int id);

	@Select("select * from money_order_product where user_id = #{0} and product_activity_id in (${param2})")
	List<OrderProduct> getUserOrderProductByActivities(int userId, String activityList);
}