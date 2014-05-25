package plow.libraries;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;

import javafx.beans.property.StringProperty;
import plow.libraries.serializer.StringPropertySerializer;
import plow.model.Playlist;
import plow.model.Track;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sun.javafx.collections.MappingChange.Map;

public class LibraryWriter implements JsonDeserializer<MusicLibrary>, JsonSerializer<Playlist> {

	private final Gson gson;

	public LibraryWriter() {
		final GsonBuilder builder = new GsonBuilder();
		// builder.registerTypeAdapter(MusicLibrary.class, this);
		builder.registerTypeAdapter(Playlist.class, this);
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

	@Override
	public JsonElement serialize(final Playlist playlist, final Type type, final JsonSerializationContext context) {
		final JsonObject res = new JsonObject();
		res.add("name", context.serialize(playlist.getName()));
		final JsonArray tracks = new JsonArray();
		for (final Track t : playlist.getTracks()) {
			tracks.add(context.serialize(t.getFilenameWithPrefix()));
		}
		res.add("tracks", tracks);
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MusicLibrary deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context)
			throws JsonParseException {
		if (element instanceof JsonObject) {
			final JsonObject object = (JsonObject) element;
			final MusicLibrary result = new MusicLibrary();
			result.setLibrary(object.get("library").getAsString());
			result.setTraktorLibrary(object.get("traktorLibrary").getAsString());
			Map<String, Track> tracks = null;
			tracks = context.deserialize(object.get("tracks"), tracks.getClass());
			result.getTracks().putAll((java.util.Map<? extends String, ? extends Track>) tracks);
			if (object.has("playlists")) {
				for (final Entry<String, JsonElement> entry : object.get("playlists").getAsJsonObject().entrySet()) {
					result.getPlaylists().add(deserializePlaylist(result, (JsonObject) entry.getValue()));
				}
			}
		}
		return null;
	}

	protected Playlist deserializePlaylist(final MusicLibrary library, final JsonObject object) {
		final Playlist playlist = new Playlist();
		playlist.setName(object.get("name").getAsString());
		playlist.setId(object.get("id").getAsString());
		for (final JsonElement element : object.get("tracks").getAsJsonArray()) {
			if (library.getTracks().containsKey(element.getAsString())) {
				playlist.getTracks().add(library.getTracks().get(element.getAsString()));
			}
		}
		return playlist;

	}

}
