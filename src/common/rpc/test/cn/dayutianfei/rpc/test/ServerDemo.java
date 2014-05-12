package cn.dayutianfei.rpc.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import cn.dayutianfei.common.util.LogUtils;
import cn.dayutianfei.common.util.LogUtils.LogInitializationException;
import cn.dayutianfei.hadoop.rpc.server.RPCServer;

public class ServerDemo {
	public static void main(String[] args) throws FileNotFoundException, IOException, LogInitializationException {
		LogUtils.initLog4j();
		try{
			RPCServer ss = new RPCServer();
			ss.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
