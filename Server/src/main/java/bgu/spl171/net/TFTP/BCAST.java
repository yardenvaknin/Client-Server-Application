package bgu.spl171.net.TFTP;

/**
 * 
 * @author yardenv and nogaKi
 * A BCAST packet is used to notify all logged-in clients that a file was deleted/added.
 */
import java.io.UnsupportedEncodingException;

public class BCAST extends Packet {
	private char deletedOrAdded;
	private String filename;

	public BCAST(short myOpcode, char deletedOrAdded, String filename) {
		super(myOpcode);
		this.deletedOrAdded = deletedOrAdded;
		this.filename = filename + '\0';
	}

	public byte[] encode() {
		try {
			byte[] filenameArray = filename.getBytes("UTF-8");
			encodeArray = new byte[3 + filenameArray.length];
			encodeArray[0] = (byte) ((myOpcode >> 8) & 0xFF);
			encodeArray[1] = (byte) (myOpcode & 0xFF);
			if (deletedOrAdded == '0')
				encodeArray[2] = '0';
			else
				encodeArray[2] = '1';
			for (int i = 3, j = 0; i < encodeArray.length; i++, j++) {
				encodeArray[i] = filenameArray[j];
			}
		} catch (UnsupportedEncodingException e) {

		}
		return encodeArray;
	}

	/**
	 * 
	 * @return the delOrAdd value
	 */
	public char getDelOrAdd() {
		return this.deletedOrAdded;
	}

	/**
	 * 
	 * @return the filename
	 */
	public String getFileName() {
		return this.filename;
	}

}
