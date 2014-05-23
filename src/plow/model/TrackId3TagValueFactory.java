package plow.model;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import org.jaudiotagger.tag.FieldKey;

public class TrackId3TagValueFactory implements Callback<CellDataFeatures<Track, String>, ObservableValue<String>> {

	private final FieldKey key;

	public TrackId3TagValueFactory(FieldKey key) {
		this.key = key;
	}

	@Override
	public ObservableValue<String> call(CellDataFeatures<Track, String> param) {
		return param.getValue().getId3TagProperty(key); 
	}

}