package com.cyhd.service.impl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.service.dao.db.mapper.UserTaskFinishJobMapper;
import com.cyhd.service.dao.po.UserTaskFinishJob;
import com.cyhd.service.util.GlobalConfig;


@Service
public class UserTaskFinishJobService extends BaseService {

	@Resource
	private UserTaskFinishJobMapper userTaskFinishJobMapper;
	
	@Resource
	private UserTaskCalculateService userTaskCalculateService;
	
	private volatile long lastReadId = 0;
	private static final int fetch_size = 300;
	
	private ExecutorService executor = null;

	private int executor_size = 10;
	
	private volatile boolean running = true;

	@PostConstruct
	public void init() {
		ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("finish_job_thread");
				return t;
			}
		};
		if(GlobalConfig.isDeploy){
			executor_size = 20;
		}
		executor = Executors.newFixedThreadPool(executor_size, threadFactory);
	}

	@PreDestroy
	public void shutdown() {
		running = false;
		if (executor != null)
			executor.shutdown();
	}
	
	private void proccess(UserTaskFinishJob job) {
		try{
			if(job.isAuditPass()){
				userTaskCalculateService.onUserTaskFinishCheckOK(job.getUser_task_id());
			}else{
				userTaskCalculateService.onUserTaskFinishCheckFail(job.getUser_task_id(), job.getReason());
			}
			userTaskFinishJobMapper.setProcessFinished(getCurrentTable(), job.getId());
		}catch(Exception e){
			logger.error("UserTaskFinishJobService process error! job=" + job, e);
		}
	}
	
	private boolean start = false;
	
	public void startLoad() {
		if(start)
			return;
		start = true;
		while(true){
			try{
				logger.info("UserTaskFinishJobService read waitings..............");
				List<UserTaskFinishJob> jobs = userTaskFinishJobMapper.getWaitings(getCurrentTable(), this.lastReadId, fetch_size);
				if(jobs != null && jobs.size() > 0){
					for(final UserTaskFinishJob job : jobs){
						if(job.getId() > this.lastReadId){
							this.lastReadId = job.getId();
						}
						executor.execute(new Runnable() {
							@Override
							public void run() {
								proccess(job);
							}
						});
					}
				}
			}catch(Exception e){
				logger.error("UserTaskFinishJobService error!", e);
			}
			try{
				Thread.sleep(1000 * 30);
			}catch(Exception e){
			}
			if(!running)
				break;
		}
	}
	
	private static String getCurrentTable(){
		return "money_user_task_finish_job";
	}
	
	
	
	
}
