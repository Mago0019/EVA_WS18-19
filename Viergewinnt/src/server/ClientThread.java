package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import com.sun.xml.internal.bind.v2.runtime.Name;

/**
 * Kleine Thread-Klasse, die den Clienten nach dem Namen fragt und ihn dann in
 * die Lobby einträgt. Danach wird die Kommunikation mit der Lobby geregelt.
 * 
 * Erzeugt die GameSessions. Kümmert sich also weiter um die Kommunikation mit
 * den Clienten.
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
		if (Lobby.debugMode)
			System.out.println("\nNeuer ClientThread gestartet.");

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
			System.out.println("- New client joined: " + this.client.name);
			lobby.addPlayer(this.client);

			// jetzt die Kommunikation regeln
			listenToClient();

		} catch (NameException ne) {
			// nix tun -> Client wird sich nochmal melden
			if(Lobby.debugMode)
				System.out.println("Name war ungültig/vergeben -> schließe Socket <" + this.client.name + ">");
		} 
		catch (IOException ioe) {
			if(Lobby.debugMode)
				System.out.println("ERROR: Verbindung mit Clienten verloren -> " + ioe);
			//ioe.printStackTrace();
		} catch (Exception e) {
			if(Lobby.debugMode)
				System.out.println("ERROR: ClientThread abgestürzt -> " + e.getMessage() );
			//e.printStackTrace();
		} finally {
			System.out.println("- Client left: " + this.client.name + " -> close connection.");
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
				if (Lobby.debugMode && !order.equals("~~99") && !order.equals("~~98"))
					System.out.println("- " + this.client.name + " -> Order:<" + order + "> Content:<" + content + ">");

				switch (order) {

				case "~~50": // z.B. Starte eigene SpielLobby
					createGameSession();
					break;

				case "~~51": // Join anderer SpielLobby
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
						System.out
								.println("ERROR: Das hätte nicht passieren sollen. NumberFormatExc. auf Serverseite!");
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
					// System.out.println("Pinge Clienten an. Versuch: " + pingCount);
					this.output.println("~~98"); // Pinge Clienten an
					pingCount++;
				} else {
					if(Lobby.debugMode)
						System.out.println("ERROR: <" + this.client.name + "> antwortet nicht mehr - Pingcount: " + pingCount + " -> " + stoe + ": " + stoe.getMessage() );
					running = false;
				}
			} catch (IOException ioe) {
				if (Lobby.debugMode)
					System.out.println("ERROR: keine Antwort von <" + this.client.name + "> : " + ioe + ". Versuch: "
							+ tryCount);
				tryCount++;
			}
		}

	}

	/************************************
	 * Methoden für Aufrufe vom Clienten
	 ************************************/
	private void createGameSession() {
		this.gameSession = new GameSession(this);
		lobby.addGameSession(this.gameSession);
		this.iAmHost = true;
	}

	private void joinGameSession(String gameName) { // ich bin Spieler 2 und joine einem Spiel
		for (GameSession gs : lobby.getOpenGames()) {
			if (gs.gameName.equals(gameName)) {
				this.gameSession = gs;
				this.gameSession.player2 = this;

				lobby.removeGameSession(gs);
				this.iAmHost = false;

				this.gameSession.player1.output.println("~~20" + this.client.name); // Host mitteilen, dass gejoint
																					// wurde
			}
		}
	}

	private synchronized void leaveGameSession() {
		try {
			if (iAmHost) {
				if (this.gameSession.player2 != null) { // Spieler2 in der Lobby? -> rauswerfen
					this.gameSession.player2.output.println("~~21");
					this.gameSession.player2.gameSession = null;
				}
				lobby.removeGameSession(this.gameSession);
				// this.output.println("~~21");
				this.gameSession = null;

			} else { // bin zweiter Spieler
				this.gameSession.player1.output.println("~~21");
				lobby.addGameSession(this.gameSession);
				this.gameSession = null;
				// this.output.println("~~21");
			}
		} catch (Exception e) { // Falls der Client schon geschlossen ist
			if (Lobby.debugMode && e.getMessage() != null) {
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
			this.gameSession.player2.output.println("~~31false;" + gameSession.gameFieldToString());
		} else if (gameSession.playerTurn < 0 && !iAmHost) {
			this.output.println("~~31true;" + gameSession.gameFieldToString());
			this.gameSession.player1.output.println("~~31false;" + gameSession.gameFieldToString());
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
			// reset Gamefield

			this.gameSession.gameField.clearField();

			// Gewinnnachricht abschicken
			if (iAmHost) { // bin Host
				this.gameSession.player2.output.println("~~33" + !win);
				this.output.println("~~33" + win);
			} else { // bin zweiter Spieler
				this.gameSession.player1.output.println("~~33" + !win);
				this.output.println("~~33" + win);
			}
		} catch (Exception e) { // Falls der Client schon geschlossen ist
			if (Lobby.debugMode && e.getMessage() != null) {
				System.out.println("E: win_lose -> " + e.getMessage());
			}
		}
	}

	private void setStone(int collumn) {
		boolean correct = this.gameSession.setStone(collumn, this.client);

		if (Lobby.debugMode)
			System.out.println("Setstone - Collumn=" + collumn + " correct=" + correct);

		if (correct) {
			this.output.println("~~32true"); // Stein-setzen hat geklappt
			int playerNr = 0;
			if (iAmHost) {
				playerNr = 1;
			} else {
				playerNr = -1;
			}

			// System.out.println("Setstone - currentPlayerNr:" + playerNr + " . Prüfe ob
			// gewonnen...");

			if (this.gameSession.gameField.checkWin(collumn, playerNr)) { // Überprüfen, ob ich gewonnen hab
				win_lose(true);
				if (Lobby.debugMode)
					System.out.println("Setstone - sollte jetzt gewonnen haben.");

			} else {
				if (Lobby.debugMode)
					System.out.println("Setstone - leider nicht gewonnen.");

				this.output.println("~~31false" + ";" + gameSession.gameFieldToString());
				if (iAmHost) { // anderem Clienten bescheid geben, wer am Zug ist
					this.gameSession.player2.output.println("~~31true" + ";" + gameSession.gameFieldToString());
				} else {
					this.gameSession.player1.output.println("~~31true" + ";" + gameSession.gameFieldToString());
				}
			}
		} else {
			if (Lobby.debugMode)
				System.out.println("Setstone - Steinsetzen fehlgeschlagen!");

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

		this.output.println(sb.toString());
	}

	private void sendOpenGames() { // Antwort auf ~~71
		StringBuilder sb = new StringBuilder("~~11");
		for (GameSession gs : this.lobby.getOpenGames()) {
			sb.append(gs.gameName + ",");
		}
		if (sb.length() > 4) { // das Komma nur entfernen, wenn min ein Game angehängt wurde
			sb.deleteCharAt(sb.length() - 1);
		}
		this.output.println(sb.toString());
	}

	private void logoutClient() {
		if (this.gameSession != null) { // bin min in der Lobby
			if (this.gameSession.playerTurn != 0) { // Spiel hat schon angefangen
				win_lose(false); // gebe auf
			}
			leaveGameSession(); // verlasse GameLobby
		}
		this.lobby.removePlayer(this.client); // aus Lobby austragen

	}

	private void askForPlayerName() throws IOException, NameException {
		boolean done = false;
		String newName = null;
		int trycount = 1;
		String err = null;

		while (!done && trycount <= 3) {
			try {
				newName = null;
				// Namen Anfordern
				output.println("~~00");

				if (Lobby.debugMode)
					System.out.println("Client nach Name gefragt (~~00)");

				String msg = input.readLine();

				if (msg.substring(0, 4).equals("~~00")) {
					newName = msg.substring(4, msg.length());
					if (Lobby.debugMode)
						System.out.println("Angefragter Name von Client: " + newName);
				} // im else-Fall -> newName bleibt null

				// neuen Namen überprüfen
				if (newName != null && newName.length() > 2 && newName.length() < 15) {

					// Schauen, ob der Name schon vorhanden ist
					if (lobby.containsPlayer(newName)) {
						if (Lobby.debugMode)
							System.out.println("Name ist schon vergeben -> ~~02 an Client");
						output.println("~~02"); // Name schon vorhanden
						trycount = 1;
						err = "Name alredy used ~~02";
						break;
					} else {
						if (Lobby.debugMode)
							System.out.println("Name ist gültig -> ~~01 an Client");
						this.client.name = newName;
						output.println("~~01"); // Name ist gültig
						done = true;
					}
				} else {
					if (Lobby.debugMode)
						System.out.println("Name ist ungültig -> ~~03 an Client");
					output.println("~~03"); // Ungültiger Name
					trycount = 1;
					err = "Name invalid ~~03";
					break;
				}

			} catch (Exception e) {
				String error = "-";
				if (e.getMessage() != null) {
					error = e.getMessage();
				}
				if (Lobby.debugMode)
					System.out.println(
							"ERROR: Abfragen des ClientNamens fehlgeschlagen: " + error + ". Versuch: " + trycount);
				trycount++;
			}
		} // end While

		if (trycount > 3) { // Falls sich der Client nicht mehr gemeldet hat
			throw new IOException();
		}
		if (err != null) {
			throw new NameException(err);
		}
	}

	private void formatPlayerList(List<Player> list) {

		StringBuilder sb = new StringBuilder("");

		for (Player p : list) {
			sb.append(p.name + "; ");
		}
		sb.substring(2); // letzte zwei Zeichen (; ) löschen.

	}
	
	private class NameException extends Exception{
		public NameException() {
			super("NameException");
		}
		
		public NameException(String errMsg) {
			super(errMsg);
		}
	}
}
