package plow.controllers;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import plow.model.FileSettings;
import plow.ui.Main;

public class SettingsController extends PlowController {

	@FXML
	private TextField musicLibraryFolderTextField, traktorLibraryFileTextField;

	private FileSettings settings;

	@FXML
	public void initialize() {
		settings = new FileSettings();

		musicLibraryFolderTextField.textProperty().bind(settings.musicLibraryFolderProperty());
		traktorLibraryFileTextField.textProperty().bind(settings.traktorLibraryFileProperty());
	}

	public void browseMusicLibraryFolder() {
		final DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("Select music library folder");
		fileChooser.setInitialDirectory(new File(settings.getMusicLibraryFolder()));
		final File file = fileChooser.showDialog(getScene().getWindow());

		if (file != null) {
			settings.musicLibraryFolderProperty().set(file.getAbsolutePath());
		}
	}

	public void browseTraktorLibraryFile() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select music library folder");
		fileChooser.setInitialDirectory(new File(settings.getTraktorLibraryFile()).getParentFile());
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Traktor Collection Files", "*.nml"));
		final File file = fileChooser.showOpenDialog(getScene().getWindow());

		if (file != null) {
			settings.traktorLibraryFileProperty().set(file.getAbsolutePath());
		}
	}

	public void close() {
		getScene().getWindow().hide();
	}

	public static void start(final Stage stage) {
		stage.setTitle("Settings");
		final FXMLLoader loader = new FXMLLoader(Main.class.getResource("Settings.fxml"));

		try {
			final Scene scene = new Scene((Parent) loader.load());
			((PlowController) loader.getController()).setScene(scene);
			stage.setScene(scene);
			stage.show();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

	}

}
