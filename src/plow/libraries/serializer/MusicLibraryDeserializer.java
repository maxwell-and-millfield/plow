package plow.libraries.serializer;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import plow.libraries.MusicLibrary;
import plow.model.Playlist;
import plow.model.Track;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class MusicLibraryDeserializer implements JsonDeserializer<MusicLibrary> {

	@SuppressWarnings("unchecked")
	@Override
	public MusicLibrary deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context)
			throws JsonParseException {
		if (element instanceof JsonObject) {
			final JsonObject object = (JsonObject) element;
			final MusicLibrary result = new MusicLibrary();
			result.setLibrary(object.get("library").getAsString());
			result.setTraktorLibrary(object.get("traktorLibrary").getAsString());
			for (final Entry<String, JsonElement> e : object.get("tracks").getAsJsonObject().entrySet()) {
				result.getTracks().put(
						e.getKey(),
						new Track(result, e.getValue().getAsJsonObject().get("filenamePrefix").getAsString(), e
								.getKey()));
			}
			if (object.has("playlists")) {
				for (final JsonElement e : object.get("playlists").getAsJsonArray()) {
					result.getPlaylists().add(deserializePlaylist(result, e.getAsJsonObject()));
				}
			}
			return result;
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
