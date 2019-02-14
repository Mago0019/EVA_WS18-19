package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import sun.nio.cs.Surrogate;

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
	GameSession gameSession;
	boolean iAmHost;

	boolean running;
	BufferedReader input;
	PrintStream output;

	public ClientThread(Socket clientSocket, Lobby lobby) {
		client = new Player("", clientSocket);
		// this.client.socket = clientSocket;
		this.lobby = lobby;
		this.running = true;
	}

	@Override
	public void run() {
		System.out.println("\nClientThread gestartet.");

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
		} finally {
			logoutClient(); // Wenn es keine Kommunikation mehr gibt -> Client vom Server entfernen
		}
	}

	private void listenToClient() {
		// darauf warten, dass der Client eine Aktion in der Lobby ausführen will
		String msg = null;
		String order, content;
		int tryCount = 1;
		int pingCount = 1;
		while (running && tryCount <= 3) {
			try {

				// Marshalling - erste 4 Bytes (Befehlscode) anschauen.
				msg = input.readLine();

				if (msg == null) {
					continue;
				}
				

				order = msg.substring(0, 4);
				content = msg.substring(4, msg.length());

				// zun Debuggen:
				if(!order.equals("~~99") && !order.equals("~~98"))
					System.out.println("- " + this.client.name + " -> Order:<" + order + "> content:<" + content + ">");
					//System.out.println("Nachricht von C.: " + msg);

				
				switch (order) {
				
				case "~~50": // z.B. Starte eigene SpielLobby
					createGameSession();
					break;

				case "~~51": // Join anderer SpielLobby
					System.out.println("Anfage nach Join -> " + content);
					joinGameSession(content);
					break;

				case "~~52": // Verlassen der SpielLobby
					leaveGameSession();
					break;

				case "~~53": // Starten des Spiels
					startGame();
					break;

				case "~~54": // Aufgeben des Spiels
					win_lose(false);
					break;

				case "~~60": // Stein setzen
					try {
						setStone(Integer.parseInt(content));
					} catch (NumberFormatException e) {
						// Sollte nicht auftreten, da die Client-UI auf Fehleingaben prüft
					}
					break;

				case "~~70": // Client Fragt nach Lobbyliste
					sendLobbyList();
					break;

				case "~~71": // Client Fragt nach GameListe
					sendOpenGames();
					break;

				case "~~80": // logout Client
					logoutClient();
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
					//System.out.println("Pinge Clienten an. Versuch: " + pingCount);
					this.output.println("~~98"); // Pinge Clienten an
					pingCount++;
				} else {
					running = false;
				}
			} catch (IOException ioe) {
				String error = "-";
				if (ioe.getMessage() != null) {
					error = ioe.getMessage();
				}
				System.out.println("ERROR: keine Antwort von Client: " + error + ". Versuch: " + tryCount);
				tryCount++;
				// ioe.printStackTrace();
			}
		}

	}

	/************
	 * Methoden für Aufrufe vom Clienten
	 *************/
	private void createGameSession() {
		this.gameSession = new GameSession(this);
		lobby.addGameSession(this.gameSession);
		this.iAmHost = true;
	}

	private void joinGameSession(String gameName) { // ich bin Spieler 2 und joine einem Spiel
		System.out.println("Wird Join aufgerufen?");
		for (GameSession gs : lobby.getOpenGames()) {
			if (gs.gameName.equals(gameName)) {
				
				System.out.println("join Game -> " + gameName);

				this.gameSession = gs;
				this.gameSession.player2 = this;
				System.out.println("This.gameS: " + this.gameSession.player2.client.name);
				System.out.println("gs gameS: " + gs.player2.client.name);
				
				
				lobby.removeGameSession(gs);
				this.iAmHost = false;
				
				
				this.gameSession.player1.output.println("~~20" + this.client.name); // Host mitteilen, dass gejoint wurde
			}
		}
	}

	private synchronized void leaveGameSession() {
		try {
			if (iAmHost) { // bin Host
				if(this.gameSession.player2 != null) {
					this.gameSession.player2.output.println("~~21");
					this.gameSession.player2.gameSession = null;
				} else {
					lobby.removeGameSession(this.gameSession);
				}
			//	this.output.println("~~21");
				this.gameSession = null;

			} else { // bin zweiter Spieler
				this.gameSession.player1.output.println("~~21");
				lobby.addGameSession(this.gameSession);
				this.gameSession = null;
			//	this.output.println("~~21");
			}
		} catch (Exception e) { // Falls der Client schon geschlossen ist
			if (e.getMessage() != null) {
				System.out.println("E: leaveGame -> " + e.getMessage());
			}
		}

	}

	private void startGame() { // Du bist Host und hast das Spiel gestartet.
		this.gameSession.randomStart();
		this.output.println("~~30");
		this.gameSession.player2.output.println("~~30");

		// Gebe Startspieler und Spielfeld (leer) an
		if (gameSession.playerTurn > 0 && iAmHost) {
			this.output.println("~~31true;" + gameSession.gameFieldToString());
			this.gameSession.player2.output.println("~~31true;" + gameSession.gameFieldToString());
		} else if (gameSession.playerTurn < 0 && !iAmHost) {
			this.output.println("~~31true;" + gameSession.gameFieldToString());
			this.gameSession.player1.output.println("~~31true;" + gameSession.gameFieldToString());
		} else {
			if (iAmHost) {
				this.output.println("~~31false;" + gameSession.gameFieldToString());
				this.gameSession.player2.output.println("~~31true;" + gameSession.gameFieldToString());
			} else {
				this.output.println("~~31false;" + gameSession.gameFieldToString());
				this.gameSession.player1.output.println("~~31true;" + gameSession.gameFieldToString());
			}
		}

	}

	private void win_lose(boolean win) { // ich habe aufgegeben -> win = false
		try {
			if (iAmHost) { // bin Host
				this.gameSession.player2.output.println("~~33" + !win);
				this.output.println("~~33" + win);
			} else { // bin zweiter Spieler
				this.gameSession.player1.output.println("~~33" + !win);
				this.output.println("~~33" + win);
			}
		} catch (Exception e) { // Falls der Client schon geschlossen ist
			if (e.getMessage() != null) {
				System.out.println("E: win_lose -> " + e.getMessage());
			}
		}
	}

	private void setStone(int collumn) {
		boolean correct = this.gameSession.setStone(collumn, this.client);

		if (correct) {
			this.output.println("~~32true"); // Stein-setzen hat geklappt
			int playerNr = 0;
			if (iAmHost) {
				playerNr = 1;
			} else {
				playerNr = -1;
			}

			if (this.gameSession.gameField.checkWin(collumn, playerNr)) { // Überprüfen, ob ich gewonnen hab
				if (iAmHost) {
					win_lose(true);
				} else {
					this.output.println("~~31true" + ";" + gameSession.gameFieldToString()); // eigenem Clienten
																								// bescheid geben, wer
																								// am Zug ist
					if (iAmHost) { // anderem Clienten bescheid geben, wer am Zug ist
						this.gameSession.player2.output.println("~~31false" + ";" + gameSession.gameFieldToString());
					} else {
						this.gameSession.player1.output.println("~~31false" + ";" + gameSession.gameFieldToString());
					}
				}
			}

		} else {
			this.output.println("~~32false"); // Stein-setzen hat nicht geklappt
		}

	}

	private void sendLobbyList() { // Antwort auf ~~70
		StringBuilder sb = new StringBuilder("~~10");
		for (Player p : this.lobby.getLobbyList()) {
			sb.append(p.name + ",");
		}
		
		if (sb.length() > 4) { // das Komma nur entfernen, wenn min ein Game angehängt wurde
			sb.deleteCharAt(sb.length() - 1); 
		}
		
		this.output.println(sb.toString() );
	}

	private void sendOpenGames() { // Antwort auf ~~71
		StringBuilder sb = new StringBuilder("~~11");
		for (GameSession gs : this.lobby.getOpenGames()) {
			sb.append(gs.gameName + ",");
		}
		if (sb.length() > 4) { // das Komma nur entfernen, wenn min ein Game angehängt wurde
			sb.deleteCharAt(sb.length() - 1); 
		}
		System.out.println("Info: sendOpenGames -> " + sb.toString());
		this.output.println(sb.toString());
	}

	private void logoutClient() {

		if (this.gameSession != null) { // bin min in der Lobby
			if (this.gameSession.playerTurn != 0) { // Spiel hat schon angefangen
				win_lose(false); // gebe auf
			}
			leaveGameSession(); // verlasse GameLobby
		}
		this.lobby.removePlayer(this.client);

	}

	private void askForPlayerName() throws IOException {
		boolean done = false;
		String newName = null;
		int trycount = 1;

		while (!done && trycount <= 3) {
			try {
				newName = null;
				// Namen Anfordern
				output.println("~~00");
				// output.flush();
				System.out.println("Client nach Name gefragt (~~00)");

				// TODO: Namen empfangen / Marshalling + DeMarshalling in extra Methoden
				String msg = input.readLine();

				// System.out.println("DEBUG: msg: " + msg + " Code: " + msg.subSequence(0, 4));

				if (msg.substring(0, 4).equals("~~00")) {
					newName = msg.substring(4, msg.length());
					System.out.println("Angefragter Name von Client: " + newName);
				} // im else-Fall -> newName bleibt null

				// neuen Namen überprüfen
				if (newName != null && newName.length() > 2 && newName.length() < 15) {

					// Schauen, ob der Name schon vorhanden ist
					if (lobby.containsPlayer(newName)) {
						System.out.println("Name ist schon vergeben -> ~~02 an Client");
						output.println("~~02"); // Name schon vorhanden
						trycount = 0;

					} else {
						System.out.println("Name ist gültig -> ~~01 an Client");
						this.client.name = newName;
						output.println("~~01"); // Name ist gültig
						done = true;
					}
				} else {
					System.out.println("Name ist ungültig -> ~~03 an Client");
					output.println("~~03"); // Ungültiger Name
					trycount = 0;
				}

			} catch (Exception e) {
				String error = "-";
				if (e.getMessage() != null) {
					error = e.getMessage();
				}
				System.out.println(
						"ERROR: Abfragen des ClientNamens fehlgeschlagen: " + error + ". Versuch: " + trycount);
				trycount++;
			}
		} // end While

		if (trycount > 3) { // Falls sich der Client nicht mehr gemeldet hat
			// System.out.println("Error: Namen Abfragen fehlgeschlagen -> Trycount = " +
			// trycount);
			throw new IOException();
		}
	}

	private void formatPlayerList(List<Player> list) {

		StringBuilder sb = new StringBuilder("");

		for (Player p : list) {
			sb.append(p.name + "; ");
		}
		sb.substring(2); // letzte zwei Zeichen (; ) löschen.

	}
}
