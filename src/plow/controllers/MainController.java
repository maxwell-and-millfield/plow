package plow.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import plow.libraries.DirectoryScanner;
import plow.libraries.TraktorLibraryWriter;
import plow.model.ArgumentSettings;
import plow.model.Playlist;
import plow.model.Settings;
import plow.model.Track;

public class MainController implements Initializable {

	@FXML
	private ListView<Playlist> playlistsView;

	@FXML
	private TableColumn<Track, String> titleColumn, artistColumn, filenameColumn;

	@FXML
	private TableView<Track> tracksTable;

	private ObservableList<Playlist> playlists;

	private void initializeModels() {
		Settings settings = new ArgumentSettings();
		TraktorLibraryWriter tw = new TraktorLibraryWriter();
		tw.writeToTraktorLibrary(settings.getTraktorLibraryPath(), settings.getLibraryPath(), null);
		DirectoryScanner ds = new DirectoryScanner();

		playlists = FXCollections.observableArrayList(ds.scanDirectory(settings.getLibraryPath()));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeModels();
		
		playlistsView.setItems(playlists);
		playlistsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		playlistsView.getSelectionModel().selectedItemProperty().addListener(playlistChangeListener);

		titleColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("title"));
		artistColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("artist"));
		filenameColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("filename"));

		if (playlists.size() > 0) {
			playlistsView.getSelectionModel().select(0);
		}
	}

	private final ChangeListener<Playlist> playlistChangeListener = new ChangeListener<Playlist>() {
		@Override
		public void changed(ObservableValue<? extends Playlist> observable, Playlist old, Playlist selected) {
			tracksTable.setItems(selected.getTracks());
		}
	};

}
