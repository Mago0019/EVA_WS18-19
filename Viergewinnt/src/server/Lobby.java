package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread-Klasse die alle aktiven Clients des Servers verwaltet.
 * TODO: Lobby statisch machen (falls es geht) -> Zugriff von überall möglich (singleton, vll threadlos)
 * 
 * TODO(EVTL): Spieler ID Generieren lassen -> später vll interessant, falls Spieler den gleichen Namen haben.
 * 
 * KRITISCHE KLASSE! MUSS THREADSAFE SEIN!
 */
public class Lobby {

	private List<Player> lobbyList;
	private List<Player> inGameList;
	private int gameCount;
	private static Lobby instance;

	private Lobby() {	 // privater Konstruktor
		this.lobbyList = new ArrayList<Player>();
		this.inGameList = new ArrayList<Player>();
		this.gameCount = 0;
		
		// TODO: NUR ZUM TESTEN: Füllen der Lobby
		for(int i = 1; i<5; i++) {
			lobbyList.add( new Player("Dummy " + i , null));
		}
	}
	
	public static synchronized Lobby getInstance() { // Synchronized, damit nicht ausversehen 2 Objekte erzeugt werden.
		if(instance != null)
			return instance;
		instance = new Lobby();
		return instance;
	}

	
	/**
	 * Fügt einen Clienten zur Lobby hinzu
	 */
	public void addUser(Socket socket, String name) {
		Player pl = new Player();
		pl.socket = socket;
		pl.name = name;
		this.lobbyList.add(pl);
	}
	
	/**
	 * Wird von einem EmpfangsThread aufgerufen und verbindet beide Threads um während dem Spiel kommunizieren zu können.
	 * @param player1 der Spieler des Threads, der die Methode aufruft
	 * @param player2 der andere Spieler, mit dem sich verbunden werden soll
	 */
	public void startGameSession(Player player1, Player player2) {
		gameCount++;
		try {
			String gameName = "Game " + gameCount;  // TODO: bessere Namensgenerierung
			GameSession gs = new GameSession(player1, player2, gameName); 
			gs.start();
			
			inGameList.add(player1);
			inGameList.add(player2);
			
		} catch (Exception e) {
			System.out.println("ERROR: StartGameSession fehlgeschlagen!");
		}
	}
	
	/**
	 * Formattierte String ausgabe für alle Spieler in der Lobby und alle Spieler, die momentan in einem Spiel sind.
	 * 
	 * @return zwei-zeiliger String
	 */
	public String getLobbyList() {
		StringBuilder sb = new StringBuilder("Lobby: ");
		
		for(Player p : lobbyList) {
			sb.append(p.name + "; ");
		}
		sb.substring(2);  // letzte zwei Zeichen (; ) löschen.
		
		sb.append("\nIn Game: ");
		for(Player p : inGameList) {
			sb.append(p.name);
		}
		sb.substring(2);  // letzte zwei Zeichen (; ) löschen.
		
		return sb.toString();
	}
}
