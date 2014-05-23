package plow.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Track extends Bound {

	private StringProperty artist = new SimpleStringProperty();
	private StringProperty title = new SimpleStringProperty();
	private StringProperty filename = new SimpleStringProperty();
	
	public String getArtist() {
		return artist.get();
	}

	public void setArtist(String artist) {
		this.artist.set(artist);
	}
	
	public StringProperty getArtistProperty() {
		return artist;
	}

	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title.set(title);
	}
	
	public StringProperty getTitleProperty() {
		return title;
	}

	public String getFilename() {
		return filename.get();
	}

	public void setFilename(String filename) {
		this.filename.set(filename);
	}
	
	public StringProperty getFilenameProperty() {
		return filename;
	}

}
