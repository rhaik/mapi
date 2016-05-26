package com.cyhd.common.util.job;

import java.util.concurrent.atomic.AtomicInteger;

public class JobResult {
	
	private int pushBatchId;
	//总数
	private int total;
	
	//成功次数
	private AtomicInteger success;
	
	//失败次数
	private AtomicInteger fail;
	
	public JobResult(){
		this(0);
	}
	
	public JobResult(int total){
		this.total = total;
		success = new AtomicInteger();
		fail = new AtomicInteger();
	}
	
	public boolean isFinished(){
		return success.get() + fail.get() >= total;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void increaseSuc(){
		success.incrementAndGet();
	}
	public void increaseFail(){
		fail.incrementAndGet();
	}
	
	public int getSuccess(){
		return success.get();
	}
	
	public int getFail(){
		return fail.get();
	}

	public int getPushBatchId() {
		return pushBatchId;
	}

	public void setPushBatchId(int pushBatchId) {
		this.pushBatchId = pushBatchId;
	}
	
}
