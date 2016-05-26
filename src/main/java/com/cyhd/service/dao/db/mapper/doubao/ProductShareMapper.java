package com.cyhd.service.dao.db.mapper.doubao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.doubao.ProductShare;
import com.cyhd.service.dao.po.doubao.ProductShareLog;


@Repository
public interface ProductShareMapper {
	
	@Select("select * from money_product_share where product_id=#{0} and status=1 order by id desc limit #{1},#{2}")
	public List<ProductShare> getProductShareListByProductId(int productId, int start, int size);
	
	@Select("select count(*) from money_product_share where product_id=#{0} and status=1")
	public int countProductShareListByProductId(int productId);
	
	@Select("select count(*) from money_product_share where user_id=#{0}")
	public int countMyProductShare(int userId);
	
	@Select("select * from money_product_share where user_id=#{0} order by id desc limit #{1},#{2}")
	public List<ProductShare> getMyProductShareList(int userId, int start, int size);
	
	@Select("select count(*) from money_product_share where status=1")
	public int countShare();
	
	@Select("select * from money_product_share where status=1 order by id desc limit #{0},#{1}")
	public List<ProductShare> getProductShareList(int start, int size);
	
	
	@Select("select * from money_product_share where id=#{0}")
	public ProductShare getProductShare(int id);
	
	@Insert("INSERT INTO `money_product_share` (`order_sn`, `product_id`, `order_product_id`, `product_activity_id`, `user_id`, `title`, `images`, `createtime`, `status`) VALUES "
			+ "(#{order_sn}, #{product_id}, #{order_product_id}, #{product_activity_id}, #{user_id}, #{title}, #{images}, #{createtime}, #{status})")
	public int add(ProductShare productShare);
	
	@Insert("INSERT INTO `money_product_share_log` (`share_id`, `operator`, `operator_time`, `status`, `remarks`) VALUES "
			+ "(#{share_id}, #{operator}, #{operator_time}, #{status}, #{remarks})")
	public int addLog(ProductShareLog log);

	@Select("select * from money_product_share where user_id=#{0} and product_activity_id=#{1} limit 1")
	ProductShare getMyShareByActivityId(int userId, int activityId);
}