package server;

import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GameSession extends Thread
{
	Player player1;
	Player player2;
	String gameName;
	GameField gameField;
	
	
	public GameSession(Player player1, Player player2, String gameName, int width, int hight)
	{
		this.player1 = player1;
		this.player2 = player2;
		this.gameName = gameName;
		this.gameField = new GameField(width, hight);
	}
	
	public GameSession(Player player1, Player player2, String gameName)
	{
		this.player1 = player1;
		this.player2 = player2;
		this.gameName = gameName;
		this.gameField = new GameField(); //nutzt default Größe fürs Spielfeld Breite : 7, Höhe : 6
	}
	
	@Override
	public void run()
	{
		//TODO ...
		
		
		// TODO: Beim Beenden des Spiels müssen die beiden Player wieder aus der "InGame"-LobbyListe ausgetragen werden.
		
	}
	
	
	private int randomStart()
	{
		if(ThreadLocalRandom.current().nextBoolean())
		{
			return 1; // Spieler 1 darf starten
		}
		else
		{
			return -1; // Spieler 2 darf starten
		}
	}
	
}
