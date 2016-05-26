package com.cyhd.service.dao.db.mapper.doubao;


import java.util.Date;
import java.util.List;

import com.cyhd.service.dao.po.doubao.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.doubao.ProductActivity;



@Repository
public interface ProductActivityMapper {
	//根据ID获取数据
	@Select("select * from money_product_activity where id=#{0}")
	public ProductActivity getProductActivityById(int id);
	
	//获取最新一期活动商品
	@Select("select * from money_product_activity order by id limit 1")
	public ProductActivity getLastProductActivity();
	
	@Insert("INSERT INTO `money_product_activity` (`product_id`, `activity_rule_id`, `product_name`, `product_lable`,`product_number`, `price`, `presell`, `start_time`, `end_time`, `createtime`, `buy_number`, `status`,`number`,`min_buy_number`,`vendor_price`) VALUES "
			+ "(#{product_id}, #{activity_rule_id},#{product_name},#{product_lable}, #{product_number}, #{price}, #{presell}, #{start_time},#{end_time},#{createtime},#{buy_number},#{status},#{number},#{min_buy_number},#{vendor_price})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	public int add(ProductActivity product);
	
	//根据商品的最大期数
	@Select("select product_number from money_product_activity where product_id=#{0} order by product_number desc limit 1")
	public Integer getMaxNumberByProductId(int productId);
	
	//设置为已揭晓，同时保存获奖号码，中奖用户等信息
	@Update("update money_product_activity set status=3, lottery_time=now(), lottery_user=#{1}, lottery_number=#{2},lottery_order=#{3},lottery_order_product=#{4}, lottery_buy_number=#{5} where id=#{0} AND status=2")
	public int setLotterySuccess(int id, int userId, int number, long lotteryOrder,int orderProductId, int totalBuy);
	
	//更新购买数
	@Update("update money_product_activity set buy_number=buy_number+#{1} where id=#{0} AND status=1 AND (buy_number+#{1})<=number")
	public int updateBuyNumber(int id, int number);
	
	//揭晓中
	@Update("update money_product_activity set finish_time=now(),status=2,shishicai=#{1} where id=#{0} AND buy_number=number")
	public int updateAnnounced(int id, int periods);
	
	//已过期
	@Update("update money_product_activity set expire_time=now(),status=5 where id=#{0} and expire_time is null")
	public int updateExpire(int id);
	
	//已退款
	@Update("update money_product_activity set refund_time=now(),refund=1 where id=#{0}")
	public int updateRefund(int id);
	
	//更新历史订单记录
	@Update("update money_product_activity set history_order=1,history_time=now() where id=#{0} and history_order = 0")
	public int updateHistoryOrder(int id);
	
	//获取揭晓中和已揭晓的商品
	@Select("select * from money_product_activity where status = 2 order by finish_time ASC LIMIT #{0},#{1}")
	public List<ProductActivity> getAnnouncingProductActivity(int start, int size);

	//统计揭晓中和已揭晓的商品
	@Select("select count(*) from money_product_activity where status in (2,3)")
	public int countAnnouncedProductActivity();
	
	//获取最快揭晓商品
	@Select("select *, buy_number/number as buyRate  from money_product_activity where status=1 order by buyRate DESC LIMIT #{0},#{1}")
	public List<ProductActivity> getLeastResidualProductActivity(int start, int size);

	//统计最快揭晓商品
	@Select("select count(*) from money_product_activity where status=1")
	public int countProductActivity();

	//根据时间逆序获取进行中的夺宝活动
	@Select("select * from money_product_activity where status=1 order by id DESC LIMIT #{0},#{1}")
	public  List<ProductActivity> getProductActivityOrderByTime(int start, int size);
	
	//获取价高商品
	@Select("select * from money_product_activity where status=1 order by number DESC LIMIT #{0},#{1}")
	public List<ProductActivity> getProductActivityOrderByMaxPrice(int start, int size);

	//获取价低商品
	@Select("select * from money_product_activity where status=1 order by number ASC LIMIT #{0},#{1}")
	public List<ProductActivity> getProductActivityOrderByMinPrice(int start, int size);
	
	//获取往期揭晓期数
	@Select("select * from money_product_activity where product_id=#{0} AND status =3 order by lottery_time DESC LIMIT #{1},#{2}")
	public List<ProductActivity> getHistoryActivityList(int productId, int start, int size);
	
	//统计往期揭晓期数
	@Select("select count(*) from money_product_activity where product_id=#{0} AND status =3")
	public int countHistoryActivity(int productId);

	//获取最近中奖的订单，按时间倒序
	@Select("select * from money_product_activity where status =3 order by lottery_time DESC LIMIT #{0},#{1}")
	public List<ProductActivity> getLotteryActivityList(int start, int size);
	
	//获取已经满员的商品活动
	@Select("select * from money_product_activity where status in(1,2) AND number=buy_number")
	public List<ProductActivity> getSoldoutProductList();
	
	//获取当前商品最近一期的活动
	@Select("select * from money_product_activity where product_id=#{0} ORDER BY id DESC LIMIT 1")
	public ProductActivity getLatestProductActivityByProductId(int productId);


	//获取过期的活动列表，理论上过期的不多，暂时不分页，每次取10条
	@Select("select * from money_product_activity where status=1 and buy_number < number and end_time < NOW() LIMIT 10 ")
	public List<ProductActivity> getExpiredProductActivity();

	//获取用户参与过的活动列表
	List<ProductActivity> getUserJoinedActivities(int userId, int status, int start, int size);

	//获取用户参与过的活动数量
	int countUserJoinedActivites(int userId, int status);

	//获取用户在某时间后的中奖纪录
	@Select("select * from money_product_activity where lottery_user=#{0} and lottery_time > #{1} LIMIT 1")
	ProductActivity getUserLatestLottery(int userId, Date lastLotteryDate);
}