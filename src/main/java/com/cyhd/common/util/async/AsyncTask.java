package com.cyhd.common.util.async;


public abstract class AsyncTask implements Runnable {
	public abstract void doTask();

	public boolean start(boolean forceRunAtExit, boolean daemon) {
		if (runningNum < maxTask)
			synchronized (runningNum) {
				if (runningNum < maxTask) {
					runningNum++;
					Async.exec(this, forceRunAtExit, daemon);
					return true;
				}
			}
		return false;
	}

	public boolean start() {
		return start(ensureRun, daemon);
	}

	@Override
	public void run() {
		if (syncExec) {
			synchronized (runLock) {
				try {
					doTask();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				doTask();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		synchronized (runningNum) {
			runningNum = runningNum <= 0 ? 0 : runningNum - 1;
		}
	}

	public boolean isRunning() {
		return runningNum > 0;
	}

	public int getRunningNum() {
		return runningNum;
	}

	public int getMaxTaskNum() {
		return maxTask;
	}

	public void setMaxTaskNum(int n) {
		maxTask = n;
	}

	private volatile Integer runningNum = 0;
	private Object runLock = new Object();
	private int maxTask;
	private boolean ensureRun, daemon, syncExec;

	/**
	 * 
	 * @param maxTask
	 *            max task can be started
	 * @param sync
	 *            weather the task should be locked when running
	 * @param run
	 *            run when shutdown or not
	 * @param daemon
	 *            use a daemon thread to run or not
	 */
	public AsyncTask(int maxTask, boolean sync, boolean run, boolean daemon) {
		this.maxTask = maxTask;
		this.ensureRun = run;
		this.daemon = daemon;
		this.syncExec = sync;
	}

	@Override
	public String toString() {
		return "AsyncTask [runningNum=" + runningNum + ", maxTask=" + maxTask
				+ ", ensureRun=" + ensureRun + ", daemon=" + daemon
				+ ", syncExec=" + syncExec + "]";
	}
	
	
}
