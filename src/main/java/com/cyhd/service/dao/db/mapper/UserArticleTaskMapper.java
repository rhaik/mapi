package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserArticleTask;

@Repository
public interface UserArticleTaskMapper {
	@Select("select * from money_user_article_task where user_id=#{0} and status=2 and `reward`=1 order by starttime desc limit #{1}, #{2}")
	public List<UserArticleTask> getUserFinishTaskLogs(int userid, int start, int size);
	
	@Select("select count(*) from money_user_article_task where user_id=#{0} and status=2 and `reward`=1")
	public int getUserArticleTaskTotal(int userid);
	
	@Select("select * from money_user_article_task where id=#{0}")
	public UserArticleTask getUserArticleTaskById(long id);

	@Insert("INSERT INTO `money_user_article_task` (`id`, `user_id`, `article_task_id`, `article_id`, `status`, `did`, `starttime`, `expiretime`, `idfa`,`client_type`,`sharetime`,`wx_account_id`,`user_ip`) VALUES "
			+ "(#{id}, #{user_id}, #{article_task_id}, #{article_id}, #{status}, #{did}, #{starttime}, #{expiretime},   #{idfa},#{client_type},#{sharetime},#{wx_account_id},#{user_ip})")
	public int addTask(UserArticleTask UserArticleTask);
	
	@Update("update money_user_article_task set article_task_id=#{article_task_id}, article_id=#{article_id}, `status`=#{status}, starttime=#{starttime},expiretime=#{expiretime}, "
			+ "finishtime=#{finishtime}, type=#{type}, reward=#{reward} where id=#{id}")
	public int updateTask(UserArticleTask ut);
	
	public List<UserArticleTask> getUserArticleTasksByTaskIds(int userId, List<Integer> taskIds);
	
	public List<UserArticleTask> getUserArticleTasksByArticleIds(int userId, List<Integer> articleIds);

	@Select("select * from money_user_article_task where user_id=#{0} and article_task_id=#{1} ")
	public UserArticleTask getUserArticleTask(int userId, int taskId);
	
	@Select("select * from money_user_article_task where user_id=#{0} and article_id=#{1}")
	public UserArticleTask getUserArticleTaskByArticleId(int userId, int appId);
	
	@Select("select * from money_user_article_task where article_id=#{0} and did=#{1}")
	public UserArticleTask getUserArticleTaskByDid(int appId, String did);

	@Select("select * from money_user_article_task where user_id=#{0} and status=1 and expiretime > now()")
	public List<UserArticleTask> getDoingTasks(int userId);

	@Update("update money_user_article_task set status=2, finishtime=now(), reward=1, rewardtime=now() where id=#{0} and user_id=#{1} and reward=0")
	public int finishTaskAndReward(long id, int userId);
	
	@Update("update money_user_article_task set status=2, finishtime=now() where id=#{0} and user_id=#{1}")
	public int finishTaskAndNoReward(long id, int userId);
	
	@Update("update money_user_article_task set reward=1, rewardtime=now() where id=#{0} and user_id=#{1}")
	public int reward(long id, int userId);

	@Update("update money_user_article_task set view_num=view_num+1 where article_task_id=#{0} and user_id=#{1}")
	public int addViewNum(long task_id, int userId);
	
	@Select("select * from money_user_article_task where starttime > from_unixtime(UNIX_TIMESTAMP()-(#{0} + 5*60)) AND starttime < from_unixtime(UNIX_TIMESTAMP() - #{0}) AND `status`=1 AND `will_expire` = 0 limit #{1},#{2}")
	public List<UserArticleTask> getExpireTask(int expiredTime,int start,int size);
	
	@Update("update money_user_article_task set will_expire=1 where id=#{0} and user_id=#{1}")
	public int updateWillExpire(long id, int userId);
	
	@Select("select * from money_user_article_task where idfa=#{0} and article_id=#{1}")
	public UserArticleTask getUserArticleTaskByIdfaAndAppId(String idfa,int appId);
	
	@Update("update money_user_article_task set wx_account_id=CONCAT(wx_account_id,',',#{1}) where id=#{0}")
	public int addWX_account_id(long id,int wx_account_id);

	/**
	 * 用户阅读数为0，未获得奖励，设置为过期状态
	 * @param id
	 * @return
	 */
	@Update("update money_user_article_task set status=3 where id=#{0}")
	public int setExpired(long id);
	
	@Select("select * from money_user_article_task where article_task_id=#{0} and status=1 and id>#{1} order by id limit #{2} ")
	public List<UserArticleTask> getUserArticleTasksToCloseAccount(int taskid, long start, int size);
	
	@Select("select  * from money_user_article_task where user_id=#{0} order by id desc limit 1")
	public UserArticleTask getUserLasttask(int userId);

	@Update("update money_user_article_task set opened=1 where user_id=#{0} and article_id=#{1}")
	public int setArticleOpened(int userId, int articleId);

}
