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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SampleController {

	@FXML
	private BorderPane root;
	@FXML
	private HBox hBox;
	@FXML
	private VBox vBox;
	@FXML
	private Button �ffnenB;
	@FXML
	private Button speichernB;
	@FXML
	private Button verschlB;
	@FXML
	private Button entschlB;
	@FXML
	private Label titelLabel;
	@FXML
	private Label ostLabel;
	@FXML
	private TextField SWort;
	@FXML
	private TextArea textArea;
	@FXML
	private VBox topVBox;
	@FXML
	private MenuBar menuBar;
	@FXML
	private Menu menuDatei;
	@FXML
	private Menu menuBearbeiten;
	@FXML
	private Menu menuHilfe;
	@FXML
	private MenuItem mDatei_beenden;
	@FXML
	private MenuItem mBearbeiten_KeyGenerieren;
	@FXML
	private MenuItem mHilfe_ueber;
	@FXML
	private MenuItem mHilfe_hilfe;

	@FXML
	public void event_Verschluesseln(ActionEvent eventVSPressed) {
		try {
			System.out.println("Verschl�sseln ausgel�st.");
			String areaInhalt = textArea.getText();
			char[] key = SWort.getText().toCharArray();
			char[] charArray = areaInhalt.toCharArray();
			if (key.length == 0 || charArray.length == 0) {
				showNoInputAlert();
			} else {
				String verschlText = "";
				int iText = -1;
				int iKey = -1;
				int i = 0;
				char newChar;

				for (char c : charArray) {
					iText = getIndex(c);
					iKey = getIndex(key[i]);
					newChar = getChar((iText + iKey) % 108);
					verschlText += newChar;
					i = (i + 1) % key.length;
				}
				textArea.setText(verschlText);
			}
		} catch (Exception e) {
			System.out.println("ERROR: Verschl�sseln fehgeschlagen!");
			e.printStackTrace();
		}
	}

	@FXML
	public void event_Entschluesseln(ActionEvent eventESPressed) {
		try {
			System.out.println("Entschl�sseln ausgel�st.");
			String areaInhalt = textArea.getText();
			char[] key = SWort.getText().toCharArray();
			char[] charArray = areaInhalt.toCharArray();

			if (key.length == 0 || charArray.length == 0) {
				showNoInputAlert();
			} else {
				String entschlText = "";
				int iText = -1;
				int iKey = -1;
				int i = 0;
				char newChar;

				for (char c : charArray) {
					iText = getIndex(c);
					iKey = getIndex(key[i]);
					newChar = getChar((iText - iKey + 108) % 108);
					entschlText += newChar;
					i = (i + 1) % key.length;
				}
				textArea.setText(entschlText);
			}
		} catch (Exception e) {
			System.out.println("ERROR: Entschl�sseln fehgeschlagen!");
			e.printStackTrace();
		}
	}

	@FXML
	public void event_DateiOeffnen(ActionEvent eventOeffnenPressed) {
		try {
			System.out.println("�ffnen ausgel�st.");

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			File file = fileChooser.showOpenDialog(root.getScene().getWindow());
			// Ich brauche die Stage, um das Fenster zu �ffnen. Das greif ich
			// vom root ab.

			FileReader fReader = new FileReader(file);
			BufferedReader bReader = new BufferedReader(fReader);
			String txtLine = "";
			StringBuilder txtContent = new StringBuilder();
			while ((txtLine = bReader.readLine()) != null) {
				txtContent.append(txtLine + "\n");
			}
			textArea.setText(txtContent.toString());
			fReader.close();
			bReader.close();

		} catch (Exception e) {
			System.out.println("ERROR: �ffnen fehgeschlagen! Keine Datei aufgew�hlt.");
			//e.printStackTrace();
		}
	}

	@FXML
	public void event_DateiSpeichern(ActionEvent eventSpeichernPressed) {
		try {
			System.out.println("Speichern ausgel�st.");

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

		} catch (Exception e) {
			System.out.println("ERROR: Speichern fehgeschlagen! Keine Datei aufgew�hlt.");
			//e.printStackTrace();
		}
	}

	public static int getIndex(char text) {
		String tabelle = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ �������!\"#$%&'()*+,-./0123456789:;<=>?@[\\]^_`�{}|~����";
		// 108 Zeichen mit Z.Umbruch (EditV3.1: noch 11 zugef�gt)
		char zeilenumbruch = 10;
		tabelle += zeilenumbruch;
		// tabelle += System.lineSeparator();
		char[] charTabelle = tabelle.toCharArray();
		for (int i = 0; i < charTabelle.length; i++) {
			if (text == charTabelle[i]) {
				return i;
			}
		}
		// Falls nichts gefunden:
		System.out.println(
				"ERROR: Unbekanntes Zeichen im zu verschl�sselnden Text. Wurde im Schl�sselsatz nicht gefunden: " + text
						+ " Nummer: " + (int) text);
		return 0;
	}

	private static char getChar(int a) {
		String tabelle = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ �������!\"#$%&'()*+,-./0123456789:;<=>?@[\\]^_`�{}|~����";
		// 108 Zeichen mit Z.Umbruch (EditV3.1: noch 11 zugef�gt)
		char zeilenumbruch = 10;
		tabelle += zeilenumbruch;
		// tabelle += System.lineSeparator();
		char[] charTabelle = tabelle.toCharArray();
		return charTabelle[a];
	}

	@FXML
	public void event_Menue_Beenden(ActionEvent eventBeendenPressed) {
		try {
			System.out.println("Programm beenden.");
			Stage stage = (Stage) root.getScene().getWindow();
			stage.close();

		} catch (Exception e) {
			System.out.println("ERROR: Beenden fehgeschlagen!");
			e.printStackTrace();
		}
	}
	
	@FXML
	public void event_Menue_KeyGenerieren(){
		try {
			System.out.println("Key generieren.");
			String generatedKey = "";
			char c;
			String tabelle = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ �������!\"#$%&'()*+,-./0123456789:;<=>?@[\\]^_`�{}|~����";
			for(int i = 0; i<=3; i++){
				c = tabelle.charAt((int) (Math.random()*108));
				generatedKey += c;
			}
			SWort.setText(generatedKey);

		} catch (Exception e) {
			System.out.println("ERROR: Key konnte nicht generiert werden!");
			e.printStackTrace();
		}
	}

	@FXML
	public void event_Menue_Ueber(ActionEvent eventUeberPressed) {
		try {
			System.out.println("Info-Dialog ge�ffnet.");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("�ber");
			alert.setHeaderText("Vigen�re Verschl�sselungsprogramm");
			alert.setContentText(
					"Verschl�sselungsprogramm basierend auf dem Vigen�re-Verfahren.\nVersion 4.0  -  16.05.17\n\nGeschrieben von Patrick Geerds\nProgrammier-Techniken 1 - �B 5 Aufgabe 4 (erweitert)");
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
					"Bei dem Vigen�re-Verfahren wird ein Text mithilfe eines Schl�ssels umgewandelt. Mithilfe desselben Schl�ssels kann der Text wieder lesbar gemacht werden.\n\n- Im Text, als auch im Schl�ssel sind Alle ASCII-Zeichen, als auch Umlaute erlaubt.\n- Sowohl der Text, als auch der Schl�ssel sollten schon eingegeben sein, bevor versucht wird zu Ver- oder Entschl�sseln.\n- Ein Schl�sselwort, das l�nger ist als der eigentliche Text ist wenig sinnvoll.\n- Der Inhalt des Textfelds kann - �ber die unteren Buttons - als Textdatei abgespeichert und wieder geladen werden.");
			alert.show();

		} catch (Exception e) {
			System.out.println("ERROR: Hilfe konnte nicht aufgerufen werden!");
			e.printStackTrace();
		}
	}

	public void showNoInputAlert() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Eingabefehler");
		alert.setHeaderText("Keine Eingabe");
		alert.setContentText("Zum Ver- oder Entschl�sseln geben Sie bitte sowohl einen zu verschl�sselnden Text ein, als auch ein Schl�sselwort.\nSolange nicht beides vorhanden ist, kann das Verfahren nicht durchgef�hrt werden.");
		alert.show();
	}

}