package bgu.spl171.net.impl.TFTPreactor;

import bgu.spl171.net.TFTP.BidiMessagingProtocolImpl;
import bgu.spl171.net.srv.BidiEncoderDecoder;
import bgu.spl171.net.srv.Server;

public class ReactorMain {

	public static void main(String[] args) {
		Server.reactor(Runtime.getRuntime().availableProcessors(),// numberOfThreads
				7777,//port
				() -> new BidiMessagingProtocolImpl(),// protocol factory	
				BidiEncoderDecoder::new).serve();// message encoder decoder factory

	}
}
