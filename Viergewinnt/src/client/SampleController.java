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
	private Button öffnenB;
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
			System.out.println("Verschlüsseln ausgelöst.");
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
			System.out.println("ERROR: Verschlüsseln fehgeschlagen!");
			e.printStackTrace();
		}
	}

	@FXML
	public void event_Entschluesseln(ActionEvent eventESPressed) {
		try {
			System.out.println("Entschlüsseln ausgelöst.");
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
			textArea.setText(txtContent.toString());
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
			System.out.println("Speichern ausgelöst.");

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
			System.out.println("ERROR: Speichern fehgeschlagen! Keine Datei aufgewählt.");
			//e.printStackTrace();
		}
	}

	public static int getIndex(char text) {
		String tabelle = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ äöüÄÖÜß!\"#$%&'()*+,-./0123456789:;<=>?@[\\]^_`´{}|~°²³€";
		// 108 Zeichen mit Z.Umbruch (EditV3.1: noch 11 zugefügt)
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
				"ERROR: Unbekanntes Zeichen im zu verschlüsselnden Text. Wurde im Schlüsselsatz nicht gefunden: " + text
						+ " Nummer: " + (int) text);
		return 0;
	}

	private static char getChar(int a) {
		String tabelle = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ äöüÄÖÜß!\"#$%&'()*+,-./0123456789:;<=>?@[\\]^_`´{}|~°²³€";
		// 108 Zeichen mit Z.Umbruch (EditV3.1: noch 11 zugefügt)
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
			String tabelle = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ äöüÄÖÜß!\"#$%&'()*+,-./0123456789:;<=>?@[\\]^_`´{}|~°²³€";
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
			System.out.println("Info-Dialog geöffnet.");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Über");
			alert.setHeaderText("Vigenère Verschlüsselungsprogramm");
			alert.setContentText(
					"Verschlüsselungsprogramm basierend auf dem Vigenère-Verfahren.\nVersion 4.0  -  16.05.17\n\nGeschrieben von Patrick Geerds\nProgrammier-Techniken 1 - ÜB 5 Aufgabe 4 (erweitert)");
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
					"Bei dem Vigenère-Verfahren wird ein Text mithilfe eines Schlüssels umgewandelt. Mithilfe desselben Schlüssels kann der Text wieder lesbar gemacht werden.\n\n- Im Text, als auch im Schlüssel sind Alle ASCII-Zeichen, als auch Umlaute erlaubt.\n- Sowohl der Text, als auch der Schlüssel sollten schon eingegeben sein, bevor versucht wird zu Ver- oder Entschlüsseln.\n- Ein Schlüsselwort, das länger ist als der eigentliche Text ist wenig sinnvoll.\n- Der Inhalt des Textfelds kann - über die unteren Buttons - als Textdatei abgespeichert und wieder geladen werden.");
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
		alert.setContentText("Zum Ver- oder Entschlüsseln geben Sie bitte sowohl einen zu verschlüsselnden Text ein, als auch ein Schlüsselwort.\nSolange nicht beides vorhanden ist, kann das Verfahren nicht durchgeführt werden.");
		alert.show();
	}

}