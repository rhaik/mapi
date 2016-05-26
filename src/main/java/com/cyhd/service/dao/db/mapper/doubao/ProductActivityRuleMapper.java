package com.cyhd.service.dao.db.mapper.doubao;


import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.doubao.ProductActivityRule;


@Repository
public interface ProductActivityRuleMapper {
	
	//获取当前时间内活动规则，库存必须大于锁定库存
	@Select("select * from money_product_activity_rule where product_id=#{0} AND start_time<=now() AND end_time>now() AND status=1 AND stock>lock_stock limit 1")
	public ProductActivityRule getProductActivityRuleByProductId(int productId);
	
	@Select("select * from money_product_activity_rule where id=#{0}")
	public ProductActivityRule getProductActivityRuleById(int id);

	//获取当前未进行夺宝的商品规则
	@Select("select * from money_product_activity_rule where start_time<=now() AND now()<end_time AND status=1 AND stock>lock_stock AND product_id NOT IN(select product_id from money_product_activity where `status`= 1)")
	public List<ProductActivityRule> getNotDoingProductActivityRuleList();
	 
	//更新当前锁定库存数
	@Update("update money_product_activity_rule set lock_stock=lock_stock+1 where id=#{0} AND stock>lock_stock")
	public int updateLockStock(int id);
}