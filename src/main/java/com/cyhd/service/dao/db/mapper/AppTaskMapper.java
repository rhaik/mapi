package com.cyhd.service.dao.db.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.AppTask;


@Repository
public interface AppTaskMapper {
	//新增排除 第三方的快速任务
	@Select("select * from money_app_task where start_time < now() and show_end_time > now() and state=1 AND task_type=0 AND is_quick_task=0 order by sort asc, id desc")
	public List<AppTask> getValidTasks();
	
	@Select("select * from money_app_task where id=#{0}")
	public AppTask getAppTask(int id);
	
	@Select("select * from money_app_task where start_time < now() and end_time > now() and state=1 AND task_type=1 order by sort desc, start_time asc")
	public List<AppTask> getSystemAppTask();

	@Update("update money_app_task set received_task=received_task+1, total_received_task=total_received_task+1 where id=#{0}")
	public int updateReceiveNum(int taskId);

	@Update("update money_app_task set received_task=received_task-1, total_received_task=total_received_task-1 where id=#{0} and received_task > 0")
	public int reduceReceiveNum(int taskId);
	
	@Update("update money_app_task set total_completed_task=total_completed_task+1 where id=#{0}")
	public int updateCompleteNum(int taskId);
	
	@Update("update money_app_task set active_task=active_task+1 where id=#{0}")
	public int updateActiveNum(int taskId);
	
	@Select("select * from money_app_task where start_time < now() and end_time > now() and state=1 and task_type=0 order by id desc limit 1")
	public AppTask getLastValidTask();

	@Select("select * from money_app_task where app_id=#{0} AND distribution_id=#{1} AND start_time < now() and end_time > now() and state=1 order by id desc limit 1")
	public AppTask getTaskByAppIdAndChannelId(int appId, int channelId);

	@Select("select * from money_app_task where start_time > now() and  start_time < DATE_ADD(now(),INTERVAL 1 DAY)  and state=1 AND task_type=0 order by sort asc, start_time asc")
	public List<AppTask> getFutureTasks();
	
	public int addAppTask(AppTask appTask);
	
	@Update("update money_app_task set current_task=current_task+#{1} ,total_task=total_task+#{1} where id=#{0}")
	public int updateCurrTaskNum(int taskId,int addNum);
	
	@Select("select * from money_app_task where start_time < now() and show_end_time > now() and state=1 AND task_type=0 order by sort asc, id desc")
	public List<AppTask> getAllQuickTaskList(Date now);

	@Select("SELECT * FROM `money_app_task` WHERE `app_id`=#{0} AND ad_id=#{2} AND `start_time` < #{1} AND `end_time` > #{1}  AND `task_type`=0 AND `is_quick_task` > 0 order by sort asc, id desc limit 1")
	public AppTask getQuickTaskByAppId(int app_id, Date currentDate,String ad_id);
	
	@Update("update money_app_task set current_task=#{current_task},received_task=#{received_task},description=#{description},amount=#{amount},current_rank=#{current_rank},keywords=#{keywords} where id=#{id}")
	public int updateQuickAppTask(AppTask appTask);
	
	@Update("update money_app_task set current_task=0  where id=#{0} and current_task != 0")
	public int updateQuickTaskInvalid(int task_id);
	
	@Select("select id from money_app_task where app_id=#{0} and start_time < now() and end_time > now() AND task_type=0 order by sort asc, id desc limit 1")
	public Integer getExistTodayTask(int appId);
	

	@Select("SELECT * FROM `money_app_task` WHERE  `start_time` < now() AND date_add(`show_end_time`,interval 60 minute) >= now() AND `task_type`=0 AND `is_quick_task` > 0 AND direct_reward=1")
	public List<AppTask> getQuickDoingAppTask();
	
	@Select("select * from money_app_task where start_time < now() and show_end_time > now() and state=1 AND task_type=0 and is_quick_task=#{0} order by sort asc, id desc")
	public List<AppTask> getQuickTaskTasks(int quickChannel);


	//获取当前所有的分发任务
	@Select("select * from money_app_task where start_time < now() and end_time > now() and state=1 AND task_type=2 order by sort asc, id desc")
	public List<AppTask> getValidChannelTasks();
}
