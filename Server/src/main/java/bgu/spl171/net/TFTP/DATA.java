package bgu.spl171.net.TFTP;

/**
 * 
 * @author yardenv and nogaki 
 * These packets can appear either in a
 * Client-to-Server communication (uploading a file) or in a
 * Server-to-Client communication (downloading a file or dir listing).
 */
public class DATA extends Packet {
	private short packetSize;
	private short block;
	private byte[] data;

	public DATA(short myOpcode, short packetSize, short block, byte[] data) {
		super(myOpcode);
		this.packetSize = packetSize; // string length in bytes. must not be
										// longer then 512.
		this.block = block;
		this.data = data;// should check for number of bytes. must not be longer
							// then 512 bytes.
	}

	public byte[] encode() {
		encodeArray = new byte[6 + data.length];
		encodeArray[0] = (byte) ((myOpcode >> 8) & 0xFF);
		encodeArray[1] = (byte) (myOpcode & 0xFF);
		encodeArray[2] = (byte) ((packetSize >> 8) & 0xFF);
		encodeArray[3] = (byte) (packetSize & 0xFF);
		encodeArray[4] = (byte) ((block >> 8) & 0xFF);
		encodeArray[5] = (byte) (block & 0xFF);
		for (int i = 6, j = 0; i < encodeArray.length; i++, j++) {
			encodeArray[i] = data[j];
		}
		return encodeArray;
	}
    /**
     * 
     * @return the PacketSize
     */
	public short getPacketSize() {
		return this.packetSize;
	}
    /**
     * 
     * @return the data array
     */
	public byte[] getData() {
		return this.data;
	}
    /**
     * 
     * @return the Block number
     */
	public short getBlockNum() {
		return this.block;
	}

}
