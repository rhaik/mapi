package com.cyhd.common.util.async;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
/**
 * request之后延迟一定时间执行，此期间request将会合并一起执行
 * 执行过程中request将会在执行后检查并执行
 */
public class ScheduleTask implements Delayed, Runnable{
	
	// millisecond
	private long delayTime;
	private long requestTime = 0; // 表示请求时间,0 表示无请求
	private boolean running = false;
	private Runnable runner;
	
	public ScheduleTask(Runnable runner, long delayTime){
		this.runner = runner;
		this.delayTime = delayTime;
	}
	
	// 从现在开始延迟执行的时间
	public void request() {
		synchronized (this) {
			if (requestTime == 0){
				// 初次请求
				requestTime = System.currentTimeMillis();
				if (!running){
					Async.schedule(this, delayTime, TimeUnit.MILLISECONDS);
				}
			}
		}
	}
	
	@Override
	public int compareTo(Delayed o) {
		if (o == this) return 0;
		return (int)(this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long delay = delayTime - (System.currentTimeMillis() - requestTime);
		return unit.convert(delay, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		synchronized (this) {
			running = true;
			requestTime = 0; // 消除请求记录
		}
		runner.run();
		synchronized (this) {
			running = false;
			if (requestTime > 0){
				// 执行过程中，有新的请求
				long delay = delayTime - (System.currentTimeMillis() - requestTime);
				if (delay <=0 ){
					Async.exec(this);
				}else {
					Async.schedule(this, delay, TimeUnit.MILLISECONDS);
				}
			}
		}
	}

}
