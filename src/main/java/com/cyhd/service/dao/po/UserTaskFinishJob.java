package com.cyhd.service.dao.po;

import java.util.Date;

/**
 * 用户任务完成日志
 * @author luckyee
 *
 */
public class UserTaskFinishJob {

	private long id;
	private int user_id;
	private long user_task_id;
	private Date finishtime;
	private int state;  //1:未处理，2：已处理
	private Date statetime; //处理时间
	private int audit_results;
	private String reason;
	
	public static final int AUDIT_PASS_AUTO = 1;
	public static final int AUDIT_FAIL_AUTO = 2;
	
	public static final int AUDIT_PASS = 3;
	public static final int AUDIT_FAIL = 4;
	
	public boolean isAuditPass(){
		return this.audit_results == AUDIT_PASS_AUTO || this.audit_results == AUDIT_PASS;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public long getUser_task_id() {
		return user_task_id;
	}
	public void setUser_task_id(long user_task_id) {
		this.user_task_id = user_task_id;
	}
	public Date getFinishtime() {
		return finishtime;
	}
	public void setFinishtime(Date finishtime) {
		this.finishtime = finishtime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public Date getStatetime() {
		return statetime;
	}
	public void setStatetime(Date statetime) {
		this.statetime = statetime;
	}
	public int getAudit_results() {
		return audit_results;
	}
	public void setAudit_results(int audit_results) {
		this.audit_results = audit_results;
	}
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "UserTaskFinishJob [id=" + id + ", user_id=" + user_id + ", user_task_id=" + user_task_id + ", finishtime=" + finishtime + ", state=" + state
				+ ", statetime=" + statetime + ", audit_results=" + audit_results + "]";
	}
	
}
