//package dv201.labb2;


public class main {

	public static void main(String[] args) {
		UDPEchoClient m = new UDPEchoClient();
		m.argumentsAreValid(args);
		m.run();
//		TCPEchoClient t = new TCPEchoClient();
//		t.argumentsAreValid(args);
//		t.run();
	}

}
