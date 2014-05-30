package plow.controllers;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.inject.Inject;

import plow.model.FileSettings;

public class SettingsController extends PlowController {

	@FXML
	private TextField musicLibraryFolderTextField, traktorLibraryFileTextField;

	@Inject
	private FileSettings settings;

	@FXML
	public void initialize() {
		musicLibraryFolderTextField.textProperty().bindBidirectional(settings.musicLibraryFolderProperty());
		traktorLibraryFileTextField.textProperty().bindBidirectional(settings.traktorLibraryFileProperty());
	}

	public void browseMusicLibraryFolder() {
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select music library folder");
		directoryChooser.setInitialDirectory(new File(settings.getMusicLibraryFolder()));
		final File file = directoryChooser.showDialog(getScene().getWindow());

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

}
