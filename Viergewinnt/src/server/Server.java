package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Server {

	public static void main(String[] args) {

		boolean serverRunning = true;
		Lobby lobby = null;
		ServerSocket empfangsSocket = null;

		ExecutorService tPool = Executors.newScheduledThreadPool(5); // CoreSize = 5
		
		try {
			System.out.println("Server gestartet.");
			lobby = new Lobby(tPool);
			lobby.start();

			int port = 19009;
			InetAddress adr = InetAddress.getLocalHost();
			System.out.println("Server IP: " + adr.getHostAddress());

			empfangsSocket = new ServerSocket(port, 50, adr);

			while (serverRunning) {
				try {
					Socket newSocket = empfangsSocket.accept();
					newSocket.setSoTimeout(2000); //in ms

					// TODO: Socket weitergeben an EmpfangsThread || THREADPOOL
					
					tPool.execute( new EmpfangsThread(newSocket, lobby) );
					
					// TODO: Shutdown für ThreadPool + Server selbst (auch als eigener IO-Thread)
				} catch (SocketException te) {
					System.out.println("ERROR: Setzen des TimeOuts für ClientSocket fehlgeschlagen.");
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
