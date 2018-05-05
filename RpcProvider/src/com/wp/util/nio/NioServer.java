package com.wp.util.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.omg.Messaging.SyncScopeHelper;

import com.wp.Exception.nioException.ClientChannelAbnormalClosedException;

import sun.org.mozilla.javascript.internal.Synchronizer;

public class NioServer {
	
	private static int capacity = 2048;
	
	private static Queue<NioServiceInvoker> invokeServiceQueue = new ConcurrentLinkedQueue<NioServiceInvoker>();
	
	public static void listenPublishService(int port) throws IOException{
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				listenInvokeServiceQueue();
				
			}
			
		}).start();
		
		
       
        ServerSocketChannel ssc = ServerSocketChannel.open();  
        Selector selector = Selector.open();  
        ssc.configureBlocking(false);  
        ServerSocket ss = ssc.socket();  
        InetSocketAddress address = new InetSocketAddress(port);  
        ss.bind(address);  
        SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);  
        System.out.println("��ʼ��������");  
        while (true) {  
        	try{
        		int num = selector.select();  
                Set selectedKeys = selector.selectedKeys();  
                Iterator it = selectedKeys.iterator();  
                while (it.hasNext()) {  
                    SelectionKey sKey = (SelectionKey) it.next();  
                    it.remove(); 
                    SocketChannel channel = null;  
                    if (sKey.isAcceptable()) {  
                        ServerSocketChannel sc = (ServerSocketChannel) sKey.channel();  
                        channel = sc.accept();// ������������  
                        channel.configureBlocking(false);  
                        channel.register(selector, SelectionKey.OP_READ);  
                    } else if (sKey.isReadable()) {  
                        channel = (SocketChannel) sKey.channel();  
                        ByteBuffer echoBuffer = ByteBuffer.allocate(capacity);  
                        
                        int r = 1;
                        
                        while (r>0) {  
                            echoBuffer.clear();  
                            try{
                            	r = channel.read(echoBuffer);  
                            }catch(IOException e){
                            	String clientAddress = channel.getRemoteAddress().toString();
                            	channel.close();
                            	throw new ClientChannelAbnormalClosedException(clientAddress+"�ͻ��������쳣�ر�");
                            }
                            echoBuffer.flip();  
                        }  
                        
                        /**
                         * TODO 
                         * ��Ϊ��nioServiceInvoker������У�����һ���ػ��߳��������������
                         * ԭ��1����ֹ������̳߳��쳣�Ӷ��������������  2�����������Ƿ��������������Ͽ���
                         */
                        NioServiceInvoker nioServiceInvoker = new NioServiceInvoker(echoBuffer.array(), channel);
                        
                        invokeServiceQueue.add(nioServiceInvoker);
                        
                        synchronized(invokeServiceQueue){
                        	invokeServiceQueue.notify();
                        }
                    }
                }
        	}catch(ClientChannelAbnormalClosedException e){
        		System.out.println(e.getMessage());
        	}
        }  
	}
	
	private static void listenInvokeServiceQueue(){
		while(true){
			NioServiceInvoker nioServiceInvoker = invokeServiceQueue.poll();
			if(nioServiceInvoker != null){
				NioServiceInvokerManager.excuteService(nioServiceInvoker);
			}else{
				synchronized(invokeServiceQueue){
					try {
						invokeServiceQueue.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
