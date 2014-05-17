package plow.model;

public class Track extends Bound {

	private String artist, title, filename;

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		changes.firePropertyChange("artist", this.artist, artist);
		this.artist = artist;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		String oldTitle = this.title;
		this.title = title;
		changes.firePropertyChange("title", oldTitle, title);
		
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		String oldFilename = this.filename;
		this.filename = filename;
		changes.firePropertyChange("artist", oldFilename, filename);
	}

}
