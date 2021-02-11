import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.management.RuntimeErrorException;

public class HTTPServerThread extends Thread{
	public static final int BUFSIZE= 1024;
	byte[] buf= new byte[BUFSIZE];
	public Socket socket;
	public static  File file = null;
	public String root = "C:\\Users\\serwa\\Desktop\\MyServer";
	ResponseHeader responseHeader = new ResponseHeader();
	Date date = new Date();

	public HTTPServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		//Receive data from client 
		StringBuilder dataReceivedFromClient = new StringBuilder();
		CheckFiles checkFile = new CheckFiles();
		InputStream input = null;
		try {
			input = socket.getInputStream();
			int length = input.read(buf);
			String receivedMessage = new String(buf);
			dataReceivedFromClient.append(receivedMessage);

			//			 request header
			//			 System.out.println(receivedMessage);

			String[] clientRequest = receivedMessage.split(" ");
			String path = clientRequest[1];
			String clientFileName = path.substring(path.lastIndexOf("/")+1);
			String contentType = "";
			if(clientFileName.contains(".")) {
				path = path.substring(0,path.lastIndexOf(clientFileName));
				contentType = clientFileName.substring(clientFileName.lastIndexOf(".")+1);
				if(contentType.equals("png"))
					contentType = "image/png";
				else
					contentType = "text/html";
			}

			System.out.println(path);

			// when client request a directory with or without "/" at the end.
			if(checkFile.isDirectory(clientFileName)) {
				String fileIndex;
				fileIndex = checkFile.directoryHasIndex(root+path);
				if(fileIndex != null)
				{
					file = new File(root+path+fileIndex);
					responseHeader = new ResponseHeader(
							"HTTP/1.1 200 OK\n", "Date:"+date+"\n", "Content-Type: text/html\r\n\r\n");
				}
				// otherwise there is no index file for the requested directory.
				else
				{
					file = new File(root+"/fileNotFound.html");
					responseHeader = new ResponseHeader(
							"HTTP/1.1 404 Not Found\n",  "Date:"+date+"\n", "Content-Type:text/html\r\n\r\n");
				}
			}
			// when client request a file 
			else if(checkFile.fileExistsOnServer(root+path, clientFileName)) {
				file = new File(root+path+clientFileName);
				responseHeader = new ResponseHeader(
						"HTTP/1.1 200 OK\n", "Date:"+date+"\n", "Content-Type: "+contentType+ "\r\n\r\n");
			}

			// file is not exists status code 404. (File not Found)
			else if(!checkFile.fileExistsOnServer(root+path, clientFileName)) {
				file = new File(root+"/fileNotFound.html");
				responseHeader = new ResponseHeader(
						"HTTP/1.1 404 Not Found\n",  "Date:"+date+"\n", "Content-Type: text/html\r\n\r\n");
			}

			// just to demonstrate status code 403. (Forbidden access)
			if(path.equals("/Forbidden")) {
				file = new File(root+"/ForbiddenAccess.html");
				responseHeader = new ResponseHeader(
						"HTTP/1.1 403 Forbidden \n", "Date:"+date+"\n", "Content-Type:text/html\r\n\r\n");
			}

			// just to demonstrate status code 302. (redirect)
			if(path.equals("/Page1/red")) {
				file = new File(root+"/redirect.html");
				responseHeader = new ResponseHeader("HTTP/1.1 302 Found\n", 
						"Date:"+date+"\n",
						"Content-Type: text/html \n",
						"Location: /redirect.html \r\n\r\n"
						);
			}
			
			// to demonstrate status code 500 (Server side exception)
			if(path.equals("/ServerException")) {
				try {
					throw new IOException();

				}catch(IOException e) {
					file = new File(root+"/ServerException.html");
					responseHeader = new ResponseHeader(
							"HTTP/1.1 500 Internal Server Error \n", "Date:"+date+"\n", "Content-Type:text/html\r\n\r\n");
				}
			}
			OutputStream output = null;
			output = socket.getOutputStream();

			FileInputStream fis =null;
			BufferedInputStream bis =null;
			//				
			byte [] mybytearray  = new byte [(int)file.length()];
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			bis.read(mybytearray,0,mybytearray.length);


			System.out.println(responseHeader.toString());
			output.write(responseHeader.toString().getBytes());
			output.write(mybytearray);

			bis.close();
			input.close();
			output.close();
			socket.close();

		}

		catch(Exception e) {
			// an error from the server.
			System.out.println(e.getStackTrace());
		}
	}
}




