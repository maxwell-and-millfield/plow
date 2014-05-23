package plow.model;

public class ArgumentSettings implements Settings {

	String traktor, library;

	public ArgumentSettings() {
		if (System.getProperty("library") == null) {
			throw new RuntimeException("No path for music library folder given. "
					+ "Usage: -Dlibrary=/path/to/music/library/");
		} else if (System.getProperty("traktor") == null) {
			throw new RuntimeException("No path for Traktor collection given. "
					+ "Usage -Dtraktor=/path/to/traktor/collection.nml");
		} else {
			library = System.getProperty("library");
			traktor = System.getProperty("traktor");
		}
	}

	@Override
	public String getTraktorLibraryPath() {
		return traktor;
	}

	@Override
	public String getLibraryPath() {
		return library;
	}

}
