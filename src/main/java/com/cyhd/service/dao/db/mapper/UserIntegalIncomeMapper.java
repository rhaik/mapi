package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserIntegalIncome;
/**
 * 参数{@code client_type} 已失效 具体参见
 * mapper
 *
 */
@Repository
public interface UserIntegalIncomeMapper {

	//public int createNewRecord(int userId);
	
	public UserIntegalIncome getIntegalByUserBysource(int userId,int source,int clientType);
	
	public List<UserIntegalIncome> getIntegalByUser(int userId);
	
	public int insertOrUpdate(UserIntegalIncome userIntegalIncome);
	
	public int update(UserIntegalIncome userIntegalIncome);
}
