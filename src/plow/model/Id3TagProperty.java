package plow.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;

public class Id3TagProperty extends SimpleStringProperty {

	private final FieldKey key;

	private transient Track track;

	public Id3TagProperty(final Track track, final FieldKey key) {
		this.track = track;
		this.key = key;
		update();
		this.addListener(onChangeListener);
	}

	public Id3TagProperty(final Track track, final FieldKey key, final String cachedValue) {
		this.track = track;
		this.key = key;
		this.set(cachedValue);
		this.addListener(onChangeListener);
	}

	public void update() {
		if (track.getTag() != null) {
			this.set(track.getTag().getFirst(this.key));
		}
	}

	public FieldKey getKey() {
		return key;
	}

	private final ChangeListener<String> onChangeListener = new ChangeListener<String>() {

		@Override
		public void changed(final ObservableValue<? extends String> observable, final String oldValue,
				final String newValue) {
			try {
				if (track.getTag() != null) {
					track.getTag().setField(key, newValue);
					// TODO: Write Tag to File?
				}
			} catch (KeyNotFoundException | FieldDataInvalidException e) {
				e.printStackTrace();
			}
		}
	};

}
