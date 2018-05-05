package com.wp.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;
import java.util.UUID;

import com.google.gson.Gson;
import com.wp.InvokeInfo;
import com.wp.MessageBox;

public class ProxyFactory<T>{

	public <T> T createProxy(final Class<?> proxyInterface){
		return (T) Proxy.newProxyInstance(proxyInterface.getClassLoader(), new Class<?>[]{proxyInterface}, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {

				NioClientInterface nioClient = NioManager.getNioClient("localhost", 8080);
				
				if(nioClient == null){
					throw new Exception("创建rpc连接异常");
				}
				
				try{
					InvokeInfo invokeInfo = new InvokeInfo();
					
					invokeInfo.setArgs(args);
					invokeInfo.setMethodName(method.getName());
					invokeInfo.setServiceName("com.wp.util.HelloServiceImpl");
					invokeInfo.setParameterTypes(method.getParameterTypes());
					invokeInfo.setCode(createMessageCode());
					
					System.out.println(invokeInfo.toString());
					
					MessageBox messageBox = new MessageBox();
					messageBox.setMessageCode(invokeInfo.getCode());
					
					return nioClient.sendMessage(invokeInfo, messageBox);
				}finally{
					nioClient.close();
				}
				
			}
		});
	}

	private String createMessageCode(){
		return UUID.randomUUID().toString();
	}
	
}
