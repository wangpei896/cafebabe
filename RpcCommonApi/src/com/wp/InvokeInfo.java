package com.wp;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * RPC服务间调用信息
 * @author WANGPEI896
 *
 */
public class InvokeInfo implements Serializable{

	String code;//请求编号
	
	String serviceName;//服务名
	String methodName;//方法名
	Object[] args;//参数
	Class<?>[] parameterTypes;
	String version;//版本,默认1.0.0
	Object returnMsg;
	
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return "InvokeInfo [code=" + code + ", serviceName=" + serviceName
				+ ", methodName=" + methodName + ", args="
				+ Arrays.toString(args) + ", parameterTypes="
				+ Arrays.toString(parameterTypes) + ", version=" + version
				+ ", returnMsg=" + returnMsg + "]";
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public Object getReturnMsg() {
		return returnMsg;
	}
	public void setReturnMsg(Object returnMsg) {
		this.returnMsg = returnMsg;
	}
}
