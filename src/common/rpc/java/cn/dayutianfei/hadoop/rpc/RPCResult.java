package cn.dayutianfei.hadoop.rpc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

	
public class RPCResult implements Serializable,Writable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Text message = new Text();
	
	public String getMessage() {
		return message.toString();
	}

	public void setMessage(String message) {
		this.message = new Text(message);
	}

	public void readFields(DataInput in) throws IOException {
		message.readFields(in);
	}

	public void write(DataOutput out) throws IOException {
		message.write(out);
	}
}
