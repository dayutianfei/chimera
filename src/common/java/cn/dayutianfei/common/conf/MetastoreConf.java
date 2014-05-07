package cn.dayutianfei.common.conf;


import iie.mdss.server.metaDataClient.MetaDataInteraction;
import iie.mdss.server.metaDataClient.MetaStoreClientPool;

import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.*;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;


public class MetastoreConf {
	private static final int MAX_METASTORE_CLIENT_INIT_RETRIES = 5;
	private Logger LOG = Logger.getLogger(MetastoreConf.class);
	HiveMetaStoreClient client = null;
	HiveConf hiveConf = null;
	String thriftAddr = null;

	//��Ҫ����HiveConf�ķ�������Ҫ�������ļ�
	
	//private Logger LOG = Logger.getLogger(MetastoreConf.class);
	
	public HiveMetaStoreClient getMetaStoreClient() throws MetaException {
		LOG.info("2");
//		HiveConfiguration hc = new HiveConfiguration();
		HiveConf hiveConf = new HiveConf();
		
//		hiveConf.set("hive.metastore.uris", hc.getProperty("URIS", "localhost"));
		hiveConf.set("hive.metastore.uris", "thrift://192.168.0.242:9083");
		
	    for (int retryAttempt = 0; retryAttempt <= MAX_METASTORE_CLIENT_INIT_RETRIES; ++retryAttempt) {
	        try {
	        	LOG.info("3");
	        	client = new HiveMetaStoreClient(hiveConf);
	        	LOG.info("get meta client.");
	        	return client;
	        } catch (MetaException e) {
//	      	  e.printStackTrace();
	        	LOG.error("Error initializing Hive Meta Store client", e);
       }catch(Exception e){
    	   LOG.error("3 error.", e);
       }
		
		
		
	   }
	   throw new MetaException("failed initializing Hive Meta Store client");
	    

	    
}
	
	public static void main(String[] args) throws TException{		
		HiveMetaStoreClient conf=new MetastoreConf().getMetaStoreClient();
		String defaultDBName = conf.getDefaultDbName();
		System.out.println(defaultDBName);
		
	}
	
}
