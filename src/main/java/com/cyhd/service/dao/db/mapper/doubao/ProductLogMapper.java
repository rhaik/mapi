package com.cyhd.service.dao.db.mapper.doubao;




import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.doubao.ProductLog;


@Repository
public interface ProductLogMapper {
	
	@Insert("INSERT INTO `money_product_log` (`product_id`, `operator`, `operation_content`, `remarks`, `operator_time`) VALUES "
			+ "(#{product_id}, #{operator}, #{operation_content}, #{remarks}, now())")
	public int add(ProductLog log);
	
}