package iie.dataplatform.master.rpc;

import iie.dataplatform.rpc.RPCResult;
import iie.dataplatform.prototype.ShellSql;

public interface IMasterRpcServer {

	public static final long versionID = 1l;
	public RPCResult sentHeartBeat(String nodeName);
	public RPCResult tableOperator(ShellSql shellSql);
	public RPCResult deleteShard(String tableName,String splitValue);
}