package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client
{
	String serverIP;
	int port;
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
			this.port = port;
			
			Socket socket = new Socket(this.serverIP, this.port);
			socket.setSoTimeout(2000);
			
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream output = new PrintStream(socket.getOutputStream());
			
			String serverNachricht = input.readLine();
			
			System.out.println(serverNachricht);
			
			output.print("ACK");
			output.flush();
			socket.close();
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
