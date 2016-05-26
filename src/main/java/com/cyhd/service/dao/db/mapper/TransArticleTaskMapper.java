package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.TransArticleTask;

@Repository
public interface TransArticleTaskMapper {

	@Select("select * from money_trans_article_task where start_time < now() and show_end_time > now() and state=1  order by sort asc, id desc")
	public List<TransArticleTask> getValidTasks();
	
	@Select("select * from money_trans_article_task where id=#{0}")
	public TransArticleTask getTransArticleTask(int id);
	
	@Update("update money_trans_article_task set received_task=received_task+1, total_received_task=total_received_task+1 where id=#{0} and current_task > received_task")
	public int updateReceiveNum(int taskId);
	/***
	 * 系统任务 不判断时候有任务剩余
	 * @param taskId
	 * @return
	 */
	@Update("update money_trans_article_task set received_task=received_task+1, total_received_task=total_received_task+1 where id=#{0} ")
	public int updateReceiveNumBySysTask(int taskId);
	
	@Update("update money_trans_article_task set total_completed_task=total_completed_task+1 where id=#{0}")
	public int updateCompleteNum(int taskId);
	
	@Update("update money_trans_article_task set active_task=active_task+1 where id=#{0}")
	public int updateActiveNum(int taskId);
	
	@Select("select * from money_trans_article_task where start_time < now() and end_time > now() and state=1 order by id desc limit 1")
	public TransArticleTask getLastValidTask();
	
	/****
	 * 加上起止Id最小是5
	 * @param start
	 * @param size
	 * @return
	 */
	@Select("select * from money_trans_article_task where execute_flag=1 AND unix_timestamp(end_time) > unix_timestamp(date_sub(now(),interval 1 day)) AND unix_timestamp(end_time) < unix_timestamp(now()) and state=1 and id>5 limit #{0},#{1}")
	public List<TransArticleTask> getNotExecuteAccount(int start,int size);
	
	@Update("update money_trans_article_task set execute_flag=2 where id = #{0} and execute_flag=1")
	public int executeAccounting(int id);
	
	@Update("update money_trans_article_task set execute_flag=3 where id = #{0} and execute_flag=2")
	public int executeAccounted(int id);
	
}
