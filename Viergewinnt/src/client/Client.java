package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

public class Client
{
	String serverIP;
	int serverPort;
	String name;
	int[][] field;
	int width;
	int hight;
	LobbyController lobbyController;
	LinkedList<String> lobbyList;
	
	public Client(LobbyController lobbyC)
	{
		this.lobbyController = lobbyC;		
		lobbyList = new LinkedList<String>();
		lobbyList.add("Manuel");		//eine defaultliste zum testen
		lobbyList.add("Patrick");
		lobbyList.add("Andi");
		lobbyList.add("Pol");
		lobbyController.setClient(this);
	}
	
	public void serverConnect()
	{
		try 
		{
			
			System.out.println("Server - ip: " + serverIP + ":" + serverPort );
			System.out.println("Player - ip: " + InetAddress.getLocalHost().getHostAddress()  + " PlayerName: " + name );
						
			Socket socket = new Socket(this.serverIP, this.serverPort);  // TODO: vll Port ändern
			//socket.setSoTimeout(2000);

			
			
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream output = new PrintStream(socket.getOutputStream()); 
			System.out.println("Warte auf Anfrage vom Server");
			String serverNachricht = input.readLine();
			System.out.println(serverNachricht);
			
			System.out.println("nachricht erhalten");
			
			
			
			output.print("ACK");
			socket.close();
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
