/*
  UDPEchoServer.java
  A simple echo server with no error handling
 */

//package dv201.labb2;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPEchoServer {
	public static final int BUFSIZE= 1024;
	public static final int MYPORT= 4950;

	public static void main(String[] args)  {
		byte[] buf= new byte[BUFSIZE];
		try {
			/* Create socket */
			DatagramSocket socket= new DatagramSocket(null);

			/* Create local bind point */
			SocketAddress localBindPoint= new InetSocketAddress(MYPORT);
			socket.bind(localBindPoint);
			while (true) {
				/* Create datagram packet for receiving message */
				DatagramPacket receivePacket= new DatagramPacket(buf, buf.length);

				/* Receiving message */
				socket.receive(receivePacket);

				/* Create datagram packet for sending message */
				DatagramPacket sendPacket=
						new DatagramPacket(receivePacket.getData(),
								receivePacket.getLength(),
								receivePacket.getAddress(),
								receivePacket.getPort());

				/* Send message*/
				socket.send(sendPacket);
				System.out.printf("UDP echo request from %s", receivePacket.getAddress().getHostAddress());
				System.out.printf(" using port %d\n", receivePacket.getPort());
			}
		}catch (IOException e) {
			System.out.println("Some Input or Output has failed in your code");
		}
	} 
}