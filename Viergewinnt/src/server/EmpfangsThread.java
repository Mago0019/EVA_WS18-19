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
public class EmpfangsThread extends Thread {

	Socket clientSocket;
	String clientName;
	Lobby lobby;
	boolean running;
	BufferedReader input;
	PrintStream output;

	public EmpfangsThread(Socket clientSocket, Lobby lobby) {
		this.clientSocket = clientSocket;
		this.clientName = "noName";
		this.lobby = lobby;
		this.running = true;
	}

	@Override
	public void run() {
		System.out.println("EmpfangsThread gestartet.");

		// Besorge Namen für Spieler
		askForPlayerName();

		// In die Lobby einfügen
		lobby.addUser(clientSocket, clientName);

		// jetzt die Kommunikation regeln
		listenToClient();
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
				case "~~2": // z.B. Starte SpielLobby
					break;

				case "~~3": // z.B. joine anderer Lobby
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
		int tryCount = 0;

		while (!done && tryCount < 5) {
			try {
				this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				this.output = new PrintStream(clientSocket.getOutputStream());

				// TODO: Befehlscode für NamensAnforderung
				output.print("~~0 \n");
				// output.flush();
				System.out.println("Client angefragt - Code: ~~0");

				// TODO: Namen empfangen / Marshalling + DeMarshalling in extra MEthoden
				newName = input.readLine();
				System.out.println("Antwort von Client: " + newName);

				// neuen Namen überprüfen
				if (newName != null && newName.length() > 2 && newName.length() < 10) {
					this.clientName = newName;
					done = true;
				}
				tryCount++;

			} catch (Exception e) {
				tryCount++;
				System.out.println("ERROR: Abfragen des ClientNamens fehlgeschlagen!");
			}
		} // end While
	}
}
