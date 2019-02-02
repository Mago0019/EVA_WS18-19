package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

public class Client
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
		gameController.lobby_LV.setItems(gameController.lobby);
	}
	

	public void setOpenGameView() {
		gameController.openGames.setAll(this.openGames);
		gameController.openGames_LV.setItems(gameController.openGames);
	}
	
	public String getName() {
		return this.name;
	}

}
