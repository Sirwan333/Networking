//package dv201.labb2;

public class InvalidTransferRate extends Exception {
	public InvalidTransferRate() {
		super("Transfer Rate Can Not Be Less Than 0");
	}
	public InvalidTransferRate(String message) {
		super(message);
	}

}
