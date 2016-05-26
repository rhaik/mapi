package com.cyhd.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.cyhd.service.util.GlobalConfig;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.LiveAccess;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.TransArticleMapper;
import com.cyhd.service.dao.db.mapper.TransArticleTaskMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.TransArticle;
import com.cyhd.service.dao.po.TransArticleTask;
import com.cyhd.service.vo.UserArticleTaskVo;

@Service
public class TransArticleTaskService extends BaseService {

	@Resource
	private TransArticleMapper transArticleMapper;
	
	@Resource
	private TransArticleTaskMapper transArticleTaskMapper;

	@Resource
	PropertiesService propertyService;
	
	private ConcurrentHashMap<Integer, TransArticle> cachedTransArticles = new ConcurrentHashMap<Integer, TransArticle>();
	/**缓存时间变为5秒*/
	private final int ttl_valids_tasks = 10*1000;
	
	private LiveAccess<List<TransArticleTask>> cachedValidTasks = new LiveAccess<List<TransArticleTask>>(ttl_valids_tasks, null);
	
	private CacheLRULiveAccessDaoImpl<TransArticleTask> cachedTasks = new CacheLRULiveAccessDaoImpl<TransArticleTask>(1000 * 30, 1024);

	private CacheLRULiveAccessDaoImpl<Integer> readNumCache = new CacheLRULiveAccessDaoImpl<>(Constants.minutes_millis, 50);
	
	private volatile boolean loading = false;

	private final int lastValidTaskTTL = Constants.minutes_millis*2;
	//最新可用的TransArticleTasktask
	private LiveAccess<TransArticleTask> lastValidTask = new LiveAccess<TransArticleTask>(lastValidTaskTTL, null);
	
	private final int systemTaskTTL = Constants.minutes_millis*10;
	private LiveAccess<TransArticleTask> systemTask = new LiveAccess<TransArticleTask>(systemTaskTTL, null);

	private volatile boolean loadingHosts = false;
	private List<String> hostList = null;

	@PostConstruct
	public void reloadShareHosts(){
		if (loadingHosts){
			return;
		}
		try{
			loadingHosts = true;
			String hosts = propertyService.getShareHosts();
			if (hosts != null){
				List<String> list = new ArrayList<>();
				String[] hostArray = hosts.split(",");
				for (String host : hostArray){
					if (host != null && host.trim().length() > 0) {
						list.add(host.trim());
					}
				}
				hostList = list;
			}
		}finally {
			loadingHosts = false;
		}
	}

	public void reloadArticles(){
		if(loading)
			return;
		try{
			loading = true;
			int start = 0;
			int size = 100;
			while(true){
				List<TransArticle> articles = transArticleMapper.getArticles(start, size);
				if(articles == null || articles.size() == 0)
					break;
				start += size;
				for(TransArticle article : articles){
					cachedTransArticles.put(article.getId(), article);
				}
			}
			
		}finally{
			loading = false;
		}
	}
	
	public TransArticle getTransArticle(int id){
		return this.cachedTransArticles.get(id);
	}
	
	public TransArticle getTransArticleByName(String name){
		Collection<TransArticle> articles = cachedTransArticles.values();
		for(TransArticle transArticle : articles){
			if(transArticle.getName().equalsIgnoreCase(name)){
				return transArticle;
			}
		}
		return null;
	}
	
	
	public List<TransArticleTask> getTransArticleTasks(int articleId, int type){
		List<TransArticleTask> tasks = getValidTasks(type);

		List<TransArticleTask> results = tasks.stream().filter(tk -> tk.getArticle_id() == articleId).collect(Collectors.toList());
		return results;
	}
	
	public TransArticleTask getTransArticleTask(int taskId){
		TransArticleTask articleTask = this.cachedTasks.get(String.valueOf(taskId));
		if(articleTask == null){
			articleTask = this.transArticleTaskMapper.getTransArticleTask(taskId);
			if(articleTask != null){
				cachedTasks.set(String.valueOf(taskId), articleTask);
			}
		}
		
		return articleTask;
	}
	
