package com.cyhd.service.dao.db.mapper;

import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.IdAutoIncrease;

@Repository
public interface IdAutoIncreaseMapper {
	public void insert(IdAutoIncrease increase);
}
