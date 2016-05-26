package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.Account;


@Repository
public interface AcountMapper {
	
	@Select("select * from money_public_account where id=#{0}")
	public Account getAccount(int id) throws DataAccessException;
	
	@Update("update money_public_account set wxaccesstoken=#{wxaccesstoken}, wxtokenfetchtime=#{wxtokenfetchtime}, wxtokenexpiretime=#{wxtokenexpiretime},updatetime=now() where id=#{id}")
	public int updateAccessToken(Account account) throws DataAccessException;
	
	@Select("select * from money_public_account where estate=1")
	public List<Account> getAllIds() throws DataAccessException;
	
}
