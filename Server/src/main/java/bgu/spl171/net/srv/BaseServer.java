package bgu.spl171.net.srv;

import bgu.spl171.net.api.MessageEncoderDecoder;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private ConnectionsImpl<T> myConnections;
    private int connectedId=0;
    private BidiMessagingProtocol<T> protocol=null; 
 
	public BaseServer(int port, Supplier<BidiMessagingProtocol<T>> protocolFactory,
			Supplier<MessageEncoderDecoder<T>> encdecFactory) {
		myConnections=new ConnectionsImpl<T>();
		this.port = port;
		this.protocolFactory = protocolFactory;
		this.encdecFactory = encdecFactory;
		this.sock = null;
	}

    @Override
	public void serve() {

		try (ServerSocket serverSock = new ServerSocket(port)) {

			this.sock = serverSock; // just to be able to close

			while (!Thread.currentThread().isInterrupted()) {

				Socket clientSock = serverSock.accept();
             
				protocol =protocolFactory.get();
			
				BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(clientSock, encdecFactory.get(),
						protocol);
				protocol.start(connectedId,myConnections);
				myConnections.add(connectedId, handler);
				connectedId++;
				execute(handler);
			}
		} catch (IOException ex) {
		}

		System.out.println("server closed!!!");
	}

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}
