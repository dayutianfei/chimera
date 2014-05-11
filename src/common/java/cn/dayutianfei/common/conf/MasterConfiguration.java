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
public class MasterConfiguration extends ChimeraConfiguration {
	// search
	// public final static String SAFE_MODE_MAX_TIME = "safemode.maxTime";
	public final static String SEARCH_MASTER_RPC_PORT = "master.rpc.port";
	public final static String SEARCH_MASTER_RPC_HANDLER_COUNT = "master.rpc.handler.count";

	public static String SEARCH_MAX_RECORD = "search.max.record";
	public static String TIMEOUT_FOR_SEARCH = "search.timeout";
	// public static String SEARCH_MIX_BUFFER_SIZE = "search.max.buffer_size";
	public static String MAX_TOP_FOR_ORDER = "search.max.top_for order";
	public final static String META_URI = "meta.uri";

	// load
	public final static String SAFE_MODE_MAX_TIME = "safemode.maxTime";
	public final static String MASTER_RPC_PORT = "master.rpc.port";
	public final static String MASTER_PRC_HANDLER_COUNT = "master.rpc.handler.count";
	public final static String MASTER_HTTP_PORT = "master.http.port";

	// search
	public MasterConfiguration() {
		super("/dplatform.server.master.properties");
	}

	public MasterConfiguration(File file) {
		super(file);
	}

	public int getSearchMasterRpcPort() {
		return getInt(SEARCH_MASTER_RPC_PORT, 50101);
	}

	// public int getSafeModeMaxTime() {
	// return getInt(SAFE_MODE_MAX_TIME);
	// }

	public int getSearchMaxRecord() {
		return getInt(SEARCH_MAX_RECORD, 100);
	}

	// public int getSearchMixBufferSize() {
	// return getInt(SEARCH_MIX_BUFFER_SIZE, 1000);
	// }

	public int getSearchMasterHandlerCount() {
		return getInt(SEARCH_MASTER_RPC_HANDLER_COUNT, 2000);
	}

	public int getMaxTopForOrder() {
		return getInt(MAX_TOP_FOR_ORDER, 100);
	}

	public String getMetaUri() {
		return getProperty(META_URI);
	}

	// load
	public int getMasterRpcPort() {
		return getInt(MASTER_RPC_PORT, 50001);
	}

	public int getRpcHandlerCount() {
		return getInt(MASTER_PRC_HANDLER_COUNT, 2000);
	}

	public int getSafeModeMaxTime() {
		return getInt(SAFE_MODE_MAX_TIME);
	}

	public int getMasterHttPort() {
		return getInt(MASTER_HTTP_PORT, 50011);
	}

}
