import java.io.File;
import java.util.List;

public class CheckFiles {

	/**
	 * check if requested file from client exists on the server
	 * @param filesInServer
	 * @param clientFileName
	 * @return true if file exists
	 */
	public boolean fileExistsOnServer(String directoryName, String clientFileName) {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		if(fList != null)
		for(File serverFile : fList) {
			if(serverFile.getName().equals(clientFileName))
				return true;
		}
		return false;
	}

	
	/**
	 * this method check if index.html file exists in a directory
	 * @param directoryName
	 * @return true if index exists
	 * 
	 */
	public String directoryHasIndex(String directoryName) {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		if(fList != null)
			for (File file : fList) {      
				if (file.getName().equals("index.html") || file.getName().equals("index.htm")) 
					return "/"+file.getName();
			}
		return null;
	}


	/**
	 * check if the requested url is directory
	 * @param clientFileName
	 * @return
	 */
	public boolean isDirectory(String clientFileName) {
		return !clientFileName.contains(".");
	}
	
}