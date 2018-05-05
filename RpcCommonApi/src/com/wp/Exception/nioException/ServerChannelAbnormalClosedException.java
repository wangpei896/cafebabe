package com.wp.Exception.nioException;

public class ServerChannelAbnormalClosedException extends RuntimeException{

	String message;
	
	public ServerChannelAbnormalClosedException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
