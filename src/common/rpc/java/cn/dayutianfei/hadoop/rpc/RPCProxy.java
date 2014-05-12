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
package cn.dayutianfei.hadoop.rpc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.VersionedProtocol;
import org.apache.log4j.Logger;

import cn.dayutianfei.common.conf.ChimeraConfiguration;

public class RPCProxy {

	protected final static Logger LOG = Logger.getLogger(RPCProxy.class);

	private List<String> nodes;
	private RPCProxyManager proxyManager;
	/******* search ********/
	private static Method getMetaDataResult_METHOD = null;
	private static Method getKVResult_METHOD = null;

	static {
		try {
			// search
			getMetaDataResult_METHOD = IRPCHandler.class.getMethod("getMetaDataResultBySQL", new Class[] { String.class });
			getKVResult_METHOD = IRPCHandler.class.getMethod("getMetaDataResultByKV", new Class[] { String.class, String.class });
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Could not find methods in IMasterRPCServer!");
		}
	}

	public String getThingsDone(String sql) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return (String)broadtoMaster(getMetaDataResult_METHOD, sql);
	}

	public void getThings(String tableSpace, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		broadtoMaster(getKVResult_METHOD, tableSpace, key);
	}

	public RPCProxy(List<String> masterlist, ChimeraConfiguration _clientConfiguration, Configuration hadoopConf)
	{

		this.nodes = masterlist;
		LOG.debug("masters:" + nodes);
		proxyManager = new RPCProxyManager(IRPCHandler.class, hadoopConf);

		// 为每个master创建代理
		boolean createProxyOk = false;
		for (String node : nodes) {
			try {
				proxyManager.createProxy(node);
				createProxyOk = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!createProxyOk) {
			LOG.debug("Create node proxy false");
		}
	}

	public Object broadtoMaster(Method method, Object... args) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		VersionedProtocol proxy = null;
		for (VersionedProtocol p : proxyManager.getProxys()) {
			if (p != null) {
				proxy = p;
				break;
			}
		}
		if (proxy == null) {
			LOG.debug("no master is availiable");
		}
		if (method == null || args == null) {
			throw new IllegalArgumentException("Null method or args!");
		}

		// method types
		Class<?>[] types = method.getParameterTypes();
		if (args.length != types.length) {
			throw new IllegalArgumentException("Wrong number of args: found " + args.length + ", expected " + types.length + "!");
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null) {
				Class<?> from = args[i].getClass();
				Class<?> to = types[i];
				if (!to.isAssignableFrom(from) && !(from.isPrimitive() || to.isPrimitive())) {
					// Assume autoboxing will work.
					throw new IllegalArgumentException("Incorrect argument type for param " + i + ": expected " + types[i] + "!");
				}
			}
		}

		// MetaDataResult result = (MetaDataResult) method.invoke(proxy, args);
		return method.invoke(proxy, args);

	}

	public void close() {
		this.proxyManager.shutdown();
	}

}
