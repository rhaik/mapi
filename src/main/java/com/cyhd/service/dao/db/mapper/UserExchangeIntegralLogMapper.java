package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserExchangeIntegralLog;


@Repository
public interface UserExchangeIntegralLogMapper {
	
	@Select("select * from money_user_exchange_integral_log where user_id=#{0} and source=#{1} and client_type=#{2} ORDER BY createtime DESC LIMIT #{3},#{4}")
	public List<UserExchangeIntegralLog> getListByUserId(int userid,int source,int clientType, int start, int size);
	
	
	@Select("select count(*) from money_user_exchange_integral_log where user_id= #{0} and source=#{1} and client_type=#{2}")
	public int countUserExchangeIntegralLog(int userId,int source,int clientType);
	
	public int insert(UserExchangeIntegralLog log);
}