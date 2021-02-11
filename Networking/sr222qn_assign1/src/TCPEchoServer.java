//package dv201.labb2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPEchoServer{

	public static final int MYPORT= 5002;


	public static void main(String[] args) throws IOException {


		/* Create socket */
		ServerSocket socket= new ServerSocket(MYPORT);

		while(true) {
			Socket s = socket.accept();
			new TCPEchoServerThread(s).start();

		}

	}

}
