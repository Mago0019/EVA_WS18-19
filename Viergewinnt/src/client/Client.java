package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;

/**
 * Hilfsklasse, die alle n�tigen Informationen zwischenspeichert und die
 * Kommunikation mit dem Server �bernimmt.
 */
public class Client extends Thread
{
	private String serverIP;
	private int serverPort;
	private String name;
	private int[][] field; // positiv = player1; negativ = player2; 0 = leeres Feld
	private int width; // falls es sp�ter dynamisch sein soll
	private int hight;
	private int playerNumber; // positiv = player1; negativ = player2
	private LinkedList<String> lobbyList;
	private LinkedList<String> openGames;
	private boolean running;

	// ---- UI ------
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
		running = true;
//		lobbyList.add("Manuel");		//eine defaultliste zum testen
//		lobbyList.add("Patrick");
//		lobbyList.add("Andi");
//		lobbyList.add("Pol");
//		openGames.add("Patricks Game");
//		openGames.add("Manuels Game");
	}

	@Override
	public synchronized void run()
	{
		// Output �bernimmt der Controller, der Input vom Server wird hier verarbeitet.
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream out = new PrintStream(socket.getOutputStream());)
		{

			this.input = in;
			this.output = out;

			updateLists();

			listenToServer();

		} catch (Exception e)
		{
			e.printStackTrace();
		}finally {
			this.gameController.backToLogin();
			try
			{
				socket.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Erste Verbindung mit dem Server wird aufgebaut und IO deklariert. Wird vom
	 * JoinServerController aufgerufen.
	 * 
	 * @param serverIP    IP-Adresse des Servers
	 * @param serverPort  Port des Servers
	 * @param spielerName gew�nschter Name
	 * @return
	 */
	public int serverConnect(String serverIP, int serverPort, String spielerName)
	{
		try
		{
			this.serverIP = serverIP;
			this.serverPort = serverPort;
			this.name = spielerName;
			boolean done = false;

			System.out.println("Server - ip: " + serverIP + ":" + serverPort);
			System.out.println("Player - ip: " + InetAddress.getLocalHost().getHostAddress() + " PlayerName: " + name);
			if (this.socket == null)
			{
				this.socket = new Socket(this.serverIP, this.serverPort); // TODO: vll Port �ndern
				socket.setSoTimeout(15_000);
			}
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream output = new PrintStream(socket.getOutputStream());
			while (!done)
			{
				System.out.println("Warte auf Anfrage vom Server");
				String serverNachricht = input.readLine();
				System.out.println("Nachricht vom Server erhalten: " + serverNachricht);
				switch (serverNachricht)
				{
				case "~~00":
					output.println("~~00" + this.name);
					break;
				case "~~01":
					done = true;
					break;
				case "~~02":
					return 1;
				case "~~03":
					return 2;
				}
			}

			return 0; // Verbinden hat geklappt

		} catch (Exception e)
		{
			System.out.println("Exception e: ");
			e.printStackTrace();
			// Verbinden fehlgeschlagen
			try
			{
				if (socket != null)
				{
					socket.close();
				}
			} catch (IOException e1)
			{
				System.out.println("IOException e1: ");
				e1.printStackTrace();
			} catch (Exception ex)
			{
				System.out.println("Allgemeine Exception beim Socket close");
				ex.printStackTrace();
			}
			return 3;
		}

	}

	private void listenToServer()
	{
		// TODO: darauf warten, dass der Client eine Aktion in der Lobby ausf�hren will
		String msg = null;
		int tryCount = 1;
		int pingCount = 1;
		if (this.socket.isClosed())
		{
			System.out.println("Client-Socket ist closed.");
		}
		while (running && tryCount <= 3)
		{
			try
			{
				// TODO: Marshalling - erste 4 Bytes (Befehlscode) anschauen.
				msg = input.readLine(); // TODO: bricht leider nach ein paar Sek ab, wenn keine Meldung kommt
				if (msg == null || msg.length() < 4)
				{
					continue;
				}
				String order = msg.substring(0, 4);
				String content = msg.substring(4, msg.length());
				LinkedList<String> tempList = new LinkedList<String>();
				if(!order.equals("~~98") || !order.equals("~~99")) {
					System.out.println("Nachricht vom Server erhalten: " + msg);					
				}
				switch (order)
				{

				case "~~10": // update LobbyList
					tempList.clear();
					for (String s : content.split(","))
					{
						tempList.add(s);
					}
					this.lobbyList = tempList;
					setLobbyListView();
					break;

				case "~~11": // update openGames
					tempList.clear();
					for (String s : content.split(","))
					{
						if(!s.equals("")) {
						tempList.add(s);
						}
					}
					this.openGames = tempList;
					setOpenGameView();
					break;

				case "~~20": // Player joined
					playerJoinedGame(content);
					break;

				case "~~21": // Player left
					oponentLeftGame();
					break;

				case "~~30": // Game started
					gameHasStarted();
					break;

				case "~~31": // yourTurn + update Field
					int[][] tempField = new int[7][6];
					String[] contents = content.split(";");
					setYourTurn(Boolean.parseBoolean(contents[0]));
					String[] field = contents[1].split(",");
					int counter = 0;
					for (int row = 0; row < 7; row++)
					{
						for (int collumn = 0; collumn < 6; collumn++)
						{
							tempField[row][collumn] = Integer.parseInt(field[counter]);
							counter++;
						}
					}
					break;

				case "~~32": // Turn-Response
					turnResponse(Boolean.parseBoolean(content));
					break;

				case "~~33": // win/loose
					win_loose(Boolean.parseBoolean(content));
					break;

				case "~~98":
					output.println("~~99");

//					System.out.println("Schicke Nachricht an Server: ~~99");
					break;

				case "~~99":
					pingCount = 1; // zur�cksetzen

				default:
					break;
				}

			} catch (SocketTimeoutException stoe)
			{
				if (pingCount <= 3)
				{
//					System.out.println("Pinge Server an. Versuch: " + pingCount);
//
//					System.out.println("Schicke Nachricht an Server: ~~98");
					this.output.println("~~98"); // Pinge Server an
					pingCount++;
				} else
				{
					running = false;
				}
			} catch (IOException ioe)
			{
				System.out.println("ERROR: keine Antwort von Server. Versuch: " + tryCount);
				tryCount++;
				// ioe.printStackTrace();
			} catch (Exception e)
			{
				System.out.println("DEBUG: Allgemeine Exceptionn: ");
				e.printStackTrace();
			}
		}

	}

	public void updateLists()
	{
		this.output.println("~~70");
		System.out.println("Nachricht an Server geschickt: ~~70");
		this.output.println("~~71");
		System.out.println("Nachricht an Server geschickt: ~~71");
	}

	public String getPlayerName()
	{
		return this.name;
	}

	public int getPlayerNumber()
	{
		return this.playerNumber;
	}

	/* ---Nachrichten an den Server--- */

	public void createGame()
	{
		this.playerNumber = 1;
		this.output.println("~~50");
		System.out.println("Nachricht an Server geschickt: ~~50");
	}

	/**
	 * Die Methode schickt dem Server, dass man einem offenem Spiel beitreten will.
	 * Der Server schickt einem dann einen String zur�ck, welcher den Namen des 1.
	 * Spielers beinhaltet.
	 * 
	 * @return Name Spieler 1
	 */
	public void joinGame(String gameName)
	{
		this.playerNumber = -1;
		this.output.println("~~51" + gameName);
		System.out.println("Nachricht an Server geschickt: ~~51" + gameName);

	}

	/**
	 * Die Methode schickt dem Server eine Nachricht, dass der Client das Spiel
	 * verlassen hat
	 */
	public void leaveYourGame()
	{
		this.output.println("~~52");
		System.out.println("Nachricht an Server geschickt: ~~52");
	}

	public void startGame()
	{
		this.output.println("~~53");
		System.out.println("Nachricht an Server geschickt: ~~53");
	}

	public void surrenderGame()
	{
		this.output.println("~~54");
		System.out.println("Nachricht an Server geschickt: ~~54");
	}

	public void setStone(int collumn)
	{
		this.output.println("~~60" + collumn);
		System.out.println("Nachricht an Server geschickt: ~~60" + collumn);
	}

	public void logout()
	{
		this.output.println("~~80");
		System.out.println("Nachricht an Server geschickt: ~~80");

	}

	/* ---Kommunikation mit dem GameController--- */

	public void setLobbyListView()
	{
		gameController.updateLobby(this.lobbyList);
	}

	public void setOpenGameView()
	{
		gameController.updateOpenGames(this.openGames);
	}

	/**
	 * die Methode wird aufgerufen, wenn der Client vom Server benachrichtigt wird,
	 * dass der Gegenspieler das Spiel verlassen hat.
	 */
	public void oponentLeftGame()
	{
		this.gameController.otherPlayerLeftGame();
	}

	/**
	 * in der Methode wird dem Gamecontroller den namen des 2. Spielers �bergeben,
	 * den der Client zuvor vom Server erhalten hat.
	 */
	public void playerJoinedGame(String name)
	{
		this.gameController.otherPlayerJoinedGame(name);
	}

	public void setYourTurn(boolean yourTurn)
	{
		this.gameController.yourTurn(yourTurn);
	}

	public void turnResponse(boolean correct)
	{
		this.gameController.turnResponse(correct);
	}

	public void gameHasStarted()
	{
		this.gameController.startGame();
	}

	public void win_loose(boolean win)
	{
		this.gameController.winLoose(win);
	}

}
