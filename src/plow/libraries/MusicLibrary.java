package plow.libraries;

import java.util.HashMap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import plow.model.Playlist;
import plow.model.Track;

public class MusicLibrary {

	private final ObservableMap<String, Track> tracks = FXCollections
			.observableMap(new HashMap<String, Track>());

	private final StringProperty library = new SimpleStringProperty();
	private final StringProperty traktorLibrary = new SimpleStringProperty();

	private final ObservableMap<String, Playlist> playlists = FXCollections
			.observableMap(new HashMap<String, Playlist>());

	public ObservableMap<String, Track> getTracks() {
		return tracks;
	}

	public ObservableMap<String, Playlist> getPlaylists() {
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
