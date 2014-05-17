package plow.model;

public class Track extends Bound {

	private String artist, title, filename;

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		changes.firePropertyChange("artist", this.artist, this.artist = artist);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		changes.firePropertyChange("title", this.title, this.title = title);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		changes.firePropertyChange("artist", this.filename,
				this.filename = filename);
	}

}
