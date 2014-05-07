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
package iie.dataplatform.rpc.client;

//import iie.dataplatform.conf.ClientConfiguration;
import iie.dataplatform.log.LoggerConfig;
import iie.dataplatform.master.rpc.IMasterRpcServer;
import iie.dataplatform.prototype.ShellSql;
import iie.dataplatform.prototype.SplitValue;
import iie.dataplatform.rpc.RPCResult;

import java.lang.reflect.Method;
import java.util.List;
//import java.util.List;

import org.apache.log4j.Logger;

/**
 * master rpc客户端
 * 
 * @author :LiBinBin function: date :2012-9-7
 */
public class MasterClient4J {

	protected final static Logger logger = LoggerConfig.getInstance(LoggerConfig.DATAPLATFORM_LOG);
	private MasterProxyManager masterProxyManager;
	private static Method sentHeartBeat_METHOD = null;
	private static Method tableOperator_METHOD = null;
	private static Method deleteShard_METHOD = null;

	static {
		try {
			sentHeartBeat_METHOD = IMasterRpcServer.class.getMethod(
					"sentHeartBeat", new Class[] { String.class });
			tableOperator_METHOD = IMasterRpcServer.class.getMethod(
					"tableOperator", new Class[] { ShellSql.class });
			deleteShard_METHOD = IMasterRpcServer.class.getMethod(
					"deleteShard", new Class[] { String.class,String.class });
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"Could not find methods in IMetaDataServer!");
		}
	}

	public MasterClient4J(String node, int port) throws Exception {
		masterProxyManager = new MasterProxyManager(IMasterRpcServer.class);
		// 为每个master创建代理
		boolean createProxyOk = false;
		masterProxyManager.createMasterProxy(node, port);
		createProxyOk = true;
		if (!createProxyOk) {
			logger.info("Create master proxy false");
			throw new Exception("Create master proxy false");
		}
	}

	/**
	 * 执行master中的方法.
	 * @param method
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public RPCResult broadtoMaster(Method method, Object... args)
			throws Exception {

		Object proxy = null;
		for (Object p : masterProxyManager.getProxys()) {
			if (p != null) {
				proxy = p;
				break;
			}
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

	public void close() {
		masterProxyManager.shutdown();
	}

	/**
	 * 节点信息发送给
	 * @param nodeName
	 * @return
	 * @throws Exception
	 */
	public boolean sentHeartBeat(String nodeName) throws Exception {
		return broadtoMaster(sentHeartBeat_METHOD, nodeName).isSuccess();
	}
	
//	/**
//	 * 节点信息发送给
//	 * @param nodeName
//	 * @return
//	 * @throws Exception
//	 */
//	public boolean creatTable(String tableName) throws Exception {
//		return broadtoMaster(creatTable_METHOD, tableName).isSuccess();
//	}
//	
	/**
	 * 节点信息发送给
	 * @param nodeName
	 * @return
	 * @throws Exception
	 */
	public boolean tableOperator(ShellSql shellSql) throws Exception {
		return broadtoMaster(tableOperator_METHOD, shellSql).isSuccess();
	}
	
	/**
	 * 节点信息发送给
	 * @param nodeName
	 * @return
	 * @throws Exception
	 */
	public boolean deletaShard(String tableName,String splitKey) throws Exception {
		return broadtoMaster(deleteShard_METHOD, tableName, splitKey).isSuccess();
	}
	
	
	public static void main(String[] args) throws Exception {
		
	}
}
