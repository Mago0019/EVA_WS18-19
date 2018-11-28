package server;

import java.net.Socket;

public class GameSession extends Thread
{
	Socket client1;
	Socket client2;
	String gameName;
	int[][] field;
	
	
	public GameSession(Socket client1, Socket client2, String gameName)
	{
		this.client1 = client1;
		this.client2 = client2;
		this.gameName = gameName;
		this.field = field;
	}
	
	@Override
	public void run()
	{
		//TODO
	}
}
