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
package cn.dayutianfei.hadoop.rpc.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.net.UnknownHostException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.Server;
import org.apache.hadoop.net.NetUtils;
import org.apache.log4j.Logger;

import cn.dayutianfei.common.conf.ChimeraConfiguration;
import cn.dayutianfei.common.util.ClassUtil;
import cn.dayutianfei.hadoop.rpc.IRPCHandler;

import com.google.common.base.Preconditions;

public class RPCServer {

	protected final static Logger LOG = Logger.getLogger(RPCServer.class);

	private String _nodeName;

	//
//	private long _safeModeMaxTime;
	private int _rpcServerport;
	private int _rpcHandlerCount;
	private Server _rpcServer;

	public RPCServer() throws FileNotFoundException, IOException {
		this(new ChimeraConfiguration());
	}

	public RPCServer(ChimeraConfiguration configuration) throws FileNotFoundException, UnknownHostException {
		_nodeName = NetUtils.getHostname() + ":" + "8080";
		System.out.println(_nodeName);
		this._rpcServerport = 8080;
		this._rpcHandlerCount= 20;
	}

	public synchronized void start() {
		Preconditions.checkState(!isShutdown(), "master was already shut-down");
		becomePrimaryOrSecondaryMaster();
	}

	private synchronized void becomePrimaryOrSecondaryMaster() {
		if (isShutdown()) {
			LOG.error("Master is shutdown aready.");
			return;
		}

		try {
			// start RPC Server
			Class<?> serverClass = ClassUtil.forName("cn.dayutianfei.hadoop.rpc.server.RPCHandler", IRPCHandler.class);// iie.mdss.server.master.MasterRpcServer
			IRPCHandler _server = (IRPCHandler) ClassUtil.newInstance(serverClass);

			LOG.info("starting rpc server with server class = " + _server.getClass().getCanonicalName());

			this._rpcServer = startRPCServer(NetUtils.getHostname(), this._rpcServerport, _server, _rpcHandlerCount);
		} catch (Exception e) {
			LOG.error("error", e);
		}
	}

	private synchronized boolean isShutdown() {
		return false;
	}

	public String getNodeName() {
		return _nodeName;
	}

	/***
	 * 函数：startNodeManagement。 添加监听器
	 * 
	 * 
	 * private void startNodeManagement() { LOG.info("start managing nodes...");
	 * List<String> nodes = _protocol.registerChildListener(this,
	 * PathDef.NODES_LIVE, new IAddRemoveListener() {
	 * 
	 * @Override public void removed(String name) { synchronized (Master.this) {
	 *           if (!isInSafeMode()) { _protocol.addMasterOperation(new
	 *           CheckIndicesOperation()); } } }
	 * @Override public void added(String name) { synchronized (Master.this) {
	 *           if (!isMaster()) { return; } _protocol.addMasterOperation(new
	 *           RemoveObsoleteShardsOperation(name)); if (!isInSafeMode()) {
	 *           _protocol.addMasterOperation(new CheckIndicesOperation()); } }
	 *           } }); _protocol.addMasterOperation(new
	 *           CheckIndicesOperation()); for (String node : nodes) {
	 *           _protocol.addMasterOperation(new
	 *           RemoveObsoleteShardsOperation(node)); }
	 * 
	 *           LOG.info("found following nodes connected: " + nodes); }
	 */
	public synchronized void shutdown() {
		this._rpcServer.stop();
	}

	@SuppressWarnings("deprecation")
	private static Server startRPCServer(String hostName, final int startPort, IRPCHandler handler, int handlerCount) {
		int serverPort = startPort;
		int tryCount = 10000;
		Server _rpcServer = null;
		while (_rpcServer == null) {
			try {
				LOG.info("start rpc server,host:" + hostName + ",port:" + serverPort);
				_rpcServer = RPC.getServer(handler, "0.0.0.0", serverPort, handlerCount, false, new Configuration());
				LOG.info(handler.getClass().getSimpleName() + " server started on : " + hostName + ":" + serverPort);
				break;
			} catch (final BindException e) {
				if (serverPort - startPort < tryCount) {
					serverPort++;
					// try again
				} else {
					throw new RuntimeException("tried " + tryCount + " ports and no one is free...");
				}
			} catch (final IOException e) {
				throw new RuntimeException("unable to create rpc server", e);
			}
		}
		_rpcServer.start();
		return _rpcServer;
	}
}
