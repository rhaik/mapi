package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.App;


@Repository
public interface AppMapper {

	@Select("select * from money_app where status=1 limit #{0}, #{1}")
	public List<App> getApps(int start, int size);
	
	@Select("select * from money_app where id=#{0}")
	public App getApp(int id);
	
	@Select("select * from money_app where appstore_id=#{0} order by id desc limit 1")
	public App getAppByAppStoreId(String appStoreId);
	
	public int addAppByQucikTask(App app);
	
	@Select("select * from money_app where bundle_id=#{0} order by id desc limit 1")
	public App getAppByBundleID(String bunleID);
	
	@Update("update money_app set process_name=#{0} ,download_size=#{1} where id=#{2}")
	public int updateProcessName(String process,String size,int app_id);
	
}