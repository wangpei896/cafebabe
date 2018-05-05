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

	private int maxCoreSize;//�������߳���
	private int maxPoolSize;//�̳߳��������
	private long keepAlive=1000;//�Ǻ����̵߳ȴ���ʱʱ��
	private Queue<Runnable> coreWaitQueue;//���ĵȴ�����
	private Queue<Runnable> slaveWaitQueue = null;//�Ǻ��ĵȴ�����
	private List<Thread> coreThreads = new ArrayList<Thread>();//�����߳�
	private List<Thread> slaveThreads = new ArrayList<Thread>();//�Ǻ����߳�
	private AtomicInteger coreThreadCount = new AtomicInteger(0);//�����̸߳���
	private AtomicInteger slaveThreadCreateTimes = new AtomicInteger(0);//�Ǻ����̴߳�������
	private AtomicInteger slaveThreadCount = new AtomicInteger(0);//�Ǻ����̸߳���
	
	private static ThreadPool tp = new ThreadPool(4, 8, 20);//����ʹ��
	
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
		//�����̶߳����Ƿ���ӳɹ�
		boolean add2coreWaitQueueSuccess = coreWaitQueue.offer(r);
		if(add2coreWaitQueueSuccess){
			//���������߳�
			startCoreThread();
			return;
		}
		
		
		//�������Ǻ����߳�
		if(slaveWaitQueue==null){
			System.out.println("�ܾ��߳�");
			return;
		}
		
		//�Ǻ����̶߳����Ƿ���ӳɹ�
		boolean add2slaveWaitQueueSuccess = slaveWaitQueue.offer(r);
		if(add2slaveWaitQueueSuccess){
			//�����Ǻ����߳�
			startOtherThread();
			return;
		}
		
		System.out.println("�ܾ��߳�");
		return;
	}
	
	public void startCoreThread(){
		
		if(coreWaitQueue.isEmpty()){
			return;
		}
		
		int s = 0;
		if((s = coreThreadCount.incrementAndGet())<=maxCoreSize){
			
			coreWorker w = new coreWorker("�����߳�"+s);
			Thread t = new Thread(w);
			coreThreads.add(t);
			t.start();
			System.out.println("���������߳�"+s);
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
			
			slaveWorker w = new slaveWorker("�Ǻ����߳�"+s);
			Thread t = new Thread(w);
			slaveThreads.add(t);
			t.start();
			System.out.println("�����Ǻ����߳�"+s);
		}
	}
	
	//���Ĺ����߳�
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
							System.out.println(name+"����");
							coreWaitQueue.wait();
							System.out.println(name+"����");
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}

				runTimes++;
				System.out.println("�߳�����"+name+"�����д�����"+runTimes);
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
	
	
	//�Ǻ��Ĺ����߳�
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
							System.out.println("�رշǺ����߳�"+name);
						}catch(Exception e){
							continue;
						}
						
						break;
						
					}else{
						continue;
					}
				}
				
				runTimes++;
				System.out.println("�߳�����"+name+"�����д�����"+runTimes);
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
					
					Person p= new Person("С��",5);
					
					HelloService hello = new HelloServiceImpl();
					//System.out.println(hello.sayHello(p,"����ȥ").getName());
				}
			});
		}
	}
}
