package plow.model;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class Track {

	private AudioFile file;

	private Tag tag;
	private Map<FieldKey, Id3TagProperty> tagProperties = new HashMap<>();

	/**
	 * a prefix displayed before the filename. Good prefixes can be playlist
	 * names, parent directory names or music collection names.
	 */
	private String filenamePrefix = "";

	public Track(AudioFile file) {
		this.file = file;
		this.tag = file.getTag();
	}

	public Id3TagProperty getId3TagProperty(final FieldKey key) {
		if (key == null) {
			throw new NullPointerException();
		} else {
			Id3TagProperty property = tagProperties.get(key);

			if (property == null) {
				property = new Id3TagProperty(tag, key);
				tagProperties.put(key, property);
			}

			return property;
		}
	}

	public String getId3TagValue(FieldKey key) {
		SimpleStringProperty property = getId3TagProperty(key);
		return property == null ? null : property.get();
	}

	public void setId3TagValue(FieldKey key, String value) {
		getId3TagProperty(key).set(value);
	}

	public String getFilename() {
		return file.getFile().getName();
	}

	/**
	 * Returns the file name with the given prefix.
	 * 
	 * @return the file name with a given prefix, e.g.
	 *         "Deep House/Sleepless.mp3"
	 */
	public String getFilenameWithPrefix() {
		return filenamePrefix + file.getFile().getName();
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
