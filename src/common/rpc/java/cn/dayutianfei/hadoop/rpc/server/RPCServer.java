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
import org.apache.log4j.Logger;
import cn.dayutianfei.common.conf.ChimeraConfiguration;
import cn.dayutianfei.hadoop.rpc.IRPCHandler;

import com.google.common.base.Preconditions;

public class RPCServer {

	protected final static Logger LOG = Logger.getLogger(RPCServer.class);

	private String _nodeName;

	private int _rpcServerport;
	private int _rpcHandlerCount;
	private Server _rpcServer;

	public RPCServer() throws FileNotFoundException, IOException {
		this(new ChimeraConfiguration());
	}

	public RPCServer(ChimeraConfiguration configuration)
			throws FileNotFoundException, UnknownHostException {
		_nodeName = "localhost" + ":" + "8082";
		this._rpcServerport = 8082;
		this._rpcHandlerCount = 20;
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
			IRPCHandler _handler = new RPCHandler();
			LOG.info("starting rpc server with server class = "
					+ _handler.getClass().getCanonicalName());
			this._rpcServer = startRPCServer("localhost", this._rpcServerport,
					_handler, _rpcHandlerCount);
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

	public synchronized void shutdown() {
		this._rpcServer.stop();
	}

	@SuppressWarnings("deprecation")
	private static Server startRPCServer(String hostName, final int startPort,
			IRPCHandler handler, int handlerCount) {
		int serverPort = startPort;
		int tryCount = 10000;
		int triedTimes = 0;
		Server _rpcServer = null;
		while (_rpcServer == null) {
			try {
				LOG.info("start rpc server,host:" + hostName + ",port:"
						+ serverPort);
				_rpcServer = RPC.getServer(handler, "0.0.0.0", serverPort,
						handlerCount, false, new Configuration());
				LOG.info(handler.getClass().getSimpleName()
						+ " server started on : " + hostName + ":" + serverPort);
				break;
			} catch (final BindException e) {
				if (triedTimes < tryCount) {
					triedTimes++;
				} else {
					throw new RuntimeException("tried " + tryCount
							+ " ports and no one is free...");
				}
			} catch (final IOException e) {
				throw new RuntimeException("unable to create rpc server", e);
			}
		}
		_rpcServer.start();
		return _rpcServer;
	}
}
