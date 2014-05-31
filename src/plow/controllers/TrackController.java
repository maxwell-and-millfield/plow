package plow.controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

import org.jaudiotagger.tag.FieldKey;

import plow.libraries.MusicLibrary;
import plow.model.Playlist;
import plow.model.TagSearchResult;
import plow.model.Track;

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

	private Track track;

	private MusicLibrary lib;

	public void initialize() {
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
}
