package server;

import java.net.InetAddress;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

/**
 * Startet das WellcomeSocket und arbeitet dann als IO-Thread für den Server.
 */
public class Server {
	public static boolean serverRunning = true;
	private static WellcomeSocket wellcomeSocket;
	private static ExecutorService tPool; // momentan nicht gebraucht
	private static Lobby lobby;

	public static void main(String[] args) {

		try (Scanner sc = new Scanner(System.in)) {
			System.out.println("Server gestartet.");

			// SERVER-DATEN
			int PORT = 19009;
			InetAddress ADR = InetAddress.getLocalHost();
//			InetAddress ADR = InetAddress.getByName("10.0.3.36");
			System.out.println("Server IP: " + ADR.getHostAddress() + ":" + PORT + "\n");

			lobby = Lobby.getInstance();
			Server.wellcomeSocket = new WellcomeSocket(ADR, PORT);
			wellcomeSocket.start();
			Server.tPool = wellcomeSocket.getThreadPool();

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

					case "info":
						b_showLobby();
						break;

					case "dm":
						System.out.println("s - show current \nt - set true \nf - set false \nanyKey - cancel");
						eingabe = sc.nextLine();
						setDebugMode(eingabe.charAt(0));
						break;

					default:
						System.out.println(" - wrong command -");
						break;
					}

				} catch (Exception e) {
					System.out.println("Server-Error -> " + e);
					e.printStackTrace();

				}
			} // end While
			System.out.println("Server closed.");
			System.exit(0);

		} catch (Exception e) {
			System.out.println("Erver-Error: Server abgestürzt!");
		}

	}

	private static void b_showCommands() {
		System.out.println("-------------------------------------------");
		System.out.printf("ServerCommands: \n %s %s %s %s", "?/help - show Commands\n",
				"stop/shutdown - shutdown entire server\n", "info - show ServerInfo\n", "dm - setDebugMode\n");
		System.out.println("-------------------------------------------");
	}

	private static void b_showLobby() {

		List<Player> pList = lobby.getLobbyList();
		List<GameSession> gList = lobby.getOpenGames();

		System.out.println("Current player online:");
		StringBuilder sb = new StringBuilder("[");
		for (Player p : pList) {
			sb.append(p.name + ", ");
		}
		if (sb.length() > 1)
			sb.delete(sb.length() - 2, sb.length());
		System.out.println(sb.toString() + "]");

		System.out.println("Current open Games:");
		sb = new StringBuilder("[");
		for (GameSession gs : gList) {
			sb.append(gs.gameName + ", ");
		}
		if (sb.length() > 1)
			sb.delete(sb.length() - 2, sb.length());
		System.out.println(sb.toString() + "]");
	}

	/**
	 * Schließt/Beendet der Reihe nach alle Serverseitigen Threads.
	 */
	private static void b_shutdownServer() {
		System.out.println("Shutdown server...");

		// 1. Beende WellcomeSocket -> es werden keine neuen Clients angenommen
		System.out.print("Beende WellcomeSocket...");
		Server.wellcomeSocket.shutdown();
		Server.wellcomeSocket = null;
		System.out.println("...done");

		// 2. Beende Spiele
		System.out.print("Disconnect all clients...");
		for (int i = 0; i < 3; i++) {
			try {
				if (lobby.getLobbyList().size() > 0) { // ist noch jmd eingeloggt?
					Lobby.getInstance().disconnectAllPlayers();
					Thread.sleep(2_000); // 2 Sek warten, dass sich Clienten sauber abmelden können
				} else {
					break; // oder i = 3;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		System.out.println("...done");

		// 3. Lösche Lobby
		System.out.print("Clear Lobby...");
		lobby.clearLobby();
		lobby = null;
		Server.tPool = null;
		System.out.println("...done");
		

		// 4. Beende diesen Server
		System.out.print("Close Server...");
		serverRunning = false;
		System.out.println("...bye");

		/*
		 * // From docs.oracle.com - Interface ExecutorService Server.tPool.shutdown();
		 * // Disable new tasks from being submitted try { // Wait a while for existing
		 * tasks to terminate if (!tPool.awaitTermination(10, TimeUnit.SECONDS)) {
		 * tPool.shutdownNow(); // Cancel currently executing tasks // Wait a while for
		 * tasks to respond to being cancelled if (!tPool.awaitTermination(10,
		 * TimeUnit.SECONDS)) System.err.println("Pool did not terminate"); } } catch
		 * (InterruptedException ie) { // (Re-)Cancel if current thread also interrupted
		 * tPool.shutdownNow(); // Preserve interrupt status
		 * Thread.currentThread().interrupt(); }
		 */
	}

	private static void setDebugMode(char c) {
		if (c == 's') { // show
			System.out.println("Current debugMode: " + Lobby.debugMode);
		} else if (c == 't') { // set true
			System.out.println("DebugMode: " + Lobby.debugMode + " -> true");
			Lobby.debugMode = true;
		} else if (c == 'f') { // set false
			System.out.println("DebugMode: " + Lobby.debugMode + " -> false");
			Lobby.debugMode = false;
		} else {
			System.out.println("Set DebugMode abgebrochen.");
		}
	}

}
