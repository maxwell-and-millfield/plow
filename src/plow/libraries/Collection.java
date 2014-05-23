package plow.libraries;

import java.util.HashMap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import plow.model.Playlist;
import plow.model.Track;

public class Collection {

	private final ObservableMap<String, Track> tracks = FXCollections
			.observableMap(new HashMap<String, Track>());

	private final StringProperty library = new SimpleStringProperty();
	private final StringProperty traktorLibrary = new SimpleStringProperty();

	private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

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

}