package server;

import java.net.Socket;

public class Player {

	public String name = "noname";
	public Socket socket;
	
	public Player() {		
	}
	public Player(String name, Socket socket) {
		this.name = name;
		this.socket = socket;
	}
}
