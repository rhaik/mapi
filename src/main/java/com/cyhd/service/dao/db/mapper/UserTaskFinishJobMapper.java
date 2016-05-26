package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserTaskFinishJob;


@Repository
public interface UserTaskFinishJobMapper {

	@Select("select * from ${param1} where id > #{1} and state=1 order by id asc limit #{2}")
	public List<UserTaskFinishJob> getWaitings(String tablename, long lastId, int size);
	
	@Update("update ${param1} set state=2, statetime=now() where id=#{1}")
	public int setProcessFinished(String tablename, long id);
	
}