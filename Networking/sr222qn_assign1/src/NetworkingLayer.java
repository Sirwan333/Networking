//package dv201.labb2;


public abstract class NetworkingLayer {


	public static String IPAdress;
	public static int bufSize;
	public static final int MYPORT= 0;
	public static int remotePortNumber = 0;
	public static int transferRate = 0;
	public static final String MSG= "An Echo Message!";

	public NetworkingLayer() {

	}

	public boolean argumentsAreValid(String[] args){

		try {

			// Check number of arguments provided by the client 
			if (args.length != 4) {
				throw new WrongNumberOfArguments();
			}

			//Check if the IP is valid
			IPAdress = args[0];
			if ( IPAdress == null || IPAdress.isEmpty() ) {
				throw new InvalidIPFormat();
			}
			String[] oneByteOfIP = IPAdress.split( "\\." );
			for ( String s : oneByteOfIP ) {
				int i = Integer.parseInt( s );
				if ( (i < 0) || (i > 255) ) {
					throw new InvalidIPFormat();
				}
			}
			if ( IPAdress.endsWith(".") ) {
				throw new InvalidIPFormat();
			}

			// Check Transfer Rate
			transferRate = Integer.valueOf(args[3]);
			if(transferRate < 0 || !isInteger(args[3])) {
				throw new InvalidTransferRate();
			}

			// Check Port Number
			remotePortNumber = Integer.valueOf(args[1]);
			if(remotePortNumber<0 || remotePortNumber>65535 || !isInteger(args[1])) {
				throw new InvalidPortNumber();
			}

			// Check The Buffer Size
			bufSize = Integer.valueOf(args[2]);
			if (bufSize<MSG.length() || bufSize<0 || !isInteger(args[2])){
				throw new InvalidBuffer();
			}

		}
		catch (InvalidIPFormat e) {
			System.out.println(e.getMessage());
		}
		catch (WrongNumberOfArguments e) {
			System.out.println((new WrongNumberOfArguments()).getMessage());
		}
		catch(InvalidBuffer e) {
			System.out.println((new InvalidBuffer()).getMessage());
		}
		catch(InvalidPortNumber e) {
			System.out.println((new InvalidPortNumber()).getMessage());
		}
		catch(InvalidTransferRate e) {
			System.out.println((new InvalidTransferRate()).getMessage());
		}
		return true;
	}

	public boolean isInteger(String input) { 
		// Check If value is Integer
		try { 
			Integer.parseInt( input );
			return true; 
		}
		catch( Exception e ) { 
			return false; 
		}
	} 

	public void sentAndReceivedMessagesVerification(String sent, String recived) {
		if (recived.compareTo(sent) == 0) {
			System.out.printf("%d bytes sent and received\n", recived.length());
		}
		else {

			System.out.printf("Sent and received msg not equal!\n");
		}
	}

	public void printConnectionDetalis(long begninigTime, int messagesCounter) {

		//Print some details
		if((1000-(System.currentTimeMillis()-begninigTime))<0) {
			System.out.println("Total Messages Sent = "+ messagesCounter);
			System.out.println("Total Messages Remains = "+ (transferRate - messagesCounter));
		}
		else {
			System.out.println("Total Messages Sent = "+messagesCounter);
			try {
				Thread.sleep(1000-(System.currentTimeMillis()-begninigTime));
			} catch (InterruptedException e) {
				System.out.println((new ThreadIntruptException()).getMessage());
			}
			System.out.println("Actual Time Spent = "+ (System.currentTimeMillis()-begninigTime));

		}

	}

	public abstract void run();

}
