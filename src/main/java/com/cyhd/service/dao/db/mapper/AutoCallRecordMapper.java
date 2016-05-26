/*
 * All rights reserved.
 * 
 */
package com.cyhd.service.dao.db.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.AutoCallRecord;

@Repository
public interface AutoCallRecordMapper {	
	
	public int insert(AutoCallRecord callRecord) throws DataAccessException;
	public int afterCall(AutoCallRecord callRecord) throws DataAccessException;
	public int startReply(AutoCallRecord callRecord) throws DataAccessException;
	public int endReply(AutoCallRecord callRecord) throws DataAccessException;
	public AutoCallRecord getCallRecord(long id) throws DataAccessException;
	public AutoCallRecord getCallRecordByResource(long resourceId, int type) throws DataAccessException;
	
	
}
