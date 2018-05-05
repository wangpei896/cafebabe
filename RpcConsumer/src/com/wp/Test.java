package com.wp;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wp.pojo.Person;
import com.wp.util.ProxyFactory;

/**
 * 2018-03-19 
 * version 1.0.0 
 * 接口自动注入;注册中心;服务请求分发多策略;服务治理，降级，限流，自动发布;多序列化协议（json）;高并发支持（多个selector）;请求无回调balabala    未实现
 * @author wangpei896
 *
 */
public class Test {
	
	HelloService hello;
	
	public static void main(String[] args){
		
		
		
		long startTime = System.currentTimeMillis();
		
		
		
		for(int i=0;i<100;i++){
			
			HelloService hello = new ProxyFactory<HelloService>().createProxy(HelloService.class);
			
			Person p= new Person("小明"+i,5);
			System.out.println(hello.sayHello(p,"滚出去").getName());
		}
		
		for(int i=0;i<200;i++){
			new Thread(new Runnable(){

				@Override
				public void run() {
					
					int j = new Random(System.nanoTime()).nextInt();
					
					HelloService hello = new ProxyFactory<HelloService>().createProxy(HelloService.class);
					
					Person p= new Person("小明"+j,5);
					System.out.println(hello.sayHello(p,"滚出去").getName());
					
				}
			}).start();
		}
		
		
		System.out.println("请求耗时："+(System.currentTimeMillis()-startTime));
		
	}
}
