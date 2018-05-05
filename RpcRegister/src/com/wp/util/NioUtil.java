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
	
	public static void sendMessage(String ip, int port, String message) throws IOException{
		ByteBuffer echoBuffer = ByteBuffer.allocate(1024);  
        SocketChannel channel = null;  
        Selector selector = null;  
        channel = SocketChannel.open();  
        channel.configureBlocking(false);  
        // 请求连接  
        channel.connect(new InetSocketAddress(ip, port));  
        selector = Selector.open();  
        channel.register(selector, SelectionKey.OP_CONNECT);  
        int num = selector.select();  
        Set selectedKeys = selector.selectedKeys();  
        Iterator it = selectedKeys.iterator();  
        while (it.hasNext()) {  
            SelectionKey key = (SelectionKey) it.next();  
            it.remove();  
            if (key.isConnectable()) {  
                if (channel.isConnectionPending()) {  
                    if (channel.finishConnect()) {  
                        // 只有当连接成功后才能注册OP_READ事件  
                        key.interestOps(SelectionKey.OP_READ);  
                        echoBuffer.put(message.getBytes());  
                        echoBuffer.flip();  
                        System.out.println("##" + new String(echoBuffer.array()));  
                        channel.write(echoBuffer);  
                        System.out.println("写入完毕");  
                    } else {  
                        key.cancel();  
                    }  
                }  
            }  
        } 
	}
	
	public static void receiveMessage(int port, CallBack callback) throws IOException{
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
                    int r = 1;
                    while (r>0&&channel.isOpen()) {  
                        echoBuffer.clear();  
                        r = channel.read(echoBuffer);  
                        /*if (r <= 0) {  
                            channel.close();  
                            break;  
                        } */ 
                        echoBuffer.flip();  
                        message = message+  new String(echoBuffer.array(), echoBuffer.position(), echoBuffer.limit());
                    }  
                    
                    it.remove(); 
                    
                    callback.call(message);
                    
                }
                /*else {  
                    channel.close();  
                } */ 
            }  
        }  
	}
}
