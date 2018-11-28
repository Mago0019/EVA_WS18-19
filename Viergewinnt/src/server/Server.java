package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) {

		boolean serverRunning = true;
		Lobby lobby = null;
		ServerSocket empfangsSocket = null;

		try {
			System.out.println("Server gestartet.");
			lobby = new Lobby();
			lobby.start();

			int port = 19009;
			InetAddress adr = InetAddress.getLocalHost();
			System.out.println("Server IP: " + adr.getHostAddress());

			empfangsSocket = new ServerSocket(port, 50, adr);

			while (serverRunning) {
				try {
					Socket newSocket = empfangsSocket.accept();

					// TODO: Socket weitergeben an EmpfangsThread
					new EmpfangsThread(newSocket);
					
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("ERROR: Verbindungsaufbau mit Clienten fehlgeschlagen.");
				}
			}
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
