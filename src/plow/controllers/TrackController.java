package plow.controllers;

import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import org.jaudiotagger.tag.FieldKey;

import plow.libraries.MusicLibrary;
import plow.model.Playlist;
import plow.model.Track;
import plow.model.tag.provider.SpotifyTagProvider;
import plow.model.tag.provider.TagSearchResult;

public class TrackController extends PlowController {

	@FXML private SplitMenuButton searchButton;

	@FXML private TextField artist;

	@FXML private TextField year;

	@FXML private ToggleGroup searchProvider;

	@FXML private TextField album;

	@FXML private ListView<Playlist> playlists;

	@FXML private Button saveButton;

	@FXML private TextField title;

	@FXML private TableView<TagSearchResult> searchResults;

	@FXML private TextField searchBox;

	@FXML private TableColumn<TagSearchResult, String> artistColumn;
	@FXML private TableColumn<TagSearchResult, String> titleColumn;
	@FXML private TableColumn<TagSearchResult, String> yearColumn;
	@FXML private TableColumn<TagSearchResult, String> albumColumn;

	private Track track;

	private MusicLibrary lib;

	public void initialize() {
		artistColumn.setCellValueFactory(new PropertyValueFactory<TagSearchResult, String>("artist"));
		titleColumn.setCellValueFactory(new PropertyValueFactory<TagSearchResult, String>("title"));
		yearColumn.setCellValueFactory(new PropertyValueFactory<TagSearchResult, String>("year"));
		albumColumn.setCellValueFactory(new PropertyValueFactory<TagSearchResult, String>("album"));

		final Callback<TableColumn<TagSearchResult, String>, TableCell<TagSearchResult, String>> cellFactory = new Callback<TableColumn<TagSearchResult, String>, TableCell<TagSearchResult, String>>() {
			@Override
			public TableCell<TagSearchResult, String> call(final TableColumn p) {
				final TableCell<TagSearchResult, String> cell = new TableCell<TagSearchResult, String>() {
					@Override
					public void updateItem(final String item, final boolean empty) {
						super.updateItem(item, empty);
						setText(empty ? null : getString());
						setGraphic(null);
					}

					private String getString() {
						return getItem() == null ? "" : getItem().toString();
					}
				};

				cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(final MouseEvent event) {
						if (event.getClickCount() > 1) {
							System.out.println("key");
							final TagSearchResult trs = searchResults.getSelectionModel().getSelectedItem();
							if (trs == null) {
								return;
							}
							System.out.println("OK");
							track.getId3TagProperty(FieldKey.ARTIST).set(trs.getArtist());
							track.getId3TagProperty(FieldKey.ALBUM).set(trs.getAlbum());
							track.getId3TagProperty(FieldKey.TITLE).set(trs.getTitle());
							track.getId3TagProperty(FieldKey.YEAR).set(trs.getYear());
						}
					}
				});
				return cell;
			}
		};
		artistColumn.setCellFactory(cellFactory);
	}

	public Track getTrack() {
		return track;
	}

	public void setTrack(final Track track) {
		System.out.println("set track " + track);
		this.track = track;
		title.textProperty().bindBidirectional(track.getId3TagProperty(FieldKey.TITLE));
		artist.textProperty().bindBidirectional(track.getId3TagProperty(FieldKey.ARTIST));
		year.textProperty().bindBidirectional(track.getId3TagProperty(FieldKey.YEAR));
		album.textProperty().bindBidirectional(track.getId3TagProperty(FieldKey.ALBUM));
	}

	public void setLibrary(final MusicLibrary lib) {
		this.lib = lib;
		this.playlists.setCellFactory(CheckBoxListCell.forListView(new Callback<Playlist, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(final Playlist param) {
				return param.trackInPlaylistProperty(track);
			}
		}));

		this.playlists.setItems(lib.getPlaylists());
	}

	public void searchTags() {
		final SpotifyTagProvider tp = new SpotifyTagProvider(searchBox.getText());
		tp.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@SuppressWarnings("unchecked")
			@Override
			public void handle(final WorkerStateEvent event) {
				searchResults.getItems().clear();
				searchResults.getItems().addAll((List<TagSearchResult>) event.getSource().getValue());
			}

		});
		new Thread(tp).run();
	}
}
