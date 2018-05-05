package com.wp.util.nio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import com.wp.util.ThreadPool;

public class NioServiceInvokerManager {
	
	private static int maxCoreSize = 4;//�������߳���
	private static int maxPoolSize = 8;//�̳߳��������
	private static long keepAlive=20;//�Ǻ����̵߳ȴ���ʱʱ��
	
	static ThreadPool threadPool = new ThreadPool(maxCoreSize, maxPoolSize, keepAlive);
	//static ExecutorService threadPool = Executors.newFixedThreadPool(maxPoolSize);
	
	private static volatile AtomicInteger count = new AtomicInteger(0);
	
	public static void excuteService(NioServiceInvoker nioServiceInvoker){
		
		System.out.println("call times="+(count.incrementAndGet()));
		threadPool.execute(nioServiceInvoker);
	}
}
