package bgu.spl171.net.TFTP;
/**
 * 
 * @author yardenv
 * This is an abstract class which represents the Packets, The extended classes 
 * supports 10 for types of Packets.
 */
public abstract class Packet {
	protected byte[] encodeArray;
	protected final short myOpcode;

	public Packet(short myOpcode) {
		this.myOpcode = myOpcode;
	}

	public abstract byte[] encode();
    public short getOpcode(){
    	return this.myOpcode;
    }
}
