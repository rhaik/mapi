package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

import com.cyhd.common.util.GenerateDateUtil;


public class UserTaskNotify implements Serializable {

	private static final long serialVersionUID = -9213025870158665237L;
	
	private int id;
	private int task_id;
	private int app_id;
	private int callback;		//渠道是否回调
	private Date callbacktime;	//渠道回调时间
	private int channel;		//渠道来源
	private int type; //	类型(1:排重2:回调)
	private int vendor;			//是否厂商回调
	private Date vendortime;	//厂商回调时间
	private Date starttime;
	private Date expiretime;
	private Date finishtime;
	private String callbackurl;
	private int status;	//1:已接任务2:厂商回调成功3:回调渠道成功4:回调渠道失败,5:上报成功
	private String action;
	private String idfa;
	private String ip; //用户接任务时的ip地址
	private String mac;
	private int reward;
	
	/**激活*/
	public static String  activate="activate";
	/**已激活*/
	public static String activated = "activated";
	/**disctinct*/
	public static final int TYPE_DISTINCT = 1;
	/**回调*/
	public static final int TYPE_CALLBACK = 2;
	/**上报*/
	public static final int TYPE_REPORT = 3;

	public boolean isVilid(){
		return GenerateDateUtil.getCurrentDate().before(expiretime);
	}
	/***
	 * 是不是上报任务
	 * @return true :yes
	 */
	public boolean isReportTask(){
		return type == 1;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	public int getApp_id() {
		return app_id;
	}
	public void setApp_id(int app_id) {
		this.app_id = app_id;
	}
	public String getCallbackurl() {
		return callbackurl;
	}
	public void setCallbackurl(String callbackurl) {
		this.callbackurl = callbackurl;
	}
	public int getCallback() {
		return callback;
	}
	public void setCallback(int callback) {
		this.callback = callback;
	}
	public Date getCallbacktime() {
		return callbacktime;
	}
	public void setCallbacktime(Date callbacktime) {
		this.callbacktime = callbacktime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getVendor() {
		return vendor;
	}
	public void setVendor(int vendor) {
		this.vendor = vendor;
	}
	public Date getVendortime() {
		return vendortime;
	}
	public void setVendortime(Date vendortime) {
		this.vendortime = vendortime;
	}
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	public Date getExpiretime() {
		return expiretime;
	}
	public void setExpiretime(Date expiretime) {
		this.expiretime = expiretime;
	}
	public Date getFinishtime() {
		return finishtime;
	}
	public void setFinishtime(Date finishtime) {
		this.finishtime = finishtime;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getIdfa() {
		return idfa;
	}
	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	@Override
	public String toString() {
		return "UserTaskNotify{" +
				"app_id=" + app_id +
				", task_id=" + task_id +
				", channel=" + channel +
				", vendor=" + vendor +
				", vendortime=" + vendortime +
				", status=" + status +
				", idfa='" + idfa + '\'' +
				", id=" + id +
				'}';
	}
}
