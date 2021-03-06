package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread der den ServerSocket verwaltet -> Clients annehmen und an einen neuen EmpfangsThread �bergeben.
 */
public class WellcomeSocket extends Thread {

	private boolean wellcomeSockRunning;
	private InetAddress ADR;
	private int PORT;
	private Lobby lobby = null;
	private ExecutorService tPool;
	//private ThreadPoolExecutor tPoolExecutor; // Das w�re noch eine sch�ne Erweiterung um die Threads zu z�hlen
	private ServerSocket empfangsSocket;

	public WellcomeSocket(InetAddress adr, int port) {
		this.wellcomeSockRunning = true;
		this.ADR = adr;
		this.PORT = port;		
		this.lobby = Lobby.getInstance();
	}
	
	@Override
	public void run() {
		try {
			// Ben�tigte Werkzeuge generieren
			empfangsSocket = new ServerSocket(PORT, 50, ADR);
			tPool = Executors.newScheduledThreadPool(10); // CoreSize = 10 f�r EmpfangsThreads und GameSessions		
						
		} catch (IOException e) {
			if(Lobby.debugMode)
				System.out.println("ERROR (WellcomeSocket): Starten des ServerSockets fehlgeschlagen -> " + e);
			wellcomeSockRunning = false; // der Rest darf nun nicht mehr ausgef�hrt werden.
		}
		
		while(wellcomeSockRunning) {
			try {				
				Socket newSocket = empfangsSocket.accept();
				//newSocket.setSoTimeout(30_000); //in ms
				
				tPool.execute( new ClientThread(newSocket, lobby) );
				
			} catch (SocketException se) {
				if(Lobby.debugMode)
					System.out.println("ERROR (WellcomeSocket): SocketException / Timeout");
			} catch (IOException ioe) {
				if(Lobby.debugMode)
					System.out.println("ERROR (WellcomeSocket):" + ioe);
			} catch(Exception e) {
				if(Lobby.debugMode)
					System.out.println("ERROR (WellcomeSocket): " + e);
			}
			
		} // end While
	}
	
	public ExecutorService getThreadPool() {
		return this.tPool;
	}
	
	public void shutdown() {
		this.wellcomeSockRunning = false;
		this.interrupt();
	}
}
