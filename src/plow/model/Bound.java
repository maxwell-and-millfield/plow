package plow.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

abstract public class Bound {

	protected final PropertyChangeSupport changes = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

}
