package bgu.spl171.net.srv;

import java.util.function.Supplier;

import bgu.spl171.net.TFTP.BidiMessagingProtocolImpl;
import bgu.spl171.net.TFTP.Packet;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;

public class BidiProtocolSupplier implements Supplier<BidiMessagingProtocol<Packet>> {
	@Override
	public BidiMessagingProtocol<Packet> get(){
		return new BidiMessagingProtocolImpl();
	}

}
