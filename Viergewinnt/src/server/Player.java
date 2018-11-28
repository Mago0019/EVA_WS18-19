package server;

import java.net.Socket;

public class Player {

	public String name;
	public Socket socket;
	
	public Player(String name, Socket socket) {
		this.name = name;
		this.socket = socket;
	}
}
