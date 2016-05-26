package com.cyhd.service.dao.db.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserTaskReport;


@Repository
public interface UserTaskReportMapper {

	@Insert("INSERT INTO ${tablename} (`id`,`user_id`, `user_task_id`, `report_index`, `report_gap`, `duration`, `reporttime`, `devicetype`, `did`) VALUES"
			+ " (#{report.id}, #{report.user_id}, #{report.user_task_id}, #{report.report_index}, #{report.report_gap}, #{report.duration}, #{report.reporttime}, #{report.devicetype}, #{report.did})")
	public int add(@Param("tablename")String tablename, @Param("report")UserTaskReport report);

	@Select("select * from ${tablename} where user_task_id=#{1} order by reporttime desc limit 1")
	public UserTaskReport getLastReport(@Param("tablename")String tablename, long id);
	
}