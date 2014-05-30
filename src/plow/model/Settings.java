package plow.model;

import com.google.inject.ImplementedBy;

@ImplementedBy(FileSettings.class)
public interface Settings {

	/**
	 * Returns the absolute path to Traktor's collection.nml, e.g.
	 * C:\Users\DJ\Documents\Native Instruments\Traktor 2.X.X\collection.nml
	 * 
	 * @return the absolute path to Traktor's library file
	 */
	public String getTraktorLibraryFile();

	/**
	 * Returns the absolute path to the music library folder, e.g.
	 * C:\Users\DJ\Music\
	 * 
	 * @return the absolute path to the music library folder
	 */
	public String getMusicLibraryFolder();

	/**
	 * Returns the absolute path to Plow's library file, commonly stored in the
	 * root of {@link #getMusicLibraryFolder()}, e.g.
	 * C:\Users\DJ\Music\library.plow
	 * 
	 * @return the absolute path to Plow's library file
	 */
	public String getPlowLibraryFile();

	/**
	 * Returns true, if there are Settings to use, or false if this is a fresh
	 * run.
	 * 
	 * @return true, if all settings exist.
	 */
	public boolean exist();

}
