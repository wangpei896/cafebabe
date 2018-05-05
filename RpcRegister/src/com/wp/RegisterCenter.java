package com.wp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import com.wp.util.NioUtil;

/**
 * 注册中心
 * @author WANGPEI896
 *
 */
public class RegisterCenter implements CallBack{
	
	private Map<String,Set<String>> providers = new HashMap<String,Set<String>>();
	private static int port = 8080;
	
	/**
	 * 生产者注册服务
	 */
	public void providerRegistService(){
		try {
			NioUtil.receiveMessage(port, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 消费者获取注册服务信息
	 */
	public void consumerAcquireService(){
		
	}

	@Override
	public void call(String message) {
		this.diposeMessage(message);
		
	}
	
	public void diposeMessage(String message){
		System.out.println(message);
	}
	
	public static void main(String[] args){
		new RegisterCenter().providerRegistService();
	}
}
