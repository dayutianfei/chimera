package cn.dayutianfei.egret.rpc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;



public class Server {
		private int port = 20382;
		private Listener listener; 
		private boolean isRuning = true;
		
		/**
		 * @param isRuning the isRuning to set
		 */
		public void setRuning(boolean isRuning) {
			this.isRuning = isRuning;
		}

		/**
		 * @return the port
		 */
		public int getPort() {
			return port;
		}

		/**
		 * @param port the port to set
		 */
		public void setPort(int port) {
			this.port = port;
		}

		private Map<String ,Object> serviceEngine = new HashMap<String, Object>();
		
		
		public void call(Invocation invo) {
			Object obj = serviceEngine.get(invo.getInterfaces().getName());
			if(obj!=null) {
				try {
					Method m = obj.getClass().getMethod(invo.getMethod().getMethodName(), invo.getMethod().getParams());
					Object result = m.invoke(obj, invo.getParams());
					invo.setResult(result);
				} catch (Throwable th) {
					th.printStackTrace();
				}
			} else {
				throw new IllegalArgumentException("has no these class");
			}
		}

		public void register(Class<?> impl,Object instance) {
			try {
				this.serviceEngine.put(impl.getInterfaces()[0].getName(), instance);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}

		public void start() {
			listener = new Listener(this);
			this.isRuning = true;
			listener.start();
		}

		public void stop() {
			listener.interrupt();
			this.setRuning(false);
		}

		public boolean isRunning() {
			return isRuning;
		}
		
}
