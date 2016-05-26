package com.cyhd.service.dao.db.mapper;

import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.UserTask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AppVendorMapper {

	/**
	 * 根据id获取app vendor
	 * @param id
	 * @return
	 */
	@Select("select * from money_app_vendor where id=#{0}")
	public AppVendor getAppVendor(int id);


	@Select("select * from money_app_vendor where app_key=#{0}")
	AppVendor getAppVendorByAppKey(String appkey);
}