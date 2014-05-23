package plow.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.jaudiotagger.tag.FieldKey;

import plow.libraries.DirectoryScanner;
import plow.libraries.TraktorLibraryWriter;
import plow.model.ArgumentSettings;
import plow.model.Playlist;
import plow.model.Settings;
import plow.model.Track;
import plow.model.TrackId3TagValueFactory;
import plow.ui.EditingCell;

/**
 * The Main Controller. It initializes the Models and Views for Main.fxml.
 * 
 * @author Maxwell & Millfield
 */
public class MainController implements Initializable {

	@FXML
	private ListView<Playlist> playlistsView;

	@FXML
	private TableColumn<Track, String> titleColumn, artistColumn, filenameColumn;

	@FXML
	private TableView<Track> tracksTable;

	private ObservableList<Playlist> playlists;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		playlistsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		playlistsView.getSelectionModel().selectedItemProperty().addListener(playlistChangeListener);

		titleColumn.setCellValueFactory(new TrackId3TagValueFactory(FieldKey.TITLE));
		artistColumn.setCellValueFactory(new TrackId3TagValueFactory(FieldKey.ARTIST));
		filenameColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("filenameWithPrefix"));

		// make the Table editable, by placing TextFields in the Cells
		titleColumn.setCellFactory(EditingCell.<Track> getCellFactory());
		artistColumn.setCellFactory(EditingCell.<Track> getCellFactory());

		tracksTable.getSortOrder().add(titleColumn);

		// Display a spinner as placeholder while the playlists load
		playlistsView.setPlaceholder(null);
		ProgressIndicator spinner = new ProgressIndicator();
		spinner.setMaxHeight(50);
		tracksTable.setPlaceholder(spinner);

		// Load the playlists!
		initializeModels();
	}

	private void initializeModels() {
		Settings settings = new ArgumentSettings();

		// TODO: Init Traktor stuff in background
		TraktorLibraryWriter tw = new TraktorLibraryWriter();
		tw.writeToTraktorLibrary(settings.getTraktorLibraryPath(), settings.getLibraryPath(), null);

		// Scan the Library path and load all tracks and playlists
		new Thread(new LoadPlaylistsTask(settings.getLibraryPath())).start();
	}

	private final ChangeListener<Playlist> playlistChangeListener = new ChangeListener<Playlist>() {

		@Override
		public void changed(ObservableValue<? extends Playlist> observable, Playlist old, Playlist selected) {
			tracksTable.setPlaceholder(new Label("No tracks in \"" + selected.getName() + "\"."));
			tracksTable.setItems(selected.getTracks());

			// manually sort the table, elsewise the rows are unsorted even
			// though a sort order may still be indicated in the table header
			// #FunWithJava
			tracksTable.sort();
		}

	};

	private class LoadPlaylistsTask extends Task<ObservableList<Playlist>> {

		private String path;

		public LoadPlaylistsTask(String path) {
			super();
			this.path = path;
		}

		@Override
		protected ObservableList<Playlist> call() throws Exception {
			DirectoryScanner ds = new DirectoryScanner();
			return FXCollections.observableArrayList(ds.scanDirectory(path));
		}

		@Override
		protected void succeeded() {
			playlists = getValue();

			playlistsView.setItems(playlists);
			playlistsView.setPlaceholder(new Label("No playlists found."));

			if (!playlists.isEmpty()) {
				// display the first playlist if possible
				playlistsView.getSelectionModel().select(0);
			}
		}
	};

}