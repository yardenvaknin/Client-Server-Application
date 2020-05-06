package bgu.spl171.net.TFTP;
/**
 * 
 * @author yardenv and nogaK
 * This class represents the state of the process ,here we check what is the next packet we are waiting for.
 */
public class HelperForBidiMP {
	private boolean IfConn;
	short whatIsNextAckNumber;
	short whatIsNextDATABlock;
	private boolean waitingForACK;
	private boolean waitingForDATA;
	private boolean isFreeForRQ;

	public boolean isIfConn() {
		return IfConn;
	}

	public void setIfConn(boolean ifConn) {
		IfConn = ifConn;
	}

	public short getWhatIsNextAckNumber() {
		return whatIsNextAckNumber;
	}

	public void setWhatIsNextAckNumber(short whatIsNextAckNumber) {
		this.whatIsNextAckNumber = whatIsNextAckNumber;
	}

	public short getWhatIsNextDATABlock() {
		return whatIsNextDATABlock;
	}

	public void setWhatIsNextDATABlock(short whatIsNextDATABlock) {
		this.whatIsNextDATABlock = whatIsNextDATABlock;
	}

	public boolean isWaitingForACK() {
		return waitingForACK;
	}

	public void setWaitingForACK(boolean waitingForACK) {
		this.waitingForACK = waitingForACK;
		if (waitingForACK){
			this.isFreeForRQ = false;
			this.waitingForDATA = false;
		}
		if (!waitingForACK)
			whatIsNextAckNumber = 0;
	}

	public boolean isWaitingForDATA() {
		return waitingForDATA;
	}

	public void setWaitingForDATA(boolean waitingForDATA) {
		this.waitingForDATA = waitingForDATA;
		if(waitingForDATA){
			waitingForACK = false;
			isFreeForRQ = false;
		}
		if (!waitingForDATA)
			whatIsNextDATABlock = 1;
			isFreeForRQ = true;
	}

	public boolean isFreeForRQ() {
		return isFreeForRQ;
	}

	public void setFreeForRQ(boolean isFreeForRQ) {
		this.isFreeForRQ = isFreeForRQ;
		if (isFreeForRQ) {
			setWaitingForACK(false);
			setWaitingForDATA(false);
		}
	}

}
