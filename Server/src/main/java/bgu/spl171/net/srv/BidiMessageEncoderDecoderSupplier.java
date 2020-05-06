package bgu.spl171.net.srv;

import java.util.function.Supplier;
import bgu.spl171.net.TFTP.Packet;
import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.srv.BidiEncoderDecoder;

public class BidiMessageEncoderDecoderSupplier implements Supplier<MessageEncoderDecoder<Packet>> {
	@Override
	public MessageEncoderDecoder<Packet> get() {
		return new BidiEncoderDecoder();
	}

}
