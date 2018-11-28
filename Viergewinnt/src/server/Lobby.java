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

	public Lobby() {	
		this.lobbyList = new ArrayList<Player>();
		this.inGameList = new ArrayList<Player>();
	}

	@Override
	public void run() {
		
	}

	
	public void addUser(Socket socket) {
		Player pl = new Player();
		pl.socket = socket;
		this.lobbyList.add(pl);
	}
}
