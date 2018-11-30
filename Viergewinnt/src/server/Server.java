package server;

import java.net.InetAddress;
import java.util.Scanner;

/**
 * Startet das Empfangssocket und arbeitet dann als IO-Thread für den Server.
 */
public class Server {

	public static boolean serverRunning = true;

	public static void main(String[] args) {

		try (Scanner sc = new Scanner(System.in)) {
			System.out.println("Server gestartet.");

			// SERVER-DATEN
			int PORT = 19009;
			InetAddress ADR = InetAddress.getLocalHost();
			System.out.println("Server IP: " + ADR.getHostAddress() + ":" + PORT + "\n");

			String eingabe;

			while (serverRunning) {
				try {
					eingabe = sc.nextLine();
					eingabe.toLowerCase();

					switch (eingabe) {
					case "?":
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
					e.printStackTrace();
					System.out.println("ERROR: Verbindungsaufbau mit Clienten fehlgeschlagen.");
				}
			} // end While

			System.out.println("Server erfolgreich beendet.");

		} catch (Exception e) {
			System.out.println("ERROR: Server abgestürzt!");
		}

	}

	private static void b_showCommands() {
		System.out.println("-------------------------------------------");
		System.out.printf("ServerCommands: %s %s %s", "? - show Commands", "stop/shutdown - shutdown entire server",
				"lobby - show the players current online");
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

	public static void starteWellcomeSocket() {

	}

}
