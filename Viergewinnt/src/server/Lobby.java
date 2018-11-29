package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread-Klasse die alle aktiven Clients des Servers verwaltet.
 */
public class Lobby extends Thread {

	List<Player> lobbyList;
	List<Player> inGameList;
	// TODO

	public Lobby() {	
		this.lobbyList = new ArrayList<Player>();
		this.inGameList = new ArrayList<Player>();
	}

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
	
	private void startGameSession(Player p1, Player p2) {
		try {
			
			
			inGameList.add(p1);
			inGameList.add(p2);
			
		} catch (Exception e) {
			System.out.println("ERROR: StartGameSession fehlgeschlagen!");
		}
	}
}
