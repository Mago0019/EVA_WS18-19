package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Hilfsklasse, die alle nötigen Informationen zwischenspeichert und die
 * Kommunikation mit dem Server übernimmt.
 */
public class Client extends Thread
{
	private String serverIP;
	private int serverPort;
	private String name;
	private int[][] field; // positiv = player1; negativ = player2; 0 = leeres Feld
	private int width; // falls es später dynamisch sein soll
	private int hight;
	private int playerNumber; // positiv = player1; negativ = player2
	private LinkedList<String> lobbyList;
	private LinkedList<String> openGames;
	private boolean yourTurn;

	// ---- UI ------
	private GameController gameController;

	// ---- IO ----
	private Socket socket;
	private BufferedReader input;
	private PrintStream output;

	public Client(GameController gameC)
	{
		this.gameController = gameC;
		lobbyList = new LinkedList<String>();
		openGames = new LinkedList<String>();
		gameController.setClient(this);
//		lobbyList.add("Manuel");		//eine defaultliste zum testen
//		lobbyList.add("Patrick");
//		lobbyList.add("Andi");
//		lobbyList.add("Pol");
//		openGames.add("Patricks Game");
//		openGames.add("Manuels Game");
	}
	
	@Override
	public synchronized void start() {
		// Output übernimmt der Controller, der Input vom Server wird hier verarbeitet.
		try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream output = new PrintStream(socket.getOutputStream()); ) {
			this.input = input;
			this.output = output;
			
			
			
		} catch (Exception e) {
		}
		
	}
	
	/**
	 * Erste Verbindung mit dem Server wird aufgebaut und IO deklariert.
	 * Wird vom JoinServerController aufgerufen.
	 * @param serverIP IP-Adresse des Servers
	 * @param serverPort Port des Servers
	 * @param spielerName gewünschter Name
	 * @return
	 */
	public int serverConnect(String serverIP, int serverPort, String spielerName) { 
		try {
			this.serverIP = serverIP;
			this.serverPort = serverPort;
			this.name = spielerName;
			boolean done = false;

			System.out.println("Server - ip: " + serverIP + ":" + serverPort);
			System.out.println("Player - ip: " + InetAddress.getLocalHost().getHostAddress() + " PlayerName: " + name);
			if (this.socket == null)
			{
				this.socket = new Socket(this.serverIP, this.serverPort); // TODO: vll Port ändern
				// socket.setSoTimeout(2000);
			}
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintStream(socket.getOutputStream());
			while (!done)
			{
				System.out.println("Warte auf Anfrage vom Server");
				String serverNachricht = input.readLine();
				System.out.println("Nachricht vom Server: " + serverNachricht);
				switch (serverNachricht)
				{
				case "~~0":
					output.println(this.name);
					break;
				case "~~00":
					done = true;
					break;
				case "~~01":
					return 1;
				case "~~02":
					return 2;
				}
			}

			return 0; // Verbinden hat geklappt

		} catch (Exception e)
		{
			// Verbinden fehlgeschlagen
			try
			{
				if (socket != null)
				{
					socket.close();
				}
			} catch (IOException e1)
			{
			}
			return 3;
		}

	}

	public void setLobbyListView()
	{
		gameController.lobby.setAll(this.lobbyList);
//		gameController.lobby_LV.setItems(gameController.lobby);
	}

	public void setOpenGameView()
	{
		gameController.openGames.setAll(this.openGames);
//		gameController.openGames_LV.setItems(gameController.openGames);
	}

	public boolean setStone(int collumn)
	{
		// sende collumn an Server
		return true;
	}
	
	public void setYourTurn(boolean yourTurn) {
		this.yourTurn= yourTurn;
	}
	
	public String getPlayerName() {
		return this.name;
	}
	
	public int getPlayerNumber() {
		return this.playerNumber;
	}
	
	public void createGame()
	{
		this.playerNumber = 1;
	}
	
	/**
	 * Die Methode schickt dem Server, dass man einem offenem Spiel beitreten will.
	 * Der Server schickt einem dann einen String zurück, welcher den Namen des 1. Spielers beinhaltet.
	 * @return Name Spieler 1
	 */
	public String joinGame()
	{
		playerNumber = -1;
		String player1 ="";
		return player1;
	}

	/**
	 * Die Methode schickt dem Server eine Nachricht, dass der Client das Spiel verlassen hat
	 */
	public void leaveYourGame()
	{

	}

	/**
	 * die Methode wird aufgerufen, wenn der Client vom Server benachrichtigt wird, dass der Gegenspieler das Spiel verlassen hat. 
	 */
	public void oponentLeftGame()
	{
		this.gameController.otherPlayerLeftGame();
	}
	
	/**
	 * in der Methode wird dem Gamecontroller den namen des 2. Spielers übergeben, den der Client zuvor vom Server erhalten hat.
	 */
	public void playerJoinedGame() {
		this.gameController.otherPlayerJoinedGame("name");
	}
	
	public void startGame()
	{

	}

	public void surrenderGame()
	{

	}
	
	public void yourTurn() {
		this.yourTurn= true;
		this.gameController.yourTurn();
	}
	

}
