package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户任务上报日志
 * @author luckyee
 *
 */
public class UserTaskReport implements Serializable {
	
	private long id;
	private int user_id;
	private long user_task_id;
	private int report_index;
	private int report_gap;
	private int duration;
	private int devicetype;
	private String did;
	private Date reporttime;
	
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getDevicetype() {
		return devicetype;
	}
	public void setDevicetype(int devicetype) {
		this.devicetype = devicetype;
	}
	public String getDid() {
		return did;
	}
	public void setDid(String did) {
		this.did = did;
	}
	public Date getReporttime() {
		return reporttime;
	}
	public void setReporttime(Date reporttime) {
		this.reporttime = reporttime;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUser_task_id() {
		return user_task_id;
	}
	public void setUser_task_id(long user_task_id) {
		this.user_task_id = user_task_id;
	}
	public int getReport_index() {
		return report_index;
	}
	public void setReport_index(int report_index) {
		this.report_index = report_index;
	}
	public int getReport_gap() {
		return report_gap;
	}
	public void setReport_gap(int report_gap) {
		this.report_gap = report_gap;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	@Override
	public String toString() {
		return "UserTaskReport [id=" + id + ", user_id=" + user_id + ", user_task_id=" + user_task_id + ", report_index=" + report_index + ", report_gap="
				+ report_gap + ", duration=" + duration + ", devicetype=" + devicetype + ", did=" + did + ", reporttime=" + reporttime + "]";
	}
	
}
