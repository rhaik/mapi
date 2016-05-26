package com.cyhd.service.dao.db.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserTask;


@Repository
public interface UserTaskMapper {

//	@Select("select * from money_user_task where user_id=#{0} AND `type`=0 order by id desc limit #{1}, #{2}")
//	public List<UserTask> getTasks(int userid, int start, int size);

	@Select("select * from money_user_task where user_id=#{0} AND `type`=0 and status=2 and `reward`=1 order by starttime desc limit #{1}, #{2}")
	public List<UserTask> getUserFinishTaskLogs(int userid, int start, int size);

	@Select("select count(*) from money_user_task where user_id=#{0} AND `type`=0 and status=2 and `reward`=1")
	public int getUserTaskTotal(int userid);

	@Select("select * from money_user_task where id=#{0}")
	public UserTask getUserTaskById(long id);

	@Insert("INSERT INTO `money_user_task` (`id`, `user_id`, `task_id`, `app_id`, `status`, `did`, `starttime`, `expiretime`, `finishtime`,`type`, `reward`, `rewardtime`,`idfa`, `user_ip`, `battery_id`) VALUES "
			+ "(#{id}, #{user_id}, #{task_id}, #{app_id}, #{status}, #{did}, #{starttime}, #{expiretime}, #{finishtime}, #{type}, #{reward}, #{rewardtime},#{idfa}, #{user_ip}, #{battery_id})")
	public int addTask(UserTask userTask);

	@Update("update money_user_task set task_id=#{task_id}, app_id=#{app_id}, `status`=#{status}, starttime=#{starttime},expiretime=#{expiretime}, "
			+ "finishtime=#{finishtime}, type=#{type}, reward=#{reward} where id=#{id}")
	public int updateTask(UserTask ut);

	public List<UserTask> getUserTasksByTaskIds(int userId, List<Integer> taskIds);

	public List<UserTask> getUserTasksByAppIds(int userId, List<Integer> appIds);

	@Select("select * from money_user_task where user_id=#{0} AND `type`=1")
	public List<UserTask> getSystemUserTaskByUserId(int userid);

	@Select("select * from money_user_task where user_id=#{0} and task_id=#{1}")
	public UserTask getUserTask(int userId, int taskId);

	@Select("select * from money_user_task where user_id=#{0} and app_id=#{1} order by task_id desc limit 1")
	public UserTask getUserTaskByAppId(int userId, int appId);

	@Select("select * from money_user_task where app_id=#{0} and did=#{1} order by task_id desc limit 1")
	public UserTask getUserTaskByDid(int appId, String did);

	@Select("select * from money_user_task where user_id=#{0} AND `type`=0 and status=1 and expiretime > now()")
	public List<UserTask> getDoingTasks(int userId);

	@Update("update money_user_task set status=2, finishtime=now(), reward=1, rewardtime=now() where id=#{0} and user_id=#{1} and status=1 and reward=0")
	public int finishTaskAndReward(long id, int userId);

	@Update("update money_user_task set status=2, finishtime=now() where id=#{0} and user_id=#{1} and status=1")
	public int finishTaskAndNoReward(long id, int userId);

	@Update("update money_user_task set status=2, finishtime=now(), confirm_finish=1, confirmtime=now(), reward=1, rewardtime=now() where id=#{0} and user_id=#{1} AND confirm_finish=0 AND reward=0")
	public int confirmFinishTaskReward(long id, int userId);

	@Update("update money_user_task set status=2, finishtime=now(), confirm_finish=1, confirmtime=now() where id=#{0} and user_id=#{1} AND confirm_finish=0")
	public int confirmFinishTask(long id, int userId);

	@Update("update money_user_task set reward=1, rewardtime=now() where id=#{0} and user_id=#{1} and status=2")
	public int reward(long id, int userId);

	@Update("update money_user_task set download=1, downloadtime=now() where id=#{0} and user_id=#{1}")
	public int finishDownload(long id, int userId);

	@Update("update money_user_task set active=1, active_time=now() where id=#{0} and user_id=#{1} and active=0")
	public int finishActive(long id, int userId);

	@Select("select * from money_user_task where starttime > from_unixtime(UNIX_TIMESTAMP()-(#{0} + 5*60)) AND starttime < from_unixtime(UNIX_TIMESTAMP() - #{0}) AND `status`=1 AND `type`=0 AND `will_expire` = 0")
	public List<UserTask> getExpireTask(int expiredTime);

	@Update("update money_user_task set will_expire=1 where id=#{0} and user_id=#{1}")
	public int updateWillExpire(long id, int userId);

	@Select("select * from money_user_task where app_id=#{1} and idfa=#{0} order by task_id desc limit 1")
	public UserTask getUserTaskByIdfaAndAppId(String idfa, int appId);

	/**
	 * 记录任务对应的app在客户端首次被打开
	 * @param id
	 * @param userId
	 * @return
	 */
	@Update("update money_user_task set opened=1, open_time=now() where id=#{0} and user_id=#{1} and opened=0")
	public int setAppOpened(long id, int userId);

	@Select("select count(id) from money_user_task where user_id=#{0} and reward=1 and type=0")
	public int countFinshTask(int userId);

	@Update("update money_user_task set earned_amount=#{2} where user_id=#{0} and id=#{1} and earned_amount=0")
	public int updateEarned_amount(int userId, long userTaskId, int earned_amount);


	/**
	 * 放弃任务
	 *
	 * @param userId
	 * @param userTaskId
	 * @return
	 */
	@Update("update money_user_task set expiretime=now(), status=4 where user_id=#{0} and id=#{1} and status=1")
	public int abortTask(int userId, long userTaskId);


	/**
	 * 重新开启已放弃的任务
	 * @return
	 */
	@Update("update money_user_task set starttime=#{starttime},expiretime=#{expiretime},status=1,task_id=#{task_id},user_ip=#{user_ip} where user_id=#{user_id} and id=#{id} and status=4")
	public int restartTask(UserTask userTask);
	
	@Update("update money_user_task set report=1 where user_id=#{0} and id=#{1} and report=0")
	public int updateReportStatus(int userId,long userTaskId);
	
	@Select("select * from money_user_task where task_id=#{0} AND `active`=1  AND report=0 AND starttime > #{1} AND starttime <= #{2} AND `type`=0  ")
	public List<UserTask> getReportTaskList(int appTaskId,Date start,Date end);
	

	@Select("select count(id) from money_user_task where battery_id=#{0} and app_id=#{1} and (status=2 or status=3)")
	public int countUserTaskNumByBatterIdAndAppId(String battery_id,int app_id);
	
	@Select("select count(id) from money_user_task where user_id=#{0} and download=1 and type=0 ")
	public int countUserDownloadTask(int userId);
	
}