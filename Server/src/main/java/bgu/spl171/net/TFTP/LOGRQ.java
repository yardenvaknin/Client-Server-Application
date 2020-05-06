package bgu.spl171.net.TFTP;

import java.io.UnsupportedEncodingException;
/**
 * 
 * @author yardenv
 * A LOGRQ packet is used to login a user into the server.
 * This packet must be the first packet to be sent by the client to the server, or an ERROR packet is returned.
 * If successful an ACK packet will be sent in return.
 */
public class LOGRQ extends Packet {

	private String username;

	public LOGRQ(short myOpcode, String username) {
		super(myOpcode);
		this.username = username + '\0'; // must check for availability !
	}

	public byte[] encode() {
		try {
			byte[] usernameArray = username.getBytes("UTF-8");
			encodeArray = new byte[2 + usernameArray.length];
			encodeArray[0] = (byte) ((myOpcode >> 8) & 0xFF);
			encodeArray[1] = (byte) (myOpcode & 0xFF);
			for (int i = 2, j = 0; i < encodeArray.length; i++, j++) {
				encodeArray[i] = usernameArray[j];
			}
		} catch (UnsupportedEncodingException e) {
		}
		return encodeArray;
	}

	/***
	 * 
	 * @return user name
	 */
	public String getUserName() {
		return this.username;
	}

}
