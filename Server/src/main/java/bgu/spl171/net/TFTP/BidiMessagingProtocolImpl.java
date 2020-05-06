package bgu.spl171.net.TFTP;

import bgu.spl171.net.srv.ConnectionsImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl171.net.TFTP.HelperForBidiMP;
/**
 * 
 * @author yardenv
 * This class processes the received Packet 
 */
public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Packet> {
	private boolean shouldTerminate = false;
	private ConnectionsImpl<Packet> myConn;
	private int connId;
	private ArrayList<Byte> allBytes;
	private int indexOfLastData = 0;
	private ByteBuffer bufferToDATA;
	private ByteBuffer bufferToRRQ;
	private String uploadedFile;
	private String toReadFile;
	private String fileToDelete; 
	private FileManagement stateOfFiles;
	private ConcurrentLinkedDeque<DATA> packetBlocks = new ConcurrentLinkedDeque<>();
	static ConcurrentHashMap<Integer, String> mapOfConnectedUserNames = new ConcurrentHashMap<>();
	private HelperForBidiMP stateSaver= new HelperForBidiMP();

	public void start(int connectionId, Connections<Packet> connections) {
		this.connId = connectionId;
		this.myConn = (ConnectionsImpl<Packet>) connections;
		this.stateOfFiles =  new FileManagement();
	}

	// Receives a packet from the client and should response according to opcode

	public void process(Packet message) {
		short opcode = message.getOpcode();
		
		switch (opcode) {
		case (1):
		
			stateOfFiles.addAFile("Packet.h");
			// Read Request - should return a DATA packet with the asked file
			if (!(isConnectedError(connId))) {
				break;
			}
			RRQ a = (RRQ) message;
			if (!(stateOfFiles.hasFile(a.getRealFilename()))) {
				myConn.send(connId, new ERROR((short) 5, (short) 1, "File not found"));
				break;
			} else {
				toReadFile = a.getFileName(); 
				File file = new File(toReadFile);
				long sizeOfFile = file.length(); // in bytes
				int position = 0;
				long numOfBlocks = (sizeOfFile / 512)+1;
				short blockCreated = 1;
				try (FileInputStream in = new FileInputStream(file)) {
					FileChannel inChan = in.getChannel();
					while (blockCreated <= numOfBlocks) {
						if (blockCreated == numOfBlocks) {
							for (int j = position; j < sizeOfFile; j++) {
								inChan.read(bufferToRRQ, j);
							}
						}
						for (int i = position; i < position + 512; i++) {
							inChan.read(bufferToRRQ, i);
						}
						packetBlocks.add(new DATA((short) 3, (short) 512, blockCreated, bufferToRRQ.array()));
						bufferToRRQ.clear();
						blockCreated++;
						position += 512;
					}
					myConn.send(connId, packetBlocks.remove());
					stateSaver.setWaitingForACK(true);
					stateSaver.setWhatIsNextAckNumber((short) 1);
					in.close();
				} catch (Exception e) {
					myConn.send(connId, new ERROR((short) 5, (short) 0, "Not defined"));
					break;
				}
				break;
			}
			/**
			 * write Request - Files should be saved in the Files folder on the
			 * server side and in the current working directory in the client
			 * side. should send BCAST notifying all clients about addition
			 */
		case (2):
				if (!(myConn.isConnected(connId))) {
				myConn.send(connId, new ERROR((short) 5, (short) 6, "User not logged in"));
				break;
			}
			uploadedFile = ((WRQ) message).getFileName();

			if (stateOfFiles.hasFile(uploadedFile)) {
				myConn.send(connId, new ERROR((short) 5, (short) 5, "File already exists"));
				break;
			}
			// sending ack now, and will add to list of files only after receiving all data packets
			else {
				myConn.send(connId, new ACK((short) 4, (short) 0));
				stateSaver.setWaitingForDATA(true);
				stateSaver.setWhatIsNextDATABlock((short) 1);
				break;
			}
			/**
			 * DATA - If a file or directory listing are longer than 512 bytes,
			 * the data will be divided to blocks of 512 bytes. The next block
			 * will be sent only after the former is acknowledged blocks begin
			 * with 1 and increase by one for each new block of data. This
			 * restriction allows for an implementation that does not care for
			 * order
			 */
		case (3): // got DATA! WOOOHOOOOO YEEEEEH
	
			if (!(isConnectedError(connId))) {
				break;
			}
			DATA packet = (DATA) message;
			short block = packet.getBlockNum();
			int packetSize = packet.getData().length;
			if (!(stateSaver.getWhatIsNextDATABlock() == block)) {
				myConn.send(connId, new ERROR((short) 5, (short) 2, "Access violation "));
				break;
			}
			if (block == 1) {
				
				packetBlocks.add(packet);
				
		
			} else if (packetSize < 512) {
				
				packetBlocks.add(packet);
				
				
				try (FileOutputStream out = new FileOutputStream("Files/"+ uploadedFile)) {
					
					for(int i = 0; i< packetBlocks.size(); i++){
						bufferToDATA.put(packetBlocks.pollFirst().getData());
						
					}
					byte[] tmp = new byte[packetBlocks.size()*512];
					bufferToDATA.get(tmp);
					out.write(tmp);
					bufferToDATA.clear();				
					out.close(); 
					stateOfFiles.addAFile(uploadedFile);
					myConn.broadcast(new BCAST((short) 9, '1', uploadedFile));
					stateSaver.setWaitingForDATA(false);

				} catch (FileNotFoundException e) {
					myConn.send(connId, new ERROR((short) 5, (short) 1, "File not found"));
					break;
				} catch (IOException e) {
					myConn.send(connId, new ERROR((short) 5, (short) 0,
							"Not defined- maybe attempting to write to a file with invalid name"));
					break;
				}

			} else
				packetBlocks.add(packet);
				myConn.send(connId, new ACK((short) 4, block));
				stateSaver.setWhatIsNextDATABlock((short) (block + 1));
			break;
		/**
		 * ACK - once a DATA packet is acknowledged , the next block can be
		 * sent. LOGRQ, WRQ, DELRQ and DISC should be acknowledged with block =
		 * 0
		 */
		case (4): // got ACK
	
			if ((!(stateSaver.isWaitingForACK())) // if wasn't waiting for ACK or waiting for different block num, send ERROR
					|| (!(stateSaver.getWhatIsNextAckNumber() == ((ACK) message).getBlockNum()))) {
				myConn.send(connId, new ERROR((short) 5, (short) 0, "Not defined"));
				break;
			} else {
				myConn.send(connId, packetBlocks.remove());
				break;
			}
		case (5): { // got ERROR!!!!
		
			if (stateSaver.isWaitingForACK()) {
				for (int i = 0; i < packetBlocks.size(); i++) {
					packetBlocks.remove();
					stateSaver.setFreeForRQ(true);
				}
				break;
			}

			if (stateSaver.isWaitingForDATA()) {
				bufferToDATA.clear();
				stateSaver.setFreeForRQ(true);
				break;
			}
		}
		/**
		 * DIRQ - returns DATA packet - list of files on the directory each
		 * filename is divided by '\0' should not include files currently
		 * uploading but should include any file in the Files directory
		 */
		case (6): // got DIRQ
		
			try{
			ConcurrentLinkedQueue<String> allFilesNames = stateOfFiles.getListOfNameFiles();
			for (int i = 0; i < allFilesNames.size(); i++) {
				try {
					boolean toContinue = true;
					// we convert and insert all of the data from Strings to
					// byte array.
					byte[] nbytes = allFilesNames.iterator().next().getBytes("UTF-8");
					for (int j = 0; j < nbytes.length && toContinue; j++) {
						toContinue = allBytes.add(nbytes[j]);
					}
				} catch (UnsupportedEncodingException e) {
					myConn.send(connId, new ERROR((short) 5, (short) 2,
							"Access violation – File cannot be written, read or deleted"));
					break;
				}
			}
			/**
			 * find how many blocks we needed by dividing all of the data to 512
			 * bytes .
			 */
			int numOfBlocks = allBytes.size() / 512;
			if (!(allBytes.size() % 512 == 0))
				numOfBlocks++;
			for (int i = 1; i <= numOfBlocks; i++) { // creating all the packets
				short myPacketSize = 0;
				if (!(i == numOfBlocks)) {
					myPacketSize = 512;
				} else {
					myPacketSize = (short) (allBytes.size() - indexOfLastData);
				}
				byte[] toFunc = byteArray(Arrays.copyOfRange(allBytes.toArray(new Byte[allBytes.size()]),
						indexOfLastData, indexOfLastData + 511));
				packetBlocks.add(new DATA((short) 3, myPacketSize, (short) i, toFunc));
				indexOfLastData += 512;
				myConn.send(connId, packetBlocks.remove());
				stateSaver.setWaitingForACK(true);
				stateSaver.setWhatIsNextAckNumber((short) 1);

			
		}
			}
		catch(NullPointerException e){
			myConn.send(connId, new ERROR((short)5, (short)0, "dir is empty"));
			
		}
		
			break;
		/**
		 * LOGRQ - must be 1st packet If successful an ACK packet will be sent
		 * in return. must check uniqueness
		 */
		case (7): //got LOGRQ
			
			if (mapOfConnectedUserNames.contains(((LOGRQ) message).getUserName())) { //already have such a user
				myConn.send(connId, new ERROR((short) 5, (short) 7, "Login username already connected"));
				break;
			} else {
				mapOfConnectedUserNames.put(connId, ((LOGRQ) message).getUserName()); // else adding to our lists and approving
				myConn.send(connId, new ACK((short) 4, (short) 0));
				stateSaver.setFreeForRQ(true);
				break;
			}
			/**
			 * DELRQ - should delete file from dirq and send BCAST packet
			 * announcing deletion
			 * 
			 */
		case (8):// got DELRQ
		
			fileToDelete = ((DELRQ) message).getFileName();
			File myFileToDelete = new File("Files/" + fileToDelete);
			ConcurrentLinkedQueue<String> allMyFilesNames = stateOfFiles.getListOfNameFiles();
			boolean found = false;
			for (int i = 0; i < allMyFilesNames.size() && !(found); i++) {
				if (allMyFilesNames.iterator().next().equals(fileToDelete)) {
					found = true;
				}
			}
			if (found == false) {
				myConn.send(connId, new ERROR((short) 5, (short) 1, "File not found – DELRQ of non-existing file"));
				break;
			} else {
				try {
					myFileToDelete.delete();
					stateOfFiles.deleteFile(fileToDelete);
					myConn.broadcast(new BCAST((short) 9, '0', fileToDelete));
				} catch (SecurityException e) {
					myConn.send(connId, new ERROR((short) 5, (short) 0, "Not defined"));
					break;
				}
				break;
			}
		case (10): // got DISC
			// send ACK packet and disconnect the client
			
			Packet ack = new ACK((short) 4, (short) 0);
			myConn.send(connId, ack);
			mapOfConnectedUserNames.remove(connId);
			myConn.disconnect(connId);
			break;
		default: // probably got illegal opcode, so sending error...
			myConn.send(connId, new ERROR((short) 5, (short) 4, "Illegal TFTP operation – Unknown Opcode."));
			break;
		}
	}

	/**
	 * @return true if the connection should be terminated
	 */
	public boolean shouldTerminate() {
		return shouldTerminate;
	}

	private byte[] byteArray(Byte[] array) {
		byte[] bytes = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			bytes[i] = array[i];
		}
		return bytes;
	}
   public boolean isConnectedError(int connId){
	   if (mapOfConnectedUserNames.get(connId) == null){
		   myConn.send(connId, new ERROR((short)5, (short) 6, "User not logged in"));
		   return false;
	   }
	   return true;
	   
   }
}
