package plow.model;

import java.util.ArrayList;
import java.util.List;

public class Playlists extends Bound {

	private final List<Playlist> playlists = new ArrayList<Playlist>();

	public List<Playlist> getPlaylists() {
		return playlists;
	}

	public void addPlaylist(Playlist p) {
		playlists.add(p);
		changes.firePropertyChange("playlists", null, playlists);
	}
}
