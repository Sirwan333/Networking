//package dv201.labb2;

public class InvalidBuffer extends Exception{
	public InvalidBuffer() {
		super("You Provided Wrong Buffer Size");
	}
	public InvalidBuffer(String message) {
		super(message);
	}

}
