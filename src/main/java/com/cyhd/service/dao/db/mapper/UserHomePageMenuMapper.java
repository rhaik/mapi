package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserHomePageMenu;

@Repository
public interface UserHomePageMenuMapper {

	//public int insert();
	
	//public List<UserHomePageMenu> getHomePageMenus();
	public List<UserHomePageMenu> getHomePageMenus();

}
