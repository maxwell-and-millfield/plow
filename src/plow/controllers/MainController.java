package plow.controllers;

import java.nio.file.Paths;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.WindowEvent;

import org.jaudiotagger.tag.FieldKey;

import plow.libraries.DirectoryScanner;
import plow.libraries.LibraryWriter;
import plow.libraries.MusicLibrary;
import plow.libraries.TraktorLibraryWriter;
import plow.model.ArgumentSettings;
import plow.model.Playlist;
import plow.model.Settings;
import plow.model.Track;
import plow.model.TrackId3TagValueFactory;

/**
 * The Main Controller. It initializes the Models and Views for Main.fxml.
 * 
 * @author Maxwell & Millfield
 */
public class MainController extends PlowController {

	@FXML private ListView<Playlist> playlistsView;

	@FXML private TableColumn<Track, String> titleColumn, artistColumn, filenameColumn;

	@FXML private TableView<Track> tracksTable;

	@FXML private Label backgroundLabel;

	@FXML private ProgressIndicator backgroundIndicator;

	private final LibraryWriter libWriter = new LibraryWriter();
	private MusicLibrary lib;

	@FXML
	public void initialize() {
		playlistsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		playlistsView.getSelectionModel().selectedItemProperty().addListener(playlistChangeListener);

		titleColumn.setCellValueFactory(new TrackId3TagValueFactory(FieldKey.TITLE));
		artistColumn.setCellValueFactory(new TrackId3TagValueFactory(FieldKey.ARTIST));
		filenameColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("filenameWithPrefix"));

		// make the Table editable, by placing TextFields in the Cells
		titleColumn.setCellFactory(TextFieldTableCell.<Track> forTableColumn());
		artistColumn.setCellFactory(TextFieldTableCell.<Track> forTableColumn());

		tracksTable.getSortOrder().add(titleColumn);

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
		lib = libWriter.load(Paths.get("library.plow"));
		// lib.setLibrary(settings.getLibraryPath());
		// lib.setTraktorLibrary(settings.getTraktorLibraryPath());
		playlistsView.setItems(lib.getPlaylists());
		if (!playlistsView.getItems().isEmpty() && playlistsView.getSelectionModel().isEmpty()) {
			playlistsView.getSelectionModel().select(0);
		}
		// TODO: Init Traktor stuff in background
		final TraktorLibraryWriter tw = new TraktorLibraryWriter();
		tw.writeToTraktorLibrary(settings.getTraktorLibraryPath(), settings.getLibraryPath(), null);
		executeBackgroundTask(new LoadPlaylistsTask());
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

	private class LoadPlaylistsTask extends Task<Boolean> {

		@Override
		protected Boolean call() throws Exception {
			updateMessage("Checking Consistency");
			final DirectoryScanner ds = new DirectoryScanner();
			ds.synchronizeLibrary(lib);
			return true;
		}

		@Override
		protected void succeeded() {

		}
	};

	public void close() {
		playlistsView.getScene().getWindow().hide();
	}

	public void saveMusicLibrary() {
		executeBackgroundTask(new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				updateMessage("Saving...");
				libWriter.save(lib, Paths.get("library.plow"));
				return true;
			}

		});

	}

	@Override
	public void setScene(final Scene scene) {
		super.setScene(scene);
		scene.getWindow().setOnHiding(new EventHandler<WindowEvent>() {

			@Override
			public void handle(final WindowEvent event) {
				System.out.println("hidde,");
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void executeBackgroundTask(@SuppressWarnings("rawtypes") final Task t) {
		backgroundLabel.textProperty().bind(t.messageProperty());
		t.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(final ObservableValue<? extends State> observable, final State oldValue,
					final State newValue) {
				backgroundIndicator.setVisible(newValue == State.RUNNING);
				backgroundLabel.setVisible(newValue == State.RUNNING);
			}

		});
		new Thread(t).start();
	}
}
