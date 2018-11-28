package server;

import java.util.ArrayList;
import java.util.List;

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

}
