package cn.dayutianfei.rpc.test;

import cn.dayutianfei.common.util.LogUtils;
import cn.dayutianfei.common.util.LogUtils.LogInitializationException;
import cn.dayutianfei.hadoop.rpc.client.RPCClient;

public class ClientDemo {
	public static void main(String[] args) throws LogInitializationException {
		LogUtils.initLog4j();
		try {
			RPCClient client = new RPCClient("172.16.0.127",8081);
			client.getThings("test","test");
			client.getThingsResult("test");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
