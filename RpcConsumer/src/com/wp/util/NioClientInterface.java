package com.wp.util;

import java.io.IOException;

import com.wp.InvokeInfo;
import com.wp.MessageBox;
import com.wp.Exception.MessageCodeRepeatException;

public interface NioClientInterface {

	Object sendMessage(InvokeInfo invokeInfo, MessageBox messageBox) throws IOException, MessageCodeRepeatException;

	void close();

}
