package com.cyhd.common.util.job;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedbackAsyncJob<T> {
	
	private static Logger logger = LoggerFactory.getLogger(FeedbackAsyncJob.class);
	
	private ExecutorService executor = null;
	
	private ConcurrentHashMap<String, JobResult> results = new ConcurrentHashMap<String, JobResult>();
	
	public FeedbackAsyncJob(final String name, int threadNum){
		executor = Executors.newFixedThreadPool(threadNum, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName(name);
				return t;
			}
		});
	}
	
	
	public void submit(int pushBatchId, final String name, final Collection<T> elements, final JobHandler<T> handler){
		final JobResult jobResult = new JobResult();
		jobResult.setPushBatchId(pushBatchId);
		results.put(name, jobResult);
		if(elements == null || elements.size() == 0){
			return;
		}
		jobResult.setTotal(elements.size());
		executor.execute(new Runnable() {
			@Override
			public void run() {
				for(T t : elements){
					try{
						if(handler.handle(t))
							jobResult.increaseSuc();
						else
							jobResult.increaseFail();
					}catch(Exception e){
						logger.error("FeedbackAsyncJob run job name=" + name + ", element="+t + " error", e);
						jobResult.increaseFail();
					}
				}
			}
		});
	}
	
	public JobResult getJobResult(String name){
		JobResult result = results.get(name);
		if(result == null){
			return new JobResult();
		}
		if(result.isFinished()){
			results.remove(name);
		}
		return result;
	}
	
	
	public void shutdown(){
		executor.shutdown();
	}
	
}
