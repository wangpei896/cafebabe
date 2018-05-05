package com.wp;

import java.io.IOException;
import com.google.gson.Gson;
import com.wp.util.nio.NioServer;

public class ProviderCenter{
	
	Gson gson = new Gson();
	
	private static int listenPublishService_port = 8080;//·þÎñ¼àÌý¶Ë¿Ú
	
	/*public void registerService(){
		Map<String,String> serviceInfo = new HashMap<String,String>();
		
		serviceInfo.put("HelloService", "localhost");
		String serviceString = gson.toJson(serviceInfo);
		
		try {
			NioUtil nio = new NioUtil();
			SocketChannel ch = new NioUtil().initSocketChannel("localhost", 8080);
			nio.sendMessage(serviceString,ch);
			nio.sendMessage(serviceString,ch);
			Thread.sleep(10000);
			nio.sendMessage(serviceString,ch);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}*/
	
	public void publishService(){
		try {
			NioServer.listenPublishService(listenPublishService_port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		new ProviderCenter().publishService();
	}

}
