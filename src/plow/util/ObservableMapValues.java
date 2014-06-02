package plow.util;

import java.util.ArrayList;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class ObservableMapValues<T> extends com.sun.javafx.collections.ObservableListWrapper<T> {

	public ObservableMapValues(final ObservableMap<?, T> map) {
		super(new ArrayList<>(map.values()));
		map.addListener(new MapChangeListener<Object, T>() {

			@Override
			public void onChanged(
					final javafx.collections.MapChangeListener.Change<? extends Object, ? extends T> change) {
				if (change.wasAdded()) {
					add(change.getValueAdded());
				}
				if (change.wasRemoved()) {
					remove(change.getValueRemoved());
				}
			}

		});
	}

}
