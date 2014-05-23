package plow.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class Track {

	private AudioFile file;

	/**
	 * a prefix displayed before the filename. Good prefixes can be playlist
	 * names, parent directory names or music collection names.
	 */
	private String filenamePrefix = "";

	private StringProperty artist = new SimpleStringProperty();
	private StringProperty title = new SimpleStringProperty();

	public Track(AudioFile file) {
		Tag tag = file.getTag();

		this.file = file;
		this.artist.set(tag.getFirst(FieldKey.ARTIST));
		this.title.set(tag.getFirst(FieldKey.TITLE));
	}

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
		return (file == null) ? null : file.getFile().getName();
	}

	/**
	 * Returns the file name with the given prefix.
	 * 
	 * @return the file name with a given prefix, e.g.
	 *         "Deep House/Sleepless.mp3"
	 */
	public String getFilenameWithPrefix() {
		return file == null ? null : filenamePrefix + file.getFile().getName();
	}

	/**
	 * Returns the file name prefix. Prefixes can be playlist names, parent
	 * directory names or music collection names, e.g. "Deep House/".
	 * 
	 * @return
	 */
	public String getFilenamePrefix() {
		return filenamePrefix;
	}

	/**
	 * Sets the file name prefix. Good prefixes can be playlist names, parent
	 * directory names or music collection names. Don't forget to add a trailing
	 * slash to the prefix!
	 * 
	 * @param filenamePrefix
	 */
	public void setFilenamePrefix(String filenamePrefix) {
		if (filenamePrefix == null) {
			filenamePrefix = "";
		}

		this.filenamePrefix = filenamePrefix;
	}

}
