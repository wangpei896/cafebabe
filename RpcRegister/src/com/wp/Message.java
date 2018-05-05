package com.wp;

import java.util.Observable;
import java.util.Observer;

public class Message implements Observer{
	
	public String message;
		
	public Message(String message) {
		super();
		this.message = message;
	}

	@Override
	public void update(Observable o, Object arg) {
		
	}

	
}
