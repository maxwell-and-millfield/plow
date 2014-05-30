package plow.ui;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import plow.controllers.PlowController;
import plow.model.FileSettings;

import com.cathive.fx.guice.GuiceApplication;
import com.cathive.fx.guice.GuiceFXMLLoader;
import com.cathive.fx.guice.GuiceFXMLLoader.Result;
import com.google.inject.Inject;
import com.google.inject.Module;

/**
 * The Main Class.
 * 
 * @author Maxwell & Millfield
 */
public class Main extends GuiceApplication {

	@Inject
	FileSettings settings;

	@Inject
	GuiceFXMLLoader fxmlLoader;

	@Override
	public void start(final Stage primaryStage) throws IOException {
		primaryStage.setTitle("Plow");
		// plow/ui/Main.fxml contains the Main layout
		final Result result = fxmlLoader.load(getClass().getResource("Main.fxml"));
		final Scene scene = new Scene(result.<Parent> getRoot());
		primaryStage.setScene(scene);
		((PlowController) result.getController()).setScene(scene);
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

	@Override
	public void init(final List<Module> modules) throws Exception {
	}

}
