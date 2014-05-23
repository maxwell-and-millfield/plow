package plow.ui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The Main Class.
 * 
 * @author Maxwell & Millfield
 */
public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		primaryStage.setTitle("Plow");
		// plow/ui/Main.fxml contains the Main layout
		Parent parent = (Parent) FXMLLoader.load(getClass().getResource("Main.fxml"));
		primaryStage.setScene(new Scene(parent));
		primaryStage.show();
	}

	/**
	 * The main program.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Prevent JAudioTagger from dumping thousands of useless INFO and
		// WARNING messages to the log
		Logger.getLogger("org.jaudiotagger").setLevel(Level.SEVERE);

		// calls Main.start() which calls MainController.initialize() and
		// displays the main window
		launch(args);
	}

}
