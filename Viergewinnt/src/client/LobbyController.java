package client;

import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LobbyController
{
	@FXML
	private BorderPane root;
	@FXML
	private Label titelLabel;
	@FXML
	private VBox topVBox;
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
	private Button createGame_B;
	@FXML
	private Button joinGame_B;
	@FXML
	private ListView<String> lobby_LV = new ListView<>();
	@FXML
	private ListView<String> openGames_LV = new ListView<>();

	ObservableList<String> lobby;
	ObservableList<String> openGames;
	private Client client;
		

	public void initialize() {
		lobby = FXCollections.observableArrayList();
		openGames = FXCollections.observableArrayList();
		this.lobby_LV.setItems(lobby);
		this.openGames_LV.setItems(openGames);
	}
	
	@FXML
	public void event_Menue_Beenden(ActionEvent eventBeendenPressed) {
		try {
			// TODO: vll Fenster mit Abfrage, ob beendet werden soll / ob noch gespeichert werden soll.
			
			System.out.println("Programm beenden.");
			Stage stage = (Stage) root.getScene().getWindow();
			stage.close();

		} catch (Exception e) {
			System.out.println("ERROR: Beenden fehgeschlagen!");
			e.printStackTrace();
		}
	}
	
	@FXML
	public void event_Menue_Ueber(ActionEvent eventUeberPressed) {
		try {
			System.out.println("Info-Dialog geöffnet.");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Über");
			alert.setHeaderText("VierGewinnt");
			alert.setContentText(
					"VierGewinnt 2D\nVersion 0.3  -  28.11.18\n\nGeschrieben von Patrick Geerds und Manuel Golz\nEntwicklung-Verteilter-Systeme im WS 18/19");
			alert.show();

		} catch (Exception e) {
			System.out.println("ERROR: Infofenster konnte nicht aufgerufen werden!");
			e.printStackTrace();
		}
	}

	@FXML
	public void event_Menue_Hilfe(ActionEvent eventHilfePressed) {
		try {
			System.out.println("Info-Dialog geöffnet.");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Hilfe");
			alert.setHeaderText("Eine kleine Anleitung");
			alert.setContentText(
					"How To:\n- Chip in eine Spalte werfen (fallen immer bis nach unten) \n\nGewinnen: \n- Wer 4 Chips in einer Reihe/Spalte/Diagonale liegen hat, gewinnt.");
			alert.show();

		} catch (Exception e) {
			System.out.println("ERROR: Hilfe konnte nicht aufgerufen werden!");
			e.printStackTrace();
		}
	}
	
	@FXML
	public void createGame(ActionEvent klick) {
		
	}
	
	@FXML
	public void joinGame(ActionEvent klick) {
//		lobby.setAll(client.lobbyList);
//		lobby_LV.setItems(lobby);
	}
	
	@FXML
	public void startGame(ActionEvent klick) {
		
	}
	
	public void updateLobbyList(LinkedList<String> list) {
		this.lobby.clear();
		this.lobby.addAll(list);
	}
	
	public void updateOpenGames(LinkedList<String> list) {
		this.openGames.clear();
		this.openGames.addAll(list);
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
}
