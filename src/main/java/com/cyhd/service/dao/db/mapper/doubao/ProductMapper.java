package com.cyhd.service.dao.db.mapper.doubao;



import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.doubao.Product;


@Repository
public interface ProductMapper {
	
	@Select("select * from money_product where id=#{0}")
	public Product getProductById(int id);
	
	@Select("select * from money_product where is_enabled=1 AND status=1")
	public List<Product> getProducts();
	
	//更新当前库存数
	@Update("update money_product set sell_stock=sell_stock+1 where id=#{0}")
	public int updateSellStock(int id);
}