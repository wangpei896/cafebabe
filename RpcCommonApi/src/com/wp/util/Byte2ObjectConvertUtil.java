package com.wp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.wp.InvokeInfo;
import com.wp.pojo.Person;

public class Byte2ObjectConvertUtil {
	
	public static byte[] object2Byte(InvokeInfo invokeInfo){
		ByteArrayOutputStream bytOut=new ByteArrayOutputStream();  
        ObjectOutputStream objOut = null; 
        
        try{
        	objOut = new ObjectOutputStream(bytOut);
        	objOut.writeObject(invokeInfo);
		}catch(IOException e){
			
		}finally{
			try {
				bytOut.close();
				objOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
        
       
        return bytOut.toByteArray();
	}
	
	public static InvokeInfo byte2Object(byte[] byteArray) throws ClassNotFoundException{
		ByteArrayInputStream byIn = new ByteArrayInputStream(byteArray);
		ObjectInputStream objIn = null;
		InvokeInfo invokeInfo = null;
		try{
			objIn = new ObjectInputStream(byIn);
			invokeInfo = (InvokeInfo) objIn.readObject();
		}catch(IOException e){
			
		}finally{
			try {
				objIn.close();
				byIn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return invokeInfo;
	}
	
	public static void main(String[] args) throws ClassNotFoundException{
		
		Person p = new Person("Ð¡ºì",1);
		
		InvokeInfo invokeInfo = new InvokeInfo();
		invokeInfo.setReturnMsg(p);
		
		byte[] byteArray = Byte2ObjectConvertUtil.object2Byte(invokeInfo);
		InvokeInfo invokeInfo1 = Byte2ObjectConvertUtil.byte2Object(byteArray);
		
		System.out.println(invokeInfo1.toString());
	}
}
