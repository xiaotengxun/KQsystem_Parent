package com.yc.jar;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {
	private static LinkedBlockingDeque<Runnable> workQueue = null;
	private static ThreadPoolExecutor threadPool = null;

	public static void initThreadPool() {
		if (workQueue == null) {

			workQueue = new LinkedBlockingDeque<Runnable>();
			threadPool = new ThreadPoolExecutor(20, 100, 10, TimeUnit.MINUTES, workQueue);
		}
	}

	public static synchronized ThreadPoolExecutor getThreadPool() {
		return threadPool;
	}
}
