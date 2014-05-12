/**
Copyright 2008 the original author or authors.
 *
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 *
http://www.apache.org/licenses/LICENSE-2.0
 *
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package cn.dayutianfei.hadoop.rpc.server;

import java.io.IOException;
import java.util.UUID;

import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.log4j.Logger;

import cn.dayutianfei.hadoop.rpc.IRPCHandler;
import cn.dayutianfei.hadoop.rpc.RPCResult;

public class RPCHandler implements IRPCHandler {
	private final static Logger LOG = Logger.getLogger(RPCHandler.class);

	

	public RPCHandler() {}



	public ProtocolSignature getProtocolSignature(String arg0, long arg1,
			int arg2) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}



	public long getProtocolVersion(String arg0, long arg1) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}



	public void getThingsDone(String thingName) {
		// TODO Auto-generated method stub
		System.out.println("hello"+thingName);
	}



	public RPCResult getThings(String thingName1, String thingName2) {
		// TODO Auto-generated method stub
		System.out.println("hello"+thingName1+thingName2);
		RPCResult ss = new RPCResult();
		ss.setMessage("hello"+thingName1);
		return ss;
	}



	public RPCResult getThingsResult(String thing) {
		// TODO Auto-generated method stub
		RPCResult ss = new RPCResult();
		 ss.setMessage(thing);
		 return ss;
	}

	

}
