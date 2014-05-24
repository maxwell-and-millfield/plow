package plow.model;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class Track {

	private final AudioFile file;

	private final Tag tag;
	private final Map<FieldKey, Id3TagProperty> tagProperties = new HashMap<>();

	/**
	 * a prefix displayed before the filename. Good prefixes can be playlist
	 * names, parent directory names or music collection names.
	 */
	private String filenamePrefix = "";

	public Track(final AudioFile file) {
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
				property.addListener(onTagChangedListener);
				tagProperties.put(key, property);
			}

			return property;
		}
	}

	public String getId3TagValue(final FieldKey key) {
		final SimpleStringProperty property = getId3TagProperty(key);
		return property == null ? null : property.get();
	}

	public void setId3TagValue(final FieldKey key, final String value) {
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

	private final ChangeListener<String> onTagChangedListener = new ChangeListener<String>() {
		@Override
		public void changed(final ObservableValue<? extends String> observable, final String oldValue,
				final String newValue) {
			// Update the file, when ID3 fields are changed
			try {
				file.commit();
			} catch (final CannotWriteException e) {
				// TODO: Display error?
				e.printStackTrace();
			}
		}
	};

}
