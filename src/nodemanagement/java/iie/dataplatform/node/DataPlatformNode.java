package iie.dataplatform.node;

import iie.dataplatform.conf.DataPlatformConfiguration;
//import org.apache.hive.service.syn.op.struct.Status;
import iie.dataplatform.conf.KattaConstant;
import iie.dataplatform.conf.MetastoreConf;
import iie.dataplatform.log.LogType;
import iie.dataplatform.log.LoggerConfig;
import iie.dataplatform.metaDataClient.MetaDataInteraction;
import iie.dataplatform.node.node_manager.NodeHeartbeatThread;
import iie.dataplatform.node.rpc.NodeRpcServer;
import iie.dataplatform.prototype.NodeInfo;
import iie.dataplatform.prototype.NodeInfo.Status;
import iie.dataplatform.replication_manager.ReplicationNodeManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.I0Itec.zkclient.NetworkUtil;
import org.apache.hadoop.hive.metastore.api.AlreadyExistsException;
import org.apache.hadoop.hive.metastore.api.Device;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

/**
 * 数据节点类. 数据节点启动流程: 1、初始化. 2、创建节点(将节点信息注册到元数据中去,并且创建节点相关文件).
 * 3、启动心跳机制,用于master节点管理. 4、启动RPC服务,用于接收master下发的生命周期管理、副本管理等任务.
 * 
 * @author Administrator
 * 
 */
public class DataPlatformNode {
	static {

	}
	protected static final Logger logger = LoggerConfig.getInstance(LoggerConfig.DATAPLATFORM_LOG);

	private static final String NODE_NAME = "D";

	private String nodeName;
	private MetaDataInteraction metaInter;
	private ArrayList<Thread> _nodeOperatorThreadList;
	private ArrayList<Thread> service;
	private boolean _stopped = false;
	private DataPlatformConfiguration _nodeConf;
	private List<String> dataFolders;
	private Map<Integer, String> diskNumToPath;
	private ReplicationNodeManager replicationNodeManager;
	private NodeRpcServer nodeRpcServer;
	private Thread heartBeatThread;

	public DataPlatformNode() throws MetaException {
		metaInter = new MetaDataInteraction(
				new MetastoreConf().getMetaStoreClient());
		nodeName = NODE_NAME + NetworkUtil.getLocalhostName().replace("-", "");
		_nodeConf = new DataPlatformConfiguration();
		diskNumToPath = _nodeConf.getDiskNumToPath();
		replicationNodeManager = new ReplicationNodeManager();
	}

	public void startDataNode() throws InvalidObjectException,
			AlreadyExistsException, TException, FileNotFoundException,
			IOException {
		if (_stopped) {
			throw new IllegalStateException(
					"Node cannot be started again after it was shutdown.");
		}
		/********* 创建节点 **********/
		publishNodeToHive(); // 将节点信息发布到Hive元数据.
		mkdirs(); // 创建节点相关的文件路径.

		/********* 启动心跳 ************/
		startHeartbeat(nodeName, _nodeConf);
		logger.info("DataPlatFormNode start heart beat  !");

		/** 启动RPC服务 **/
		nodeRpcServer = new NodeRpcServer(metaInter, _nodeConf,
				replicationNodeManager);
		nodeRpcServer.startRPCServer(_nodeConf.getNodeRpcPort());
	}

	/**
	 * 启动心跳机制.
	 * 
	 * @param nodeName
	 * @param _nodeConf
	 * @throws UnknownHostException
	 */
	private void startHeartbeat(String nodeName,
			DataPlatformConfiguration _nodeConf) throws UnknownHostException {
		heartBeatThread = new Thread(new NodeHeartbeatThread(
				_nodeConf.getMaster(), nodeName, _nodeConf.getMasterRpcPort()));
		heartBeatThread.start();
	}

	/**
	 * 将节点信息发布到元数据.
	 * 
	 * @throws UnknownHostException
	 * @throws MetaException
	 * @throws TException
	 */
	public void publishNodeToHive() throws UnknownHostException, MetaException,
			TException {
		NodeInfo node = new NodeInfo();
		node.setNodeName(nodeName);
		node.setIp(InetAddress.getLocalHost().getHostAddress());
		node.setStatus(Status.live);
		try {
			metaInter.publishNodeInfo(node);
			logger.info("publish node success !");
		} catch (Exception e) {
			logger.warn("publish node failed !", e);
		}
	}

	public boolean isRunning() {
		return !_stopped;
	}

	public void shutdown() throws InterruptedException {
		logger.info(nodeName + " disconnected");
		heartBeatThread.interrupt();
		nodeRpcServer.close();
		_stopped = true;
	}

	/**
	 * 创建文件路径.
	 * 
	 * @throws InvalidObjectException
	 * @throws AlreadyExistsException
	 * @throws MetaException
	 * @throws TException
	 */
	public void mkdirs() throws InvalidObjectException, AlreadyExistsException,
			MetaException, TException {
		dataFolders = new ArrayList<String>();
//		dataFolders.add(KattaConstant.digestFolder);
		dataFolders.add(KattaConstant.shardFolder);
//		dataFolders.add(KattaConstant.storeFileFolder);
		for (Integer diskNum : diskNumToPath.keySet()) {
			Device dev = new Device();
			dev.setMount_location(diskNumToPath.get(diskNum));
			dev.setName(nodeName + "_" + diskNum);
			dev.setNodename(nodeName);
			Map<String, String> map = new HashMap<String, String>();
			map.put("dev.status",
					org.apache.hive.service.syn.op.struct.Status.DeviceStatus.ONLINE
							+ "");
			dev.setParameters(map);
			try {
				metaInter.publishDevice(dev);
				logger.info("publish device success ! ");
			} catch (Exception e) {
				logger.warn("publish device failed !", e);
			}
			for (String folder : dataFolders) {
				File shardsFolder = new File(diskNumToPath.get(diskNum), folder);
				if (!shardsFolder.exists()) {
					shardsFolder.mkdirs();
				}
			}
		}
	}

	protected void finalize() throws Throwable {
		super.finalize();
		shutdown();
	}

	public static void main(String[] args) throws InvalidObjectException,
			AlreadyExistsException, TException, FileNotFoundException,
			IOException {
		DataPlatformNode node = new DataPlatformNode();
		node.startDataNode();
	}

}
