package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread der den ServerSocket verwaltet -> Clients annehmen und an einen neuen EmpfangsThread übergeben.
 */
public class WellcomeSocket extends Thread {

	private boolean wellcomeSockRunning;
	private InetAddress ADR;
	private int PORT;
	private Lobby lobby = null;
	private ExecutorService tPool;
	private ServerSocket empfangsSocket;

	public WellcomeSocket(InetAddress adr, int port) {
		this.wellcomeSockRunning = true;
		this.ADR = adr;
		this.PORT = port;		
	}
	
	@Override
	public void run() {
		try {
			// Benötigte Werkzeuge generieren
			empfangsSocket = new ServerSocket(PORT, 50, ADR);
			tPool = Executors.newScheduledThreadPool(10); // CoreSize = 10 für EmpfangsThreads und GameSessions		
			this.lobby = Lobby.getInstance();
			
		} catch (IOException e) {
			System.out.println("ERROR (WellcomeSocket): Starten des ServerSockets fehlgeschlagen -> " + e);
			wellcomeSockRunning = false; // der Rest darf nun nicht mehr ausgeführt werden.
		}
		
		while(wellcomeSockRunning) {
			try {				
				Socket newSocket = empfangsSocket.accept();
				//newSocket.setSoTimeout(30_000); //in ms
				
				tPool.execute( new ClientThread(newSocket, lobby) );
				
			} catch (SocketException se) {
				System.out.println("ERROR: SocketException / Timeout");
			} catch (IOException ioe) {
				System.out.println();
			}
			
		}
	}
}
