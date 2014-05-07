/**
 * Copyright 2011 the original author or authors.
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
package iie.dataplatform.rpc.client;

import iie.dataplatform.log.LoggerConfig;
import iie.dataplatform.rpc.INodeProxyManager;
import iie.dataplatform.rpc.RPC;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**master RCP 管理
 * @author :LiBinBin
 * function:
 * date    :2012-9-7
 */
public class MasterProxyManager implements INodeProxyManager {

  private final static Logger LOG = LoggerConfig.getInstance(LoggerConfig.DATAPLATFORM_LOG);
  /**
 * 
 */
private final Class _serverClass;
  /**
 * 缓存各个master的rpc代理
 */
private final Map<String, Object> master2ProxyMap = new ConcurrentHashMap<String, Object>();
  private int _successiveProxyFailuresBeforeReestablishing = 3;
  private final Multiset<String> _failedNodeInteractions = HashMultiset.create();

  public MasterProxyManager(Class serverClass) {
    _serverClass = serverClass;
  }

  /**
   * @return how many successive proxy invocation errors must happen before the
   *         proxy is re-established.
   */
  public int getSuccessiveProxyFailuresBeforeReestablishing() {
    return _successiveProxyFailuresBeforeReestablishing;
  }

  public void setSuccessiveProxyFailuresBeforeReestablishing(int successiveProxyFailuresBeforeReestablishing) {
    _successiveProxyFailuresBeforeReestablishing = successiveProxyFailuresBeforeReestablishing;
  }

  public Object createMasterProxy(String hostName,int port) throws IOException {
    LOG.debug("creating proxy for master: " + hostName);
    final InetSocketAddress inetSocketAddress = new InetSocketAddress(hostName, port);
    Object proxy = RPC.getProxy(_serverClass, hostName,port);
    LOG.debug(String.format("Created a proxy %s for %s:%s %s", Proxy.getInvocationHandler(proxy), hostName, port,
            inetSocketAddress));
    master2ProxyMap.put(hostName, proxy);
    return proxy;
  }

  @Override
  public Object getProxy(String nodeName,int port, boolean establishIfNoExists) {
	  Object versionedProtocol = master2ProxyMap.get(nodeName);
    if (versionedProtocol == null && establishIfNoExists) {
      synchronized (nodeName.intern()) {
        if (!master2ProxyMap.containsKey(nodeName)) {
          try {
        	versionedProtocol = createMasterProxy(nodeName,port);
            master2ProxyMap.put(nodeName, versionedProtocol);
          } catch (Exception e) {
            LOG.warn("Could not create proxy for master '" + nodeName + "' - " + e.getClass().getSimpleName() + ": "
                    + e.getMessage());
          }
        }
      }
    }
    return versionedProtocol;
  }
  @SuppressWarnings("unchecked")
  @Override
  public void reportNodeCommunicationFailure(String nodeName, Throwable t) {
    // TODO jz: not sure if there are cases a proxy is getting invalid and
    // re-establishing it would fix the communication. If so, we should check
    // the for the exception which occurs in such cases and re-establish the
    // proxy.
    _failedNodeInteractions.add(nodeName);
    int failureCount = _failedNodeInteractions.count(nodeName);
    if (failureCount >= _successiveProxyFailuresBeforeReestablishing
            || exceptionContains(t, ConnectException.class, EOFException.class)) {
      dropNodeProxy(nodeName, failureCount);
    }
  }

  private boolean exceptionContains(Throwable t, Class<? extends Throwable>... exceptionClasses) {
    while (t != null) {
      for (Class<? extends Throwable> exceptionClass : exceptionClasses) {
        if (t.getClass().equals(exceptionClass)) {
          return true;
        }
      }
      t = t.getCause();
    }
    return false;
  }

  private void dropNodeProxy(String nodeName, int failureCount) {
    synchronized (nodeName.intern()) {
      if (master2ProxyMap.containsKey(nodeName)) {
        LOG.warn("removing proxy for node '" + nodeName + "' after " + failureCount + " proxy-invocation errors");
        _failedNodeInteractions.remove(nodeName, Integer.MAX_VALUE);
        Object proxy = master2ProxyMap.remove(nodeName);
        RPC.stopProxy(proxy);
      }
    }
  }

  @Override
  public void reportNodeCommunicationSuccess(String node) {
    _failedNodeInteractions.remove(node, Integer.MAX_VALUE);
  }

  @Override
  public void shutdown() {
    Collection<Object> proxies = master2ProxyMap.values();
    for (Object search : proxies) {
      RPC.stopProxy(search);
    }
  }
  public Collection<Object> getProxys(){
	  return master2ProxyMap.values();
  }

}
