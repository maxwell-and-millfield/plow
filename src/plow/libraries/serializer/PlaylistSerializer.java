package plow.libraries.serializer;

import java.lang.reflect.Type;

import plow.model.Playlist;
import plow.model.Track;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PlaylistSerializer implements JsonSerializer<Playlist> {

	@Override
	public JsonElement serialize(final Playlist playlist, final Type type, final JsonSerializationContext context) {
		final JsonObject res = new JsonObject();
		res.add("name", context.serialize(playlist.getName()));
		res.add("id", context.serialize(playlist.getId()));
		final JsonArray tracks = new JsonArray();
		for (final Track t : playlist.getTracks()) {
			tracks.add(context.serialize(t.getFilenameWithPrefix()));
		}
		res.add("tracks", tracks);
		return res;
	}

}
