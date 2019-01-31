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
	LobbyController lobbyC;
	LinkedList<String> lobbyList;
	
	public Client(LobbyController lobbyC)
	{
		this.lobbyC = lobbyC;
		this.lobbyList= new LinkedList<String>();
		lobbyList.add("Patrick");
		lobbyList.add("Manuel");
		lobbyList.add("Thomas");
		lobbyList.add("Nadine");
	}
	
	public void serverConnect(String serverIP, int port, String name)
	{
		try 
		{
			this.serverIP = serverIP;
			this.name = name;
			this.serverPort = port;
			
			System.out.println("Server - ip: " + serverIP + ":" + port );
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
