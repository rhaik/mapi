package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserInstalledApp;


@Repository
public interface UserInstalledAppMapper {
	
	public int insert(UserInstalledApp userInstalledApp);
	
	public List<Integer> getListByUserId(int userid);
}
