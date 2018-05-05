package com.wp.util;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 2018-04-22 一个selector监听所有ip+port的通道？
 * 
 * 
 * 
 * socket通道连接池,连接池默认常备连接最大数量capacity，
 * 超过此数量的连接会在空闲超过timeout秒后关闭
 * @author wp
 *
 */
public class NioClientPool {
	LinkedList<NioClient> nioClientList = new LinkedList<NioClient>();
	private int timeout;//单位：秒
    private int capacity;
    private String ip;
    private int port;
    private AtomicInteger count = new AtomicInteger(0);//已创建的连接数
    
	public NioClientPool(int timeout, int capacity, String ip, int port) {
		this.timeout = timeout;
		this.capacity = capacity;
		this.ip = ip;
		this.port = port;
	}
	
	public NioClientPool( String ip, int port) {
		this.timeout = 1000;
		this.capacity = 10;
		this.ip = ip;
		this.port = port;
	}

	public NioClientInterface getNioClient() throws IOException, InterruptedException{
		
		NioClient nioClient = null;
		
		synchronized (nioClientList) {
			if(nioClientList.size()==0){
				
				//当连接池深度不够，线程等待有连接重新进入连接池，唤醒该线程
				if(count.get()==capacity){
					try{
						nioClientList.wait();
						return getNioClient();
					}catch(Exception e){
						System.out.println("获取nioClientList锁的线程异常");
					}
				}
				
				try {
					nioClient = new NioClient(ip,port);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(nioClient == null){
					System.out.println("创建连接ip="+ip+",port="+port+"失败");
				}
				
				NioClientManager.getInstance().registerNioClient(nioClient);
				count.incrementAndGet();
			}else{
				nioClient = nioClientList.pop();
			}
		}
		
		
		final NioClient client = nioClient;
		
		return (NioClientInterface) Proxy.newProxyInstance(
				NioClientPool.class.getClassLoader(),
				nioClient.getClass().getInterfaces(),
				 new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (method.getName().equals("close")) {//当为调用close函数并且连接池正在运行，则将连接放入stack中，否则直接调用相关函数
							nioClientList.push(client);//
							
							try{
								if(count.get()==capacity){
									synchronized (nioClientList){
										nioClientList.notify();
									}
								}
							}catch(Exception e){
								System.out.println("唤醒拥有nioClientList锁的线程异常");
							}
							
                        } else {
                            return method.invoke(client, args);
                        }
                        return null;
					}
				});
	}
}
