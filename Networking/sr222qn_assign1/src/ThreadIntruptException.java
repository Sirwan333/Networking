//package dv201.labb2;

public class ThreadIntruptException extends Exception{
	public ThreadIntruptException() {
		super("Thread was intrrupted during or before its activity");
	}
	public ThreadIntruptException(String message) {
		super(message);
	}

}