	public List<TransArticleTask> getValidTasks(int type){
		List<TransArticleTask> tasks = cachedValidTasks.getElement();
		if(tasks == null){
			tasks = this.transArticleTaskMapper.getValidTasks();
			if(tasks != null){
				Map<Integer, TransArticleTask> appTaskMap = new LinkedHashMap<Integer, TransArticleTask>();
				tasks.stream().forEach(task -> {
					TransArticleTask currentTask = appTaskMap.get(task.getArticle_id());
					if(currentTask != null){
						if (task.getStart_time().after(currentTask.getStart_time())){ //后开始的任务优先展示
							appTaskMap.put(task.getArticle_id(), task);
						}
					}else{
						appTaskMap.put(task.getArticle_id(), task);
					}
				});
				tasks = new ArrayList<>(appTaskMap.values());
				cachedValidTasks = new LiveAccess<List<TransArticleTask>>(ttl_valids_tasks, tasks);
			}
		}

		//根据类型取列表
		List<TransArticleTask> taskList = new ArrayList<>();
		if (tasks != null && tasks.size() > 0){
			taskList = tasks.stream().filter(tk -> tk.getType() == type).collect(Collectors.toList());
		}

		return taskList;
	}
	
	public List<UserArticleTaskVo> getValidUserArticleTaskVos(int type){
		List<UserArticleTaskVo> vos = new ArrayList<UserArticleTaskVo>();
		List<TransArticleTask> tasks = getValidTasks(type);
		for(TransArticleTask task : tasks){
			UserArticleTaskVo vo = new UserArticleTaskVo();
			vo.setTransArticle(this.getTransArticle(task.getArticle_id()));
			vo.setTransArticleTask(task);
			vos.add(vo);
		}
		return vos;
	}

	/**
	 * 获取分享的域名
	 * @return
	 */
	public String getRandomShareHost(int articleId){
		if (hostList != null && hostList.size() > 0){
			return hostList.get(articleId % hostList.size());
		}
		return GlobalConfig.base_url_https;
	}

	/**
	 * 用户添加任务成功，则处理任务相关信息，给总接收任务数+1，当前接收数+1
	 * @param taskId
	 */
	public boolean onUserReceiveTask(int taskId) {
		int i = this.transArticleTaskMapper.updateReceiveNum(taskId);
		if(i > 0){
			this.cachedTasks.remove(String.valueOf(taskId));
		}
		return i > 0;
	}
	/**
	 * 用户激活任务数加1
	 * @param taskId
	 * @return
	 */
	public boolean onActiveTask(int taskId){
		return this.transArticleTaskMapper.updateActiveNum(taskId) > 0;
	}
	public boolean onTaskFinished(int taskId){
		int i = this.transArticleTaskMapper.updateCompleteNum(taskId);
		if(i > 0){
			//this.cachedTasks.remove(String.valueOf(taskId));
		}
		return i > 0;
	}
	
	/**
	 * 獲取最新的可用的
	 * @return
	 */
	public TransArticleTask getLastArticleTaskTask(){
		TransArticleTask  task = lastValidTask.getElement();
		
		if(task== null){
			task = transArticleTaskMapper.getLastValidTask();
			lastValidTask = new LiveAccess<TransArticleTask>(lastValidTaskTTL, task);
		}
		
		return task;
	}
	
	public boolean updateReceiveNum(int taskId){
		boolean ret =  transArticleTaskMapper.updateReceiveNum(taskId) > 0;
		if(ret){
			this.cachedTasks.remove(String.valueOf(taskId));
		}
		return ret;
	}
	
	public boolean updateReceiveNumBySysTask(int taskId){
		boolean ret =  transArticleTaskMapper.updateReceiveNumBySysTask(taskId) > 0;
		if(ret){
			this.cachedTasks.remove(String.valueOf(taskId));
		}
		return ret;
	}
	
	public List<TransArticleTask> getNotExecuteAccount(int start,int size){
		return transArticleTaskMapper.getNotExecuteAccount(start, size);
	}
	
	public boolean executeAccounting(int id){
		return transArticleTaskMapper.executeAccounting(id) > 0;
	}
	
	public boolean executeAccounted(int id){
		return transArticleTaskMapper.executeAccounted(id) > 0;
	}


	/**
	 * 获取文章的阅读数量
	 * @param articleId
	 * @return
	 */
	public int getReadNum(int articleId){
		String key = "" + articleId;
		Integer readNum = readNumCache.get(key);
		if (readNum == null){
			readNum = transArticleMapper.getReadNum(articleId);
			if (readNum != null){
				readNumCache.set(key, readNum);
			}
		}
		return readNum == null? 0 : readNum.intValue();
	}
	/**
	 * 创建这个系统任务时  需要将 execute_flag弄成3标示已完成 这样后台job就不会去执行
	 * @return
	 */
	public TransArticleTask getSystemTask(){
		TransArticleTask task = systemTask.getElement();
		if(task == null){
			task = transArticleTaskMapper.getTransArticleTask(1);
			
			if(task != null){
				systemTask = new LiveAccess<TransArticleTask>(systemTaskTTL, task);
			}
		}
		return task;
	}
}

