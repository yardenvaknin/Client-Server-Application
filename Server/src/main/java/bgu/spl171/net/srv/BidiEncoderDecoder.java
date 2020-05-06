package bgu.spl171.net.srv;


import bgu.spl171.net.api.MessageEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import bgu.spl171.net.TFTP.*;

public class BidiEncoderDecoder implements MessageEncoderDecoder<Packet> {
	private Packet p;
	private byte []  array= new byte [518];
	private int indexOfNextByte=0;
	private short opcode;
	private String name;
	private short size = 0;
	private short block = 0;

	/**
	 * add the next byte to the decoding process
	 *
	 * @param nextByte
	 *            the next byte to consider for the currently decoded message
	 * @return a message if this byte completes one or null if it doesnt.
	 */
	
	
	public Packet decodeNextByte(byte nextByte) {
		
		//finding the opcode
		if (indexOfNextByte == 0) {
			array[0] = nextByte;
			indexOfNextByte++;
			return null;
		}
		//finding the opcode
		if (indexOfNextByte == 1) {
			array[1] = nextByte;
			indexOfNextByte++;
			this.opcode = bytesToShort(array[0], array[1]);
	
			if(opcode == 6){
		
				p = new DIRQ(opcode);
				indexOfNextByte = 0;
				opcode= 0;
				return p;
			}
			if(opcode == 10){
			
				p = new DISC(opcode);
				indexOfNextByte = 0;
				opcode= 0;
				return p;
			}
			return null;
		}
		if (opcode <1 || opcode >10 ){
			
			p=new ERROR((short)5,(short)4, "Illegal TFTP operation – Unknown Opcode.");
			return p; //ckthis
		}
		if (opcode == 4) {
			if (indexOfNextByte == 2) {
				array[2] = nextByte;
				indexOfNextByte++;
			} else if (indexOfNextByte == 3) {
				array[3] = nextByte; // finding the block number
				short block = bytesToShort(array[2], array[3]);
				
				p = new ACK(opcode, block);
				indexOfNextByte = 0;
				opcode= 0;
				return p;
			}
		}
		//create new LOGRQ Packet
		if (opcode == 7) {
			if (nextByte == '\0') {
				name = popString(2); //finding the user-name by using the help function popstring
			
				p = new LOGRQ(opcode, name);
				indexOfNextByte = 0;
				opcode= 0;
				return p;
			}
			pushByte(nextByte);
			indexOfNextByte++; //ck
			return null; // not a line yet
		}
		//create new DELRQ Packet
		if (opcode == 8) {
			if (nextByte == '\0') {
				name = popString(2);
				
				p = new DELRQ(opcode, name);
				indexOfNextByte = 0;
				opcode= 0;
				// completes = true;
				return p;
			}
			pushByte(nextByte);
			indexOfNextByte++; 
			return null; // not a line yet
		}
		//create new RRQ Packet
		if (opcode == 1) {
			if (nextByte == '\0') {
				name = popString(2);
			
				p = new RRQ(opcode, name);
				indexOfNextByte = 0;
				opcode= 0;
				// completes = true;
				return p;
			}
			pushByte(nextByte);
			indexOfNextByte++;
			return null; // not a line yet
		}
		//create new WRQ Packet
		if (opcode == 2) {
			if (nextByte == '\0') {
				name = popString(2);
			
				p = new WRQ(opcode, name);
				indexOfNextByte = 0;
				opcode= 0;
				// completes = true;
				return p;
			}
			pushByte(nextByte);
			indexOfNextByte++;
			return null; // not a line yet
		}
		//create new DATA Packet
		if (opcode == 3) {
	
			byte[] data;
			// finding the size of the data
			if (indexOfNextByte == 2) {
				array[2] = nextByte;
				indexOfNextByte++;
				return null;
				// finding the size of the data
			} else if (indexOfNextByte == 3) {
				array[3] = nextByte;
				size = bytesToShort(array[2], array[3]);
				indexOfNextByte++;
				return null;
             //finding the block number(2 bytes)
			} else if (indexOfNextByte == 4) {
				array[4] = nextByte;
				indexOfNextByte++;
				return null;
	          //finding the block number
			} else if (indexOfNextByte == 5) {
				array[5] = nextByte;
				indexOfNextByte++;
				block = bytesToShort(array[4], array[5]);
				return null;

             //if the indexOfNextByte is equal the size of the data we know that we have finished reading the data
			} else if ( indexOfNextByte >5 && indexOfNextByte < size +6) {
				pushByte(nextByte);
				indexOfNextByte++;
				if (indexOfNextByte == size + 6) {
					data = Arrays.copyOfRange(array, 6, size + 6);
					p = new DATA(opcode, size, block, data);
					indexOfNextByte = 0;
					opcode= 0;
					return p;
					
				}
				else return null;
				

			}

			}
			
		
		// create new BCAST Packet
		if (opcode == 9) {
			char delOrAdd = '0';
			//finding the deletedOradded file
			if (indexOfNextByte == 2) {
				delOrAdd = (char) (nextByte);
			}
			//finding the filename 
			if (nextByte == '\0') {
				name = popString(2); //help function the converts from bytes to String
				p = new BCAST(opcode, delOrAdd, name);
				indexOfNextByte = 0;
				opcode= 0;
				// completes = true;
				return p;
			}
			pushByte(nextByte);
			indexOfNextByte++;
			return null;
		}
		//create new ERROR Packet
		if (opcode == 5) {
			short errCode = 0;
			//finding the error code 
			if (indexOfNextByte == 2) {
				array[2] = nextByte;
				indexOfNextByte++;
				//finding the error code
			} else if (indexOfNextByte == 3) {
				array[3] = nextByte;
				errCode = bytesToShort(array[2], array[3]);
				indexOfNextByte++;
				//finding the error message(bytes to String) knowing that 0 is the last byte to read
			} else if (nextByte == '\0') {
				String msg = popString(4);
				p = new ERROR(opcode, errCode, msg);
				indexOfNextByte = 0;
				opcode= 0;
				// completes = true;
				return p;
			}
			pushByte(nextByte);
			indexOfNextByte++;
			return null;
		}
		
		return null;

	}
    /**
     * @return String- converted from byte[]
     * @param int - indicating first index to  read from in the array
     */
    private String popString(int ind) {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
    	//ck if the indexOfNextByte is correct
		String result = new String(array, ind, indexOfNextByte - ind, StandardCharsets.UTF_8);
		return result;
	}
    /**
     * 
     * @param nextByte- add nextByre to the byte array 
     */
	private void pushByte(byte nextByte) {
		if (indexOfNextByte >= array.length) {
			array = Arrays.copyOf(array, indexOfNextByte * 2);
		}
		array[indexOfNextByte] = nextByte;
		// indexOfNextByte++;
	}

    /**
     * encodes the given message to bytes array
     *
     * @param message the message to encode
     * @return the encoded bytes
     */
	public byte[] encode(Packet message) {
		return message.encode();
	}
	
	private short bytesToShort(byte a, byte b) {
		short result = (short) ((a & 0xff) << 8);
		result += (short) (b & 0xff);
		return result;
	}
}

