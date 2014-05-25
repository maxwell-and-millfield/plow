package plow.libraries.serializer;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.jaudiotagger.tag.FieldKey;

import plow.model.Id3TagProperty;
import plow.model.Track;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TrackSerializer implements JsonSerializer<Track> {

	@Override
	public JsonElement serialize(final Track track, final Type arg1, final JsonSerializationContext arg2) {
		final JsonObject result = new JsonObject();
		result.addProperty("lastModified", track.getLastModified());
		result.addProperty("filenamePrefix", track.getFilenamePrefix());
		final JsonObject tags = new JsonObject();
		for (final Entry<FieldKey, Id3TagProperty> e : track.getTagProperties().entrySet()) {
			tags.addProperty(e.getKey().toString(), e.getValue().get());
		}
		result.add("tags", tags);
		return result;
	}

}
