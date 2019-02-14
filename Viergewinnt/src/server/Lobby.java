package server;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Thread-Klasse die alle aktiven Clients des Servers verwaltet. TODO: Lobby
 * statisch machen (falls es geht) -> Zugriff von überall möglich (singleton,
 * vll threadlos)
 * 
 * TODO(EVTL): Spieler ID Generieren lassen -> später vll interessant, falls
 * Spieler den gleichen Namen haben.
 * 
 * KRITISCHE KLASSE! MUSS THREADSAFE SEIN!
 */
public class Lobby {

	private List<Player> lobbyList;
	// private List<Player> inGameList;
	private List<GameSession> openGameList;
	private static Lobby instance;

	private Lobby() { // privater Konstruktor
		this.lobbyList = new LinkedList<Player>();
		// this.inGameList = new LinkedList<Player>();
		this.openGameList = new LinkedList<GameSession>();

		/*
		 * // TODO: NUR ZUM TESTEN: Füllen der Lobby for (int i = 1; i < 5; i++) {
		 * lobbyList.add(new Player("Dummy " + i, null)); }
		 */ }

	public static synchronized Lobby getInstance() { // Synchronized, damit nicht ausversehen 2 Objekte erzeugt werden.
		if (instance != null)
			return instance;
		instance = new Lobby();
		return instance;
	}

	public synchronized void addPlayer(Player player) {
		this.lobbyList.add(player);
		// Alle anderen Spieler informieren
		for (Player p : lobbyList) {
			// System.out.println("Player: " + p.name);
			try {
				PrintStream ps = new PrintStream(p.socket.getOutputStream()); // darf nicht geschlossen werden
				StringBuilder sb = new StringBuilder("~~10");
				for (Player pl : lobbyList) {
					sb.append(pl.name + ",");
				}

				if (sb.length() > 4) { // das Komma nur entfernen, wenn min ein Game angehängt wurde
					sb.deleteCharAt(sb.length() - 1);
				}

				ps.println(sb.toString());

			} catch (Exception e) {
			}
		}
	}

	public synchronized void removePlayer(Player player) {
		this.lobbyList.remove(player);

		// Alle anderen Spieler informieren
		for (Player p : lobbyList) {
			try {
				PrintStream ps = new PrintStream(p.socket.getOutputStream()); // darf nicht geschlossen werden
				StringBuilder sb = new StringBuilder("~~10");
				for (Player pl : lobbyList) {
					sb.append(pl.name + ",");
				}

				if (sb.length() > 4) { // das Komma nur entfernen, wenn min ein Game angehängt wurde
					sb.deleteCharAt(sb.length() - 1);
				}

				ps.println(sb.toString());

			} catch (Exception e) {
			}
		}
	}

	public synchronized void addGameSession(GameSession gs) {
		this.openGameList.add(gs);

		// Alle anderen Spieler informieren
		for (Player p : lobbyList) {
			try {
				PrintStream ps = new PrintStream(p.socket.getOutputStream()); // darf nicht geschlossen werden
				StringBuilder sb = new StringBuilder("~~11");
				for (GameSession session : openGameList) {
					sb.append(session.gameName + ",");
				}
				ps.println(sb.toString().substring(0, sb.length() - 1));

			} catch (Exception e) {
			}
		}
	}

	public synchronized void removeGameSession(GameSession gs) {
		this.openGameList.remove(gs);

		// Alle anderen Spieler informieren
		for (Player p : lobbyList) {
			try {
				PrintStream ps = new PrintStream(p.socket.getOutputStream()); // darf nicht geschlossen werden
				StringBuilder sb = new StringBuilder("~~11");
				for (GameSession session : openGameList) {
					sb.append(session.gameName + ",");
				}
				ps.println(sb.toString());

			} catch (Exception e) {
			}
		}
	}

	public List<Player> getLobbyList() {
		return lobbyList;
	}

	public List<GameSession> getOpenGames() {
		return this.openGameList;
	}

//	public synchronized void addPlayerToLobby(Player player) {
//		
//	}

	public boolean containsPlayer(String name) { // Problem: Wenn ein spieler gerade ein spiel verlässt/startet
		for (Player p : lobbyList) {
			if (p.name.equals(name)) {
				return true;
			}
		}
		return false;
	}
}
