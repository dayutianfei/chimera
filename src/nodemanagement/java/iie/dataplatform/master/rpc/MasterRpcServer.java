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
package iie.dataplatform.master.rpc;

import iie.dataplatform.conf.MasterConfiguration;
import iie.dataplatform.log.LoggerConfig;
import iie.dataplatform.metaDataClient.MetaDataInteraction;
import iie.dataplatform.node.node_manager.NodeManagerServer;
import iie.dataplatform.prototype.ShardInfo;
import iie.dataplatform.prototype.ShardToNode;
import iie.dataplatform.prototype.ShellSql;
import iie.dataplatform.prototype.SplitValue;
import iie.dataplatform.rpc.RPC;
import iie.dataplatform.rpc.RPCResult;
import iie.dataplatform.rpc.server.Server;
import iie.dataplatform.util.DDLClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

/***
 * 类：katta客户端
 * 
 * @author Administrator
 * 
 */
public class MasterRpcServer implements IMasterRpcServer {
	private final static Logger logger = LoggerConfig.getInstance(LoggerConfig.DATAPLATFORM_LOG);

	private MetaDataInteraction _protocol;

	private Server _rpcServer = null;
	private NodeManagerServer nodeManagerServer;

	public MasterRpcServer() {

	}

	public MasterRpcServer(MetaDataInteraction protocol,
			MasterConfiguration conf, NodeManagerServer nodeManagerServer)
			throws FileNotFoundException, IOException {
		this._protocol = protocol;
		this.nodeManagerServer = nodeManagerServer;
	}

	/****************************** 功能函数 ****************************/

	public void close() {
		_protocol.close();
		_rpcServer.stop();
	}

	protected void finalize() {
		close();
	}

	//

	/***
	 * RCP Server是为了响应请求的
	 */
	public Server startRPCServer(int serverPort) throws IOException {
		_rpcServer = null;
		while (_rpcServer == null) {
			_rpcServer = RPC.getServer(this, "0.0.0.0", serverPort);
		}
		_rpcServer.start();
		logger.info(this.getClass().getSimpleName() + " server started on : "
				+ serverPort);
		return _rpcServer;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
	}

	/**
	 * 发送心跳.
	 * 
	 * @param nodeName
	 *            心跳发送者名称.
	 */
	@Override
	public RPCResult sentHeartBeat(String nodeName) {
		try {
			nodeManagerServer.recieveHeartBeat(nodeName);
		} catch (Exception e) {
			logger.error("sentHeartBeat error,", e);
		}
		RPCResult result = new RPCResult();
		result.setSuccess(true);
		return result;
	}

	/**
	 * 创建表.
	 * @param sql
	 * @throws Exception
	 */
	public void creatTable(String sql) throws Exception {
		DDLClient ddlClient = new DDLClient(_protocol);
		ddlClient.exeCmd(sql);
	}

	/**
	 * 删除表信息.
	 * 先删除表下的物理文件.再删除表即表下元数据中的文件.
	 * @throws Exception 
	 */
	public void dropTable(String sql) throws Exception {
		String[] values = sql.trim().split(" ");
		String tableName = values[1];
		try{
			List<ShardInfo> shards = _protocol.getAllShard(tableName);// 获取表下的所有文件.
			List<String> liveNodes = _protocol.getNodeNames();// 所有活着的节点.
			_protocol.unpublishTable(tableName);
			for (ShardInfo shardInfo : shards) {
				List<ShardToNode> shardToNodes = shardInfo.getShardNode();
				for (ShardToNode node : shardToNodes) {// 循环该Shard所在的所有Node,删除对应Node下的文件(即副本文件).
					nodeManagerServer.deleteShard(liveNodes, node);
				}
				_protocol
						.unpublishShardOnLogic(tableName, shardInfo.getShardName());
			}
		}catch(Exception e){
			throw e;
		}
	}

	/**
	 * 删除指定分区下的文件.
	 */
	@Override
	public RPCResult deleteShard(String tableName, String splitValue) {
		logger.info("deleteShard====================tableName " + tableName);
		logger.info("deleteShard====================splitValue " + splitValue);
		RPCResult result = new RPCResult();
		List<ShardInfo> shards = _protocol.getShardListByFileSplitKey(tableName, splitValue);
		List<String> liveNodes = _protocol.getNodeNames();// 所有活着的节点.
		for (ShardInfo shardInfo : shards) {
			List<ShardToNode> shardToNodes = shardInfo.getShardNode();
			for (ShardToNode node : shardToNodes) {// 循环该Shard所在的所有Node,删除对应Node下的文件(即副本文件).
				nodeManagerServer.deleteShard(liveNodes, node);
			}
			_protocol
					.unpublishShardOnLogic(tableName, shardInfo.getShardName());
		}
		result.setSuccess(true);
		logger.info("result.setSuccess(true);====================splitValue "+result.toString());
		return result;
	}

	/**
	 * 对表的操作,目前支持表的创建、表的删除.
	 */
	@Override
	public RPCResult tableOperator(ShellSql shellSql) {
		int type  = shellSql.getType();
		String sql = shellSql.getSql();
		RPCResult result = new RPCResult();
		try{
			if(type == ShellSql.SqlType.CREAT){
				creatTable(sql);
			}
			if(type == ShellSql.SqlType.ALTER){
				creatTable(sql);
			}
			if( type == ShellSql.SqlType.DROP ){
				dropTable(sql);
			}
		}catch(Exception e){
			logger.error("tableOperator fail : " + e.getMessage());
			result.setSuccess(false);
			result.setMessage(e.getMessage());
			return result;
		}
		
		logger.info("tableOperator success !!!");
		
		result.setSuccess(true);
		return result;
	}
}
