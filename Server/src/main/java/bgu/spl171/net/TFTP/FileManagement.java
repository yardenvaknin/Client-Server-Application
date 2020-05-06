package bgu.spl171.net.TFTP;

import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * 
 * @author yardenv and noga
 * This class hold a list of all the files 
 */
public class FileManagement {
	
	 private ConcurrentLinkedQueue<String> listOfNameFiles= new ConcurrentLinkedQueue<>();
	/**
	 * 
	 * @param namefile -represent the filename of the file we  want to add
	 * @return true if we succeed to add the file and false otherwise
	 */
	public boolean addAFile(String namefile) {
		if (listOfNameFiles.contains(namefile))
			return false;
		listOfNameFiles.add(namefile);
		return true;
	}
	/**
	 * 
	 * @param namefile
	 * @return true if the file exists and false otherwise
	 */
	public boolean hasFile(String namefile) {
		if (listOfNameFiles.contains(namefile))
			return true;
		return false;
	}
  /**
   * 
   * @param namefile
   * @return true if succeed to delete the file and false otherwise
   */
	public boolean deleteFile(String namefile) {
		if (!(listOfNameFiles.contains(namefile)))
			return false;
		listOfNameFiles.remove(namefile);
		return true;
	}
	public ConcurrentLinkedQueue<String> getListOfNameFiles() {
		return this.listOfNameFiles;
		
	}
}
