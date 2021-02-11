//package dv201.labb2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPEchoServerThread extends Thread {

	public static final int BUFSIZE= 1024;
	byte[] buf= new byte[BUFSIZE];
	public Socket s;


	public TCPEchoServerThread(Socket socket) {
		this.s = socket;
	}
	public void run() {
		while(true) {

			//Receive data from client 
			StringBuilder dataReceivedFromClient = new StringBuilder();
			InputStream input = null;
			try {
				input = s.getInputStream();
				do {
					int length = input.read(buf);
					String receivedMessage = new String(buf, 0, length);
					dataReceivedFromClient.append(receivedMessage);
				}while(input.available()>0);

				// send data to client
				OutputStream output = null;
				output = s.getOutputStream();
				output.write(dataReceivedFromClient.toString().getBytes());
				System.out.printf("UDP echo request from %s", s.getInetAddress().getHostAddress());
				System.out.printf(" using port %d\n", s.getPort());

			} catch (IOException e) {
				try {
					s.close();
				} catch (IOException e1) {
					System.out.println("Some Input or Output has failed in your code");
				};
			}
		}	

	}

}
