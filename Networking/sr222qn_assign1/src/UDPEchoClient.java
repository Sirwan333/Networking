//package dv201.labb2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class UDPEchoClient extends NetworkingLayer {

	@Override
	public void run() {
		byte[] buf= new byte[bufSize];

		/* Create socket */

		try {
			DatagramSocket socket = new DatagramSocket(null);

			/* Create local endpoint using bind() */
			SocketAddress localBindPoint= new InetSocketAddress(MYPORT);
			socket.bind(localBindPoint);


			/* Create remote endpoint */
			SocketAddress remoteBindPoint=
					new InetSocketAddress(IPAdress,
							Integer.valueOf(remotePortNumber));

			/* Create datagram packet for sending message */
			DatagramPacket sendPacket=
					new DatagramPacket(MSG.getBytes(),
							MSG.length(),
							remoteBindPoint);

			/* Create datagram packet for receiving echoed message */
			DatagramPacket receivePacket= new DatagramPacket(buf, buf.length);

			while(true) {
				long startTime = System.currentTimeMillis();

				//counter to keep track of the actual messages sent and received
				int counter = 0;

				//if transfer rate is 0 make 1 to send at least one message
				if (transferRate==0)
					transferRate=1;

				for (int i=0; i<transferRate;i++) {
					socket.send(sendPacket);
					socket.receive(receivePacket);
					String receivedString=
							new String(receivePacket.getData(),
									receivePacket.getOffset(),
									receivePacket.getLength());

					sentAndReceivedMessagesVerification(MSG, receivedString);

					counter++;

					//Check if more than one second has passed and exit if yes
					if((1000-(System.currentTimeMillis()-startTime))<0) {
						break;
					}


				}
				printConnectionDetalis(startTime, counter);
			}
		} 
		catch (SocketException e) {
			System.out.println((new InvalidIPAddress()).getMessage());
		}
		catch (IOException e) {
			System.out.println((new IOExceptionHandling()).getMessage());
		}

	}

}
