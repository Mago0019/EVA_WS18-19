package server;

import java.util.concurrent.ThreadLocalRandom;

public class GameSession {

	Player player1;
	Player player2;
	String gameName;
	GameField gameField;
	int playerTurn;
	
	
	public GameSession(Player player1)
	{
		this.player1 = player1;
		this.gameName = player1.name + "'s Game";
		this.gameField = new GameField();
		this.playerTurn = randomStart();
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
