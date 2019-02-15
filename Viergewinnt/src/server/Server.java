package server;

import java.net.InetAddress;
import java.util.Scanner;

/**
 * Startet das WellcomeSocket und arbeitet dann als IO-Thread für den Server.
 */
public class Server {
	public static boolean serverRunning = true;	
	public static void main(String[] args) {

		try (Scanner sc = new Scanner(System.in)) {
			System.out.println("Server gestartet.");

			// SERVER-DATEN
			int PORT = 19009;
			InetAddress ADR = InetAddress.getLocalHost();
			//InetAddress ADR = InetAddress.getByName("10.0.3.36");
			System.out.println("Server IP: " + ADR.getHostAddress() + ":" + PORT + "\n");
			
			WellcomeSocket wellcomeSocket = new WellcomeSocket(ADR, PORT);
			wellcomeSocket.start();
			
			b_showCommands();
			
			String eingabe;

			while (serverRunning) {
				try {
					eingabe = sc.nextLine();
					eingabe.toLowerCase();

					switch (eingabe) {
					case "?":
					case "help":
						b_showCommands();
						break;

					case "stop":
					case "shutdown":
						b_shutdownServer();
						break;

					case "lobby":
						b_showLobby();
						break;

					default:
							System.out.println(" - wrong command -");
						break;
					}

				} catch (Exception e) {
					if(e.getMessage() != null) {
						System.out.println("Server-Error -> " + e.getMessage());						
					}
				}
			} // end While
			System.out.println("Server beendet.");

		} catch (Exception e) {
			System.out.println("Erver-Error: Server abgestürzt!");
		}

	}

	private static void b_showCommands() {
		System.out.println("-------------------------------------------");
		System.out.printf("ServerCommands: %s %s %s", "?/help - show Commands\n", "stop/shutdown - shutdown entire server\n",
				"lobby - show the players current online\n");
		System.out.println("-------------------------------------------");
	}
	
	private static void b_showLobby() {
		
		System.out.println("");
	} 
	
	private static void b_shutdownServer() {
		// 1. Beende WellcomeSocket
		// TODO

		// 2. Beende Spiele

		// 3. Beende Lobby

		// 4. Beende diesen Server
		serverRunning = false;
	}

}
