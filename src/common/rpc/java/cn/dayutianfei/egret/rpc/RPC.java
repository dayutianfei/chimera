package cn.dayutianfei.egret.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class RPC {
	@SuppressWarnings("unchecked")
	public static <T> T getProxy(final Class<T> clazz,String host,int port) {
		final Client client = new Client(host,port);
		InvocationHandler handler = new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Invocation invo = new Invocation();
				invo.setInterfaces(clazz);
				invo.setMethod(new cn.dayutianfei.egret.rpc.Method(method.getName(),method.getParameterTypes()));
				invo.setParams(args);
				client.invoke(invo);
				return invo.getResult();
			}
		};
		T t = (T) Proxy.newProxyInstance(RPC.class.getClassLoader(), new Class[] {clazz}, handler);
		return t;
	}
	public static Server getServer(int port) {
		Server server = new Server();
		server.setPort(port);
		return server;
	}
	public static void stopProxy(Object search) {
		
	}
}
	



	
	
	

