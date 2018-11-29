package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Kleine Thread-Klasse, die den Clienten nur nach dem Namen fragt und ihn dann
 * in die Lobby einträgt.
 */
public class EmpfangsThread extends Thread {

	Socket clientSocket;
	Lobby lobby;

	public EmpfangsThread(Socket clientSocket, Lobby lobby) {
		this.clientSocket = clientSocket;
		this.lobby = lobby;
	}

	@Override
	public void run() {
		// Frage den Clienten nach dem Namen und geben ihn dann an die Lobby weiter.
		String clientName = "noName";
		boolean done = false;

		while (done) {
			try {
				BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintStream output = new PrintStream(clientSocket.getOutputStream());

				// TODO: Befehlscode für NamensAnforderung
				output.print("\\0");
				output.flush();

				// TODO: Namen empfangen / Marshalling + DeMarshalling in extra MEthoden
				String newName = input.readLine();
				
				// Name 
				if(newName != null && newName.length() > 2 && newName.length() < 10) {
					clientName = newName;
					done = true;
					System.out.println("New PlayerName: " + clientName);
				}

			} catch (Exception e) {
				System.out.println("ERROR: Abfragen des ClientNamens fehlgeschlagen!");
			}
		}// end While
		
		//In die Lobby einfügen
		lobby.addUser(clientSocket, clientName);
	}
}
