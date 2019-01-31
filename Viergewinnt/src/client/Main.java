package client;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage joinServerStage) {
		try {
			Pane mainPane = (Pane) FXMLLoader.load(Main.class.getResource("JoinServer.fxml"));
			Scene scene = new Scene(mainPane);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			joinServerStage.setScene(scene);
			joinServerStage.setResizable(false);
			joinServerStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
} 
