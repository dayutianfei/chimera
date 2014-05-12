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

	public void close() {
		this.proxyManager.shutdown();
	}

}
