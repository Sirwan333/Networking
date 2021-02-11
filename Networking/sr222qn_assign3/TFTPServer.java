

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class TFTPServer 
{
	public static final int TFTPPORT = 4970;
	public static final int BUFSIZE = 516;
	public static final String READDIR = "C:\\Users\\serwa\\Desktop\\Ass3Network\\Read\\"; //custom address at your PC
	public static final String WRITEDIR = "C:\\Users\\serwa\\Desktop\\Ass3Network\\Write\\"; //custom address at your PC
	// OP codes
	public static final int OP_RRQ = 1;
	public static final int OP_WRQ = 2;
	public static final int OP_DAT = 3;
	public static final int OP_ACK = 4;
	public static final int OP_ERR = 5;

	public static void main(String[] args) {
		if (args.length > 0) 
		{
			System.err.printf("usage: java %s\n", TFTPServer.class.getCanonicalName());
			System.exit(1);
		}
		//Starting the server
		try 
		{
			TFTPServer server= new TFTPServer();
			server.start();
		}
		catch (SocketException e) 
		{e.printStackTrace();}
	}

	private void start() throws SocketException 
	{
		byte[] buf= new byte[BUFSIZE];

		// Create socket
		DatagramSocket socket= new DatagramSocket(null);

		// Create local bind point 
		SocketAddress localBindPoint= new InetSocketAddress(TFTPPORT);
		socket.bind(localBindPoint);

		System.out.printf("Listening at port %d for new requests\n", TFTPPORT);

		// Loop to handle client requests 
		while (true) 
		{        

			final InetSocketAddress clientAddress = receiveFrom(socket, buf);


			// If clientAddress is null, an error occurred in receiveFrom()
			if (clientAddress == null) 
				continue;

			final StringBuffer requestedFile= new StringBuffer();
			final int reqtype = ParseRQ(buf, requestedFile);

			new Thread() 
			{
				public void run() 
				{
					try 
					{
						DatagramSocket sendSocket= new DatagramSocket(0);

						// Connect to client
						sendSocket.connect(clientAddress);						

						System.out.printf("%s request for %s from %s using port %d\n",
								(reqtype == OP_RRQ)?"Read":"Write",
										requestedFile.toString(), clientAddress.getHostName(), clientAddress.getPort());  

						// Read request
						if (reqtype == OP_RRQ) 
						{      
							requestedFile.insert(0, READDIR);
							HandleRQ(sendSocket, requestedFile.toString(), OP_RRQ);
						}
						// Write request
						else 
						{                       
							requestedFile.insert(0, WRITEDIR);
							HandleRQ(sendSocket,requestedFile.toString(),OP_WRQ);  
						}
						sendSocket.close();
					} 
					catch (SocketException e) 
					{e.printStackTrace();} 
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	/**
	 * Reads the first block of data, i.e., the request for an action (read or write).
	 * @param socket (socket to read from)
	 * @param buf (where to store the read data)
	 * @return socketAddress (the socket address of the client)
	 */
	private InetSocketAddress receiveFrom(DatagramSocket socket, byte[] buf) 
	{
		// Create datagram packet
		DatagramPacket dp = new DatagramPacket(buf, buf.length);

		// Receive packet
		try {
			socket.receive(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Get client address and port from the packet
		InetSocketAddress socketAddress = new InetSocketAddress(dp.getAddress(), dp.getPort());
		return socketAddress;
	}

	/**
	 * Parses the request in buf to retrieve the type of request and requestedFile
	 * 
	 * @param buf (received request)
	 * @param requestedFile (name of file to read/write)
	 * @return opcode (request type: RRQ or WRQ)
	 */
	private int ParseRQ(byte[] buf, StringBuffer requestedFile) 
	{
		// See "TFTP Formats" in TFTP specification for the RRQ/WRQ request contents

		ByteBuffer wrap = ByteBuffer.wrap(buf);
		short opcode = wrap.getShort();
		int end = -1;

		for (int i = 2; i < buf.length; i++) {
			if (buf[i] == 0) {
				end = i;
				break;
			}
		}

		String fileName = new String(buf, 2, end-2);
		requestedFile.append(fileName);

		return opcode;
	}

	/**
	 * Handles RRQ and WRQ requests 
	 * 
	 * @param sendSocket (socket used to send/receive packets)
	 * @param requestedFile (name of file to read/write)
	 * @param opcode (RRQ or WRQ)
	 * @throws IOException 
	 */
	private void HandleRQ(DatagramSocket sendSocket, String requestedFile, int opcode)  
	{		
		if(opcode == OP_RRQ)
		{
			// See "TFTP Formats" in TFTP specification for the DATA and ACK packet contents

			boolean result = send_DATA_receive_ACK(sendSocket, requestedFile);


		}
		else if (opcode == OP_WRQ) 
		{

			boolean result = receive_DATA_send_ACK(sendSocket, requestedFile);

		}
		else 
		{
			System.err.println("Invalid request. Sending an error packet.");
			// See "TFTP Formats" in TFTP specification for the ERROR packet contents
			String err = "Not defined, not valid request";
			send_ERR(sendSocket, 0, err);
			return;
		}		
	}

	//	/**
	//	To be implemented
	//	*/
	private boolean send_DATA_receive_ACK(DatagramSocket sendSocket, String requestedFile)  
	{
		short blockNrData = 1;
		short dataOpcode = 3;

		short opcodeAck;
		short blockNr;

		int retransmitLimit = 6;
		int counter = 0;

		//send data
		byte[] buf = new byte[BUFSIZE-4];

		StringBuilder str = new StringBuilder();
		try {
			File file = new File(requestedFile);

			if(!file.exists()) {
				System.err.println("File not found.");
				String err = "File not found";
				send_ERR(sendSocket, 1, err);
				return false;
			}

			FileInputStream fis  = new FileInputStream(file);

			while(true) {

				int totalBytes = fis.read(buf);

				ByteBuffer wrap = ByteBuffer.allocate(BUFSIZE);
				wrap.putShort(dataOpcode);
				wrap.putShort(blockNrData);
				wrap.put(buf);

				try {

					do {

						DatagramPacket dp = new DatagramPacket(wrap.array(), totalBytes+4);

						sendSocket.send(dp);


						//send Ack

						ByteBuffer ack = ByteBuffer.allocate(4);

						DatagramPacket dpAck = new DatagramPacket(ack.array(), 4);

						sendSocket.setSoTimeout(3000);

						sendSocket.receive(dpAck);

						opcodeAck = ack.getShort();
						blockNr = ack.getShort();

						counter++;
					}while(blockNrData!=blockNr||opcodeAck!=OP_ACK||counter<retransmitLimit);

				} catch (SocketTimeoutException e) {

					System.err.println("TIMEOUT");
					return false;
				}

				if (totalBytes < 512) {
					System.out.println("Transfer finished");
					break;
				}

				blockNrData++;

				System.out.println("Recived ack from the client for the "+"Op #: "+opcodeAck+" and Block #: "+blockNr);
			}

		}catch(IOException e) {
			System.err.println("Access violation.");
			String err = "Access violation.";
			send_ERR(sendSocket, 2, err);

		}

		return true;

	}

	private boolean receive_DATA_send_ACK(DatagramSocket sendSocket, String requestedFile)
	{
		short opcodeAck = 4;
		short blockNr = 0;

		try {
			File file = new File(requestedFile);

			if(file.exists()) {
				System.err.println("File already exists.");
				String err = "File already exists";
				send_ERR(sendSocket, 6, err);
				return false;
			}

			FileOutputStream fis  = new FileOutputStream(file);


			while(true) {

				// send ack
				ByteBuffer wrap = ByteBuffer.allocate(4);
				wrap.putShort(opcodeAck);
				wrap.putShort(blockNr);

				DatagramPacket dp = new DatagramPacket(wrap.array(), 4);
				try {
					sendSocket.send(dp);



					System.out.println("Sent ack for the client of the "+"Op #: "+opcodeAck+" and Block #: "+blockNr);

					//recieve data
					byte[] buffer = new byte[BUFSIZE];

					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

					sendSocket.setSoTimeout(1000);


					sendSocket.receive(packet);



					ByteBuffer wrapper = ByteBuffer.wrap(packet.getData());

					short opcode = wrapper.getShort();

					byte[] data = Arrays.copyOfRange(packet.getData(), 4, packet.getLength());

					fis.write(data);
					fis.flush();

				}catch (SocketTimeoutException e) {

					System.err.println("TIMEOUT");
					return false;
				}

				blockNr++;
			}

		}catch(IOException e) {
			String err = "Access violation.";
			send_ERR(sendSocket, 2, err);

		}

		return true;

	}

	private void send_ERR(DatagramSocket sendSocket, int errCode, String err)
	{
		short opcodeErr = 5;
		short errorCode = (short) errCode;

		ByteBuffer errBuff = ByteBuffer.allocate(err.length() + OP_ERR);
		errBuff.putShort(opcodeErr);
		errBuff.putShort(errorCode);
		errBuff.put(err.getBytes());

		DatagramPacket dp = new DatagramPacket(errBuff.array(), errBuff.array().length);
		try {
			sendSocket.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}



