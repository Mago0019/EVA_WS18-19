package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

public class Client
{
	String serverIP;
	int serverPort;
	String name;
	SampleController sampleC;
	
	public Client(SampleController sampleC)
	{
		this.sampleC = sampleC;
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
