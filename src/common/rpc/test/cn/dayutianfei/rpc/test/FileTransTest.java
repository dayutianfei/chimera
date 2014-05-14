package cn.dayutianfei.rpc.test;

import org.apache.log4j.Logger;

import cn.dayutianfei.common.util.LogUtils;
import cn.dayutianfei.common.util.LogUtils.LogInitializationException;
import cn.dayutianfei.hadoop.rpc.server.RPCServer;

public class FileTransTest {
	protected final static Logger logger = Logger.getLogger(FileTransTest.class);
	
	public static void startRPCServer(){
		try{
			RPCServer ss = new RPCServer();
			ss.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws LogInitializationException {
		LogUtils.initLog4j();
		long time1 = System.currentTimeMillis();
		startRPCServer();
		logger.info("start rpc server cost : " + (System.currentTimeMillis()-time1));
		
	}
}
