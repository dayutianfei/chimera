package cn.dayutianfei.hadoop.rpc;

import org.apache.hadoop.ipc.VersionedProtocol;



public interface IRPCHandler  extends VersionedProtocol {
	public static final long versionID = 1l;

	public void getThingsDone(String thingName);
	
	public RPCResult getThings(String thingName1, String thingName2);
	
	public RPCResult getThingsResult(String thing);

}