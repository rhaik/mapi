package com.cyhd.service.dao.db.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserTaskNotify;


@Repository
public interface UserTaskNotifyMapper {
	
	@Insert("INSERT INTO `money_user_task_notify` (`id`, `task_id`, `app_id`, `action`, `starttime`, `expiretime`, `idfa`, `channel`, `ip`,`mac`,`callbackurl`, `type`) VALUES "
			+ "(#{id}, #{task_id}, #{app_id}, #{action}, #{starttime}, #{expiretime}, #{idfa}, #{channel}, #{ip}, #{mac}, #{callbackurl}, #{type})")
	public int addTask(UserTaskNotify userTask);

	@Update("update money_user_task_notify " +
			"set `task_id`=#{task_id}, action=#{action}, starttime=#{starttime}, expiretime=#{expiretime}, channel=#{channel}, ip=#{ip}, mac=#{mac}, callbackurl=#{callbackurl}, type=#{type}  where id=#{id} and app_id=#{app_id} and idfa=#{idfa}")
	public int restartTask(UserTaskNotify userTask);
	
	@Select("select * from money_user_task_notify where idfa=#{0} and app_id=#{1} order by id desc limit 1")
	public UserTaskNotify getByIdfaAndAppId(String idfa,int appId);
	
	@Update("update money_user_task_notify set vendor=1, vendortime=now(),status=2 where id=#{0} and vendor=0")
	public int finishVendorCallback(int id);
	
	@Update("update money_user_task_notify set callback=1, callbacktime=now() where id=#{0}")
	public int finishChannelCallback(int id);
	
	@Update("update money_user_task_notify set status=3,reward=1,rewardtime=now() where id=#{0} and reward=0")
	public int channelCallbackSuccess(int id);
	
	@Update("update money_user_task_notify set status=4,reward=1,rewardtime=now() where id=#{0} and reward=0")
	public int channelCallbackFail(int id);

	@Update("update money_user_task_notify set status=5,reward=1,rewardtime=now() where id=#{0} and reward=0")
	public int finishReportTask(int id);
	 
}