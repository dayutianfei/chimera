package iie.dataplatform.node.rpc;

import iie.dataplatform.replication_manager.ReplicationTask;
import iie.dataplatform.rpc.RPCResult;

import java.util.List;

public interface INodeRpcServer {
	public RPCResult deleteDataByPath(List<String> paths);
	public RPCResult repShardToNode(List<ReplicationTask> tasks);
}
