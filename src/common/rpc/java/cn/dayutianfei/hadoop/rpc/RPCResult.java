package cn.dayutianfei.hadoop.rpc;

import java.io.Serializable;

	
public class RPCResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean isSuccess = false;
	
	private String message = "";
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
}
