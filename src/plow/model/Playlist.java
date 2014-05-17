package plow.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist extends Bound {

	private String name;
	private final List<Track> tracks = new ArrayList<Track>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		changes.firePropertyChange("name", oldName, name);

	}

	public List<Track> getTracks() {
		return tracks;
	}

}
