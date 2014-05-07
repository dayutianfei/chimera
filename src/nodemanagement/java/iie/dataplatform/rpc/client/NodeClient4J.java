package iie.dataplatform.rpc.client;

import iie.dataplatform.log.LoggerConfig;
import iie.dataplatform.node.rpc.INodeRpcServer;
import iie.dataplatform.replication_manager.ReplicationTask;
import iie.dataplatform.rpc.RPC;
import iie.dataplatform.rpc.RPCResult;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.log4j.Logger;

public class NodeClient4J {
	private final static Logger LOG = LoggerConfig.getInstance(LoggerConfig.DATAPLATFORM_LOG);
	private Object proxy;
	private static Method deleteDataByPath_METHOD = null;
	private static Method repShardToNode_METHOD = null;
	static {
		try {
			deleteDataByPath_METHOD = INodeRpcServer.class.getMethod(
					"deleteDataByPath", new Class[] { List.class });
			repShardToNode_METHOD = INodeRpcServer.class.getMethod(
					"repShardToNode", new Class[] { List.class });
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"Could not find methods in INodeRpcServer!");
		}
	}
	public NodeClient4J(String nodeName, String ip,int port) {
		LOG.info("creating proxy for node: " + nodeName+",ip:" + ip);
		final InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
		this.proxy = RPC.getProxy(INodeRpcServer.class, ip,port);
		LOG.info(String.format("Created a proxy %s for %s:%s %s", Proxy.getInvocationHandler(proxy),nodeName, port,
	            inetSocketAddress));
	}
	public NodeClient4J(String nodeName,int port) {
		LOG.info("creating proxy for node: " + nodeName);
		final InetSocketAddress inetSocketAddress = new InetSocketAddress(nodeName, port);
		this.proxy = RPC.getProxy(INodeRpcServer.class, nodeName,port);
		LOG.info(String.format("Created a proxy %s for %s:%s %s", Proxy.getInvocationHandler(proxy),nodeName, port,
	            inetSocketAddress));
	}


	public RPCResult broadtoMaster(Method method, Object... args)
			throws Exception {
		if (method.getName().equals("deleteDataByPath")) {
			args[0] = (List<String>) args[0];
		} 
		if (proxy == null) {
			throw new Exception("no master is availiable");
		}
		if (method == null || args == null) {
			throw new IllegalArgumentException("Null method or args!");
		}

		// method types
		Class<?>[] types = method.getParameterTypes();
		if (args.length != types.length) {
			throw new IllegalArgumentException("Wrong number of args: found "
					+ args.length + ", expected " + types.length + "!");
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null) {
				Class<?> from = args[i].getClass();
				Class<?> to = types[i];
				if (!to.isAssignableFrom(from)
						&& !(from.isPrimitive() || to.isPrimitive())) {
					throw new IllegalArgumentException(
							"Incorrect argument type for param " + i
									+ ": expected " + types[i] + "!");
				}
			}
		}
		RPCResult result = (RPCResult) method.invoke(proxy, args);
		return result;
	}
	public boolean deleteDataByPath(List<String> path) throws Exception {
		return broadtoMaster(deleteDataByPath_METHOD,path).isSuccess();
	}
	public boolean repShardToNode(List<ReplicationTask> tasks) throws Exception{
		return broadtoMaster(repShardToNode_METHOD,tasks).isSuccess();
	}
}
