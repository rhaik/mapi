package com.cyhd.service.dao.db.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserTaskFinishAuditMapper {

	@Insert("INSERT INTO `money_user_task_finish_audit` (`user_id`, `user_task_id`, `cause`, `status`, `createtime`) "
			+ "VALUES (#{0}, #{1}, #{2}, 1, now());")
	public int addAudit(int userId, long userTaskId,String cause, @Param("id")Integer id);
}