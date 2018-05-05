package com.wp.Exception.nioException;

public class ClientChannelAbnormalClosedException extends RuntimeException{
	
	String message;
	
	public ClientChannelAbnormalClosedException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
