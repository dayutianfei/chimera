package cn.dayutianfei.egret.rpc.client;

import cn.dayutianfei.egret.rpc.RPC;

public class RPCClient {
	public static void main(String[] args) {
		IEcho echo = RPC.getProxy(IEcho.class, "127.0.0.1", 9090);
		System.out.println(echo.getName("dayu"));
	}
}
