package iie.dataplatform.master.rpc;

import iie.dataplatform.log.LoggerConfig;
import iie.dataplatform.metaDataClient.MetaDataInteraction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class HeartBeatNode {
	
	private Logger log = LoggerConfig.getInstance(LoggerConfig.DATAPLATFORM_LOG);
	public ConcurrentHashMap<String, Long> nodeName_lastHearBeat = new ConcurrentHashMap<String, Long>();
	/**
	 * 下线的节点集合.
	 */
	public ConcurrentHashMap<String, Long> node_offline = new ConcurrentHashMap<String, Long>();

	private MetaDataInteraction metaDataClient;
	
	public HeartBeatNode(MetaDataInteraction _protocol) {
		this.metaDataClient = _protocol;
	}


	/**
	 * 获取正常给master发送心跳的dataNode集合.
	 * 
	 * @param liveTime
	 *            心跳允许最大间隔.
	 * @return
	 */
	public List<String> getLiveNode(long liveTime) {
		List<String> liveNodes = new ArrayList<String>();
		synchronized (nodeName_lastHearBeat) {
			for (Iterator<String> it = nodeName_lastHearBeat.keySet()
					.iterator(); it.hasNext();) {
				String nodeName = it.next();
				long lastHeartBeat = nodeName_lastHearBeat.get(nodeName);
				if (liveTime > (System.currentTimeMillis() - lastHeartBeat)) {
					liveNodes.add(nodeName);
				}
			}
		}
		return liveNodes;
	}

	
	
	
	public ConcurrentHashMap<String, Long> getNodeName_lastHearBeat() {
		synchronized(nodeName_lastHearBeat){
			return nodeName_lastHearBeat;
		}
	}

	public void setNodeName_lastHearBeat(
			ConcurrentHashMap<String, Long> nodeName_lastHearBeat) {
		this.nodeName_lastHearBeat = nodeName_lastHearBeat;
	}


	/**
	 * 更新节点状态.
	 * 如果下线,则从nodeName_lastHearBeat移动到node_offline.
	 * 如果上线,则从node_offline移除,nodeName_lastHearBeat会自动添加.
	 * @param liveTime
	 */
	public void updataNodeStatus(long liveTime){
		synchronized(nodeName_lastHearBeat){
			for (Iterator<String> it = nodeName_lastHearBeat.keySet()
					.iterator(); it.hasNext();) {
				String nodeName = it.next();
				long lastHeartBeat = nodeName_lastHearBeat.get(nodeName);
				if ((System.currentTimeMillis() - lastHeartBeat) <= liveTime) {//距离上次接收的心跳时间小于等于存活时间间隙,说明节点状态为live.
					if(node_offline.containsKey(nodeName)){//如果下线节点集合中有该节点,说明该节点上线.
						System.out.println("dataNode : " + nodeName + " is online ......"  );
						node_offline.remove(nodeName);//从下线集合中移除.
						metaDataClient.dataNodeOnline(nodeName);
						log.info("dataNode : " + nodeName + " is online ......");
					}
				}else{//距离上次接收的心跳时间大于存活时间间隙,说明节点状态可能为dead.（需要确定下线之后是直接删除还是标记为dead）
					if(!node_offline.containsKey(nodeName)){//如果下线节点集合中没有有该节点,说明该节点下线.
						node_offline.put(nodeName, System.currentTimeMillis());//添加到下线节点集合.
						nodeName_lastHearBeat.remove(nodeName);//下线之后从nodeName_lastHearBeat集合中移除.
						System.out.println("dataNode : " + nodeName + " is offline ......"  );
						log.info("dataNode : " + nodeName + " is offline ......");
						try{
							metaDataClient.unpublishNodeInfo(nodeName);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
