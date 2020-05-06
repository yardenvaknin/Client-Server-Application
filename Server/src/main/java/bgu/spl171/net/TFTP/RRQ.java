package bgu.spl171.net.TFTP;

import java.io.UnsupportedEncodingException;
/**
 * 
 * @author yardenv nogaki
 * Packets that appear only in a Client-to-Server communication. 
 */
public class RRQ extends Packet{
	
	private String filename;
	private String filename2;
	
	public RRQ(short myOpcode, String filename) {
		super(myOpcode);
		this.filename = filename + '\0';
		this.filename2 = filename;
				 // file should be saved/deleted to/from the files directory of the server 
																				//according to the given opcode
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
	public String getRealFilename(){
		return this.filename2;
	}

}
