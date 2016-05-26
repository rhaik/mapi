package com.cyhd.common.util.job;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyhd.common.util.async.Async;

/**
* 默认是单线程，无限队列大小
 * maxTask 并发数
 * capacity  固定队列大小
 * @param <T>
 */
public class AsyncJob<T> implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(AsyncJob.class);
	
	protected final String name;
	protected final LinkedBlockingQueue<T> elements;
	protected final JobHandler<T> handler;
	protected final Object lock = new Object();
	protected int runningTask = 0;  // 正在执行的线程数
	protected int maxTask = 1;       // 最大并发执行线程数
	protected TimeLoad succ = new TimeLoad();
	protected TimeLoad fail = new TimeLoad();
	
	public AsyncJob(JobHandler<T> handler) {
		this("AsyncJob", handler, 1, Integer.MAX_VALUE);
	}
	
	public AsyncJob(String name, JobHandler<T> handler) {
		this(name, handler, 1, Integer.MAX_VALUE);
	}
	
	public AsyncJob(String name, JobHandler<T> handler, int maxTask) {
		this(name, handler, maxTask, Integer.MAX_VALUE);
	}

	/**
	 * 
	 * @param handler
	 * @param maxTask 并发数
	 * @param capacity  固定队列大小
	 */
	public AsyncJob(String name, JobHandler<T> handler, int maxTask, int capacity) {
		if (handler == null) throw new IllegalArgumentException("handler is null");
		if (maxTask<1) throw new IllegalArgumentException("invalid maxTask: "+ maxTask);
		if (capacity<1) throw new IllegalArgumentException("invalid capacity: "+ capacity);
		this.name = name;
		this.handler = handler;
		this.maxTask = maxTask;
		elements = new LinkedBlockingQueue<T>(capacity);
	}
	
	/**
	 * 插入成功，返回true，如果队列已满，则返回false
	 * @param t
	 * @return
	 */
	public boolean offer(T t) {
		boolean result = false;
		if(t != null) {
			result = elements.offer(t);
			synchronized (lock) {
				// 如果没有运行，则运行
				if (runningTask < maxTask){
					runningTask++;
					Async.exec(this, false, true);
				}
			}
		}
		return result;
	}

	/**
	 * 插入成功，返回true，如果队列已满，则返回false
	 * @param t
	 * @return
	 * @throws InterruptedException 
	 */
	public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
		boolean result = false;
		if(t != null) {
			result = elements.offer(t, timeout, unit);
			synchronized (lock) {
				// 如果没有运行，则运行
				if (runningTask < maxTask){
					runningTask++;
					Async.exec(this, false, true);
				}
			}
		}
		return result;
	}
	
	/**
	 * 如果队列已满，则等待
	 * @param t
	 * @return
	 * @throws InterruptedException 
	 */
	public void put(T t) throws InterruptedException {
		if(t != null) {
			elements.put(t);
			synchronized (lock) {
				// 如果没有运行，则运行
				if (runningTask < maxTask){
					runningTask++;
					Async.exec(this, false, true);
				}
			}
		}
	}
	
	public int size() {
		return elements.size();
	}
	
	@Override
	public void run() {
		boolean stop = false;
		do {
			T t = null;
			while((t = elements.poll()) != null) {
				try {
					handler.handle(t);
					succ.request();
				}
				catch (Throwable e) {
					fail.request();
					log.error("error when aysncjob exec", e);
				}
			}
			// 这里要避开put的同时锁住lock了，所以这里也等待
			synchronized (lock) {
				if (elements.size() == 0) {
					stop = true;
					runningTask--;
				}
			}
		}while(!stop);
	}
}
