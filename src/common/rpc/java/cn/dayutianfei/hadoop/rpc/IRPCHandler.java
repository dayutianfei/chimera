package cn.dayutianfei.hadoop.rpc;

import org.apache.hadoop.ipc.VersionedProtocol;

/**
 * all return type must be RPCResult
 * 
 * @author egret
 * 
 */
public interface IRPCHandler extends VersionedProtocol {
	public static final long versionID = 1l;

	public RPCResult getThings(String arg1);

	public RPCResult getThings(String arg1, String arg2);

	public RPCResult getThingsResult(String arg1);

}