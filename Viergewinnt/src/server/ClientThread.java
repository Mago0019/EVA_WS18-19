package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Kleine Thread-Klasse, die den Clienten nach dem Namen fragt und ihn dann in
 * die Lobby eintr�gt. Danach wird die Kommunikation mit der Lobby geregelt.
 * 
 * IDEE: Startet die GameSessions aus der Lobby. K�mmert sich also weiter um den
 * Clienten.
 */
public class ClientThread extends Thread {

	Socket clientSocket;
	String clientName;
	Lobby lobby;
	boolean running;
	BufferedReader input;
	PrintStream output;

	public ClientThread(Socket clientSocket, Lobby lobby) {
		this.clientSocket = clientSocket;
		this.clientName = null;
		this.lobby = lobby;
		this.running = true;
		
	}

	@Override
	public void run() {
		System.out.println("ClientThread gestartet.");
		
		// IO deklarieren
		try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintStream output = new PrintStream(clientSocket.getOutputStream());){
			this.input = input;
			this.output = output;

		
		// Besorge Namen f�r Spieler
		askForPlayerName();

		// In die Lobby einf�gen
		lobby.addUser(clientSocket, clientName);

		// jetzt die Kommunikation regeln
		listenToClient();

		} catch (Exception e) {}
	}

	private void listenToClient() {
		// TODO: darauf warten, dass der Client eine Aktion in der Lobby ausf�hren will
		String msg = "";
		int tryCount = 0;
		while (running && tryCount < 3) {
			try {
				
				// TODO: Marshalling  - erste 4 Bytes (Befehlscode) anschauen.
				msg = input.readLine(); // TODO: bricht leider nach ein paar Sek ab, wenn keine Meldung kommt
				
				switch (msg) {
				case "~~2": // z.B. Starte SpielLobby
					break;

				case "~~3": // z.B. join anderer Lobby
					break;
					
				default:
					break;
				}
				
				
			} catch (IOException ioe) {
				tryCount++;
				System.out.println("ERROR: EmpfangsThread hat verbindung zum CLienten verloren.");
				//ioe.printStackTrace();
			}
		}

	}

	/**
	 * Client wird angefragt mit Befehlscode (TODO) "~~0" einen SpielerNamen
	 * auszusuchen.
	 */
	private void askForPlayerName() {
		boolean done = false;
		String newName = null;

		while (!done) {
			try {

				// TODO: Befehlscode f�r NamensAnforderung
				output.print("~~0 \n");
				// output.flush();
				System.out.println("Client angefragt - Code: ~~0");

				// TODO: Namen empfangen / Marshalling + DeMarshalling in extra Methoden
				newName = input.readLine();
				System.out.println("Angefragter Name von Client: " + newName);

				// neuen Namen �berpr�fen
				if (newName != null && newName.length() > 2 && newName.length() < 10) {
					
					// Schauen, ob der Name schon vorhanden ist
					if(lobby.containsPlayer(newName)) {
						output.print("~~01"); // Name schon vorhanden
						
					} else {
						this.clientName = newName;
						done = true;						
					}
				} else {
					output.print("~~02"); // Ung�ltiger Name
				}
				
			} catch (Exception e) {
				System.out.println("ERROR: Abfragen des ClientNamens fehlgeschlagen!");
			}
		} // end While
	}
}
