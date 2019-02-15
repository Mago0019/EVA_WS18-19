package client;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameController
{
	@FXML
	private BorderPane root;
	@FXML
	private Label titelLabel;
	@FXML
	private VBox topVBox;
	@FXML
	private VBox openGames_VB;
	@FXML
	private VBox yourGame_VB;
	@FXML
	private VBox activeGame_VB;
	@FXML
	private HBox lobby_HB;
	@FXML
	private MenuBar menuBar;
	@FXML
	private Menu menuDatei;
	@FXML
	private Menu menuHilfe;
	@FXML
	private MenuItem mDatei_einstellungen;
	@FXML
	private MenuItem mDatei_beenden;
	@FXML
	private MenuItem mHilfe_ueber;
	@FXML
	private Label lobby_L;
	@FXML
	private Label openGames_L;
	@FXML
	private Label yourGame_L;
	@FXML
	private Label winLoose_L;
	@FXML
	private Button createGame_B;
	@FXML
	private Button joinGame_B;
	@FXML
	private Button startGame_B;
	@FXML
	private Button leaveYourGame_B;
	@FXML
	private Button surrender_B;
	@FXML
	private Button setStone_B;
	@FXML
	private TextField player1_TF;
	@FXML
	private TextField insertTurn_TF;
	@FXML
	private TextField player2_TF;
	@FXML
	private TextField info_TF;
	@FXML
	private GridPane gameField_GP;
	@FXML
	public ListView<String> lobby_LV = new ListView<>();
	@FXML
	public ListView<String> openGames_LV = new ListView<>();

	private Client client;
	ObservableList<String> lobby;
	ObservableList<String> openGames;

	
	public void initialize()
	{
		lobby = FXCollections.observableArrayList();
		openGames = FXCollections.observableArrayList();
		this.lobby_LV.setItems(lobby);
		this.openGames_LV.setItems(openGames);
		joinGame_B.disableProperty().bind(Bindings.isEmpty(openGames_LV.getSelectionModel().getSelectedItems()));
	}

	@FXML
	public void createGame(ActionEvent klick)
	{
		client.createGame();
		yourGame_L.setText(client.getPlayerName() + "'s Game");
		player1_TF.setText(client.getPlayerName());
		player2_TF.setText(" - ");
		openGames_VB.setVisible(false);
		yourGame_VB.setVisible(true);
		winLoose_L.setVisible(false);
	}

	@FXML
	public void joinGame(ActionEvent klick)
	{
		client.joinGame(openGames_LV.getSelectionModel().getSelectedItem());
		String gameName = openGames_LV.getSelectionModel().getSelectedItem();
		yourGame_L.setText(gameName);
		player1_TF.setText(gameName.substring(0, gameName.length() - 7));
		player2_TF.setText(client.getPlayerName());
		openGames_VB.setVisible(false);
		yourGame_VB.setVisible(true);
		winLoose_L.setVisible(false);
	}


	@FXML
	public void leaveYourGame(ActionEvent klick)
	{
		client.leaveYourGame();
		yourGame_VB.setVisible(false);
		openGames_VB.setVisible(true);
	}

	@FXML
	public void startGame(ActionEvent klick)
	{
		client.startGame();
		lobby_HB.setVisible(false);
		activeGame_VB.setVisible(true);
	}

	@FXML
	public void surrenderGame(ActionEvent klick)
	{
		client.surrenderGame();
	}

	@FXML
	public void setStone(ActionEvent klick)
	{
		try
		{
			int collumn = Integer.parseInt(insertTurn_TF.getText());
			this.client.setStone(collumn);
		} catch (NumberFormatException nfe)
		{
			nfe.printStackTrace();
			this.info_TF.setText("Keine gültige Eingabe.");
		}
		catch (Exception e) {
			System.out.println("Error bei set Stone");
			e.printStackTrace();
		}
	}

	public void turnResponse(boolean correct)
	{
		Platform.runLater(new Runnable()
		{

			@Override
			public void run()
			{
				if (correct)
				{
					setStone_B.setDisable(true);
					info_TF.setText("Gegner ist am Zug.");
				} else
				{
					info_TF.setText("Ungültiger Zug.");
				}
			}

		});
	}

	public void otherPlayerLeftGame()
	{
		Platform.runLater(new Runnable()
		{

			@Override
			public void run()
			{
				switch (client.getPlayerNumber())
				{
				case -1:
					yourGame_VB.setVisible(false);
					openGames_VB.setVisible(true);
					break;
				case 1:
					player2_TF.setText(" - ");
					break;
				}
			}

		});

	}

	public void otherPlayerJoinedGame(String namePlayer2)
	{
		Platform.runLater(new Runnable()
		{

			@Override
			public void run()
			{
				player2_TF.setText(namePlayer2);
				startGame_B.setDisable(false);
			}

		});
	}

	public void yourTurn(boolean yourTurn)
	{
		Platform.runLater(new Runnable()
		{

			@Override
			public void run()
			{
				if (yourTurn)
				{
					setStone_B.setDisable(false);
					info_TF.setText("Du bist am Zug.");
				} else
				{
					setStone_B.setDisable(true);
					info_TF.setText("Gegner ist am Zug.");
				}
			}

		});
	}

	public void winLoose(boolean win)
	{
		Platform.runLater(new Runnable()
		{

			@Override
			public void run()
			{
				if (win)
				{
					winLoose_L.setText("Sie haben Gewonnen :)");
					winLoose_L.setVisible(true);
					activeGame_VB.setVisible(false);
					lobby_HB.setVisible(true);
				} else
				{
					winLoose_L.setText("Sie haben Verloren :(");
					winLoose_L.setVisible(true);
					activeGame_VB.setVisible(false);
					lobby_HB.setVisible(true);
				}
			}

		});
	}

	public void startGame()
	{
		Platform.runLater(new Runnable()
		{

			@Override
			public void run()
			{
				lobby_HB.setVisible(false);
				activeGame_VB.setVisible(true);
			}

		});
	}

	public void updateGamefield(int[][] field)
	{
		Platform.runLater(new Runnable()
		{

			@Override
			public void run()
			{
				Node node = gameField_GP.getChildren().get(0);
				gameField_GP.getChildren().clear();
				gameField_GP.getChildren().add(0, node);
				
				for (int collumn = 0; collumn < field.length; collumn++)
				{
					for (int row = 0; row < field[0].length; row++)
					{
						Circle stone = new Circle();
						stone.setRadius(30.0);
						if (field[collumn][row] > 0)
						{
							stone.setFill(Paint.valueOf("RED"));
							gameField_GP.add(stone, collumn, row);
							gameField_GP.setValignment(stone, VPos.CENTER);
							gameField_GP.setHalignment(stone, HPos.CENTER);
						} else if(field[collumn][row] < 0)
						{
							stone.setFill(Paint.valueOf("BLUE"));
							gameField_GP.add(stone, collumn, row);
							gameField_GP.setValignment(stone, VPos.CENTER);
							gameField_GP.setHalignment(stone, HPos.CENTER);
						}
					}
				}
			}

		});
	}

	public void updateLobby(List<String> list)
	{
		Platform.runLater(new Runnable()
		{

			@Override
			public void run()
			{
				lobby.clear();
				lobby.addAll(list);
			}

		});
	}

	public void updateOpenGames(List<String> list)
	{
		Platform.runLater(new Runnable()
		{

			@Override
			public void run()
			{
				openGames.clear();
				openGames.addAll(list);
			}

		});
	}

	public void setClient(Client client)
	{
		this.client = client;
	}

	@FXML
	public void event_Menue_Beenden(ActionEvent eventBeendenPressed)
	{
		try
		{
			// TODO: vll Fenster mit Abfrage, ob beendet werden soll / ob noch gespeichert
			// werden soll.
			this.client.logout();
			System.out.println("Programm beenden.");
			Stage stage = (Stage) root.getScene().getWindow();
			stage.close();

		} catch (Exception e)
		{
			System.out.println("ERROR: Beenden fehgeschlagen!");
			e.printStackTrace();
		}
	}

	@FXML
	public void event_Menue_Ueber(ActionEvent eventUeberPressed)
	{
		try
		{
			System.out.println("Info-Dialog geöffnet.");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Über");
			alert.setHeaderText("VierGewinnt");
			alert.setContentText(
					"VierGewinnt 2D\nVersion 3.3  -  28.11.18\n\nGeschrieben von Patrick Geerds und Manuel Golz\nEntwicklung-Verteilter-Systeme im WS 18/19");
			alert.show();

		} catch (Exception e)
		{
			System.out.println("ERROR: Infofenster konnte nicht aufgerufen werden!");
			e.printStackTrace();
		}
	}

	@FXML
	public void event_Menue_Hilfe(ActionEvent eventHilfePressed)
	{
		try
		{
			System.out.println("Info-Dialog geöffnet.");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Hilfe");
			alert.setHeaderText("Eine kleine Anleitung");
			alert.setContentText(
					"How To:\n- Chip in eine Spalte werfen (fallen immer bis nach unten) \n\nGewinnen: \n- Wer 4 Chips in einer Reihe/Spalte/Diagonale liegen hat, gewinnt.");
			alert.show();

		} catch (Exception e)
		{
			System.out.println("ERROR: Hilfe konnte nicht aufgerufen werden!");
			e.printStackTrace();
		}
	}
	
	public void backToLogin() {
		Platform.runLater(new Runnable()
		{

			@Override
			public void run()
			{
				try {
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/JoinServer.fxml"));
					Pane mainPane = fxmlLoader.load();
					Scene scene = new Scene(mainPane); // hat mal NullPointer geworfen
					
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					Stage loginStage = (Stage) root.getScene().getWindow();
					loginStage.setScene(scene);
					loginStage.show();
				}catch(IOException ioe) {
					ioe.printStackTrace();			
				}
			}

		});
	}
	
	public void closeWindowEvent(WindowEvent event) {
		this.client.logout();
	}
	public void setCloseWindowEvent() {
		root.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
	}

}
