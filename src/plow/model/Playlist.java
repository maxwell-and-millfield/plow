package plow.model;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Playlist {

	private final StringProperty name = new SimpleStringProperty();
	private final ObservableList<Track> tracks = FXCollections.observableArrayList();
	private String id;
	private transient HashMap<Track, SimpleBooleanProperty> tracksInPlaylist = new HashMap<>();

	private boolean isHandlingChanges = false;

	{
	}

	public Playlist() {
		final SecureRandom sr = new SecureRandom();
		id = new BigInteger(130, sr).toString(36);
		tracks.addListener(new ListChangeListener<Track>() {
			@Override
			public void onChanged(final javafx.collections.ListChangeListener.Change<? extends Track> c) {
				isHandlingChanges = true;
				while (c.next()) {
					for (final Track t : c.getAddedSubList()) {
						if (tracksInPlaylist.containsKey(t)) {
							tracksInPlaylist.get(t).set(true);
						}
					}
					for (final Track t : c.getRemoved()) {
						if (tracksInPlaylist.containsKey(t)) {
							tracksInPlaylist.get(t).set(false);
						}
					}
				}
				isHandlingChanges = false;
			}

		});
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

	public BooleanProperty trackInPlaylistProperty(final Track t) {
		if (!tracksInPlaylist.containsKey(t)) {
			final SimpleBooleanProperty value = new SimpleBooleanProperty(tracks.contains(t));
			value.addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue,
						final Boolean newValue) {
					if (newValue.booleanValue() != oldValue.booleanValue() && !isHandlingChanges) {
						if (newValue) {
							tracks.add(t);
						} else if (!newValue) {
							tracks.remove(t);
						}
					}
				}
			});
			tracksInPlaylist.put(t, value);
		}
		return tracksInPlaylist.get(t);
	}
}
