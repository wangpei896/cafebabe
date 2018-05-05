package com.wp;

import java.util.concurrent.CountDownLatch;

public class MessageBox {
	String messageCode;
	CountDownLatch countDownLatch;
	Object returnMessage;

	public CountDownLatch getCountDownLatch() {
		return countDownLatch;
	}
	public void setCountDownLatch(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}
	public Object getReturnMessage() {
		return returnMessage;
	}
	public void setReturnMessage(Object returnMessage) {
		this.returnMessage = returnMessage;
	}
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
}
