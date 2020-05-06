package bgu.spl171.net.TFTP;

import java.io.UnsupportedEncodingException;
/**
 * 
 * @author yardenv and nogaki
 * Packets that appear only in a Client-to-Server communication. 
 */
public class WRQ extends Packet {

	private String filename;

	public WRQ(short myOpcode, String filename) {
		super(myOpcode);
		this.filename = filename + '\0'; // file should be saved/deleted to/from
											// the files directory of the server
											// according to the given opcode
	}

	public byte[] encode() {
		try {
			byte[] filenameArray = filename.getBytes("UTF-8");
			encodeArray = new byte[2 + filenameArray.length];
			encodeArray[0] = (byte) ((myOpcode >> 8) & 0xFF);
			encodeArray[1] = (byte) (myOpcode & 0xFF);
			for (int i = 2, j = 0; i < encodeArray.length; i++, j++) {
				encodeArray[i] = filenameArray[j];
			}
		} catch (UnsupportedEncodingException e) {
		}
		return encodeArray;
	}

	/**
	 * 
	 * @return the filename
	 */
	public String getFileName() {
		return this.filename;
	}
}
