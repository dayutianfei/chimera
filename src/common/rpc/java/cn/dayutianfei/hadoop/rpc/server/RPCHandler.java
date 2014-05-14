package cn.dayutianfei.hadoop.rpc.server;

import java.io.IOException;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.log4j.Logger;
import cn.dayutianfei.hadoop.rpc.IRPCHandler;
import cn.dayutianfei.hadoop.rpc.RPCResult;

public class RPCHandler implements IRPCHandler {
	private final static Logger LOG = Logger.getLogger(RPCHandler.class);
	public RPCHandler() {}
	public ProtocolSignature getProtocolSignature(String arg0, long arg1,
			int arg2) throws IOException {
		return null;
	}

	public long getProtocolVersion(String arg0, long arg1) throws IOException {
		return 0;
	}

	public RPCResult getThings(String arg1) {
		LOG.info("invoke the getThings method");
		RPCResult ss = new RPCResult();
		LOG.info("thing is "+arg1);
		return ss;
	}

	public RPCResult getThings(String thingName1, String thingName2) {
		LOG.info("invoke the getThings method");
		RPCResult ss = new RPCResult();
		ss.setMessage("hello "+thingName1+" "+thingName2);
		return ss;
	}

	public RPCResult getThingsResult(String thing) {
		// TODO Auto-generated method stub
		RPCResult ss = new RPCResult();
		ss.setMessage(thing);
		return ss;
	}
}
