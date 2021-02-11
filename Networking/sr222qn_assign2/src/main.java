import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class main {
	
	public static void main(String[] args) throws IOException {
		ServerSocket socket= new ServerSocket(Integer.valueOf(args[0]));
		
		while(true) {
			Socket s = socket.accept();
			new HTTPServerThread(s).start();
			

		}

	}

}
