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
 * 2018-04-22 һ��selector��������ip+port��ͨ����
 * 
 * 
 * 
 * socketͨ�����ӳ�,���ӳ�Ĭ�ϳ��������������capacity��
 * ���������������ӻ��ڿ��г���timeout���ر�
 * @author wp
 *
 */
public class NioClientPool {
	LinkedList<NioClient> nioClientList = new LinkedList<NioClient>();
	private int timeout;//��λ����
    private int capacity;
    private String ip;
    private int port;
    private AtomicInteger count = new AtomicInteger(0);//�Ѵ�����������
    
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
				
				//�����ӳ���Ȳ������̵߳ȴ����������½������ӳأ����Ѹ��߳�
				if(count.get()==capacity){
					try{
						nioClientList.wait();
						return getNioClient();
					}catch(Exception e){
						System.out.println("��ȡnioClientList�����߳��쳣");
					}
				}
				
				try {
					nioClient = new NioClient(ip,port);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(nioClient == null){
					System.out.println("��������ip="+ip+",port="+port+"ʧ��");
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
						if (method.getName().equals("close")) {//��Ϊ����close�����������ӳ��������У������ӷ���stack�У�����ֱ�ӵ�����غ���
							nioClientList.push(client);//
							
							try{
								if(count.get()==capacity){
									synchronized (nioClientList){
										nioClientList.notify();
									}
								}
							}catch(Exception e){
								System.out.println("����ӵ��nioClientList�����߳��쳣");
							}
							
                        } else {
                            return method.invoke(client, args);
                        }
                        return null;
					}
				});
	}
}
