package cn.dayutianfei.rpc.test;

import cn.dayutianfei.common.util.LogUtils;
import cn.dayutianfei.common.util.LogUtils.LogInitializationException;
import cn.dayutianfei.hadoop.rpc.RPCResult;
import cn.dayutianfei.hadoop.rpc.client.RPCClient;

public class ClientDemo {
	public static void main(String[] args) throws LogInitializationException {
		LogUtils.initLog4j();
		try {
			RPCClient client = new RPCClient("localhost",8082);
			RPCResult res = client.getThings("test","testa");
			System.out.println(res.getMessage());
//			client.getThingsResult("test");
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
