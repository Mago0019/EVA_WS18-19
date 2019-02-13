 package server;

import java.util.LinkedList;
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
	private List<GameSession> openGameList;
	private static Lobby instance;

	private Lobby() {	 // privater Konstruktor
		this.lobbyList = new LinkedList<Player>();
		this.inGameList = new LinkedList<Player>();
		this.openGameList = new LinkedList<GameSession>();
		
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

	public synchronized void addPlayer(Player player) {
		this.lobbyList.add(player);
	}
	
	
	public synchronized void addGameSession(GameSession gs) {
		this.openGameList.add(gs);
//		this.inGameList.add(gs.player1);
	}
	
	public List<Player> getLobbyList() {
		return lobbyList;
	}
	
	public List<GameSession> getOpenGames(){
		return this.openGameList;
	}
	
	public synchronized void addPlayerToLobby() {
		
	}
	
	public boolean containsPlayer(String name) { // Problem: Wenn ein spieler gerade ein spiel verlässt/startet
		if(this.lobbyList.contains(name) || this.inGameList.contains(name)) {
			return true;
		} else {
			return false;
		}
	}
}
