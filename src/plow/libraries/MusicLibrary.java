package plow.libraries;

import java.util.HashMap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import javax.inject.Inject;

import plow.model.Playlist;
import plow.model.Settings;
import plow.model.Track;

public class MusicLibrary {

	private final ObservableMap<String, Track> tracks = FXCollections.observableMap(new HashMap<String, Track>());

	private final StringProperty library = new SimpleStringProperty();
	private final StringProperty traktorLibrary = new SimpleStringProperty();

	private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

	@Inject
	private Settings settings;

	public MusicLibrary() {
		// TODO: Possible bug: When shall we use the paths from Settings and
		// when the paths injected by GSON?
		// this.library.set(settings.getMusicLibraryFolder());
		// this.traktorLibrary.set(settings.getTraktorLibraryFile());
	}

	public ObservableMap<String, Track> getTracks() {
		return tracks;
	}

	public ObservableList<Playlist> getPlaylists() {
		return playlists;
	}

	public StringProperty libraryProperty() {
		return library;
	}

	public StringProperty traktorLibraryProperty() {
		return traktorLibrary;
	}

	public void setLibrary(final String library) {
		this.library.setValue(library);
	}

	public String getLibrary() {
		return this.library.getValue();
	}

	public void setTraktorLibrary(final String traktorLibrary) {
		this.traktorLibrary.setValue(traktorLibrary);
	}

	public String getTraktorLibrary() {
		return traktorLibrary.getValue();
	}

	public void addTrack(final Track t) {
		tracks.put(t.getFilenameWithPrefix(), t);
	}

}
