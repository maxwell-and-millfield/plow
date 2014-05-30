package plow.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class FileSettings implements Settings {

	private final File settingsFile;

	private final Properties properties;

	private final Map<String, StringProperty> propertiesMap;

	private final static String[] KEYS = { "traktorLibraryPath", "libraryPath" };

	private final static String SETTINGS_FILE = ".plow.preferences";

	@Inject
	public FileSettings() {
		settingsFile = new File(System.getProperty("user.home"), SETTINGS_FILE);
		properties = new Properties();
		FileReader reader = null;
		propertiesMap = new HashMap<>();

		if (exist()) {
			try {
				reader = new FileReader(settingsFile);
				properties.load(reader);
			} catch (final IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (final String key : KEYS) {
			propertiesMap.put(key, new SimpleStringProperty(properties.getProperty(key)));
			System.out.println("Loaded Setting " + key + " = " + properties.getProperty(key));
		}
	}

	@Override
	public String getTraktorLibraryFile() {
		return traktorLibraryFileProperty().get();
	}

	public StringProperty traktorLibraryFileProperty() {
		return propertiesMap.get("traktorLibraryPath");
	}

	@Override
	public String getMusicLibraryFolder() {
		return musicLibraryFolderProperty().get();
	}

	public StringProperty musicLibraryFolderProperty() {
		return propertiesMap.get("libraryPath");
	}

	@Override
	public boolean exist() {
		return settingsFile.exists();
	}

	@Override
	public String getPlowLibraryFile() {
		return Paths.get(getMusicLibraryFolder(), Constants.LIBRARY_FILE_NAME).toString();
	}

	private final ChangeListener<String> onChangeListener = new ChangeListener<String>() {
		@Override
		public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
			for (final Entry<String, StringProperty> entry : propertiesMap.entrySet()) {
				properties.put(entry.getKey(), entry.getValue().get());

				FileWriter writer = null;
				try {
					if (!exist()) {
						settingsFile.createNewFile();
						// TODO: Hide settings file on Windows!
					}

					writer = new FileWriter(settingsFile);
					properties.store(writer, null);
				} catch (final IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (writer != null) {
							writer.close();
						}
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	};

}
