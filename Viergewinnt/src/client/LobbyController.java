package client;

import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class LobbyController
{
	@FXML
	private BorderPane root;
	@FXML
	private HBox hBox;
	@FXML
	private Label titelLabel;
	@FXML
	private Pane Spiel_Pane;
	@FXML
	private TextFlow textFlow;  // NUR EIN TEST! Vll gibt es bessere Textboxen um Nachrichten anzuzeigen
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
	private Label SpielerName_L;
	@FXML
	private Label ServerIP_L;
	@FXML
	private Label ServerPort_L; 
	@FXML
	private TextField ServerPort_TF;
	@FXML
	private TextField SpielerName_TF;
	@FXML
	private TextField ServerIP_TF; 
	@FXML
	private Button joinServer_B;
	@FXML
	public ListView<String> lobby_LV = new ListView<>();
	@FXML
	public ListView<String> gameList_LV = new ListView<>();

	ObservableList<String> lobby = FXCollections.observableArrayList();
		
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
			System.out.println("Info-Dialog ge�ffnet.");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("�ber");
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
			System.out.println("Info-Dialog ge�ffnet.");
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
	public void event_joinServerLobbyPane(ActionEvent klick) {
		try {
			Client client = new Client(this);
			client.serverConnect(ServerIP_TF.getText(), Integer.parseInt(ServerPort_TF.getText()),SpielerName_TF.getText() );
			
		} catch (Exception e) {
			System.out.println("ERROR: Entschl�sseln fehgeschlagen!");
			e.printStackTrace();
		}
	}
	
	@FXML
	public void updateLobbyListView(ActionEvent klick)
	{
		System.out.println("test - lobby_LV");
		for(String s : this.lobby_LV.getItems())
		{
			System.out.println(s);
		}
		
		System.out.println("test - lobby");
		
		for(String s : lobby)
		{
			System.out.println(s);
		}
		this.lobby_LV.refresh();
	}
	
	
	public void updateLobbyListView(LinkedList<String> lobbyList)
	{
		lobby.addAll(lobbyList);
		lobby_LV.setItems(lobby);
		for(String s : this.lobby_LV.getItems())
		{
			System.out.println(s);
		}
		
		
	}
}
