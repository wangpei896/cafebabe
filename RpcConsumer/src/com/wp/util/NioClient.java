package com.wp.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import com.wp.InvokeInfo;
import com.wp.MessageBox;
import com.wp.Exception.MessageCodeNotFindException;
import com.wp.Exception.MessageCodeRepeatException;
import com.wp.Exception.nioException.ServerChannelAbnormalClosedException;

public class NioClient implements NioClientInterface{
	
	SocketChannel socketChannel = null;
    
    private static int sendBufferCapacity = 2048;
    

	private static int waitChannelConnectTime = 1000;
	

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public NioClient(String ip, int port) throws IOException{
		socketChannel = SocketChannel.open(); 
		socketChannel.configureBlocking(false);  
        // 请求连接  
		socketChannel.connect(new InetSocketAddress(ip, port));  
	}
	
	@Override
	public Object sendMessage(InvokeInfo invokeInfo,MessageBox messageBox) throws IOException,MessageCodeRepeatException{
		
		Map<String, MessageBox> messageBoxMap = NioClientManager.getInstance().getMessageBoxMap();
		
		if(messageBoxMap.containsKey(messageBox.getMessageCode())){
			throw new MessageCodeRepeatException("消息编号:"+messageBox.getMessageCode()+"重复");
		}
		
		if(!socketChannel.isConnected()){
			try {
				Thread.sleep(waitChannelConnectTime);
				return sendMessage(invokeInfo,messageBox);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		CountDownLatch countDownLatch = new CountDownLatch(1);
		
		messageBox.setCountDownLatch(countDownLatch);
		messageBoxMap.put(messageBox.getMessageCode(), messageBox);
		
		ByteBuffer echoBuffer = ByteBuffer.allocate(sendBufferCapacity); 
		echoBuffer.clear();
        echoBuffer.put(Byte2ObjectConvertUtil.object2Byte(invokeInfo));  
        echoBuffer.flip();  
        socketChannel.write(echoBuffer);  
        /**
         * TODO需要异常处理机制
         */
        try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        MessageBox mB = messageBoxMap.get(messageBox.getMessageCode());
        if(mB == null){
        	return null;
        }
        messageBoxMap.remove(messageBox.getMessageCode());
        return mB.getReturnMessage();
        
	}
	
	//call proxy to return the nioclient to pool 
	@Override
	public void close(){
		
	}
	
	
}
