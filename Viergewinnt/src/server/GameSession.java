package server;

public class GameSession extends Thread
{
	Player player1;
	Player player2;
	String gameName;
	int[][] field;
	
	
	public GameSession(Player player1, Player player2, String gameName)
	{
		this.player1 = player1;
		this.player2 = player2;
		this.gameName = gameName;
		this.field = field;
	}
	
	@Override
	public void run()
	{
		//TODO ...
		
		
		// TODO: Beim Beenden des Spiels müssen die beiden Player wieder aus der "InGame"-LobbyListe ausgetragen werden.
		
	}
}
