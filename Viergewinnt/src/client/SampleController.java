package client;
/**
 * EVA WS 18/19
 * Patrick Geerds
 * Manuel G.
 * 
 * Version 1.0
 * 16.05.17
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SampleController {

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
	private ListView<String> lobby_LV;
	@FXML
	private ListView<String> gameList_LV;

	

	@FXML
	public void event_ChipEinwerfen(ActionEvent eventVSPressed) {
		try {
			/* TODO: 
			 * - Textfeld für Zahleingabe
			 * - Button zum Abschicken
			 * - Überprüfen ob Eingabe korrekt
			 * - an Server übermitteln, wo der Chip rein soll
			*/
				
			
		} catch (Exception e) {
			System.out.println("ERROR: Verschlüsseln fehgeschlagen!");
			e.printStackTrace();
		}
	}

	@FXML
	public void event_updateField(ActionEvent eventSpielfeldUpdate) {
		try {
			/* TODO:
			 * - Getriggert, wenn antwort vom Server
			 * - aktualisiert Spielfeld 
			*/
			
		} catch (Exception e) {
			System.out.println("ERROR: Entschlüsseln fehgeschlagen!");
			e.printStackTrace();
		}
	}

	@FXML
	public void event_DateiOeffnen(ActionEvent eventOeffnenPressed) {
		try {
			System.out.println("Öffnen ausgelöst.");

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			File file = fileChooser.showOpenDialog(root.getScene().getWindow());
			// Ich brauche die Stage, um das Fenster zu öffnen. Das greif ich
			// vom root ab.

			FileReader fReader = new FileReader(file);
			BufferedReader bReader = new BufferedReader(fReader);
			String txtLine = "";
			StringBuilder txtContent = new StringBuilder();
			while ((txtLine = bReader.readLine()) != null) {
				txtContent.append(txtLine + "\n");
			}
			//textArea.setText(txtContent.toString());
			fReader.close();
			bReader.close();

		} catch (Exception e) {
			System.out.println("ERROR: Öffnen fehgeschlagen! Keine Datei aufgewählt.");
			//e.printStackTrace();
		}
	}

	@FXML
	public void event_DateiSpeichern(ActionEvent eventSpeichernPressed) {
		try {
	/*		System.out.println("Speichern ausgelöst.");

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save Resource File");
			File file = fileChooser.showOpenDialog(root.getScene().getWindow());

			String txtFromTArea = textArea.getText();
			// System.out.println(txtFromTArea);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write(txtFromTArea);

			fWriter.flush();
			bWriter.flush();
			fWriter.close();
			bWriter.close();
*/
		} catch (Exception e) {
			System.out.println("ERROR: Speichern fehgeschlagen! Keine Datei aufgewählt.");
			//e.printStackTrace();
		}
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
					"VierGewinnt 2D\nVersion 0.1  -  28.11.18\n\nGeschrieben von Patrick Geerds und Manuel Golz\nEntwicklung-Verteilter-Systeme im WS 18/19");
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

	public void showNoInputAlert() { // TODO: Kann man vll für falsche eingabe wiederverwerten/zum Abfragen, ob beendet werden soll.
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Eingabefehler");
		alert.setHeaderText("Keine Eingabe");
		alert.setContentText("Zum Ver- oder Entschlüsseln geben Sie bitte sowohl einen zu verschlüsselnden Text ein, als auch ein Schlüsselwort.\nSolange nicht beides vorhanden ist, kann das Verfahren nicht durchgeführt werden.");
		alert.show();
	}

	
	@FXML
	public void event_joinServer(ActionEvent klick) {
		try {
			Client client = new Client(this);
			client.serverConnect(ServerIP_TF.getText(), Integer.parseInt(ServerPort_TF.getText()),SpielerName_TF.getText() );
			try {
				Pane mainPane = (Pane) FXMLLoader.load(Main.class.getResource("Test.fxml"));
				Scene scene = new Scene(mainPane);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				Stage stage = (Stage)((Node)klick.getSource()).getScene().getWindow();
				stage.setScene(scene);
				stage.show();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			System.out.println("ERROR: Entschlüsseln fehgeschlagen!");
			e.printStackTrace();
		}
	}
	@FXML
	public void event_joinServerLobbyPane(ActionEvent klick) {
		try {
			Client client = new Client(this);
			
//			Client.lobby.add("Patrick");
//			Client.lobby.add("Manuel");
//			Client.lobby.add("Lukas");
//			Client.lobby.add("Gregor");
//			Client.lobby.add("Thomas");
//			Client.lobby.add("Nadine");
//			
//			
//			lobby_LV.setItems((ObservableList<String>)Client.lobby);
			client.serverConnect(ServerIP_TF.getText(), Integer.parseInt(ServerPort_TF.getText()),SpielerName_TF.getText() );

			
		} catch (Exception e) {
			System.out.println("ERROR: Entschlüsseln fehgeschlagen!");
			e.printStackTrace();
		}
	}
}