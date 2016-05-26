package com.cyhd.service.dao.db.mapper.doubao;

import com.cyhd.service.dao.po.doubao.UserDuobaoCoinLog;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;


@Repository
public interface UserDuobaoCoinLogMapper {

	@Insert("INSERT INTO `money_user_duobao_coin_log` ( `user_id`, `duobao_product_id`, `action`, `amount`, `total_amount`, `type`, `operator_time`, `remarks`) " +
			"VALUES (#{user_id}, #{duobao_product_id}, #{action}, #{amount}, #{total_amount}, #{type}, #{operator_time}, #{remarks})")
	public int add(UserDuobaoCoinLog duobaoCoinLog);

}