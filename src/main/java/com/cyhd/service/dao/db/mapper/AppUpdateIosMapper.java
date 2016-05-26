package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.AppUpdateIos;


@Repository
public interface AppUpdateIosMapper {

	@Select("select * from money_ios_app")
	public List<AppUpdateIos> getList();
	
	@Update("update money_ios_app set download_num=download_num+1 where id=#{0}")
	public int updateAppDownloadNumber(int id);
}
