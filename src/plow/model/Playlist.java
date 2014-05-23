package plow.model;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {

	private StringProperty name = new SimpleStringProperty();
	private ObservableList<Track> tracks = FXCollections.observableArrayList();

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public ObservableList<Track> getTracks() {
		return tracks;
	}

	@Override
	public String toString() {
		return getName();
	}

}
