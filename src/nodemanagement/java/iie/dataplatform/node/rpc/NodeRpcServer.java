package iie.dataplatform.node.rpc;

import iie.dataplatform.conf.DataPlatformConfiguration;
import iie.dataplatform.dataLife_manager.NodeDataManager;
import iie.dataplatform.log.LoggerConfig;
import iie.dataplatform.metaDataClient.MetaDataInteraction;
import iie.dataplatform.replication_manager.ReplicationNodeManager;
import iie.dataplatform.replication_manager.ReplicationTask;
import iie.dataplatform.rpc.RPC;
import iie.dataplatform.rpc.RPCResult;
import iie.dataplatform.rpc.server.Server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

public class NodeRpcServer implements INodeRpcServer {
	private MetaDataInteraction _protocol;
	private final static Logger LOG = LoggerConfig.getInstance(LoggerConfig.DATAPLATFORM_LOG);
	private Server _rpcServer = null;
	private ReplicationNodeManager replicationNodeManager;
	
	public NodeRpcServer(MetaDataInteraction protocol,DataPlatformConfiguration conf,
			ReplicationNodeManager replicationNodeManager) 
					throws FileNotFoundException, IOException{
		this._protocol = protocol;
		this.replicationNodeManager = replicationNodeManager;
	}
	public NodeRpcServer(){
		
	}
	public void close() {
		_protocol.close();
		_rpcServer.stop();
	}

	protected void finalize() {
		close();
	}
	public Server startRPCServer(int serverPort) throws IOException {
		_rpcServer = null;
		while (_rpcServer == null) {
			_rpcServer = RPC.getServer(this, "0.0.0.0", serverPort);
		}
		_rpcServer.start();
		LOG.info(this.getClass().getSimpleName()
				+ " server started on : " + serverPort);
		return _rpcServer;
	}
	
	/**
	 * 删除满足条件的数据.
	 * @param path 数据所在的路径.
	 */
	public RPCResult deleteDataByPath(List<String> paths){
		RPCResult result = new RPCResult();
		try {
			NodeDataManager.mvShardsToRecycle(paths);
			result.setSuccess(true);
			return result;
		} catch (Exception e) {
			LOG.error("delete data in node RPC ,",e);
			return result;
		}
	}
	/**
	 * 执行副本任务，从源节点将文件拷贝到本地
	 * @param tasks 副本任务的集合
	 */
	public RPCResult repShardToNode(List<ReplicationTask> tasks){
		RPCResult result = new RPCResult();
		try {
			replicationNodeManager.rsync(tasks);
			result.setSuccess(true);
			return result;
		} catch (Exception e) {
			LOG.error("rep shard to node false,",e);
			return result;
		}
	}
}
