package plow.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.eclipse.core.runtime.Path;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import plow.libraries.MusicLibrary;

public class Track {

	private transient AudioFile file;

	private transient Tag tag;
	private long lastModified;
	private transient final Map<FieldKey, Id3TagProperty> tagProperties = new HashMap<>();
	private transient final BooleanProperty fileExists = new SimpleBooleanProperty(true);

	/**
	 * a prefix displayed before the filename. Good prefixes can be playlist
	 * names, parent directory names or music collection names.
	 */
	private String filenamePrefix = "";

	private transient MusicLibrary lib;

	public Track(final AudioFile file) {
		this.file = file;
		this.tag = file.getTag();
	}

	public Track(final MusicLibrary lib, final String prefix, final String fileName) {
		this.lib = lib;
		try {
			file = new AudioFileIO().readFile(new File(lib.getLibrary() + Path.SEPARATOR + fileName));
			tag = file.getTag();
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
			file = null;
			fileExists.set(false);
			tag = null;
		}
		this.filenamePrefix = prefix;
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

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(final long lastModified) {
		this.lastModified = lastModified;
	}

	private transient final ChangeListener<String> onTagChangedListener = new ChangeListener<String>() {
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
