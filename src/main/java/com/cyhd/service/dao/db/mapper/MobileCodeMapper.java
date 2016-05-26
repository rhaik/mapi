package com.cyhd.service.dao.db.mapper;

import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.MobileCode;

/**
 * 短信验证码
 * @author jack
 *
 * 2014年4月2日
 */
@Repository
public interface MobileCodeMapper {
	
	public void insert(MobileCode mobileCode) throws DataAccessException;
	
	public MobileCode getByParam(Map<String, Object> map) throws DataAccessException;
	
	public int update(Map<String, Object> map)throws DataAccessException;
	
}

