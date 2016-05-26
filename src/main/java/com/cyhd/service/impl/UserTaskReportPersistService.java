package com.cyhd.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.UserTaskReportMapper;
import com.cyhd.service.dao.po.UserTaskReport;
import com.cyhd.service.util.RedisUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@Service
public class UserTaskReportPersistService extends BaseService {

	@Resource
	private UserTaskReportMapper userTaskReportMapper;
	
	@Resource(name=RedisUtil.NAME_ALIYUAN)
	private IJedisDao taskReportCacheDao;    //把每个用户的一个任务上报信息，保存到redis的列表里面，key是usertaskid

	private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	public void remove(long userTaskId) throws Exception {
		taskReportCacheDao.remove(getCacheKey(userTaskId));
	}
	
	public UserTaskReport getLastReport(long id) {
		UserTaskReport report = null;
		try {
			List<String> ls = taskReportCacheDao.getList(getCacheKey(id), 0, 20);
			if(ls != null && ls.size() > 0){
				report = stringToReport(ls.get(0));
			}
		} catch (Exception e) {
			logger.error("Usertaskreport persist error!", e);
		}
		if(report == null){
			report = this.getLastReportFromDB(id);
			if(report != null){
				try {
					this.addToReportCache(report);
				} catch (Exception e) {
					logger.error("Usertaskreport persist error!", e);
				}
			}
		}
		return report;
		
	}
	
	private UserTaskReport getLastReportFromDB(long id) {
		return userTaskReportMapper.getLastReport(this.getTableName(id), id);
	}

	private static String getCacheKey(long userTaskId){
		return RedisUtil.buildTaskReportKey(userTaskId);
	}
	
	public boolean addReport(UserTaskReport report){
		boolean b = this.addToReportDb(report);
		if(b){
			try {
				this.addToReportCache(report);
			} catch (Exception e) {
				
			}
		}
		return b;
	}
	
	private void addToReportCache(UserTaskReport report) throws Exception {
		String key = getCacheKey(report.getUser_task_id());
		this.taskReportCacheDao.addToList(key, reportToString(report));
		taskReportCacheDao.expire(key, 60 * 60);
	}
	
	private String reportToString(UserTaskReport report){
		return gson.toJson(report);
	}
	
	private UserTaskReport stringToReport(String str){
		return gson.fromJson(str, UserTaskReport.class);
	}


	private boolean addToReportDb(UserTaskReport report){
		int i = userTaskReportMapper.add(getTableName(report.getUser_task_id()), report);
		return i>0;
	}
	
	private String getTableName(long userTaskId){
		return "user_task_report";
	}
	
}
