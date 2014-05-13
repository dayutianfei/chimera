package cn.dayutianfei.egret.rpc.client;

import cn.dayutianfei.egret.rpc.RPC;

public class RPCClient {
	public static void main(String[] args) {
		Echo echo = RPC.getProxy(Echo.class, "127.0.0.1", 20382);
	}
}
