package cn.dayutianfei.egret.rpc;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;


public class Listener extends Thread {
	private ServerSocket socket;
	private Server server;
	private final static Logger LOG = Logger.getLogger(Listener.class);
	public Listener(Server server) {
		this.server = server;
	}

	@Override
	public void run() {

		LOG.info("start rpc server on port " + server.getPort());
		try {
			socket = new ServerSocket(server.getPort());
		} catch (IOException e1) {
			LOG.error("start port "+server.getPort()+" error, ",e1);
			return;
		}
		while (server.isRunning()) {
			try {
				Socket client = socket.accept();
				ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
				Invocation invo = (Invocation) ois.readObject();
				server.call(invo);
				ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
				oos.writeObject(invo);
				oos.flush();
				oos.close();
				ois.close();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				LOG.error("execute server on port "+server.getPort()+" error, ",e);
			}

		}

		try {
			if (socket != null && !socket.isClosed())
				socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
