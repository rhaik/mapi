package com.cyhd.common.util.async;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 */
public class Async {
	public static final void exec(Runnable r) {
		exec(r, false, false);
	}
	public static final void exec(Runnable r, boolean forceRunAtExit,
			boolean daemon) {
		if (r != null) {
			if (daemon) {
				daemonES.execute(r);
			} else {
				threadES.execute(r);
			}
			if (forceRunAtExit)
				runSet.add(r);
		}
	}
	
	public static final <V> Future<V> submit(Callable<V> r) {
		return submit(r, false);
	}
	public static final <V> Future<V> submit(Callable<V> r, boolean daemon) {
		if (daemon) {
			return daemonES.submit(r);
		} else {
			return threadES.submit(r);
		}
	}

	public static final ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit){
		return scheduledES.schedule(command, delay, unit);
	}
            
	public static final void shutdown() {
		try {
			for (Runnable r : runSet)
				threadES.execute(r);
			threadES.shutdown();
			threadES.awaitTermination(3, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			daemonES.shutdown();
			daemonES.awaitTermination(3, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static final Set<Runnable> runSet = Collections
			.synchronizedSet(new HashSet<Runnable>());
	private static final ThreadFactory df = Executors.defaultThreadFactory();
	private static final ThreadFactory daemonFactory = new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = df.newThread(r);
			t.setDaemon(true);
			t.setName("Async-daemon-" + t.getName());
			return t;
		}
	};
	private static final ThreadFactory threadFactory = new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = df.newThread(r);
			t.setName("Async-" + t.getName());
			return t;
		}
	};
	private static final ThreadFactory scheduledFactory = new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = df.newThread(r);
			t.setName("Async-scheduled-" + t.getName());
			return t;
		}
	};
	private static final ExecutorService daemonES = Executors
			.newCachedThreadPool(daemonFactory);
	private static final ExecutorService threadES = Executors
			.newCachedThreadPool(threadFactory);
	private static final ScheduledExecutorService scheduledES = Executors
			.newScheduledThreadPool(1, scheduledFactory);
}