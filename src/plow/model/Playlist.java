package plow.model;

import java.math.BigInteger;
import java.security.SecureRandom;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {

	private final StringProperty name = new SimpleStringProperty();
	private final ObservableList<Track> tracks = FXCollections.observableArrayList();
	private String id;

	public Playlist() {
		final SecureRandom sr = new SecureRandom();
		id = new BigInteger(130, sr).toString(36);
	}

	public String getName() {
		return name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
	}

	public ObservableList<Track> getTracks() {
		return tracks;
	}

	@Override
	public String toString() {
		return getName();
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
