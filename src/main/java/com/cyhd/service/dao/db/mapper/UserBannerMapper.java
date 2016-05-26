package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserBanner;

/**
 * 用户轮播图mapper
 */
@Repository
public interface UserBannerMapper {
	
	public int insert(UserBanner userBanner) throws DataAccessException;
	
	public List<UserBanner> getBanners() throws DataAccessException ;
	
	public int countBanners() throws DataAccessException ;
}

