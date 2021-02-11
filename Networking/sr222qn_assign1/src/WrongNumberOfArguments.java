//package dv201.labb2;

public class WrongNumberOfArguments extends Exception {
	public WrongNumberOfArguments() {
		super("You forgot to Provide Argument(s)");
	}
	public WrongNumberOfArguments(String message) {
		super(message);
	}

}
