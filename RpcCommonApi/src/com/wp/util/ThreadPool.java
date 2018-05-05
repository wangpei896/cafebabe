package com.wp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.wp.HelloService;
import com.wp.pojo.Person;


/**
 * 2018-03-01
 * @author WANGPEI896
 *
 */
public class ThreadPool {

	private int maxCoreSize;//最大核心线程数
	private int maxPoolSize;//线程池最大容量
	private long keepAlive=1000;//非核心线程等待超时时间
	private Queue<Runnable> coreWaitQueue;//核心等待队列
	private Queue<Runnable> slaveWaitQueue = null;//非核心等待队列
	private List<Thread> coreThreads = new ArrayList<Thread>();//核心线程
	private List<Thread> slaveThreads = new ArrayList<Thread>();//非核心线程
	private AtomicInteger coreThreadCount = new AtomicInteger(0);//核心线程个数
	private AtomicInteger slaveThreadCreateTimes = new AtomicInteger(0);//非核心线程创建次数
	private AtomicInteger slaveThreadCount = new AtomicInteger(0);//非核心线程个数
	
	private static ThreadPool tp = new ThreadPool(4, 8, 20);//测试使用
	
	public ThreadPool(int maxCoreSize, int maxPoolSize,
			long keepAlive) {
		super();
		this.maxCoreSize = maxCoreSize;
		this.maxPoolSize = maxPoolSize;
		this.keepAlive = keepAlive;
		coreWaitQueue =new LinkedBlockingQueue<Runnable>(maxCoreSize);
		if(maxPoolSize>maxCoreSize){
			slaveWaitQueue = new LinkedBlockingQueue<Runnable>(maxPoolSize-maxCoreSize);
		}
	}
	
	public void execute(Runnable r){
		//核心线程队列是否添加成功
		boolean add2coreWaitQueueSuccess = coreWaitQueue.offer(r);
		if(add2coreWaitQueueSuccess){
			//启动核心线程
			startCoreThread();
			return;
		}
		
		
		//不创建非核心线程
		if(slaveWaitQueue==null){
			System.out.println("拒绝线程");
			return;
		}
		
		//非核心线程队列是否添加成功
		boolean add2slaveWaitQueueSuccess = slaveWaitQueue.offer(r);
		if(add2slaveWaitQueueSuccess){
			//启动非核心线程
			startOtherThread();
			return;
		}
		
		System.out.println("拒绝线程");
		return;
	}
	
	public void startCoreThread(){
		
		if(coreWaitQueue.isEmpty()){
			return;
		}
		
		int s = 0;
		if((s = coreThreadCount.incrementAndGet())<=maxCoreSize){
			
			coreWorker w = new coreWorker("核心线程"+s);
			Thread t = new Thread(w);
			coreThreads.add(t);
			t.start();
			System.out.println("创建核心线程"+s);
		}else{
			synchronized(coreWaitQueue){
				coreWaitQueue.notify();
			}
		}
	}
	
	public void startOtherThread(){
		
		if(slaveWaitQueue.isEmpty()){
			return;
		}
		
		int s = 0;
		if(slaveThreadCount.incrementAndGet()<(maxPoolSize-maxCoreSize)){
			
			s = slaveThreadCreateTimes.incrementAndGet();
			
			slaveWorker w = new slaveWorker("非核心线程"+s);
			Thread t = new Thread(w);
			slaveThreads.add(t);
			t.start();
			System.out.println("创建非核心线程"+s);
		}
	}
	
	//核心工作线程
	class coreWorker implements Runnable{
		
		private String name;
		private int runTimes=0;
		private boolean shutDown=false;
		
		public coreWorker(String name) {
			super();
			this.name = name;
		}

		@Override
		public void run() {
			while(true){
				Runnable r = null;
				
				if(shutDown){
					coreThreads.remove(Thread.currentThread());
					coreThreadCount.decrementAndGet();
					break;
				}
				
				r = coreWaitQueue.poll();
				
				if(r==null){
					try {
						synchronized(coreWaitQueue){
							System.out.println(name+"休眠");
							coreWaitQueue.wait();
							System.out.println(name+"唤醒");
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}

				runTimes++;
				System.out.println("线程名："+name+"；运行次数："+runTimes);
				r.run();
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		public void shutDown(){
			this.shutDown=true;
		}
	}
	
	
	//非核心工作线程
	class slaveWorker implements Runnable{
		
		private long startTime;
		private int runTimes=0;
		private String name;
		private boolean shutDown=false;
		
		public slaveWorker(String name) {
			super();
			this.name = name;
		}

		@Override
		public void run() {
			while(true){
				if(shutDown){
					slaveThreads.remove(Thread.currentThread());
					slaveThreadCount.decrementAndGet();
					break;
				}
				
				Runnable r = slaveWaitQueue.poll();
				
				if(r==null){
					if(System.currentTimeMillis()-startTime>keepAlive){
						try{
							slaveThreads.remove(Thread.currentThread());
							slaveThreadCount.decrementAndGet();
							System.out.println("关闭非核心线程"+name);
						}catch(Exception e){
							continue;
						}
						
						break;
						
					}else{
						continue;
					}
				}
				
				runTimes++;
				System.out.println("线程名："+name+"；运行次数："+runTimes);
				r.run();
				
				/*try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		}
		
		public void shutDown(){
			this.shutDown=true;
		}
		
		public void shutDownNow(){
			this.shutDown=true;
		}
	}
	
	public static void main(String[] args){
		
		int count = 100;
		
		for(int i=0;i<count;i++){
			tp.execute(new Runnable(){
				@Override
				public void run() {
					
					Person p= new Person("小明",5);
					
					HelloService hello = new HelloServiceImpl();
					//System.out.println(hello.sayHello(p,"滚出去").getName());
				}
			});
		}
	}
}
