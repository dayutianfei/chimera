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
package cn.dayutianfei.common.conf;

import java.io.File;

@SuppressWarnings("serial")
public class NodeConfiguration extends ChimeraConfiguration {
	// search
	public final static String NODE_RPC_PORT = "node.rpc.port";
	public final static String SEARCH_NODE_RPC_HANDLER_COUNT = "node.rpc.handler.count";
	private static final String SERVER_CLASS = "node.server.class";
	private static final String MAXSHARD_NUM = "node.maxshard.num";
	private static final String MAXRECORD_NUM = "node.default.maxRecords";
	public final static String META_URI = "meta.uri";

	// stat
	public final static String STAT_DISK_PATH = "stat.disk.path";
	public final static String STAT_TIMEOFF = "stat.timeoff";

	// load
	public final static String NODE_UPLOAD_PORT = "node.load.http.port";
	public final static String MAX_SHARD_SIZE = "table.kv.mix.shardsize";
	public final static String MAX_SHARD_RECORD_NUM = "table.normal.max.record.num";
	public final static String MAX_WAIT_CLOSE_TIME = "shard.max.interval";
	public final static String CHCHE_TIME = "shard.commit.interval";
	public final static String NODE_RPC_HANDLER = "node.rpc.handler";

	public NodeConfiguration() {
		super("/dplatform.server.node.properties");
	}

	public NodeConfiguration(File file) {
		super(file);
	}

	public int getNodeRpcPort() {
		return getInt(NODE_RPC_PORT, 60002);
	}

	public int getSearchNodeHandlerCount() {
		return getInt(SEARCH_NODE_RPC_HANDLER_COUNT, 12);
	}

	public String getMetaUri() {
		return getProperty(META_URI);
	}

	public int getMaxShardNum() {
		return getInt(MAXSHARD_NUM, 2);
	}

	public int getMaxRecordNum() {
		return getInt(MAXRECORD_NUM, 1000);
	}

	public String getStatDiskPath() {
		return getProperty(STAT_DISK_PATH);
	}

	public long getStatTimeOff() {
		return getLong(STAT_TIMEOFF, 1000 * 60 * 1l);
	}

	// load
	public int getNodeUploadPort() {
		return getInt(NODE_UPLOAD_PORT, 50012);
	}

	public long getMaxShardSize() {
		return getLong(MAX_SHARD_SIZE, 1024 * 1024 * 10 * 1l);
	}

	public int getMaxShardRecordNum() {
		return getInt(MAX_SHARD_RECORD_NUM, 10000);// 1000000
	}

	public long getMaxWaitCloseTime() {
		return getLong(MAX_WAIT_CLOSE_TIME, 60 * 60);
	}

	public long getCacheTime() {
		return getLong(CHCHE_TIME, 60);
	}

	public int getNodeRpcHandler() {
		return getInt(NODE_RPC_HANDLER, 2000);
	}
}
