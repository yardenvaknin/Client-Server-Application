package bgu.spl171.net.TFTP;

/**
 * 
 * @author yardenv and nogaKi This class used to acknowledge different packets
 */
public class ACK extends Packet {
	private short block;

	public ACK(short myOpcode, short block) {
		super(myOpcode);
		this.block = block;
	}

	public byte[] encode() {
		encodeArray = new byte[4];
		encodeArray[0] = (byte) ((myOpcode >> 8) & 0xFF);
		encodeArray[1] = (byte) (myOpcode & 0xFF);
		encodeArray[2] = (byte) ((block >> 8) & 0xFF);
		encodeArray[3] = (byte) (block & 0xFF);
		return encodeArray;
	}

	/**
	 * 
	 * @return the blockNum
	 */
	public short getBlockNum() {
		return this.block;
	}
}
