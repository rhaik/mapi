package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.Properties;

/**
 * system key-value information  mapper
 *
 */
@Repository
public interface PropertiesMapper {
	public int insert(Properties keyvalue) throws DataAccessException ;
	
	public Properties get(String key, String scope) throws DataAccessException ;
	
	public List<Properties> gets(String scope) throws DataAccessException ;
}
