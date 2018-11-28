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

	public EmpfangsThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
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
				output.print("");

				// TODO: Namen empfangen
				input.readLine();

			} catch (Exception e) {
			}
		}

	}
}
