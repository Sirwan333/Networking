//package dv201.labb2;

public class InvalidIPAddress extends Exception{
	public InvalidIPAddress() {
		super("Invalid IP Adress");
	}
	
	public InvalidIPAddress(String message) {
		super(message);
	}

}
