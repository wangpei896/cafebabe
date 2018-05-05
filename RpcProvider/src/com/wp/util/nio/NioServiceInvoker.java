package com.wp.util.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.google.gson.Gson;
import com.wp.InvokeInfo;
import com.wp.util.Byte2ObjectConvertUtil;

public class NioServiceInvoker implements Runnable{

	private byte[] message = null;
	SocketChannel channel = null; 
	
	private static int capacity = 2048;
	
	public NioServiceInvoker(byte[] message, SocketChannel channel) {
		this.message = message;
		this.channel = channel;
	}
	
	/*@Override
	public void run() {
		InvokeInfo invokeInfo = new Gson().fromJson(message, InvokeInfo.class);
		
		try {
			Class c = Class.forName(invokeInfo.getServiceName());
			Method m = c.getDeclaredMethod(invokeInfo.getMethodName(),String.class);
			
			String returnMsg = (String) m.invoke(c.newInstance(), invokeInfo.getArgs());
			 
			invokeInfo.setReturnMsg(returnMsg);
			String returnString = new Gson().toJson(invokeInfo);
			
			ByteBuffer echoBuffer = ByteBuffer.allocate(capacity);
			echoBuffer.clear(); 
            echoBuffer.put(returnString.getBytes());  
            echoBuffer.flip();  
            System.out.println("##" + new String(echoBuffer.array()));  
            channel.write(echoBuffer);  
            //System.out.println("写入完毕");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	@Override
	public void run() {
		
		try {
			InvokeInfo invokeInfo = Byte2ObjectConvertUtil.byte2Object(message);
			
			Class c = Class.forName(invokeInfo.getServiceName());
			Method m = c.getDeclaredMethod(invokeInfo.getMethodName(),invokeInfo.getParameterTypes());
			
			Object returnMsg =  m.invoke(c.newInstance(), invokeInfo.getArgs());
			 
			invokeInfo.setReturnMsg(returnMsg);
			
			System.out.println("响应消息:" + invokeInfo.toString());  
			
			byte[] returnByte = Byte2ObjectConvertUtil.object2Byte(invokeInfo);
			
			ByteBuffer echoBuffer = ByteBuffer.allocate(capacity);
			echoBuffer.clear(); 
            echoBuffer.put(returnByte);  
            echoBuffer.flip();  
            channel.write(echoBuffer);  
            //System.out.println("写入完毕");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
