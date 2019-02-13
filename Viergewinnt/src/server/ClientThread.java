package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Kleine Thread-Klasse, die den Clienten nach dem Namen fragt und ihn dann in
 * die Lobby einträgt. Danach wird die Kommunikation mit der Lobby geregelt.
 * 
 * IDEE: Startet die GameSessions aus der Lobby. Kümmert sich also weiter um den
 * Clienten.
 */
public class ClientThread extends Thread {

	Socket clientSocket; // Monentane TimeOutZeit: 30sek
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
		
		// IO deklarieren für Lebenszeit des Threads
		try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintStream output = new PrintStream(clientSocket.getOutputStream());) {
			this.input = input;
			this.output = output;

		// Besorge Namen für Spieler
		askForPlayerName();

		// In die Lobby einfügen
		lobby.addUser(clientSocket, clientName);

		// jetzt die Kommunikation regeln
		listenToClient();

		} catch (Exception e) {
			System.out.println("ERROR: ClientThread abgestürzt!");
		}
	}

	private void listenToClient() {
		// TODO: darauf warten, dass der Client eine Aktion in der Lobby ausführen will
		String msg = "";
		int tryCount = 0; 
		while (running && tryCount < 3) {
			try {
				
				// TODO: Marshalling  - erste 4 Bytes (Befehlscode) anschauen.
				msg = input.readLine(); // TODO: bricht leider nach ein paar Sek ab, wenn keine Meldung kommt
				
				switch (msg) {
				case "~~5": // z.B. Starte eigene SpielLobby
					break;

				case "~~52": // z.B. join anderer Lobby
					break;
					
				default:
					break;
				}
				
				
			} catch (IOException ioe) {
				tryCount++;
				System.out.println("ERROR: EmpfangsThread hat Verbindung zum CLienten verloren.");
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

				// TODO: Befehlscode für NamensAnforderung
				output.println("~~0");
				// output.flush();
				System.out.println("Client angefragt - Code: ~~0");

				// TODO: Namen empfangen / Marshalling + DeMarshalling in extra Methoden
				newName = input.readLine();
				System.out.println("Angefragter Name von Client: " + newName);

				// neuen Namen überprüfen
				if (newName != null && newName.length() > 2 && newName.length() < 15) {
					
					// Schauen, ob der Name schon vorhanden ist
					if(lobby.containsPlayer(newName)) {
						output.println("~~01"); // Name schon vorhanden
						
					} else {
						this.clientName = newName;
						output.println("~~00"); // Name ist gültig
						done = true;						
					}
				} else {
					output.println("~~02"); // Ungültiger Name
				}
				
			} catch (Exception e) {
				System.out.println("ERROR: Abfragen des ClientNamens fehlgeschlagen!");
			}
		} // end While
	}
}
