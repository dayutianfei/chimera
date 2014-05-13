/**
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dayutianfei.hadoop.rpc.client;

import java.lang.reflect.Method;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.MapWritable;
import org.apache.log4j.Logger;

import cn.dayutianfei.hadoop.rpc.IRPCHandler;
import cn.dayutianfei.hadoop.rpc.RPCResult;

/**
 * master rpc客户端
 * 
 * @author :LiBinBin function: date :2012-9-7
 */
public class RPCClient {

	protected final static Logger logger = Logger.getLogger(RPCClient.class);
	private RPCProxyManager nodeProxyManager;
	private static Method GetThings_METHOD = null;
	private static Method GetThingsResult_METHOD = null;

	static {
		try {
			GetThings_METHOD = IRPCHandler.class.getMethod("getThings",
					new Class[] { String.class, String.class });
			GetThingsResult_METHOD = IRPCHandler.class.getMethod(
					"getThingsResult", new Class[] { String.class });
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Could not find methods in IRPCHandler!");
		}
	}

	public RPCClient(String node, int port) throws Exception {
		nodeProxyManager = new RPCProxyManager(IRPCHandler.class,
				new Configuration());
		// 为每个master创建代理
		boolean createProxyOk = false;
		nodeProxyManager.createProxy(node + ":" + port);
		createProxyOk = true;
		if (!createProxyOk) {
			logger.info("Create node proxy false");
			throw new Exception("Create node proxy false");
		}
	}

	/**
	 * 执行master中的方法.
	 * 
	 * @param method
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public Object broadtoMaster(Method method, Object... args)
			throws Exception {

		Object proxy = null;
		for (Object p : nodeProxyManager.getProxys()) {
			if (p != null) {
				proxy = p;
				break;
			}
		}
		if (proxy == null) {
			throw new Exception("no proxy is availiable");
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
		//RPCResult result = (RPCResult) 
		return method.invoke(proxy, args);
	}

	public void close() {
		nodeProxyManager.shutdown();
	}

	public RPCResult getThings(String arg1, String arg2) throws Exception {
		RPCResult re = (RPCResult) broadtoMaster(GetThings_METHOD, arg1,arg2);
		return re;
	}

	public String getThingsResult(String nodeName) throws Exception {
		RPCResult re = (RPCResult) broadtoMaster(GetThingsResult_METHOD,
				nodeName);
		return re.getMessage();
	}
}
