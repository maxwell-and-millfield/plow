package plow.model;

public class ArgumentSettings implements Settings {

	String traktor, library;

	public ArgumentSettings(final String args[]) {
		System.out.println(args);
		if (args.length != 2) {
			throw new RuntimeException("No paths for library and traktor library given.");
		}
		library = args[0];
		traktor = args[1];
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
