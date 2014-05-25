package plow.ui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import plow.controllers.PlowController;

/**
 * The Main Class.
 * 
 * @author Maxwell & Millfield
 */
public class Main extends Application {

	@Override
	public void start(final Stage primaryStage) throws IOException {
		primaryStage.setTitle("Plow");
		// plow/ui/Main.fxml contains the Main layout
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
		final Scene scene = new Scene((Parent) loader.load());

		primaryStage.setScene(scene);
		((PlowController) loader.getController()).setScene(scene);
		primaryStage.show();

	}

	/**
	 * The main program.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		// Prevent JAudioTagger from dumping thousands of useless INFO and
		// WARNING messages to the log
		Logger.getLogger("org.jaudiotagger").setLevel(Level.SEVERE);

		// calls Main.start() which calls MainController.initialize() and
		// displays the main window
		launch(args);
	}

}
