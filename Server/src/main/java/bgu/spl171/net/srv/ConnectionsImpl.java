package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.Connections;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionsImpl<T> implements Connections<T> {
	private ConcurrentHashMap<Integer, ConnectionHandler<T>> ConnectedClients = new ConcurrentHashMap<>();

	/**
	 * sends a message T to client represented by the given connId
	 */
	public boolean send(int connectionId, T msg) {
		try {// try to send a message to the client
			ConnectionHandler<T> handler = ConnectedClients.get(connectionId);
			if (handler != null) {
				handler.send(msg);
				return true;
			}
		} catch (Exception e) { //##############catch the needed error)#######################
		}
		return false;
	}

	/**
	 * sends a message T to all active clients. This includes clients that has
	 * not yet completed log-in by the extended TFTP protocol
	 */
	public void broadcast(T msg) {
		// passing on the map of connected clients and send message to them
		for (ConnectionHandler<T> myhandler : ConnectedClients.values())
			myhandler.send(msg);
	}

	/**
	 * removes active client connId from map.
	 */
	public void disconnect(int connectionId) {
		if (ConnectedClients.get(connectionId) != null)
			ConnectedClients.remove(connectionId);
	}

	/**
	 * this function add new client to the ConnectedClients
	 * @return true if succeeded to add this client to the map and false otherwise
	 */
	public boolean add(int connectionId, ConnectionHandler<T> handler) {
		if (handler != null){
		ConnectedClients.put(connectionId, handler);
		return true;
		}
		return false;
	}
	/**
	 * 
	 * @param connectionId
	 * @return true if the client is connected and false otherwise
	 */
	public boolean isConnected(int connectionId) {
		if ((ConnectedClients.get(connectionId))!=null)
			return true;
		return false;
	}
}
