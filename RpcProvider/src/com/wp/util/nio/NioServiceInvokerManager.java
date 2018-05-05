package com.wp.util.nio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import com.wp.util.ThreadPool;

public class NioServiceInvokerManager {
	
	private static int maxCoreSize = 4;//最大核心线程数
	private static int maxPoolSize = 8;//线程池最大容量
	private static long keepAlive=20;//非核心线程等待超时时间
	
	static ThreadPool threadPool = new ThreadPool(maxCoreSize, maxPoolSize, keepAlive);
	//static ExecutorService threadPool = Executors.newFixedThreadPool(maxPoolSize);
	
	private static volatile AtomicInteger count = new AtomicInteger(0);
	
	public static void excuteService(NioServiceInvoker nioServiceInvoker){
		
		System.out.println("call times="+(count.incrementAndGet()));
		threadPool.execute(nioServiceInvoker);
	}
}
