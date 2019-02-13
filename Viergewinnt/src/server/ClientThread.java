package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Kleine Thread-Klasse, die den Clienten nach dem Namen fragt und ihn dann in
 * die Lobby einträgt. Danach wird die Kommunikation mit der Lobby geregelt.
 * 
 * IDEE: Startet die GameSessions aus der Lobby. Kümmert sich also weiter um den
 * Clienten.
 */
public class ClientThread extends Thread {

	Player client;
	Lobby lobby;
	ExecutorService tPool;
	GameSession gameSession;
	
	boolean running;
	BufferedReader input;
	PrintStream output;
	

	public ClientThread(Socket clientSocket, Lobby lobby, ExecutorService tPool) {
		this.client.socket = clientSocket;
		this.lobby = lobby;
		this.tPool = tPool;
		this.running = true;

	}

	@Override
	public void run() {
		System.out.println("ClientThread gestartet.");

		// IO deklarieren für Lebenszeit des Threads
		try (BufferedReader input = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));
				PrintStream output = new PrintStream(client.socket.getOutputStream());) {

			this.input = input;
			this.output = output;


			// SocketTimeout setzen
			client.socket.setSoTimeout(15_000);

			// Besorge Namen für Spieler
			askForPlayerName();

			// In die Lobby einfügen
			lobby.addPlayer(this.client);

			// jetzt die Kommunikation regeln
			listenToClient();

		} catch (IOException ioe) {
			System.out.println("ERROR: Verbindung mit Clienten verloren!");
		} catch (Exception e) {
			System.out.println("ERROR: ClientThread abgestürzt!");
		}
	}

	private void listenToClient() {
		// TODO: darauf warten, dass der Client eine Aktion in der Lobby ausführen will
		String msg = null;
		int tryCount = 1;
		int pingCount = 1;
		while (running && tryCount <= 3) {
			try {

				// TODO: Marshalling - erste 4 Bytes (Befehlscode) anschauen.
				msg = input.readLine(); // TODO: bricht leider nach ein paar Sek ab, wenn keine Meldung kommt
				
				switch (msg) {
				case "~~50": // z.B. Starte eigene SpielLobby
					createGameSession();
					break;

				case "~~51": // Join anderer SpielLobby
					break;

				case "~~52": // Verlassen der SpielLobby
					break;
				
				case "~~53": // Starten des Spiels
					break;
					
				case "~~54": // Aufgeben des Spiels
					break;

				case"~~60": // Stein setzen
					
					break;
					
				case "~~98":
					this.output.println("~~99");
					break;
					
				case "~~99":
					pingCount = 1; // zurücksetzen
					break;
				default:
					break;
				}

			} catch (SocketTimeoutException stoe) {
				if (pingCount <= 3) {
					System.out.println("Pinge Clienten an. Versuch: " + pingCount);
					this.output.println("~~98"); // Pinge Clienten an
					pingCount++;
				} else {
					running = false;
				}
			} catch (IOException ioe) {
				tryCount++;
				System.out.println("ERROR: keine Antwort von Client. Versuch: " + tryCount);
				// ioe.printStackTrace();
			}
		}

	}

	private void createGameSession() {
		this.gameSession = new GameSession(this.client);
	}
	
	private void joinGameSession(String gameName) {
		//this.lobby.getOpenGames().get
	}
	private void leaveGameSession() {
		String spielName = this.gameSession.gameName;
	// GameSession wieder in die openGames eintragen	
	}
	private void startGame() {
		
	}
	
	private void surrenderGame(){
		
	}
	
	
	private void askForPlayerName() throws IOException {
		boolean done = false;
		String newName = null;
		int trycount = 1;

		while (!done && trycount <= 3) {
			try {

				// Namen Anfordern
				output.println("~~00");
				// output.flush();
				System.out.println("Client nach Name gefragt (~~00)");

				// TODO: Namen empfangen / Marshalling + DeMarshalling in extra Methoden
				newName = input.readLine();
				System.out.println("Angefragter Name von Client: " + newName);

				// neuen Namen überprüfen
				if (newName != null && newName.length() > 2 && newName.length() < 15) {

					// Schauen, ob der Name schon vorhanden ist
					if (lobby.containsPlayer(newName)) {
						output.println("~~02"); // Name schon vorhanden
						trycount = 0;

					} else {
						this.client.name = newName;
						output.println("~~01"); // Name ist gültig
						done = true;
					}
				} else {
					output.println("~~03"); // Ungültiger Name
					trycount = 0;
				}

			} catch (Exception e) {
				System.out.println("ERROR: Abfragen des ClientNamens fehlgeschlagen! Versuch: " + trycount);
				trycount++;
			}
		} // end While

		if (trycount > 3) { // Falls sich der Client nicht mehr gemeldet hat
			throw new IOException();
		}
	}
	
	private void formatPlayerList (List<Player> list) {

		StringBuilder sb = new StringBuilder("");
		
		for(Player p : list) {
			sb.append(p.name + "; ");
		}
		sb.substring(2);  // letzte zwei Zeichen (; ) löschen.
		
	
	}
}
