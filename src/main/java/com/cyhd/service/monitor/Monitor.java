package com.cyhd.service.monitor;

import java.util.Date;

public class Monitor {
	
	private String servername;
	private String serverip;
	private String businessname;
	private String businessmethod;
	private int total;   //总请求次数
	private int succ;    //成功次数
	private int error;   //失败次数
	private int timeout; //超时次数
	private long totaltime; //总时间（不入库）
	private int avgtime;  // 平均时间
	private double errrate; //错误率
	private Date day;
	private int interval;
	private Date createtime;
	
	
	public Monitor(){
		
	}


	public String getServername() {
		return servername;
	}


	public void setServername(String servername) {
		this.servername = servername;
	}



	public String getServerip() {
		return serverip;
	}



	public void setServerip(String serverip) {
		this.serverip = serverip;
	}



	public String getBusinessname() {
		return businessname;
	}

	public void setBusinessname(String businessname) {
		this.businessname = businessname;
	}

	public String getBusinessmethod() {
		return businessmethod;
	}

	public void setBusinessmethod(String businessmethod) {
		this.businessmethod = businessmethod;
	}

	public int getSucc() {
		return succ;
	}


	public void increaseSucc(){
		this.succ++;
	}

	public void setSucc(int succ) {
		this.succ = succ;
		if(this.succ < 0){
			this.succ = 0;
		}
	}


	public long getTotaltime() {
		return totaltime;
	}


	public void setTotaltime(long totaltime) {
		this.totaltime = totaltime;
	}
	
	public void increaseTotaltime(int time){
		this.totaltime += time;
	}

	public void increaseErr(){
		this.error++;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public int getAvgtime() {
		return avgtime;
	}



	public void setAvgtime(int avgtime) {
		this.avgtime = avgtime;
	}



	public double getErrrate() {
		return errrate;
	}



	public void setErrrate(double errrate) {
		this.errrate = errrate;
	}

	public Date getCreatetime() {
		return createtime;
	}


	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public int getTotal() {
		return total;
	}


	public void setTotal(int total) {
		this.total = total;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void increaseTimeout(){
		this.timeout++;
	}


	@Override
	public String toString() {
		return "Monitor [servername=" + servername + ", serverip=" + serverip + ", businessname=" + businessname + ", businessmethod=" + businessmethod
				+ ", succ=" + succ + ", error=" + error + ", avgtime=" + avgtime + ", errrate=" + errrate + ", createtime=" + createtime + "]";
	}
	
	
	
}
