package bgu.spl171.net.TFTP;
/**
 * 
 * @author yardenv and nogaki
 * A DIRQ packet requests a directory listing from the server.
 * The directory listing will be returned as DATA packets.
 */
public class DIRQ extends Packet {

	public DIRQ(short myOpcode) {
		super(myOpcode); // should return all files (name only) saved on the
							// server.
	}

	public byte[] encode() {
		encodeArray[0] = (byte) ((myOpcode >> 8) & 0xFF);
		encodeArray[1] = (byte) (myOpcode & 0xFF);
		return encodeArray;
	}
}
