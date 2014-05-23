package plow.ui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		primaryStage.setTitle("Plow");
		Parent parent = (Parent) FXMLLoader.load(getClass().getResource("Main.fxml"));
		primaryStage.setScene(new Scene(parent));
		primaryStage.show();
	}

	public static void main(String[] args) {
		// Prevent JAudioTagger from dumping thousands of useless INFO and
		// WARNING messages to the log
		Logger.getLogger("org.jaudiotagger").setLevel(Level.SEVERE);

		// calls Main.start() and displays the main window
		launch(args);
	}

}
