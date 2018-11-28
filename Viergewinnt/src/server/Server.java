package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Server {

	public static void main(String[] args) {

		ServerSocket empfangsSocket = null;
		Lobby lobby = null;

		try {
			System.out.println("Server gestartet.");
			lobby = new Lobby();
			int port = 19009;
			InetAddress adr = InetAddress.getLocalHost();
			System.out.println("Server IP: " + adr.getHostAddress());

			empfangsSocket = new ServerSocket(port, 50, adr);

		} catch (Exception e) {
			System.out.println("ERROR: Server abgestürzt!");
		} finally {
			if (empfangsSocket != null) {
				try {
					empfangsSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
