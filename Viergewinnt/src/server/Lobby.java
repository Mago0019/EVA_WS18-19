package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Thread-Klasse die alle aktiven Clients des Servers verwaltet.
 * TODO: Lobby statisch machen (falls es geht) -> Zugriff von überall möglich
 */
public class Lobby extends Thread {

	List<Player> lobbyList;
	List<Player> inGameList;
	ExecutorService tPool;
	private int gameCount;
	static Lobby instance;

	public Lobby(ExecutorService threadPool) {	 // privater Konstruktor
		this.lobbyList = new ArrayList<Player>();
		this.inGameList = new ArrayList<Player>();
		this.tPool = threadPool;
		this.gameCount = 0;
		
		// TODO: NUR ZUM TESTEN:
		for(int i = 1; i<5; i++) {
			lobbyList.add( new Player("Dummy " + i , null));
		}
	}
	
//	public static Lobby getInstance() {
//		if(instance != null)
//			return instance;
//		instance = new Lobby() {
//			
//		}
//	}

	@Override
	public void run() {
		// TODO: Verwalten der Nutzer und Starten von GameSessions
		
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
	
	private void startGameSession(Player player1, Player player2) {
		gameCount++;
		try {
			String gameName = "Game " + gameCount;  // TODO: bessere Namensgenerierung
			GameSession gs = new GameSession(player1, player2, gameName); 
			
			
			inGameList.add(player1);
			inGameList.add(player2);
			
		} catch (Exception e) {
			System.out.println("ERROR: StartGameSession fehlgeschlagen!");
		}
	}
	
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
