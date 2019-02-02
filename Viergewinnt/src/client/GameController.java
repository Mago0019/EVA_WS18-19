package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

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
	private GridPane gameField_GP;
	@FXML
	public ListView<String> lobby_LV = new ListView<>();
	@FXML
	public ListView<String> openGames_LV = new ListView<>();

	ObservableList<String> lobby = FXCollections.observableArrayList();
	ObservableList<String> openGames = FXCollections.observableArrayList();
	private Client client;
		
	
	
	@FXML
	public void createGame(ActionEvent klick) {
		yourGame_L.setText(client.getName() + "s Game");
		player1_TF.setText(client.getName());
		openGames_VB.setVisible(false);
		yourGame_VB.setVisible(true);		
		winLoose_L.setVisible(false);
	}
	
	@FXML
	public void joinGame(ActionEvent klick) {
		player2_TF.setText(client.getName());
		openGames_VB.setVisible(false);
		yourGame_VB.setVisible(true);
		winLoose_L.setVisible(false);
	}
	
	@FXML
	public void leaveYourGame(ActionEvent klick) {
		yourGame_VB.setVisible(false);	
		openGames_VB.setVisible(true);
	}	
	
	@FXML
	public void startGame(ActionEvent klick) {
		lobby_HB.setVisible(false);
		activeGame_VB.setVisible(true);
	}
	
	@FXML
	public void surrenderGame(ActionEvent klick) {
		winLoose_L.setText("Sie haben Verloren :(");
		winLoose_L.setVisible(true);
		activeGame_VB.setVisible(false);
		lobby_HB.setVisible(true);
	}
	
	@FXML
	public void setStone(ActionEvent klick) {
		int collumn = Integer.parseInt(insertTurn_TF.getText());
		
		if(collumn < 1 || collumn > 7) {
			//Todo error Meldung
		}
		else {
			Circle stone = new Circle();
			stone.setFill(Paint.valueOf("RED"));
			stone.setRadius(30.0);
			gameField_GP.add(stone, collumn-1, 6);
			gameField_GP.setValignment(stone,VPos.CENTER);
			gameField_GP.setHalignment(stone, HPos.CENTER);
			
		}
		
	}
	
	

	public void setClient(Client client) {
		this.client = client;
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
}
