package cn.dayutianfei.egret.rpc.server;

import cn.dayutianfei.egret.rpc.RPC;
import cn.dayutianfei.egret.rpc.Server;
import cn.dayutianfei.egret.rpc.client.IEcho;

public class RPCServer {
	public static void main(String[] args) {
		Server server = RPC.getServer(9090);
		server.register(IEcho.class,Echo.class);
		server.start();
	}
}
