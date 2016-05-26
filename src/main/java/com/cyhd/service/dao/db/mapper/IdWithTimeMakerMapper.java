package com.cyhd.service.dao.db.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.IdWithTimeMaker;

@Repository
public interface IdWithTimeMakerMapper {

	public int insert(IdWithTimeMaker idWithTimeMaker) throws DataAccessException;
	
	public int truncate() throws DataAccessException ;
}
