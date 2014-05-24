package plow.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import plow.libraries.MusicLibrary;
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

	@FXML private ListView<Playlist> playlistsView;

	@FXML private TableColumn<Track, String> titleColumn, artistColumn, filenameColumn;

	@FXML private TableView<Track> tracksTable;

	private ObservableList<Playlist> playlists;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		playlistsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		playlistsView.getSelectionModel().selectedItemProperty().addListener(playlistChangeListener);

		titleColumn.setCellValueFactory(new TrackId3TagValueFactory(FieldKey.TITLE));
		artistColumn.setCellValueFactory(new TrackId3TagValueFactory(FieldKey.ARTIST));
		filenameColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("filenameWithPrefix"));

		// make the Table editable, by placing TextFields in the Cells
		titleColumn.setCellFactory(EditingCell.<Track> getCellFactory());
		artistColumn.setCellFactory(EditingCell.<Track> getCellFactory());

		tracksTable.getSortOrder().add(titleColumn);

		playlistsView.setPlaceholder(new Label("No playlists found."));

		// Display a spinner as placeholder while the playlists load
		playlistsView.setPlaceholder(null);
		final ProgressIndicator spinner = new ProgressIndicator();
		spinner.setMaxHeight(50);
		tracksTable.setPlaceholder(spinner);
		// Load the playlists!
		initializeModels();
	}

	private void initializeModels() {
		final Settings settings = new ArgumentSettings();
		lib = new MusicLibrary();
		lib.setLibrary(settings.getLibraryPath());
		lib.setTraktorLibrary(settings.getTraktorLibraryPath());
		playlistsView.setItems(lib.getPlaylists());
		// TODO: Init Traktor stuff in background
		final TraktorLibraryWriter tw = new TraktorLibraryWriter();
		tw.writeToTraktorLibrary(settings.getTraktorLibraryPath(), settings.getLibraryPath(), null);
		new Thread(new LoadPlaylistsTask()).start();
	}

	private final ChangeListener<Playlist> playlistChangeListener = new ChangeListener<Playlist>() {

		@Override
		public void changed(final ObservableValue<? extends Playlist> observable, final Playlist old,
				final Playlist selected) { //
			tracksTable.setPlaceholder(new Label("No tracks in \"" + //
					selected.getName() + "\".")); //
			tracksTable.setItems(selected.getTracks());

			// manually sort the table, elsewise the rows are unsorted even
			// though a sort order may still be indicated in the table header
			// #FunWithJava
			tracksTable.sort();
		}

	};

	private MusicLibrary lib;

	private class LoadPlaylistsTask extends Task<Boolean> {

		@Override
		protected Boolean call() throws Exception {
			final DirectoryScanner ds = new DirectoryScanner();
			ds.synchronizeLibrary(lib);
			return true;
		}

		@Override
		protected void succeeded() {
			// select the first element if possible
			if (!playlistsView.getItems().isEmpty() && playlistsView.getSelectionModel().isEmpty()) {
				playlistsView.getSelectionModel().select(0);
			}

		}
	};

}