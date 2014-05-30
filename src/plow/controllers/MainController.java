package plow.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import javax.inject.Inject;

import org.jaudiotagger.tag.FieldKey;

import plow.libraries.DirectoryScanner;
import plow.libraries.LibraryWriter;
import plow.libraries.MusicLibrary;
import plow.libraries.TraktorLibraryWriter;
import plow.model.Playlist;
import plow.model.Settings;
import plow.model.Track;
import plow.model.TrackId3TagValueFactory;
import plow.ui.Main;

import com.cathive.fx.guice.GuiceFXMLLoader;
import com.cathive.fx.guice.GuiceFXMLLoader.Result;

/**
 * The Main Controller. It initializes the Models and Views for Main.fxml.
 * 
 * @author Maxwell & Millfield
 */
public class MainController extends PlowController {

	@FXML
	private ListView<Playlist> playlistsView;

	@FXML
	private TableColumn<Track, String> titleColumn, artistColumn, filenameColumn;

	@FXML
	private TableView<Track> tracksTable;

	@FXML
	private Label backgroundLabel;

	@FXML
	private ProgressIndicator backgroundIndicator;

	@FXML
	private ContextMenu tableMenu;

	@Inject
	private LibraryWriter libWriter;

	private MusicLibrary lib;

	@Inject
	private Settings settings;

	@Inject
	GuiceFXMLLoader fxmlLoader;

	@FXML
	public void initialize() {
		playlistsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		playlistsView.getSelectionModel().selectedItemProperty().addListener(playlistChangeListener);
		playlistsView.setCellFactory(new Callback<ListView<Playlist>, ListCell<Playlist>>() {

			@Override
			public ListCell<Playlist> call(final ListView<Playlist> param) {
				final ListCell<Playlist> result = new ListCell<Playlist>() {
					@Override
					protected void updateItem(final Playlist item, final boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setText("");
						} else {
							setText(item.getName());
						}

					}
				};
				result.setOnDragOver(new EventHandler<DragEvent>() {
					@Override
					public void handle(final DragEvent event) {
						if (event.getDragboard().hasFiles() && result.getItem() != null) {
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
							event.consume();
						}
					}
				});
				result.setOnDragDropped(new EventHandler<DragEvent>() {
					@Override
					public void handle(final DragEvent event) {
						for (final File f : event.getDragboard().getFiles()) {
							final Path libraryPath = Paths.get(lib.getLibrary());
							Path path = Paths.get(f.getAbsolutePath());
							if (path.startsWith(libraryPath)) {
								path = libraryPath.relativize(path);
								String filename = path.toString();
								filename = filename.replace("\\", "/");
								if (lib.getTracks().containsKey(filename)) {
									final Track track = lib.getTracks().get(filename);
									if (!result.getItem().getTracks().contains(track)) {
										result.getItem().getTracks().add(track);
									} else {
										System.out.println("Alread in playlist: " + track.getFilenameWithPrefix());
									}
								} else {
									System.out.println("Not in library: " + filename);
								}
							} else {
								System.out.println("Not in library path: " + f.getAbsolutePath());
							}
						}
					}
				});
				return result;
			}
		});

		titleColumn.setCellValueFactory(new TrackId3TagValueFactory(FieldKey.TITLE));
		artistColumn.setCellValueFactory(new TrackId3TagValueFactory(FieldKey.ARTIST));
		filenameColumn.setCellValueFactory(new PropertyValueFactory<Track, String>("filenameWithPrefix"));

		// make the Table editable, by placing TextFields in the Cells
		titleColumn.setCellFactory(TextFieldTableCell.<Track> forTableColumn());
		artistColumn.setCellFactory(TextFieldTableCell.<Track> forTableColumn());

		tracksTable.getSortOrder().add(titleColumn);
		tracksTable.setContextMenu(tableMenu);
		// Display a spinner as placeholder while the playlists load
		playlistsView.setPlaceholder(null);
		final ProgressIndicator spinner = new ProgressIndicator();
		spinner.setMaxHeight(50);
		tracksTable.setPlaceholder(spinner);
		// Load the playlists!
		initializeModels();
	}

	private void initializeModels() {
		lib = libWriter.load(Paths.get(settings.getPlowLibraryFile()));
		// lib.setLibrary(settings.getLibraryPath());
		// lib.setTraktorLibrary(settings.getTraktorLibraryPath());
		playlistsView.setItems(lib.getPlaylists());

		tracksTable.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (tracksTable.getSelectionModel().getSelectedItems().size() > 0) {
					final Dragboard db = tracksTable.startDragAndDrop(TransferMode.COPY);
					final ClipboardContent cc = new ClipboardContent();
					final List<File> files = new ArrayList<>();
					for (final Track t : tracksTable.getSelectionModel().getSelectedItems()) {
						files.add(new File(lib.getLibrary() + "/" + t.getFilenameWithPrefix()));
					}
					cc.putFiles(files);
					db.setContent(cc);
					event.consume();
				}
			}

		});
		if (!playlistsView.getItems().isEmpty() && playlistsView.getSelectionModel().isEmpty()) {
			playlistsView.getSelectionModel().select(0);
		}
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

	private final TraktorLibraryWriter tw = new TraktorLibraryWriter();;

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

	public void displaySettings() throws IOException {
		final Stage stage = new Stage();
		final Result result = fxmlLoader.load(Main.class.getResource("Settings.fxml"));
		final Scene scene = new Scene(result.<Parent> getRoot());
		((PlowController) result.getController()).setScene(scene);
		stage.setScene(scene);
		stage.setTitle("Settings");
		stage.show();
	}

	public void saveMusicLibrary() {
		executeBackgroundTask(new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				updateMessage("Saving...");
				libWriter.save(lib, Paths.get(settings.getPlowLibraryFile()));
				return true;
			}

		});

	}

	public void exportToTraktor() {
		executeBackgroundTask(new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				updateMessage("Exporting...");
				System.out.println("mhm");
				tw.writeToTraktorLibrary(lib);
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
				System.out.println("hide");
			}
		});
	}

	protected void executeBackgroundTask(final Task<?> t) {
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

	public void deleteFromPlaylist() {
		tracksTable.getItems().remove(tracksTable.getSelectionModel().getSelectedItem());
	}

	public void tableKeyPressed(final KeyEvent e) {
		if (e.getCode() == KeyCode.DELETE) {
			deleteFromPlaylist();
		}
	}
}
