package bgu.spl171.net.impl.TFTPtpc;

import bgu.spl171.net.TFTP.BidiMessagingProtocolImpl;
import bgu.spl171.net.srv.BidiEncoderDecoder;
import bgu.spl171.net.srv.Server;

public class TPCMain {
	public static void main(String[] args) {
		Server.threadPerClient(7777, () -> new BidiMessagingProtocolImpl(), // protocol factory													
				BidiEncoderDecoder::new).serve(); // message encoder decoder factory
	
	}
}
