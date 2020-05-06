package bgu.spl171.net.TFTP;

import java.io.UnsupportedEncodingException;

/**
 * 
 * @author yardenv and nogaki 
 * A DELRQ packet is used to request the deletion of
 * a file in the server.
 */
public class DELRQ extends Packet {

	private String fileToDelete;

	public DELRQ(short myOpcode, String filename) {
		super(myOpcode);
		this.fileToDelete = filename + '\0';

	}

	public byte[] encode() {
		try {
			byte[] fileToDeleteArray = fileToDelete.getBytes("UTF-8");
			encodeArray = new byte[2 + fileToDeleteArray.length];
			encodeArray[0] = (byte) ((myOpcode >> 8) & 0xFF);
			encodeArray[1] = (byte) (myOpcode & 0xFF);
			for (int i = 2, j = 0; i < fileToDeleteArray.length; i++, j++) {
				encodeArray[i] = fileToDeleteArray[j];
			}
		} catch (UnsupportedEncodingException e) {
		}

		return encodeArray;
	}

	/**
	 * 
	 * @return the Filename
	 */
	public String getFileName() {
		return this.fileToDelete;
	}
}
