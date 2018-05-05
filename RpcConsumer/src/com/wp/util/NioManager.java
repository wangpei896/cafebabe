package com.wp.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NioManager {
	
	static Map<String,NioClientPool> clientMap = new HashMap<String,NioClientPool>();
	
	public static NioClientInterface getNioClient(String ip,int port) throws IOException, InterruptedException{
		String key = ip+port;
		
		NioClientPool nioClientPool = null;
		
		if(clientMap.containsKey(key)){
			nioClientPool = clientMap.get(key);
		}else{
			nioClientPool = new NioClientPool(ip, port);
			clientMap.put(key, nioClientPool);
		}
		
		return nioClientPool.getNioClient();
		
		/**
		 * 可以用连接池
		 */
		/*NioClient nioClient = new NioClient(ip, port);
		
		return nioClient;*/
	}
}
