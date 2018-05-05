package com.wp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import com.wp.util.NioUtil;

/**
 * ע������
 * @author WANGPEI896
 *
 */
public class RegisterCenter implements CallBack{
	
	private Map<String,Set<String>> providers = new HashMap<String,Set<String>>();
	private static int port = 8080;
	
	/**
	 * ������ע�����
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
	 * �����߻�ȡע�������Ϣ
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
