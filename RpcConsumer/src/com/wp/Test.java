package com.wp;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wp.pojo.Person;
import com.wp.util.ProxyFactory;

/**
 * 2018-03-19 
 * version 1.0.0 
 * �ӿ��Զ�ע��;ע������;��������ַ������;���������������������Զ�����;�����л�Э�飨json��;�߲���֧�֣����selector��;�����޻ص�balabala    δʵ��
 * @author wangpei896
 *
 */
public class Test {
	
	HelloService hello;
	
	public static void main(String[] args){
		
		
		
		long startTime = System.currentTimeMillis();
		
		
		
		for(int i=0;i<100;i++){
			
			HelloService hello = new ProxyFactory<HelloService>().createProxy(HelloService.class);
			
			Person p= new Person("С��"+i,5);
			System.out.println(hello.sayHello(p,"����ȥ").getName());
		}
		
		for(int i=0;i<200;i++){
			new Thread(new Runnable(){

				@Override
				public void run() {
					
					int j = new Random(System.nanoTime()).nextInt();
					
					HelloService hello = new ProxyFactory<HelloService>().createProxy(HelloService.class);
					
					Person p= new Person("С��"+j,5);
					System.out.println(hello.sayHello(p,"����ȥ").getName());
					
				}
			}).start();
		}
		
		
		System.out.println("�����ʱ��"+(System.currentTimeMillis()-startTime));
		
	}
}
