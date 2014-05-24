package plow.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;

public class Id3TagProperty extends SimpleStringProperty {

	private final Tag tag;

	private final FieldKey key;

	public Id3TagProperty(final Tag tag, final FieldKey key) {
		this.tag = tag;
		this.key = key;
		this.set(tag.getFirst(key));
		this.addListener(onChangeListener);
	}

	public Tag getTag() {
		return tag;
	}

	public FieldKey getKey() {
		return key;
	}

	private final ChangeListener<String> onChangeListener = new ChangeListener<String>() {

		@Override
		public void changed(final ObservableValue<? extends String> observable, final String oldValue,
				final String newValue) {
			try {
				tag.setField(key, newValue);
			} catch (KeyNotFoundException | FieldDataInvalidException e) {
				e.printStackTrace();
			}
		}
	};

}
