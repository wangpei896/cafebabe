package com.wp.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import com.wp.InvokeInfo;
import com.wp.MessageBox;
import com.wp.Exception.MessageCodeNotFindException;
import com.wp.Exception.nioException.ServerChannelAbnormalClosedException;

public class NioClientManager implements Runnable{
	
	public static NioClientManager nioClientManager = null;
	
	private Map<String,MessageBox> messageBoxMap = new ConcurrentHashMap<String,MessageBox>();
	
	Selector selector = null;  
    
    private static int recieveBufferCapacity = 2048;
    

	public static NioClientManager getInstance() throws IOException{
		if(nioClientManager != null){
			return nioClientManager;
		}
		
		synchronized (NioClientManager.class) {
			if(nioClientManager == null){
				nioClientManager = new NioClientManager();
				new Thread(nioClientManager).start();
			}
			
			return  nioClientManager;
		}
	}
	
	public NioClientManager() throws IOException{
		selector = Selector.open(); 
	}

	
	public void registerNioClient(NioClient nioClient) throws IOException{
		nioClient.getSocketChannel().register(selector, SelectionKey.OP_CONNECT);
	}
	
	@Override
	public void run() {
		
		while(true){
        	try {
				int num = selector.select();
	            Set selectedKeys = selector.selectedKeys();  
	            Iterator it = selectedKeys.iterator();  
	            while (it.hasNext()) {  
	                SelectionKey skey = (SelectionKey) it.next(); 
	                SocketChannel ch = (SocketChannel)skey.channel();
	                it.remove();
	                if (skey.isConnectable()) { 
                		if (ch.isConnectionPending()) {  
	                        if (ch.finishConnect()) {  
	                            // 只有当连接成功后才能注册OP_READ事件  
	                        	skey.interestOps(SelectionKey.OP_READ);
	                        } else {  
	                        	skey.cancel();  
	                        }  
	                    } 
	                }else if (skey.isReadable()) {  
	                	
	                    int r = 1;
	                    ByteBuffer echoBuffer = ByteBuffer.allocate(recieveBufferCapacity);  
	                    while (r>0) {  
	                        echoBuffer.clear();  
	                        try{
	                        	r = ch.read(echoBuffer);  
	                        }catch(IOException e){
	                        	String serverAddress = ch.getRemoteAddress().toString();
	                        	ch.close();
	                        	throw new ServerChannelAbnormalClosedException(serverAddress+"服务端连接异常关闭");
	                        }
	                        
	                        echoBuffer.flip();  
	                    }
	                    
	                    notifyConsumerService(echoBuffer.array());
	                    
	                } else {  
	                	System.out.println("断开连接");
	                	ch.close();  
	                } 
	            }
        	} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(ServerChannelAbnormalClosedException e){
				System.out.println(e.getMessage());
			}
        } 
		
	}
	
	/**
	 * 唤醒等待中的消费者服务
	 * @param message
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private void notifyConsumerService(byte[] message) throws ClassNotFoundException, IOException{

		InvokeInfo invokeInfo = Byte2ObjectConvertUtil.byte2Object(message);
		
        String key = invokeInfo.getCode();
        
        if(messageBoxMap.containsKey(key)){
        	
        	MessageBox messageBox = messageBoxMap.get(key);
        	
        	Object returnMsg = invokeInfo.getReturnMsg();
        	
        	messageBox.setReturnMessage(returnMsg);
        	
        	CountDownLatch countDownLatch = messageBox.getCountDownLatch();
        	countDownLatch.countDown();
        }else{
        	throw new MessageCodeNotFindException("消息编号:"+key+"不存在");
        }
	}

	public Map<String, MessageBox> getMessageBoxMap() {
		return messageBoxMap;
	}

	public void setMessageBoxMap(Map<String, MessageBox> messageBoxMap) {
		this.messageBoxMap = messageBoxMap;
	}
	
}

