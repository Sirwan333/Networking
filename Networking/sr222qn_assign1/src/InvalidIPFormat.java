//package dv201.labb2;

public class InvalidIPFormat extends Exception{
	public InvalidIPFormat() {
		super("Invalid IP Adress Format");
	}
	
	public InvalidIPFormat(String message) {
		super(message);
	}
	

}
