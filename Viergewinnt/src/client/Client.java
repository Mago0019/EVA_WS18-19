package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Hilfsklasse, die alle nötigen Informationen zwischenspeichert und die Kommunikation mit dem Server übernimmt.
 */
public class Client extends Thread
{
	private String serverIP;
	private int serverPort;
	private String name;
	private int[][] field;
	private int width; // falls es später dynamisch sein soll
	private int hight;
	private LinkedList<String> lobbyList;
	private LinkedList<String> openGames;
	
	//---- UI ------
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
	public boolean serverConnect(String serverIP, int serverPort, String spielerName) { 
		try {
			this.serverIP = serverIP;
			this.serverPort = serverPort;
			this.name = spielerName;			
			
			System.out.println("Server - ip: " + serverIP + ":" + serverPort );
			System.out.println("Player - ip: " + InetAddress.getLocalHost().getHostAddress()  + " PlayerName: " + name );
						
			this.socket = new Socket(this.serverIP, this.serverPort);  // TODO: vll Port ändern
			//socket.setSoTimeout(2000);

			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintStream(socket.getOutputStream()); 
		
			System.out.println("Warte auf Anfrage vom Server");
			String serverNachricht = input.readLine();
			System.out.println("Nachricht vom Server: " + serverNachricht);
			
			output.print("ACK");
			return true; // Verbinden hat geklappt
			
		}catch(Exception e) {
			// Verbinden fehlgeschlagen
			try { socket.close(); } catch (IOException e1) {}
			return false;
		}
		
	}
	
	public void setLobbyListView(){
		gameController.lobby.setAll(this.lobbyList);
//		gameController.lobby_LV.setItems(gameController.lobby);
	}
	

	public void setOpenGameView() {
		gameController.openGames.setAll(this.openGames);
//		gameController.openGames_LV.setItems(gameController.openGames);
	}
	
	public String getPlayerName() {
		return this.name;
	}
	
	public void setStone(int collumn) {
		//sende collumn an Server
	}
	
	public void createGame() {
		
	}
	
	public void joinGame() {
		
	}
	
	public void leaveYourGame() {
		
	}
	
	public void startGame() {
		
	}
	
	public void surrenderGame() {
		
	}

}
