package com.wp.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.wp.CallBack;

public class NioUtil {
	
	//public static SocketChannel ch = null;
	
	public static void initSocketChannel(String ip, int port,String sendMessage) throws IOException{
		
	    
        Selector selector = null;  
        SocketChannel ch = SocketChannel.open();  
        ch.configureBlocking(false);  
        // 请求连接  
        ch.connect(new InetSocketAddress(ip, port));  
        selector = Selector.open();  
        ch.register(selector, SelectionKey.OP_CONNECT);  
        while(true){
        	int num = selector.select();  
            Set selectedKeys = selector.selectedKeys();  
            Iterator it = selectedKeys.iterator();  
            while (it.hasNext()) {  
                SelectionKey skey = (SelectionKey) it.next();  
                it.remove();
                //SocketChannel channel = null; 
                if (skey.isConnectable()) {  
                    if (ch.isConnectionPending()) {  
                        if (ch.finishConnect()) {  
                            // 只有当连接成功后才能注册OP_READ事件  
                        	skey.interestOps(SelectionKey.OP_READ); 
                        	
                        	ByteBuffer echoBuffer = ByteBuffer.allocate(1024);  
                            echoBuffer.put(sendMessage.getBytes());  
                            echoBuffer.flip();  
                            //System.out.println("##" + new String(echoBuffer.array()));  
                            ch.write(echoBuffer);  
                            //System.out.println("写入完毕"); 
                        } else {  
                        	skey.cancel();  
                        }  
                    }  
                }
                else if (skey.isReadable()) {  
                    //channel = (SocketChannel) skey.channel();  
                    
                    String message = "";
                    int r = 1;
                    ByteBuffer echoBuffer = ByteBuffer.allocate(1024);  
                    while (r>0&&ch.isOpen()) {  
                        echoBuffer.clear();  
                        r = ch.read(echoBuffer);  
                        /*if (r <= 0) {  
                            channel.close();  
                            break;  
                        } */ 
                        echoBuffer.flip();  
                        message = message+  new String(echoBuffer.array(), echoBuffer.position(), echoBuffer.limit());
                    }  
                    
                    
                    
                    System.out.println(message);
                    
                    //it.remove(); 
                    
                    
                    
                } else {  
                	ch.close();  
                } 
            }
         
        }   
		
	}
	
	public  void sendMessage(String message, SocketChannel ch) throws IOException{
		ByteBuffer echoBuffer = ByteBuffer.allocate(1024);  
        echoBuffer.put(message.getBytes());  
        echoBuffer.flip();  
        System.out.println("##" + new String(echoBuffer.array()));  
        ch.write(echoBuffer);  
        System.out.println("写入完毕");  
	}
	
	public static void receiveMessage(int port) throws IOException{
        ByteBuffer echoBuffer = ByteBuffer.allocate(1024);  
        ServerSocketChannel ssc = ServerSocketChannel.open();  
        Selector selector = Selector.open();  
        ssc.configureBlocking(false);  
        ServerSocket ss = ssc.socket();  
        InetSocketAddress address = new InetSocketAddress(port);  
        ss.bind(address);  
        SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);  
        System.out.println("开始监听……");  
        while (true) {  
            int num = selector.select();  
            Set selectedKeys = selector.selectedKeys();  
            Iterator it = selectedKeys.iterator();  
            while (it.hasNext()) {  
                SelectionKey sKey = (SelectionKey) it.next();  
                SocketChannel channel = null;  
                if (sKey.isAcceptable()) {  
                    ServerSocketChannel sc = (ServerSocketChannel) key.channel();  
                    channel = sc.accept();// 接受连接请求  
                    channel.configureBlocking(false);  
                    channel.register(selector, SelectionKey.OP_READ);  
                    it.remove();  
                } else if (sKey.isReadable()) {  
                    channel = (SocketChannel) sKey.channel();  
                    
                    String message = "";
                    while (true) {  
                        echoBuffer.clear();  
                        int r = channel.read(echoBuffer);  
                        if (r <= 0) {  
                            channel.close();  
                            System.out.println("接收完毕，断开连接");  
                            break;  
                        }  
                        echoBuffer.flip();  
                        System.out.println("##" + r + " " + new String(echoBuffer.array(), echoBuffer.position(), echoBuffer.limit()));  
                        message = message+  new String(echoBuffer.array(), echoBuffer.position(), echoBuffer.limit());
                    }  
                    
                    it.remove(); 
                    
                    
                } else {  
                    channel.close();  
                }  
            }  
        }  
	}
}
