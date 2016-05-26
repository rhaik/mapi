package com.cyhd.common.util.job;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyhd.common.util.async.Async;

/**
 * 延迟执行，，即放进去之后，至少延迟waitTime(毫秒)之后才会被执行
 * 默认是单线程，无限队列大小
 * 注意：不保证顺序性
 * maxTask 并发数
 *
 * @param <T>
 */
public class DelayJob<T> implements Runnable{
	private static Logger log = LoggerFactory.getLogger(DelayJob.class);
	
	protected final String name;
	protected final DelayQueue<DelayElement<T>> elements = new DelayQueue<DelayElement<T>>();
	protected final JobHandler<T> handler;
	protected final long waitTime; // 延迟时间(毫秒)
	protected final Object lock = new Object();
	protected int runningTask = 0;  // 正在执行的线程数
	protected int maxTask = 1;       // 最大并发执行线程数
	protected boolean daemon = false;
	
	public DelayJob(JobHandler<T> handler, long waitTime) {
		this("DelayJob", handler, waitTime, 1, false);
	}
	
	public DelayJob(String name, JobHandler<T> handler, long waitTime) {
		this(name, handler, waitTime, 1,  false);
	}
	
	public DelayJob(String name, JobHandler<T> handler, long waitTime, int maxTask) {
		this(name, handler, waitTime, maxTask,  false);
	}
	
	public DelayJob(String name, JobHandler<T> handler, long waitTime, int maxTask, boolean daemon) {
		this.name = name;
		this.handler = handler;
		this.waitTime = waitTime;
		this.maxTask = maxTask;
		this.daemon = daemon;
	}
	
	/**
	 * 插入成功，返回true，如果队列已满，则返回false
	 * @param t
	 * @return
	 */
	public boolean offer(T t) {
		boolean result = false;
		if(t != null) {
			result = elements.offer(new DelayElement<T>(t));
			synchronized (lock) {
				// 如果没有运行，则运行
				if (runningTask < maxTask){
					runningTask++;
					Async.exec(this, false, daemon);
				}
			}
		}
		return result;
	}
	
	/**
	 * 插入成功，返回true，如果队列已满，则返回false
	 * @param t
	 * @param time 延迟时间 单位毫秒ms
	 * @return
	 */
	public boolean offer(T t, long time) {
		boolean result = false;
		if(t != null) {
			result = elements.offer(new DelayElement<T>(t, time));
			synchronized (lock) {
				// 如果没有运行，则运行
				if (runningTask < maxTask){
					runningTask++;
					Async.exec(this, false, daemon);
				}
			}
		}
		return result;
	}
	
	public boolean remove(T t){
		return elements.remove(new DelayElement<T>(t));
	}
	
	public int size() {
		return elements.size();
	}

	@Override
	public void run() {
		boolean stop = false;
		do {
			DelayElement<T> element = null;
			while((element = elements.poll()) != null) {
				try {
					handler.handle(element.t);
				}
				catch (Throwable e) {
					log.error("error when delayjob exec", e);
				}
			}
			synchronized (lock) {
				if (elements.size() == 0) {
					stop = true;
					runningTask--;
				}
			}
		}while(!stop);
	}
	
	private class DelayElement<E> implements Delayed {

		public final E t;
		private final long endTime;
		public DelayElement(E t){ 
			this.t = t;
			this.endTime = System.currentTimeMillis() + waitTime;
//			System.out.println("[time] "+endTime+", [t] "+t);
		}
		
		public DelayElement(E t, long time){ 
			this.t = t;
			this.endTime = System.currentTimeMillis() + time;
//			System.out.println("[time] "+endTime+", [t] "+t);
		}
		
		@Override
		public int compareTo(Delayed o) {
			return (int)(getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
		}

		@Override
		public boolean equals(Object obj) {
			DelayElement de = (DelayElement) obj;
			return this.t.equals(de.t);
		}

		@Override
		public long getDelay(TimeUnit unit) {
//			System.out.println("[unit] "+unit);
			long time = endTime - System.currentTimeMillis();
//			System.out.println("[Delay] "+time+", [t] "+t);
			long x = unit.convert(time, TimeUnit.MILLISECONDS);
//			System.out.println("[x] "+x+", [t] "+t);
			return x;
		}

	}
	
}
