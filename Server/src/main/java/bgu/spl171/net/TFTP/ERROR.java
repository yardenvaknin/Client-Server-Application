package bgu.spl171.net.TFTP;

import java.io.UnsupportedEncodingException;
/**
 * 
 * @author yardenv and nogaki
 * An ERROR packet may be the acknowledgment of any other type of packet.
 * In case of error, an error packet should be sent. A list or errors shown below.
 */
public class ERROR extends Packet {

	private short errNum;
	private String errMsg;

	public ERROR(short myOpcode, short num, String msg) {
		super(myOpcode);
		errNum = num;
		errMsg = msg + '\0';
	}

	public byte[] encode() {
		try {
			byte[] errMsgArray = errMsg.getBytes("UTF-8");
			encodeArray = new byte[4 + errMsgArray.length];
			encodeArray[0] = (byte) ((myOpcode >> 8) & 0xFF);
			encodeArray[1] = (byte) (myOpcode & 0xFF);
			encodeArray[2] = (byte) ((errNum >> 8) & 0xFF);
			encodeArray[3] = (byte) (errNum & 0xFF);
			for (int i = 4, j = 0; i < encodeArray.length; i++, j++) {
				encodeArray[i] = errMsgArray[j];

			}
		} catch (UnsupportedEncodingException e) {
		}
		return encodeArray;
	}

	/**
	 * 
	 * @return the error code
	 */
	public short getErrorCode() {
		return this.errNum;
	}

	/**
	 * 
	 * @return the error message
	 */
	public String getErrMsg() {
		return this.errMsg;
	}
}
