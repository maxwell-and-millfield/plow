package plow.libraries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.beans.property.StringProperty;
import plow.libraries.serializer.MusicLibraryDeserializer;
import plow.libraries.serializer.PlaylistSerializer;
import plow.libraries.serializer.StringPropertySerializer;
import plow.model.Playlist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LibraryWriter {

	private final Gson gson;

	public LibraryWriter() {
		final GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(MusicLibrary.class, new MusicLibraryDeserializer());
		builder.registerTypeAdapter(Playlist.class, new PlaylistSerializer());
		builder.registerTypeAdapter(StringProperty.class, new StringPropertySerializer());
		gson = builder.create();
	}

	public void save(final MusicLibrary c, final Path f) {
		final String s = gson.toJson(c);
		try (BufferedWriter writer = Files.newBufferedWriter(f, Charset.forName("UTF-8"))) {
			writer.write(s, 0, s.length());
		} catch (final IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	public MusicLibrary load(final Path f) {
		if (f.toFile().exists()) {
			try (BufferedReader reader = Files.newBufferedReader(f, Charset.forName("UTF-8"))) {
				String res = "";
				String nextLine;
				while ((nextLine = reader.readLine()) != null) {
					res = res + nextLine;
				}
				return gson.fromJson(res, MusicLibrary.class);
			} catch (final IOException x) {
				throw new RuntimeException(x);
			}
		} else {
			final MusicLibrary lib = new MusicLibrary();
			if (System.getProperty("library") == null) {
				throw new RuntimeException("No path for music library folder given. "
						+ "Usage: -Dlibrary=/path/to/music/library/");
			} else if (System.getProperty("traktor") == null) {
				throw new RuntimeException("No path for Traktor collection given. "
						+ "Usage -Dtraktor=/path/to/traktor/collection.nml");
			} else {
				lib.setLibrary(System.getProperty("library"));
				lib.setTraktorLibrary(System.getProperty("traktor"));
			}
			return lib;
		}
	}

}
