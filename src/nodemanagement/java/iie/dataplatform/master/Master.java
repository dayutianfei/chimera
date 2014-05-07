package iie.dataplatform.master;

import iie.dataplatform.conf.MasterConfiguration;
import iie.dataplatform.conf.MetastoreConf;
import iie.dataplatform.dataLife_manager.DataLifeManager;
import iie.dataplatform.node.node_manager.NodeManagerServer;
import iie.dataplatform.log.LoggerConfig;
import iie.dataplatform.master.rpc.MasterRpcServer;
import iie.dataplatform.metaDataClient.MetaDataInteraction;
import iie.dataplatform.replication_manager.ReplicationManager;
import iie.dataplatform.replication_manager.ReplicationThread;
import iie.dataplatform.util.NetUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;

import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.log4j.Logger;

/**
 * 数据平台master(主节点)类. 实现平台集群的节点管理、生命周期管理、副本管理功能.
 * 
 * @author MeiBaiQi.
 * 
 */
public class Master {

	private String _masterName;
	protected MetaDataInteraction _protocol;
	private NodeManagerServer nodeManagerServer;
	private ReplicationManager replicationManager;
	private MasterConfiguration masterConfiguration;
	private MasterRpcServer masterRpcServer;
	protected final static Logger logger = LoggerConfig.getInstance(LoggerConfig.DATAPLATFORM_LOG);

	public Master() throws MetaException {
		_protocol = new MetaDataInteraction(
				new MetastoreConf().getMetaStoreClient());
		masterConfiguration = new MasterConfiguration();
	}

	public synchronized void start() throws UnknownHostException {
		_masterName = NetUtil.getHostName() + ":"
				+ masterConfiguration.getMasterPort();

		/****************************************** 启动节点管理 ****************************************/
		logger.info("begin to start node manager server...");
		try {
			nodeManagerServer = new NodeManagerServer(_protocol,
					masterConfiguration);
			logger.info("node manager server start successfully...");
		} catch (Exception e1) {
			logger.error("start node manager server failed,", e1);
		}

		/************************************** 启动生命周期管理 ***************************************/
		logger.info("begin to start data life manager server...");
		try {
			new DataLifeManager(nodeManagerServer, _protocol,
					masterConfiguration.getMaitainTime(),
					masterConfiguration.getDataLifeTime(),
					masterConfiguration.getNodePort());
			logger.info("data life manager server start successfully...");
		} catch (Exception e1) {
			logger.error("start master data life manager server failed", e1);
		}

		/************************************** 启动副本管理 ***************************************/
		logger.info("begin to start replication manager server...");
		try {
			replicationManager = new ReplicationManager(
					masterConfiguration.getTableAndNumsToRep(), _protocol,
					masterConfiguration.getNodePort());
			ReplicationThread repThread = new ReplicationThread(
					masterConfiguration.getReplicationExeTime(),
					replicationManager);
			logger.info("start replication manager server successfully...");
			repThread.start();
		} catch (Exception e) {
			logger.error("start replication manager server failed", e);
		}

		/************************************* 启动master节点RPC服务 *******************************/
		try {
			masterRpcServer = new MasterRpcServer(_protocol, masterConfiguration,
					nodeManagerServer);
			masterRpcServer.startRPCServer(masterConfiguration
					.getMasterPort());
			logger.info("start MasterRpcServer");
		} catch (Exception e) {
			logger.error("start MasterRpcServer failed", e);
		}
	}

	// public synchronized void start() {
	// Preconditions.checkState(!isShutdown(), "master was already shut-down");
	// }

	/***
	 * 函数：返回 是否运行在安全模式下 返回值： 如果不是master，则返回 true 如果是master，则返回operatorThread
	 * 是否运行在安全模式下
	 * 
	 * operatorThread的安全模式是这样判定的： operatorThread的_safeMode默认为true SAFE MODE: No
	 * nodes available or state unstable within the last
	 * 当operatorThread处于以上状态时，其处于安全模式
	 */

	public Collection<String> getConnectedNodes() {
		return _protocol.getNodeNames();
	}

	private synchronized boolean isShutdown() {
		return _protocol == null;
	}

	public String getMasterName() {
		return _masterName;
	}

	public synchronized void shutdown() {
		_protocol.close();
	}

	public static void main(String[] args) throws FileNotFoundException,
			MetaException, IOException {
		new Master().start();
	}
}
