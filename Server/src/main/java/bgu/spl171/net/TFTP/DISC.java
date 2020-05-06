package bgu.spl171.net.TFTP;
/**
 * 
 * @author yardenv nogaki
 * Packets that appear only in a Client-to-Server communication.
 * Informs the server on client disconnection.
 * Client may terminate only after receiving ACK packet in replay.
 */
public class DISC extends Packet {

	public DISC(short myOpcode) {
		super(myOpcode);
	}

	public byte[] encode() {
		encodeArray[0] = (byte) ((myOpcode >> 8) & 0xFF);
		encodeArray[1] = (byte) (myOpcode & 0xFF);
		return encodeArray;
	}
}
