//package dv201.labb2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPEchoClient extends NetworkingLayer{

	@Override
	public void run() {
		byte[] buf = new byte[bufSize];
		buf = MSG.getBytes();
		Socket s = new Socket();
		InetAddress add;
		try {
			add = InetAddress.getByName(IPAdress);
			s.connect(new InetSocketAddress(add, Integer.valueOf(remotePortNumber)));
		} catch (IOException e) {
			System.out.println((new IOExceptionHandling()).getMessage());
		} 

		while(true) {

			long startTime = System.currentTimeMillis();
			int counter = 0;

			if (transferRate==0)
				transferRate=1;
			for (int i=0; i<transferRate;i++) {
				OutputStream output;
				try {
					output = s.getOutputStream();
					output.write(buf, 0, buf.length);
				} catch (IOException e) {
					System.out.println((new IOExceptionHandling()).getMessage());
				}
				String se = new String(buf);
				InputStream is;
				try {
					is = s.getInputStream();
					is.read(buf, 0, buf.length);
				} catch (IOException e) {
					System.out.println((new IOExceptionHandling()).getMessage());
				}

				String re = new String(buf);
				sentAndReceivedMessagesVerification(se, re);
				counter++;

			}
			printConnectionDetalis(startTime, counter);

		}

	}
}
