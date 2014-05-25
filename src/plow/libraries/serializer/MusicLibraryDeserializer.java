package plow.libraries.serializer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.jaudiotagger.tag.FieldKey;

import plow.libraries.MusicLibrary;
import plow.model.Id3TagProperty;
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
				final JsonObject obj = e.getValue().getAsJsonObject();
				final Track t = new Track(result, obj.get("filenamePrefix").getAsString(),
						new File(e.getKey()).getName());
				t.setLastModified(obj.get("lastModified").getAsLong());
				if (obj.has("tags")) {
					for (final Entry<String, JsonElement> tag : obj.get("tags").getAsJsonObject().entrySet()) {
						t.getTagProperties().put(FieldKey.valueOf(tag.getKey()),
								new Id3TagProperty(t, FieldKey.valueOf(tag.getKey()), tag.getValue().getAsString()));
					}
				}
				result.getTracks().put(e.getKey(), t);
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
