//package dv201.labb2;

public class IOExceptionHandling extends Exception {
	public IOExceptionHandling() {
		super("Some Input or Output has failed in your code");
	}
	public IOExceptionHandling(String message) {
		super(message);
	}
}
