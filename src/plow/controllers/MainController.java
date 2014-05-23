package plow.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		playlistsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		playlistsView.getSelectionModel().selectedItemProperty().addListener(playlistChangeListener);
		
		titleColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("title"));
		artistColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("artist"));
		filenameColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("filename"));
		
		// Only display as spinner as placeholder, while the playlists load
		playlistsView.setPlaceholder(null);
		ProgressIndicator spinner = new ProgressIndicator();
		spinner.setMaxHeight(50);
		tracksTable.setPlaceholder(spinner);
		
		// Load the playlists!
		initializeModels();
	}
	
	private void initializeModels() {
		loadPlaylistsTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				playlistsView.setItems(loadPlaylistsTask.getValue());
				playlistsView.setPlaceholder(new Label("No playlists found."));
				
				if(!playlistsView.getItems().isEmpty()) {
					// display the first playlist if possible
					playlistsView.getSelectionModel().select(0);
				}
			}
		});
		
		new Thread(loadPlaylistsTask).start();
	}

	private final ChangeListener<Playlist> playlistChangeListener = new ChangeListener<Playlist>() {
		@Override
		public void changed(ObservableValue<? extends Playlist> observable, Playlist old, Playlist selected) {
			tracksTable.setPlaceholder(new Label("No tracks in \"" + selected.getName() + "\"."));
			tracksTable.setItems(selected.getTracks());
		}
	};
	
	private final Task<ObservableList<Playlist>> loadPlaylistsTask = new Task<ObservableList<Playlist>>() {

		@Override
		protected ObservableList<Playlist> call() throws Exception {
			Settings settings = new ArgumentSettings();
			TraktorLibraryWriter tw = new TraktorLibraryWriter();
			tw.writeToTraktorLibrary(settings.getTraktorLibraryPath(), settings.getLibraryPath(), null);
			DirectoryScanner ds = new DirectoryScanner();

			return FXCollections.observableArrayList(ds.scanDirectory(settings.getLibraryPath()));
		}
	};

}
