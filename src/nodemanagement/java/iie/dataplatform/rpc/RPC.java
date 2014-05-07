package iie.dataplatform.rpc;

import iie.dataplatform.rpc.server.Client;
import iie.dataplatform.rpc.server.Invocation;
import iie.dataplatform.rpc.server.Server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class RPC {
	@SuppressWarnings("unchecked")
	public static <T> T getProxy(final Class<T> clazz,String host,int port) {
		final Client client = new Client(host,port);
		InvocationHandler handler = new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Invocation invo = new Invocation();
				invo.setInterfaces(clazz);
				invo.setMethod(new iie.dataplatform.rpc.server.Method(method.getName(),method.getParameterTypes()));
				invo.setParams(args);
				client.invoke(invo);
				return invo.getResult();
			}
		};
		T t = (T) Proxy.newProxyInstance(RPC.class.getClassLoader(), new Class[] {clazz}, handler);
		return t;
	}
	public static Server getServer(Object instance,String address, int port) {
		Server server = new Server();
		server.setPort(port);
		server.register(instance.getClass(),instance);
		return server;
	}
	public static void stopProxy(Object search) {
		
	}
}
	



	
	
	

